package com.alyndroid.thirdeyechecklist.ui.inbox.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.InboxChoice
import com.alyndroid.thirdeyechecklist.data.model.UserTasksData
import kotlinx.android.synthetic.main.inbox_task_item.view.*


class InboxTasksAdapter(val context: Context, private val radioGroupClickListener: RadioGroupClickListener) :
    ListAdapter<UserTasksData, InboxTasksAdapter.PagesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
        return PagesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.inbox_task_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
        val result = getItem(position)
        holder.taskNameTextView.text = result.name
        for (choice in result.choices){
            val radioButton = RadioButton(context)
            radioButton.id = View.generateViewId()
            radioButton.text = choice.choice_title
            radioButton.setOnClickListener{radioGroupClickListener.onClick(Pair(choice, result))}
            holder.tasksChoicesRadioGroup.addView(radioButton)
        }

    }


    companion object DiffCallback : DiffUtil.ItemCallback<UserTasksData>() {
        override fun areItemsTheSame(
            oldItem: UserTasksData,
            newItem: UserTasksData
        ): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: UserTasksData,
            newItem: UserTasksData
        ): Boolean {
            return oldItem == newItem
        }

    }


    class PagesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var taskNameTextView: TextView = view.inbox_task_name_tv
        var tasksChoicesRadioGroup: RadioGroup = view.inbox_tasks_choices_radioGroup
    }

    class RadioGroupClickListener(val clickListener: (choice: Pair<InboxChoice, UserTasksData>) -> Unit) {
        fun onClick(choice: Pair<InboxChoice, UserTasksData>) = clickListener(choice)
    }


}