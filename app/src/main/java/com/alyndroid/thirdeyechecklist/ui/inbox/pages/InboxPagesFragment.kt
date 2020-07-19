package com.alyndroid.thirdeyechecklist.ui.inbox.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.databinding.FragmentInboxPagesBinding
import com.alyndroid.thirdeyechecklist.ui.checklists.pages.PagesAdapter
import kotlinx.android.synthetic.main.fragment_check_lists.*


class InboxPagesFragment : Fragment() {

    private val args: InboxPagesFragmentArgs by navArgs()
    lateinit var binding: FragmentInboxPagesBinding
    private val viewModel: InboxPageViewModel by lazy {
        ViewModelProviders.of(this).get(InboxPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_inbox_pages, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        activity!!.title = "Pages"
        val adapter = InboxPagesAdapter(requireContext(), PagesAdapter.PageClickListener {
            val action =
                InboxPagesFragmentDirections.actionInboxPagesFragmentToInboxTasksFragment(
                    args.checklistID, it, args.dueID
                )
            navController.navigate(action)
        })
        binding.inboxPagesRecyclerView.adapter = adapter

        viewModel.getUserPages(args.checklistID)
        viewModel.userPagesResponse.observe(requireActivity(), Observer {
            if (it.success) {
                if (it.data.isEmpty()) {
                    binding.emptyChecklistsTv.isVisible = true
                } else {
                    adapter.submitList(it.data.reversed())
                    adapter.notifyDataSetChanged()
                }
            }
        })
        viewModel.loading.observe(requireActivity(), Observer {
            if (it) {
                binding.shimmerViewContainer.isVisible = true
                binding.inboxPagesRecyclerView.isVisible = false
                binding.shimmerViewContainer.startShimmerAnimation()
            } else {
                binding.shimmerViewContainer.isVisible = false
                binding.inboxPagesRecyclerView.isVisible = true
                binding.shimmerViewContainer.stopShimmerAnimation()
            }
        })

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action =
                InboxPagesFragmentDirections.actionInboxPagesFragmentToInboxFragment()
            navController.navigate(action)
        }
    }
}
