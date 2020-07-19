package com.alyndroid.thirdeyechecklist.ui.checklists.pages

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
import com.alyndroid.thirdeyechecklist.data.model.UserPagesData
import kotlinx.android.synthetic.main.page_item.view.*


class PagesAdapter(val context: Context, val pageClickListener: PageClickListener, val deletePageClickListener: PageClickListener   ) :
    ListAdapter<UserPagesData, PagesAdapter.PagesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
        return PagesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.page_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
        val result = getItem(position)
        holder.pageNameTextView.text = result.name
        holder.deletePageButton.setOnClickListener { deletePageClickListener.onClick(result.id) }
        holder.pageNameTextView.setOnClickListener { pageClickListener.onClick(result.id) }
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
        var pageNameTextView: TextView = view.page_name_tv
        var deletePageButton: ImageButton = view.delete_page_btn

    }

    class PageClickListener(val clickListener: (pageID: Int) -> Unit) {
        fun onClick(pageID: Int) = clickListener(pageID)
    }

}