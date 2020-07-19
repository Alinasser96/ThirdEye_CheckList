package com.alyndroid.thirdeyechecklist.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.SettingsModel
import com.alyndroid.thirdeyechecklist.data.model.UserChecklistData
import kotlinx.android.synthetic.main.settings_item.view.*


class SettingsAdapter(val context: Context, val settingsClickListener: SettingsClickListener) :
    ListAdapter<SettingsModel, SettingsAdapter.ChecklistsViewHolder>(DiffCallback) {
    public var list: MutableList<UserChecklistData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistsViewHolder {
        return ChecklistsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.settings_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChecklistsViewHolder, position: Int) {
        val result = getItem(position)
        holder.settingNameTextView.text = result.name
        holder.settingImageView.background=context.getDrawable(result.src)
        holder.settingImageView.setOnClickListener { settingsClickListener.onClick(result.id) }

    }


    companion object DiffCallback : DiffUtil.ItemCallback<SettingsModel>() {
        override fun areItemsTheSame(
            oldItem: SettingsModel,
            newItem: SettingsModel
        ): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(
            oldItem: SettingsModel,
            newItem: SettingsModel
        ): Boolean {
            return oldItem == newItem
        }

    }


    class ChecklistsViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var settingNameTextView: TextView = view.setting_name
        var settingImageView: ImageView = view.setting_image
    }

    class SettingsClickListener(val clickListener: (settingID: Int) -> Unit) {
        fun onClick(settingID: Int) = clickListener(settingID)
    }
}