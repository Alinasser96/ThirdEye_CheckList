package com.alyndroid.thirdeyechecklist.ui.checklists.addChecklist

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.RemoteUserData
import com.alyndroid.thirdeyechecklist.databinding.FragmentAddCheckListBinding
import com.alyndroid.thirdeyechecklist.ui.notification.AlertReceiver
import com.alyndroid.thirdeyechecklist.util.SharedPreference
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_add_check_list.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class AddCheckListFragment : Fragment() {

    lateinit var myCalendar: Calendar
    lateinit var startTimeCalendar: Calendar
    var usersList: List<RemoteUserData> = listOf()
    lateinit var date: OnDateSetListener
    lateinit var binding: FragmentAddCheckListBinding
    private val selectedUsersList = mutableListOf<Int>()
    private val viewModel: AddChecklistViewModel by lazy {
        ViewModelProviders.of(this).get(AddChecklistViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_check_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.title = "add new Checklist"
        val navController = Navigation.findNavController(view)
        viewModel.loading.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it) {
                print("hii")
            }
        })
        viewModel.usersResponse.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it.success) {
                usersList = it.data
                val usersAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
                    context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    usersList.map { c -> c.name }
                )
                binding.assignToEt.setAdapter(usersAdapter)
            }
        })
        viewModel.createChecklistResponse.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it.success) {
                val action =
                    AddCheckListFragmentDirections.actionAddCheckListFragmentToAddPageFragment(
                        it.data.id, false, null
                    )
                navController.navigate(action)
            }
        })

        initUi()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            navController.navigate(R.id.action_addCheckListFragment_to_checkListsFragment)
        }

        binding.submitChecklistButton.attachTextChangeAnimator()

        binding.submitChecklistButton.setOnClickListener {
            if (checkInputs()) {
                val map = HashMap<Any, Any>()

                map.put("name", binding.checklistNameEt.text.toString())
                map.put(
                    "start_at",
                    binding.startDateEt.text.toString() + " " + binding.startTimeEt.text.toString()
                )
                map.put("active", binding.activeCb.isChecked)
                map.put(
                    "holiday_action", when (binding.dueDateEt.editableText.toString()) {
                        "Shift to nearest working day." -> "1"
                        "Keep on that date." -> "2"
                        "Do not ask for it" -> "3"
                        else -> 0
                    }
                )
                map.put("checklist_shifted_anthor_due", binding.dueDateHolidayCb.isChecked)
                map.put("assign_later", binding.assignLaterRb.isChecked)
                map.put("users", selectedUsersList)
                map.put("show_last_status", binding.showLastStatusCb.isChecked)
                map.put("force_answer", binding.forceAnswerCb.isChecked)
                map.put("availability_num", binding.expireAfterEt.text.toString())
                map.put("availability_type", binding.expireUnitEt.editableText.toString())
                map.put("notify_before_type", binding.notifyBeforeUnitEt.editableText.toString())
                map.put("notify_before_num", binding.notifyBeforeEt.text.toString())
                map.put("notified_before", binding.notifyBeforeCheckBox.isChecked)
                map.put("created_by", SharedPreference(requireContext()).getValueInt("userID"))
                map.put("working_from", binding.fromEt.text.toString())
                map.put("working_to", binding.toEt.text.toString())
                map.put("repeat", binding.repeatEveryRb.isChecked)
                map.put("repeat_type", binding.repetitionUnitEt.editableText.toString())
                map.put("repeat_num", binding.repetitionEveryEt.text.toString())
                map.put("start_time_inmills", startTimeCalendar.timeInMillis)

                viewModel.createChecklist(map)

            }
        }

        viewModel.loading.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it) {
                binding.submitChecklistButton.showProgress {
                    buttonTextRes = R.string.loading
                    progressColor = Color.WHITE
                }
            } else {
                binding.submitChecklistButton.hideProgress(R.string.add)
            }
        })
    }

    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.startDateEt.setText(sdf.format(myCalendar.getTime()))
    }


    private fun initUi() {
        val units = arrayOf<String?>("hours", "days", "months")

        val unitAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            context!!,
            R.layout.support_simple_spinner_dropdown_item,
            units
        )

        val dueDate =
            arrayOf<String?>(
                "Shift to nearest working day.",
                "Keep on that date.",
                "Do not ask for it"
            )

        val dueDateAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            context!!,
            R.layout.support_simple_spinner_dropdown_item,
            dueDate
        )


        binding.assignToEt.setOnItemClickListener { adapterView, view, i, l ->
            binding.assignToEt.text = null

            val chip = Chip(requireContext())
            chip.text = usersList[i].name
            selectedUsersList.add(usersList[i].id)
            chip.isCloseIconVisible = true
            chip.isCheckable = false
            chip.isCheckable = false
            chip.setOnCloseIconClickListener {
                val chip: Chip = it as Chip
                selectedUsersList.remove(usersList[i].id)
                binding.chipGroup.removeView(chip)
            }

            binding.chipGroup.addView(chip)
            binding.chipGroup.visibility = View.VISIBLE
        }

        binding.expireUnitEt.setAdapter(unitAdapter)
        binding.notifyBeforeUnitEt.setAdapter(unitAdapter)
        binding.repetitionUnitEt.setAdapter(unitAdapter)
        binding.dueDateEt.setAdapter(dueDateAdapter)

        myCalendar = Calendar.getInstance()
        startTimeCalendar = Calendar.getInstance()
        date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            startTimeCalendar.set(Calendar.YEAR, year)
            startTimeCalendar.set(Calendar.MONTH, monthOfYear)
            startTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            updateLabel()
        }

        binding.startTimeEt.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                OnTimeSetListener { view, hourOfDay, minute ->
                    binding.startTimeEt.setText("$hourOfDay:$minute:00")
                    startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    startTimeCalendar.set(Calendar.MINUTE, minute)
                },
                myCalendar[Calendar.HOUR_OF_DAY],
                myCalendar[Calendar.MINUTE],
                false
            )
            timePickerDialog.show()
        }

        binding.fromEt.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                OnTimeSetListener { view, hourOfDay, minute -> binding.fromEt.setText("$hourOfDay:$minute:00") },
                myCalendar[Calendar.HOUR_OF_DAY],
                myCalendar[Calendar.MINUTE],
                false
            )
            timePickerDialog.show()
        }

        binding.toEt.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                OnTimeSetListener { view, hourOfDay, minute -> binding.toEt.setText("$hourOfDay:$minute:00") },
                myCalendar[Calendar.HOUR_OF_DAY],
                myCalendar[Calendar.MINUTE],
                false
            )
            timePickerDialog.show()
        }

        binding.startDateEt.setOnClickListener {
            DatePickerDialog(
                requireContext(), date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        binding.assignRadioGroup.setOnCheckedChangeListener { radioGroup, checkedID ->
            when (checkedID) {
                R.id.assign_later_rb -> {
                    assign_to_il.visibility = View.GONE
                    binding.chipGroup.visibility = View.GONE
                }
                R.id.assign_now_rb -> {
                    if (selectedUsersList.isEmpty())
                        viewModel.getAllUsers(SharedPreference(requireContext()).getValueInt("userID"))
                    assign_to_il.visibility = View.VISIBLE
                    binding.chipGroup.visibility = View.VISIBLE
                }
            }
        }

        binding.repetitionRadioGroup.setOnCheckedChangeListener { radioGroup, checkedID ->
            when (checkedID) {
                R.id.repeat_once_rb -> {
                    repetition_layout.visibility = View.GONE
                }
                R.id.repeat_every_rb -> {
                    repetition_layout.visibility = View.VISIBLE
                }
            }
        }

        binding.notifyBeforeCheckBox.setOnCheckedChangeListener { _, checked ->
            notify_layout.visibility = if (checked) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }


    }

    fun checkInputs(): Boolean {
        var flag: Boolean = true

        binding.checklistNameIl.error = null
        binding.assignToIl.error = null
        binding.dueDateIl.error = null
        binding.startDateIl.error = null
        binding.fromIl.error = null
        binding.toIl.error = null
        binding.repetitionUnitIl.error = null
        binding.repetitionEveryIl.error = null
        binding.notifyBeforeIl.error = null
        binding.notifyBeforeUnitIl.error = null
        binding.expireAfterIl.error = null
        binding.expireUnitIl.error = null
        binding.startTimeIl.error = null

        if (binding.checklistNameEt.text.toString().isEmpty()) {
            binding.checklistNameIl.error = getString(R.string.must_enter_name)
            flag = false
        }

        if (binding.assignNowRb.isChecked) {
            if (selectedUsersList.isEmpty()) {
                binding.assignToIl.error = getString(R.string.must_assing_to_user)
                flag = false
            }
        }

        if (binding.dueDateEt.text.toString().isEmpty()) {
            binding.dueDateIl.error = getString(R.string.must_choose)
            flag = false
        }

        if (binding.startDateEt.text.toString().isEmpty()) {
            binding.startDateIl.error = getString(R.string.must_enter_start_date)
            flag = false
        }

        if (binding.startTimeEt.text.toString().isEmpty()) {
            binding.startTimeIl.error = getString(R.string.must_enter_start_date)
            flag = false
        }

        if (binding.fromEt.text.toString().isEmpty()) {
            binding.fromIl.error = getString(R.string.must_choose)
            flag = false
        }

        if (binding.toEt.text.toString().isEmpty()) {
            binding.toIl.error = getString(R.string.must_choose)
            flag = false
        }

        if (binding.repeatEveryRb.isChecked) {
            if (binding.repetitionEveryEt.text.toString().isEmpty()) {
                binding.repetitionEveryIl.error = getString(R.string.must_choose)
                flag = false
            }
            if (binding.repetitionUnitEt.text.toString().isEmpty()) {
                binding.repetitionUnitIl.error = getString(R.string.must_choose)
                flag = false
            }
        }

        if (binding.notifyBeforeCheckBox.isChecked) {
            if (binding.notifyBeforeEt.text.toString().isEmpty()) {
                binding.notifyBeforeIl.error = getString(R.string.must_choose)
                flag = false
            }
            if (binding.notifyBeforeUnitEt.text.toString().isEmpty()) {
                binding.notifyBeforeUnitIl.error = getString(R.string.must_choose)
                flag = false
            }
        }

        if (binding.expireAfterEt.text.toString().isEmpty()) {
            binding.expireAfterIl.error = getString(R.string.must_choose)
            flag = false
        }
        if (binding.expireUnitEt.text.toString().isEmpty()) {
            binding.expireUnitIl.error = getString(R.string.must_choose)
            flag = false
        }


        return flag
    }



}
