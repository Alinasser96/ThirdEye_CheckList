package com.alyndroid.thirdeyechecklist.ui.inbox.pages

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
import com.alyndroid.thirdeyechecklist.data.model.UserPagesData
import com.alyndroid.thirdeyechecklist.ui.checklists.pages.PagesAdapter
import kotlinx.android.synthetic.main.inbox_page_item.view.*


class InboxPagesAdapter(
    val context: Context,
    private val pageClickListener: PagesAdapter.PageClickListener
) :
    ListAdapter<UserPagesData, InboxPagesAdapter.PagesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
        return PagesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.inbox_page_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
        val result = getItem(position)
        holder.pageNameTextView.text = result.name
        holder.pageLayout.setOnClickListener { pageClickListener.onClick(result.id) }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<UserPagesData>() {
        override fun areItemsTheSame(
            oldItem: UserPagesData,
            newItem: UserPagesData
        ): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: UserPagesData,
            newItem: UserPagesData
        ): Boolean {
            return oldItem == newItem
        }

    }


    class PagesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var pageNameTextView: TextView = view.inbox_page_name_tv
        var pageLayout: ConstraintLayout = view.inbox_page_item_layout

    }


}