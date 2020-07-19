package com.alyndroid.thirdeyechecklist.ui.checklists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.UserChecklistData
import kotlinx.android.synthetic.main.checklist_item.view.*


class ChecklistsAdapter(val context: Context, val checklistClickListener: ChecklistClickListener,
                        private val deleteChecklistClickListener: ChecklistClickListener) :
    ListAdapter<UserChecklistData, ChecklistsAdapter.ChecklistsViewHolder>(DiffCallback) {
    public var list: MutableList<UserChecklistData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistsViewHolder {
        return ChecklistsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.checklist_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChecklistsViewHolder, position: Int) {
        val result = getItem(position)
        holder.checklistNameTextView.text = result.name
        holder.checklistStartDateTextView.text = "start date: " + result.start_at
        holder.checklistAvailabilityTextView.text = "availability: " +
                result.availability_num.toString() + "-" + result.availability_type
        holder.deleteChecklistButton.setOnClickListener { deleteChecklistClickListener.onClick(result.id) }
        holder.checklistItemLayout.setOnClickListener { checklistClickListener.onClick(result.id) }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<UserChecklistData>() {
        override fun areItemsTheSame(
            oldItem: UserChecklistData,
            newItem: UserChecklistData
        ): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: UserChecklistData,
            newItem: UserChecklistData
        ): Boolean {
            return oldItem == newItem
        }

    }


    class ChecklistsViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var checklistNameTextView: TextView = view.checklist_name_tv
        var checklistStartDateTextView: TextView = view.start_date_tv
        var checklistAvailabilityTextView: TextView = view.availability_tv
        var deleteChecklistButton: ImageButton = view.delete_checklist_btn
        var checklistItemLayout: ConstraintLayout = view.checklist_item_layout
    }

    class ChecklistClickListener(val clickListener: (checklistID: Int) -> Unit) {
        fun onClick(checklistID: Int) = clickListener(checklistID)
    }
}