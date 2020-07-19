package com.alyndroid.thirdeyechecklist.ui.checklists.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.UserTasksData
import kotlinx.android.synthetic.main.task_item.view.*


class TasksAdapter(val context: Context, val deleteTaskClickListener: DeleteTaskClickListener   ) :
    ListAdapter<UserTasksData, TasksAdapter.PagesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
        return PagesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.task_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
        val result = getItem(position)
        holder.taskNameTextView.text = result.name
        holder.taskTypeTextView.text = when(result.type){
            "1"->"Yes/No"
            "2"->"Yes/No/NA"
            else->"Choices"
        }
        holder.deleteTaskButton.setOnClickListener { deleteTaskClickListener.onClick(result.id) }

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
        var taskNameTextView: TextView = view.task_name_tv
        var taskTypeTextView: TextView = view.task_type_tv
        var deleteTaskButton: ImageButton = view.delete_task_btn

    }

    class DeleteTaskClickListener(val clickListener: (pageID: Int) -> Unit) {
        fun onClick(pageID: Int) = clickListener(pageID)
    }

}