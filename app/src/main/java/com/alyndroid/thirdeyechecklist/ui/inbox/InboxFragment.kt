package com.alyndroid.thirdeyechecklist.ui.inbox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation

import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.UserChecklistData
import com.alyndroid.thirdeyechecklist.databinding.FragmentInboxBinding
import com.alyndroid.thirdeyechecklist.ui.checklists.ChecklistsAdapter
import com.alyndroid.thirdeyechecklist.util.SharedPreference


class InboxFragment : Fragment() {
    private lateinit var binding: FragmentInboxBinding
    private var checklists = mutableListOf<UserChecklistData>()
    private val viewModel: InboxViewModel by lazy {
        ViewModelProviders.of(this).get(InboxViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_inbox, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        val adapter = InboxChecklistsAdapter(requireContext(), InboxChecklistsAdapter.ChecklistClickListener{
            id, dueId ->
            val action =
                InboxFragmentDirections.actionInboxFragmentToInboxPagesFragment(
                    id, dueId
                    )
            navController.navigate(action)

        }

        )
        binding.inboxRecyclerView.adapter = adapter

        viewModel.getUserChecklists(SharedPreference(requireContext()).getValueInt("userID"))
        viewModel.checklistsResponse.observe(requireActivity(), Observer {
            if (it.success){
                if (it.data.isEmpty()) {
                    binding.emptyChecklistsTv.isVisible = true
                } else {
                    adapter.submitList(it.data)
                    adapter.notifyDataSetChanged()
                }

            }
        })
        viewModel.loading.observe(requireActivity(), Observer {
            if (it) {
                binding.shimmerViewContainer.startShimmerAnimation()
                binding.shimmerViewContainer.isVisible = true
                binding.inboxRecyclerView.isVisible = false
            } else {
                binding.shimmerViewContainer.stopShimmerAnimation()
                binding.shimmerViewContainer.isVisible = false
                binding.inboxRecyclerView.isVisible = true
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getUserChecklists(SharedPreference(requireContext()).getValueInt("userID"))
            binding.swipeRefreshLayout.isRefreshing = false
        }


    }
}
