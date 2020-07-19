package com.alyndroid.thirdeyechecklist.ui.checklists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.UserChecklistData
import com.alyndroid.thirdeyechecklist.databinding.FragmentCheckListsBinding
import com.alyndroid.thirdeyechecklist.util.SharedPreference
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.fragment_check_lists.*

class CheckListsFragment : Fragment() {

    private lateinit var adapter: ChecklistsAdapter
    private lateinit var binding: FragmentCheckListsBinding
    private var checklists = mutableListOf<UserChecklistData>()
    private val viewModel: ChecklistViewModel by lazy {
        ViewModelProviders.of(this).get(ChecklistViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_check_lists, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)

        adapter = ChecklistsAdapter(requireContext(),
            ChecklistsAdapter.ChecklistClickListener {
                val action =
                    CheckListsFragmentDirections.actionCheckListsFragmentToEditChecklistFragment(
                        checklists.find { c -> c.id == it }!!
                    )
                navController.navigate(action)
            }, ChecklistsAdapter.ChecklistClickListener {
                val materialDialog = MaterialDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.delete))
                    .setMessage(getString(R.string.sure_to_delete))
                    .setCancelable(false)
                    .setPositiveButton(
                        getString(R.string.yes)
                    ) { dialog, _ ->
                        viewModel.deleteChecklist(it)
                        dialog.cancel()
                    }
                    .setNegativeButton(
                        getString(R.string.no)
                    ) { dialog, _ -> dialog.cancel() }
                    .build()
                    .show()
            }
        )

        binding.cheklistsRecycler.adapter = adapter
        activity!!.title = "your Checklists"

        val addNewCheckLIstButton = add_new_checkList_btn
        addNewCheckLIstButton.setOnClickListener {
            navController.navigate(R.id.action_checkListsFragment_to_addCheckListFragment)
        }

        viewModel.getUserChecklists(SharedPreference(requireContext()).getValueInt("userID"))
        viewModel.loading.observe(requireActivity(), Observer {
            if (it) {
                shimmer_view_container.startShimmerAnimation()
                binding.shimmerViewContainer.isVisible = true
            } else {
                shimmer_view_container.stopShimmerAnimation()
                binding.shimmerViewContainer.isVisible = false
            }
        })

        viewModel.deleteChecklistResponse.observe(requireActivity(), Observer {
            if (it.success) {
                checklists.removeIf { c -> c.id == it.data.toInt() }
                adapter.submitList(checklists)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.checklistsResponse.observe(requireActivity(), Observer {
            if (it.success) {
                if (it.data.isEmpty()) {
                    binding.emptyChecklistsTv.isVisible = true
                } else {
                    checklists = it.data.reversed().toMutableList()
                    adapter.submitList(checklists)
                    adapter.notifyDataSetChanged()
                }

            }
        })

    }

}
