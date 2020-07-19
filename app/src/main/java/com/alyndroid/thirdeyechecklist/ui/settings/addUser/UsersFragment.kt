package com.alyndroid.thirdeyechecklist.ui.settings.addUser

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.RemoteUserData
import com.alyndroid.thirdeyechecklist.databinding.FragmentUsersBinding
import com.alyndroid.thirdeyechecklist.util.Constant
import com.alyndroid.thirdeyechecklist.util.SharedPreference
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.fragment_check_lists.*


class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding
    private var relatedUsersList = mutableListOf<RemoteUserData>()
    private var phone = ""
    private var editingUserId = 0
    private var editing = false
    private val viewModel: AddUserViewModel by lazy {
        ViewModelProviders.of(this).get(AddUserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = SharedPreference(requireContext()).getValueInt("userID")
        val adapter = RelatedUsersAdapter(
            requireContext(),
            RelatedUsersAdapter.DeleteRelatedUserClickListener {
                deleteUserAction(it)
            },
            RelatedUsersAdapter.CallRelatedUserClickListener {
                phone = it
                callUserAction()
            },
            RelatedUsersAdapter.EditRelatedUserClickListener {
                editUserAction(it)
            }
        )

        binding.usersRecycler.adapter = adapter

        viewModel.getUsers(userId)
        binding.addUserBtn.attachTextChangeAnimator()
        binding.addUserBtn.setOnClickListener {
            if (checkInputs()) {
                val map = HashMap<Any, Any>()

                map.put("name", binding.userNameEt.text.toString())
                map.put("email", binding.userEmailEt.text.toString())
                map.put("phone_number", binding.userPhoneEt.text.toString())
                map.put("country", Constant.countries.find { c->c.name_en == binding.countryEt.text.toString()}!!.id)
                map.put("password", binding.passwordEt.text.toString())
                map.put("create_subuser", if (binding.createSubUserCp.isChecked)1 else 0)
                map.put("self_evaluation", if (binding.selfEvaluationCp.isChecked)1 else 0)
                map.put("active", if (binding.activeCp.isChecked)1 else 0)
                map.put("chief_user", userId)
                map.put("created_by", userId)
                if (editing){
                    viewModel.editUser(editingUserId, map)
                }else {
                    viewModel.addUser(map)
                }
            }
        }

        viewModel.addUserLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.addUserBtn.showProgress {
                    buttonTextRes = R.string.loading
                    progressColor = Color.WHITE
                }
            } else {
                binding.addUserBtn.hideProgress(getString(R.string.add_new_user))
            }
        })



        viewModel.loading.observe(requireActivity(), Observer {
            if (it) {
                binding.shimmerViewContainer.isVisible = true
                shimmer_view_container.startShimmerAnimation()
            } else {
                binding.shimmerViewContainer.isVisible = false
                shimmer_view_container.stopShimmerAnimation()
            }
        })

        viewModel.addUserResponse.observe(requireActivity(), Observer {
            if (it.success) {
                clearFields()
                relatedUsersList.add(it.data)
                adapter.submitList(relatedUsersList.reversed())
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.editUserResponse.observe(requireActivity(), Observer {
            if (it.success) {
                Toast.makeText(requireContext(), getString(R.string.done), Toast.LENGTH_LONG).show()
                clearFields()
                relatedUsersList.removeIf { c->c.id== it.data.id }
                relatedUsersList.add(it.data)
                adapter.submitList(relatedUsersList.reversed())
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.relatedUsersResponse.observe(requireActivity(), Observer {
            if (it.success) {
                if (it.data.isEmpty()) {

                } else {
                    relatedUsersList = it.data.toMutableList()
                    adapter.submitList(relatedUsersList.reversed())
                    adapter.notifyDataSetChanged()
                }
            }
        })

        viewModel.deleteRelatedUserResponse.observe(requireActivity(), Observer {
            if (it.success) {
                relatedUsersList.removeIf { c -> c.id == it.data.toInt() }
                adapter.submitList(relatedUsersList.reversed())
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.cannot_delete),
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        val taskTypeAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            context!!,
            R.layout.support_simple_spinner_dropdown_item,
            Constant.countries.map { c -> c.name_en }
        )

        binding.countryEt.setAdapter(taskTypeAdapter)
    }

    private fun editUserAction(userData: RemoteUserData) {
        val materialDialog = MaterialDialog.Builder(requireActivity())
            .setTitle(getString(R.string.edit))
            .setMessage(getString(R.string.sure_to_edit))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.yes)
            ) { dialog, _ ->
                fillViews(userData)
                dialog.cancel()
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { dialog, _ -> dialog.cancel() }
            .build()
            .show()
    }

    private fun fillViews(userData: RemoteUserData) {
        editingUserId = userData.id
        editing = true
        binding.addUserBtn.text = getString(R.string.submit)
        binding.userNameEt.setText(userData.name)
        binding.countryEt.setText(Constant.countries.find{ c->c.id==userData.country}!!.name_en)
        binding.userPhoneEt.setText(userData.phone_number)
        binding.userEmailEt.setText(userData.email)
        binding.passwordEt.setText(userData.password)
        binding.confirmPasswordEt.setText(userData.password)
        binding.activeCp.isChecked = userData.active=="1"
        binding.createSubUserCp.isChecked = userData.create_subuser=="1"
        binding.selfEvaluationCp.isChecked = userData.self_evaluation=="1"
    }

    private fun callUserAction() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CALL_PHONE),
                1
            )
        } else {
            startCall()
        }

    }

    private fun startCall() {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + phone)
        startActivity(callIntent)
    }

    @SuppressLint("RestrictedApi")
    private fun deleteUserAction(it: Int) {
        val materialDialog = MaterialDialog.Builder(requireActivity())
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.sure_to_delete))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.yes)
            ) { dialog, _ ->
                viewModel.deleteUser(it)
                dialog.cancel()
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { dialog, _ -> dialog.cancel() }
            .build()
            .show()
    }

    private fun clearFields() {
        editing = false
        binding.addUserBtn.text = getString(R.string.add_new_user)
        binding.userNameEt.text = null
        binding.countryEt.text = null
        binding.userPhoneEt.text = null
        binding.userEmailEt.text = null
        binding.passwordEt.text = null
        binding.confirmPasswordEt.text = null
        binding.activeCp.isChecked = false
        binding.createSubUserCp.isChecked = false
        binding.selfEvaluationCp.isChecked = false

    }

    private fun checkInputs(): Boolean {
        var flag: Boolean = true
        binding.userNameIl.error = null
        binding.countryIl.error = null
        binding.userPhoneIl.error = null
        binding.userEmailIl.error = null
        binding.passwordIl.error = null
        binding.confirmPasswordIl.error = null

        if (binding.userNameEt.text.toString().isEmpty()) {
            binding.userNameIl.error = getString(R.string.must_enter_name)
            flag = false
        }
        if (binding.countryEt.text.toString().isEmpty()) {
            binding.countryIl.error = getString(R.string.must_choose)
            flag = false
        }
        if (binding.userPhoneEt.text.toString().isEmpty()) {
            binding.userPhoneIl.error = getString(R.string.must_enter_phone)
            flag = false
        }
        if (binding.userEmailEt.text.toString().isEmpty()) {
            binding.userEmailIl.error = getString(R.string.must_enter_email)
            flag = false
        }
        if (binding.passwordEt.text.toString().isEmpty()) {
            binding.passwordIl.error = getString(R.string.must_enter_password)
            flag = false
        }
        if (binding.confirmPasswordEt.text.toString().isEmpty()) {
            binding.confirmPasswordIl.error = getString(R.string.must_enter_password)
            flag = false
        }

        if (binding.passwordEt.text.toString() != binding.confirmPasswordEt.text.toString()) {
            binding.confirmPasswordIl.error = getString(R.string.password_not_match)
            flag = false
        }


        return flag
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) startCall()
    }
}
