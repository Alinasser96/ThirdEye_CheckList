package com.alyndroid.thirdeyechecklist.ui.inbox.tasks

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.InboxChoice
import com.alyndroid.thirdeyechecklist.data.model.UserTasksData
import com.alyndroid.thirdeyechecklist.databinding.FragmentInboxTasksBinding
import com.alyndroid.thirdeyechecklist.util.SharedPreference
import com.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_check_lists.*


class InboxTasksFragment : Fragment() {
    lateinit var binding: FragmentInboxTasksBinding
    private var updateing: Boolean = false
    private var infoId: Int = 0
    private val args: InboxTasksFragmentArgs by navArgs()
    private val choicesList = mutableListOf<Pair<InboxChoice, UserTasksData>>()
    private val viewModel: InboxTaskViewModel by lazy {
        ViewModelProviders.of(this).get(InboxTaskViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_inbox_tasks, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_pages_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.done_menu_item -> {
            submitAnswers()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun submitAnswers() {
        val map = HashMap<Any, Any>()
        map["checklist_id"] = args.cheklistId
        map["checklist_due_id"] = args.dueID
        map["page_id"] =args.pageID
        map["user_id"] = SharedPreference(requireContext()).getValueInt("userID")
        map["created_by"] = SharedPreference(requireContext()).getValueInt("userID")
        map["task_id"] = choicesList.map { c-> c.second.id }
        map["choice_id"] =choicesList.map { c-> c.first.id }
        map["choice_score"] =choicesList.map { c-> c.first.choice_score }
        map["max_score"] =choicesList.map { c-> c.first.choice_score }
        map["comment"] = listOf("123","ghjjh")
        map["image"] = listOf("123","hghghg")

        val materialDialog = MaterialDialog.Builder(requireActivity())
            .setTitle(getString(R.string.submit))
            .setMessage(getString(R.string.sure_to_submit_edit))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.yes)
            ) { dialog, _ ->
                if (updateing){
                    viewModel.updateAnswers(infoId, map)
                } else {
                    viewModel.submitAnswers(map)
                }

                dialog.cancel()
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { dialog, _ -> dialog.cancel() }
            .build()
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        activity!!.title = "tasks"
        setHasOptionsMenu(true)

        val adapter =
            InboxTasksAdapter(requireContext(), InboxTasksAdapter.RadioGroupClickListener {
                choicesList.removeIf { c -> c.second == it.second }
                choicesList.add(it)
            })

        binding.inboxTasksRecyclerView.adapter = adapter
        viewModel.getAnswerInfo(args.pageID, args.dueID)
        viewModel.getUserTasks(args.pageID)

        viewModel.userTasksResponse.observe(requireActivity(), Observer {
            if (it.success) {
                if (it.data.isEmpty()) {
                    binding.emptyChecklistsTv.isVisible = true
                } else {
                    adapter.submitList(it.data.reversed())
                    adapter.notifyDataSetChanged()
                }
            }
        })

        viewModel.answerInfoResponse.observe(requireActivity(), Observer {
            if (it.success){
                updateing = it.data.isNotEmpty()
                if (it.data.isNotEmpty()) infoId = it.data[0].info_id
            }
        })

        viewModel.loading.observe(requireActivity(), Observer {
            if (it) {
                binding.shimmerViewContainer.isVisible = true
                binding.inboxTasksRecyclerView.isVisible = false
                shimmer_view_container.startShimmerAnimation()
            } else {
                binding.shimmerViewContainer.isVisible = false
                binding.inboxTasksRecyclerView.isVisible = true
                shimmer_view_container.stopShimmerAnimation()
            }
        })

        viewModel.submitLoading.observe(requireActivity(), Observer {
            binding.submitAnswersProgress.isVisible = it
        })

        viewModel.submitTasksResponse.observe(requireActivity(), Observer {
            if (it.success){
                Toasty.success(requireContext(), getString(R.string.done), Toast.LENGTH_SHORT, true).show()
                val action =
                    InboxTasksFragmentDirections.actionInboxTasksFragmentToInboxPagesFragment(args.cheklistId, args.dueID)
                navController.navigate(action)

            } else {
                Toasty.error(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT, true).show()
            }
        })



        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action =
                InboxTasksFragmentDirections.actionInboxTasksFragmentToInboxPagesFragment(args.cheklistId, args.dueID)
            navController.navigate(action)
        }
    }
}
