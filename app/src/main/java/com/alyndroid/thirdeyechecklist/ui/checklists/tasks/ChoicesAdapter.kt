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
import com.alyndroid.thirdeyechecklist.data.model.Choice
import kotlinx.android.synthetic.main.task_choice_item.view.*


class ChoicesAdapter(val context: Context, val pageClickListener: PageClickListener   ) :
    ListAdapter<Choice, ChoicesAdapter.PagesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
        return PagesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.task_choice_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
        val result = getItem(position)
        holder.choiceNameTextView.text = result.name
        holder.choiceScoreTextView.text = result.score.toString()
        holder.deleteBtn.setOnClickListener { pageClickListener.onClick(position) }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Choice>() {
        override fun areItemsTheSame(
            oldItem: Choice,
            newItem: Choice
        ): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: Choice,
            newItem: Choice
        ): Boolean {
            return oldItem == newItem
        }

    }


    class PagesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var choiceNameTextView: TextView = view.choice_name_tv
        var choiceScoreTextView: TextView = view.choice_score_tv
        var deleteBtn: ImageButton = view.delete_task_btn

    }

    class PageClickListener(val clickListener: (pageID: Int) -> Unit) {
        fun onClick(pageID: Int) = clickListener(pageID)
    }

}