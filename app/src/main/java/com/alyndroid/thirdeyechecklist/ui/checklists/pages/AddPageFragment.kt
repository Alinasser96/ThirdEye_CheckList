package com.alyndroid.thirdeyechecklist.ui.checklists.pages

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.UserPagesData
import com.alyndroid.thirdeyechecklist.databinding.FragmentAddPageBinding
import com.alyndroid.thirdeyechecklist.util.SharedPreference
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.fragment_check_lists.*


class AddPageFragment : Fragment() {

    private lateinit var navController: NavController
    private val args: AddPageFragmentArgs by navArgs()
    lateinit var binding: FragmentAddPageBinding
    lateinit var adapter: PagesAdapter
    private var pagesList = mutableListOf<UserPagesData>()
    private val viewModel: AddPageViewModel by lazy {
        ViewModelProviders.of(this).get(AddPageViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_pages_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.done_menu_item -> {
            navController.navigate(R.id.action_addPageFragment_to_checkListsFragment)
            true
        }
        else-> super.onOptionsItemSelected(item)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_page, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        navController = Navigation.findNavController(view)
        activity!!.title = "add new Page"
        adapter = PagesAdapter(requireContext(), PagesAdapter.PageClickListener {
            val action =
                AddPageFragmentDirections.actionAddPageFragmentToAddTaskFragment(
                    it,
                    args.checkListId
                    , args.fromEditing, args.checklistData
                )
            navController.navigate(action)
        }, PagesAdapter.PageClickListener {
            val materialDialog = MaterialDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.sure_to_delete))
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, _ ->
                    viewModel.deletePage(it)
                    dialog.cancel()
                }
                .setNegativeButton(
                    getString(R.string.no)
                ) { dialog, _ -> dialog.cancel() }
                .build()
                .show()
        })
        binding.pagesRecycler.adapter = adapter
        binding.addNewPageBtn.attachTextChangeAnimator()

        viewModel.getUserPages(args.checkListId)
        viewModel.userPagesResponse.observe(requireActivity(), Observer {
            if (it.success) {
                if (it.data.isEmpty()) {

                } else {
                    pagesList = it.data.toMutableList()
                    adapter.submitList(it.data.reversed())
                    adapter.notifyDataSetChanged()
                }
            }
        })

        viewModel.addPageLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.addNewPageBtn.showProgress {
                    buttonTextRes = R.string.loading
                    progressColor = Color.WHITE
                }
            } else {
                binding.addNewPageBtn.hideProgress(R.string.add)
            }
        })

        viewModel.addPageResponse.observe(requireActivity(), Observer {
            if (it.success) {
                pagesList.add(it.data)
                adapter.submitList(pagesList)
                adapter.notifyDataSetChanged()
            }
        })

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (args.fromEditing) {
                val action =
                    AddPageFragmentDirections.actionAddPageFragmentToEditChecklistFragment(
                        args.checklistData!!
                    )
                navController.navigate(action)
            } else
                navController.navigate(R.id.action_addPageFragment_to_addCheckListFragment)
        }
        viewModel.loading.observe(requireActivity(), Observer {
            if (it) {
                binding.shimmerViewContainer.isVisible = true
                shimmer_view_container.startShimmerAnimation()
            } else {
                binding.shimmerViewContainer.isVisible = false
                shimmer_view_container.stopShimmerAnimation()
            }
        })
        binding.addNewPageBtn.setOnClickListener {
            if (checkInputs()) {
                val map = HashMap<Any, Any>()

                map.put("name", binding.pageNameEt.text.toString())
                map.put("checklist_id", args.checkListId)
                map.put("user_id", SharedPreference(requireContext()).getValueInt("userID"))
                map.put("order", 1)
                map.put("active", binding.activeCb.isChecked)

                viewModel.addPage(map)
            }
        }

        viewModel.deletePageResponse.observe(requireActivity(), Observer {
            if (it.success) {
                pagesList.removeIf { c -> c.id == it.data.toInt() }
            }
        })
    }

    private fun checkInputs(): Boolean {
        var flag: Boolean = true
        binding.pageNameIl.error = null

        if (binding.pageNameEt.text.toString().isEmpty()) {
            binding.pageNameIl.error = getString(R.string.must_enter_name)
            flag = false
        }
        return flag
    }

}