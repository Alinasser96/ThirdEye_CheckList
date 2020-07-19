package com.alyndroid.thirdeyechecklist.ui.checklists.tasks

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.alyndroid.thirdeyechecklist.data.model.Choice
import com.alyndroid.thirdeyechecklist.data.model.UserTasksData
import com.alyndroid.thirdeyechecklist.databinding.FragmentAddTaskBinding
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.fragment_check_lists.*

/**
 * A simple [Fragment] subclass.
 */
class AddTaskFragment : Fragment() {

    lateinit var binding: FragmentAddTaskBinding
    private var choicesList = mutableListOf<Choice>()
    private lateinit var choicesAdapter: ChoicesAdapter
    private lateinit var tasksAdapter: TasksAdapter
    private var tasksList = mutableListOf<UserTasksData>()
    private val viewModel: AddTaskViewModel by lazy {
        ViewModelProviders.of(this).get(AddTaskViewModel::class.java)
    }
    val args: AddTaskFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_task, container, false)
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        activity!!.title = "add new Task"

        viewModel.getUserTasks(args.pageID)
        binding.addTaskBtn.attachTextChangeAnimator()
        tasksAdapter = TasksAdapter(requireContext(), TasksAdapter.DeleteTaskClickListener {
            val materialDialog = MaterialDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.sure_to_delete))
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, _ ->
                    viewModel.deleteTask(it)
                    dialog.cancel()
                }
                .setNegativeButton(
                    getString(R.string.no)
                ) { dialog, _ -> dialog.cancel() }
                .build()
                .show()
        })
        binding.tasksRecycler.adapter = tasksAdapter
        choicesAdapter = ChoicesAdapter(requireContext(), ChoicesAdapter.PageClickListener {
            choicesList.removeAt(it)
            choicesAdapter.notifyDataSetChanged()

        })
        binding.choicesRecycler.adapter = choicesAdapter
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action =
                AddTaskFragmentDirections.actionAddTaskFragmentToAddPageFragment(
                    args.checklistID, args.fromEditing, args.checklistData
                )
            navController.navigate(action)
        }

        val taskType =
            arrayOf<String?>(
                "Yes/NO",
                "Yes/No/NA",
                "Choices"
            )

        val taskTypeAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            context!!,
            R.layout.support_simple_spinner_dropdown_item,
            taskType
        )

        binding.taskTypeEt.setAdapter(taskTypeAdapter)
        binding.taskTypeEt.setOnItemClickListener { adapterView, view, i, l ->
            binding.choicesLayout.visibility = View.VISIBLE
            when (i) {
                0 -> {
                    binding.yesLayout.visibility = View.VISIBLE
                    binding.noLayout.visibility = View.VISIBLE
                    binding.maybeLayout.visibility = View.GONE
                    binding.choicesLayout.visibility = View.GONE
                    binding.choicesRecycler.visibility = View.GONE
                }
                1 -> {
                    binding.yesLayout.visibility = View.VISIBLE
                    binding.noLayout.visibility = View.VISIBLE
                    binding.maybeLayout.visibility = View.VISIBLE
                    binding.choicesLayout.visibility = View.GONE
                    binding.choicesRecycler.visibility = View.GONE
                }
                2 -> {
                    binding.yesLayout.visibility = View.GONE
                    binding.noLayout.visibility = View.GONE
                    binding.maybeLayout.visibility = View.GONE
                    binding.choicesLayout.visibility = View.VISIBLE
                    binding.choicesRecycler.visibility = View.VISIBLE
                }
            }
        }

        binding.addNewChoiceBtn.setOnClickListener {
            choicesList.add(
                Choice(
                    binding.choiceEt.editableText.toString(),
                    binding.choiceScoreEt.editableText.toString().toInt()
                )
            )
            choicesAdapter.submitList(choicesList)
            choicesAdapter.notifyDataSetChanged()
        }

        binding.addTaskBtn.setOnClickListener {
            if (checkInputs()) {
                val map = HashMap<Any, Any>()

                map.put("name", binding.taskNameEt.text.toString())
                map.put("checklist_id", args.checklistID)
                map.put("page_id", args.pageID)
                when (binding.taskTypeEt.text.toString()) {
                    "Yes/NO" -> {
                        map.put("choice_title", listOf("yes", "no"))
                        map.put(
                            "choice_score",
                            listOf(
                                binding.yesScoreEt.text.toString(),
                                binding.noScoreEt.text.toString()
                            )
                        )
                        map.put("type", 1)
                    }
                    "Yes/No/NA" -> {
                        map.put("choice_title", listOf("yes", "no", "NA"))
                        map.put(
                            "choice_score",
                            listOf(
                                binding.yesScoreEt.text.toString(),
                                binding.noScoreEt.text.toString(),
                                binding.maybeScoreEt.text.toString()
                            )
                        )
                        map.put("type", 2)
                    }
                    "Choices" -> {
                        map.put("choice_title", choicesList.map { c -> c.name })
                        map.put("choice_score", choicesList.map { c -> c.score })
                        map.put("type", 3)
                    }
                }
                map.put("comments", binding.commentsCp.isChecked)
                map.put("images", binding.imageCp.isChecked)
                map.put("total_score", binding.totalScoreEt.text.toString())

                viewModel.addTask(map)
            }
        }

        viewModel.addTaskLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.addTaskBtn.showProgress {
                    buttonTextRes = R.string.loading
                    progressColor = Color.WHITE
                }
            } else {
                binding.addTaskBtn.hideProgress(R.string.add_new_task)
            }
        })

        viewModel.deleteTaskResponse.observe(requireActivity(), Observer {
            if (it.success){
                tasksList.removeIf { c->c.id == it.data.toInt()}
            }
        })

        viewModel.addTaskResponse.observe(requireActivity(), Observer {
            if (it.success) {
                tasksList.add(it.data)
                tasksAdapter.submitList(tasksList)
                tasksAdapter.notifyDataSetChanged()

                clearFields()
            }
        })

        viewModel.userTasksResponse.observe(requireActivity(), Observer {
            if (it.success) {
                if (it.data.isEmpty()) {

                } else {
                    tasksList = it.data.toMutableList()
                    tasksAdapter.submitList(it.data.reversed())
                    tasksAdapter.notifyDataSetChanged()
                }
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
    }

    private fun clearFields() {
        binding.taskNameEt.text = null
        binding.taskTypeEt.text = null
        binding.totalScoreEt.text = null
        binding.yesScoreEt.text = null
        binding.noScoreEt.text = null
        binding.maybeScoreEt.text = null
        binding.choiceScoreEt.text = null
        binding.choiceEt.text = null

        binding.choicesLayout.visibility = View.GONE
    }

    private fun checkInputs(): Boolean {
        var flag: Boolean = true
        binding.taskNameIl.error = null
        binding.taskTypeIl.error = null
        binding.totalScoreIl.error = null
        binding.yesScoreIl.error = null
        binding.noScoreIl.error = null
        binding.maybeScoreIl.error = null

        if (binding.taskNameEt.text.toString().isEmpty()) {
            binding.taskNameIl.error = getString(R.string.must_enter_name)
            flag = false
        }

        if (binding.taskTypeEt.text.toString().isEmpty()) {
            binding.taskTypeIl.error = getString(R.string.must_choose)
            flag = false
        }

        if (binding.totalScoreEt.text.toString().isEmpty()) {
            binding.totalScoreIl.error = getString(R.string.must_choose)
            flag = false
        }

        if (binding.taskTypeEt.text.toString() == "Yes/NO") {
            if (binding.yesScoreEt.text.toString().isEmpty()) {
                binding.yesScoreIl.error = getString(R.string.must_choose)
                flag = false
            }
            if (binding.noScoreEt.text.toString().isEmpty()) {
                binding.noScoreIl.error = getString(R.string.must_choose)
                flag = false
            }
        }

        if (binding.taskTypeEt.text.toString() == "Yes/No/NA") {
            if (binding.yesScoreEt.text.toString().isEmpty()) {
                binding.yesScoreIl.error = getString(R.string.must_choose)
                flag = false
            }
            if (binding.noScoreEt.text.toString().isEmpty()) {
                binding.noScoreIl.error = getString(R.string.must_choose)
                flag = false
            }
            if (binding.maybeScoreEt.text.toString().isEmpty()) {
                binding.maybeScoreIl.error = getString(R.string.must_choose)
                flag = false
            }
        }

        if (binding.taskTypeEt.text.toString() == "Choices") {
            if (choicesList.size < 2) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.must_enter_choice),
                    Toast.LENGTH_LONG
                ).show()
                flag = false
            }
        }

        return flag
    }
}
