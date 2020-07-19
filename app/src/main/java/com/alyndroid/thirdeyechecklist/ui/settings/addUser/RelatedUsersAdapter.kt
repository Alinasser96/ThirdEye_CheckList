package com.alyndroid.thirdeyechecklist.ui.settings.addUser

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
import com.alyndroid.thirdeyechecklist.data.model.RemoteUserData
import kotlinx.android.synthetic.main.related_user_item.view.*


class RelatedUsersAdapter(
    val context: Context,
    private val deleteRelatedUserClickListener: DeleteRelatedUserClickListener,
    private val callRelatedUserClickListener: CallRelatedUserClickListener,
    private val editRelatedUserClickListener: EditRelatedUserClickListener
) :
    ListAdapter<RemoteUserData, RelatedUsersAdapter.PagesViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
        return PagesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.related_user_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
        val result = getItem(position)
        holder.relatedUserNameTextView.text = result.name
        holder.deleteRelatedUserButton.setOnClickListener { deleteRelatedUserClickListener.onClick(result.id) }
        holder.callRelatedUserButton.setOnClickListener { callRelatedUserClickListener.onClick(result.phone_number) }
        holder.editRelatedUserButton.setOnClickListener { editRelatedUserClickListener.onClick(result) }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<RemoteUserData>() {
        override fun areItemsTheSame(
            oldItem: RemoteUserData,
            newItem: RemoteUserData
        ): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: RemoteUserData,
            newItem: RemoteUserData
        ): Boolean {
            return oldItem == newItem
        }

    }


    class PagesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var relatedUserNameTextView: TextView = view.related_user_name_tv
        var deleteRelatedUserButton: ImageButton = view.delete_related_user_btn
        var callRelatedUserButton: ImageButton = view.call_user_btn
        var editRelatedUserButton: ImageButton = view.edit_related_user_btn

    }

    class DeleteRelatedUserClickListener(val clickListener: (userID: Int) -> Unit) {
        fun onClick(userID: Int) = clickListener(userID)
    }

    class CallRelatedUserClickListener(val clickListener: (userPhone: String) -> Unit) {
        fun onClick(userPhone: String) = clickListener(userPhone)
    }

    class EditRelatedUserClickListener(val clickListener: (userData: RemoteUserData) -> Unit) {
        fun onClick(userData: RemoteUserData) = clickListener(userData)
    }

}