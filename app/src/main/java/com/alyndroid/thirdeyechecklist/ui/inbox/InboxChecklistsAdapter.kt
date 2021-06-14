package com.alyndroid.thirdeyechecklist.ui.inbox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.Checklist
import com.alyndroid.thirdeyechecklist.data.model.InboxChecklistsData
import com.alyndroid.thirdeyechecklist.data.model.UserChecklistData
import com.alyndroid.thirdeyechecklist.ui.checklists.ChecklistsAdapter
import kotlinx.android.synthetic.main.inbox_checklist_item.view.*


class InboxChecklistsAdapter(val context: Context, private val checklistClickListener: ChecklistClickListener) :
    ListAdapter<InboxChecklistsData, InboxChecklistsAdapter.ChecklistsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistsViewHolder {
        return ChecklistsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.inbox_checklist_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChecklistsViewHolder, position: Int) {
        val result = getItem(position)
        holder.checklistNameTextView.text = result.checklist.name
        holder.checklistByTextView.text = result.checklist.created_by.toString()
        holder.checklistAvailabilityTextView.text = "start date: " + result.checklist.start_at + "\navailability: " +
                result.availability_num.toString() + "-" + result.checklist.availability_type
        holder.checklistlayout.setOnClickListener { checklistClickListener.onClick(result.checklist.id, result.id) }


    }


    companion object DiffCallback : DiffUtil.ItemCallback<InboxChecklistsData>() {
        override fun areItemsTheSame(
            oldItem: InboxChecklistsData,
            newItem: InboxChecklistsData
        ): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: InboxChecklistsData,
            newItem: InboxChecklistsData
        ): Boolean {
            return oldItem == newItem
        }

    }


    class ChecklistsViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var checklistNameTextView: TextView = view.inbox_checklist_name_tv
        var checklistByTextView: TextView = view.inbox_by_tv
        var checklistAvailabilityTextView: TextView = view.inbox_availability_tv
        var checklistlayout: ConstraintLayout = view.inbox_checklist_item_layout
    }

    class ChecklistClickListener(val clickListener: (checklistID: Int, dueID: Int) -> Unit) {
        fun onClick(checklistID: Int, dueID: Int) = clickListener(checklistID, dueID)
    }

}