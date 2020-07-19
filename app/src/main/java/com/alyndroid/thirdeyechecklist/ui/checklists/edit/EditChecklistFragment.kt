package com.alyndroid.thirdeyechecklist.ui.checklists.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.AllUsersData
import com.alyndroid.thirdeyechecklist.data.model.UserChecklistData
import com.alyndroid.thirdeyechecklist.databinding.FragmentEditChecklistBinding
import com.alyndroid.thirdeyechecklist.ui.checklists.addChecklist.AddCheckListFragmentDirections
import com.alyndroid.thirdeyechecklist.util.SharedPreference
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.chip.Chip
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.fragment_add_check_list.*
import java.text.SimpleDateFormat
import java.util.*


class EditChecklistFragment : Fragment() {

    private var enabled: Boolean = false
    private lateinit var binding: FragmentEditChecklistBinding
    private val args: EditChecklistFragmentArgs by navArgs()
    private val selectedUsersList = mutableListOf<Int>()
    lateinit var date: DatePickerDialog.OnDateSetListener
    lateinit var myCalendar: Calendar
    var usersList: List<AllUsersData> = listOf()
    private val viewModel: EditChecklistViewModel by lazy {
        ViewModelProviders.of(this).get(EditChecklistViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_checklist, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        fillViews(args.checklistData)
        disableViews()
        activity!!.title = "view checklist"
        val navController = Navigation.findNavController(view)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (enabled){
                val materialDialog = MaterialDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.exit))
                    .setMessage(getString(R.string.sure_to_exit))
                    .setCancelable(false)
                    .setPositiveButton(
                        getString(R.string.yes)
                    ) { dialog, _ ->
                        navController.navigate(R.id.action_editChecklistFragment_to_checkListsFragment)
                        dialog.cancel()
                    }
                    .setNegativeButton(
                        getString(R.string.no)
                    ) { dialog, _ -> dialog.cancel() }
                    .build()
                    .show()
            }
            else
            navController.navigate(R.id.action_editChecklistFragment_to_checkListsFragment)
        }

        binding.editChecklistButton.attachTextChangeAnimator()
        binding.editChecklistButton.setOnClickListener {
            val materialDialog = MaterialDialog.Builder(requireActivity())
                .setTitle(getString(R.string.edit))
                .setMessage(if(enabled)getString(R.string.sure_to_submit_edit) else getString(R.string.sure_to_edit))
                .setCancelable(false)
                .setPositiveButton(
                    getString(R.string.yes)
                ) { dialog, _ ->
                    if (enabled) {updateChecklist()} else {
                        binding.editChecklistButton.text = getString(R.string.submit)
                        enableViews()}
                    dialog.cancel()
                }
                .setNegativeButton(
                    getString(R.string.no)
                ) { dialog, _ -> dialog.cancel() }
                .build()
                .show()
        }
        binding.gotoPagesButton.setOnClickListener {
            val action =
                EditChecklistFragmentDirections.actionEditChecklistFragmentToAddPageFragment(
                    args.checklistData.id, true, args.checklistData
                )
            navController.navigate(action)
        }
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
        viewModel.loading.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it) {
                binding.editChecklistButton.showProgress {
                    buttonTextRes = R.string.loading
                    progressColor = Color.WHITE
                }
            } else {
                binding.editChecklistButton.hideProgress(R.string.edit)
            }
        })
        viewModel.updateChecklistResponse.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it.success){
                disableViews()
                Toast.makeText(requireContext(), "updated", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "fail", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateChecklist() {
        if (checkInputs()) {
            val map = HashMap<Any, Any>()

            map.put("name", binding.checklistNameEt.text.toString())
            map.put("start_at", binding.startDateEt.text.toString()+" " +binding.startTimeEt.text.toString())
            map.put("active", binding.activeCb.isChecked)
            map.put("holiday_action", when(binding.dueDateEt.editableText.toString()){
                "Shift to nearest working day."->"1"
                "Keep on that date."->"2"
                "Do not ask for it"->"3"
                else -> 0
            })
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

            viewModel.updateChecklist(args.checklistData.id, map)
        }
    }

    private fun fillViews(checklistData: UserChecklistData) {
        binding.checklistNameEt.setText(checklistData.name)
        binding.dueDateEt.setText(
            when (checklistData.holiday_action) {
                1 -> "Shift to nearest working day."
                2 -> "Keep on that date."
                3 -> "Do not ask for it"
                else -> ""

            }
        )
        binding.expireAfterEt.setText(checklistData.availability_num.toString())
        binding.expireUnitEt.setText(checklistData.availability_type)
        binding.fromEt.setText(checklistData.working_from)
        binding.toEt.setText(checklistData.working_to)
        binding.notifyBeforeEt.setText(checklistData.notify_before_num.toString())
        binding.notifyBeforeUnitEt.setText(checklistData.notify_before_type)
        binding.startDateEt.setText(checklistData.start_at.substring(0, 11))
        binding.startTimeEt.setText(checklistData.start_at.substring(11, checklistData.start_at.length))
        binding.activeCb.isChecked = checklistData.active == "1"
        binding.assignLaterRb.isChecked = checklistData.assign_later == "1"
        binding.assignNowRb.isChecked = checklistData.assign_later != "1"
        binding.repeatEveryRb.isChecked = checklistData.repeat == "1"
        binding.repeatOnceRb.isChecked = checklistData.repeat != "1"
        binding.dueDateHolidayCb.isChecked = checklistData.checklist_shifted_anthor_due == "1"
        binding.forceAnswerCb.isChecked = checklistData.force_answer == "1"
        binding.showLastStatusCb.isChecked = checklistData.show_last_status == "1"
        binding.notifyBeforeCheckBox.isChecked = checklistData.notified_before == "1"
        binding.repetitionEveryEt.setText(checklistData.repeat_num ?: "0")
        binding.repetitionUnitEt.setText(checklistData.repeat_type)
        if (checklistData.users.isNotEmpty()){
        for (user in checklistData.users) {
            val chip = Chip(requireContext())
            chip.text = user.name
            selectedUsersList.add(user.user_id)
            chip.isCloseIconVisible = true
            chip.isCheckable = false
            chip.isCheckable = false
            chip.setOnCloseIconClickListener {
                val chip2: Chip = it as Chip
                selectedUsersList.remove(user.user_id)
                binding.chipGroup.removeView(chip2)
            }

            binding.chipGroup.addView(chip)
        }}
        binding.chipGroup.visibility = View.VISIBLE
    }

    private fun disableViews() {
        for (chip in binding.chipGroup.children){
            chip.isEnabled = false
        }
        binding.checklistNameEt.isEnabled = false
        binding.assignToEt.isEnabled = false
        binding.dueDateEt.isEnabled = false
        binding.expireAfterEt.isEnabled = false
        binding.expireUnitEt.isEnabled = false
        binding.fromEt.isEnabled = false
        binding.notifyBeforeEt.isEnabled = false
        binding.notifyBeforeUnitEt.isEnabled = false
        binding.startDateEt.isEnabled = false
        binding.startTimeEt.isEnabled = false
        binding.toEt.isEnabled = false
        binding.activeCb.isEnabled = false
        binding.assignLaterRb.isEnabled = false
        binding.assignNowRb.isEnabled = false
        binding.repeatEveryRb.isEnabled = false
        binding.repeatOnceRb.isEnabled = false
        binding.dueDateHolidayCb.isEnabled = false
        binding.forceAnswerCb.isEnabled = false
        binding.showLastStatusCb.isEnabled = false
        binding.notifyBeforeCheckBox.isEnabled = false
        binding.repetitionUnitEt.isEnabled = false
        binding.repetitionEveryEt.isEnabled = false
        binding.chipGroup.isEnabled = false
    }

    private fun enableViews(){
        enabled = true
        for (chip in binding.chipGroup.children){
            chip.isEnabled = true
        }
        binding.checklistNameEt.isEnabled = true
        binding.assignToEt.isEnabled = true
        binding.dueDateEt.isEnabled = true
        binding.expireAfterEt.isEnabled = true
        binding.expireUnitEt.isEnabled = true
        binding.fromEt.isEnabled = true
        binding.notifyBeforeEt.isEnabled = true
        binding.notifyBeforeUnitEt.isEnabled = true
        binding.startDateEt.isEnabled = true
        binding.startTimeEt.isEnabled = true
        binding.toEt.isEnabled = true
        binding.activeCb.isEnabled = true
        binding.assignLaterRb.isEnabled = true
        binding.assignNowRb.isEnabled = true
        binding.repeatEveryRb.isEnabled = true
        binding.repeatOnceRb.isEnabled = true
        binding.dueDateHolidayCb.isEnabled = true
        binding.forceAnswerCb.isEnabled = true
        binding.showLastStatusCb.isEnabled = true
        binding.notifyBeforeCheckBox.isEnabled = true
        binding.repetitionUnitEt.isEnabled = true
        binding.repetitionEveryEt.isEnabled = true
        binding.chipGroup.isEnabled = true
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
        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        binding.startTimeEt.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    binding.startTimeEt.setText(
                        "$hourOfDay:$minute:00"
                    )
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
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    binding.fromEt.setText(
                        "$hourOfDay:$minute:00"
                    )
                },
                myCalendar[Calendar.HOUR_OF_DAY],
                myCalendar[Calendar.MINUTE],
                false
            )
            timePickerDialog.show()
        }

        binding.toEt.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    binding.toEt.setText(
                        "$hourOfDay:$minute:00"
                    )
                },
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
                        viewModel.getAllUsers()
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

    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.startDateEt.setText(sdf.format(myCalendar.getTime()))
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
