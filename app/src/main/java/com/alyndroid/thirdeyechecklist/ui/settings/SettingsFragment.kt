package com.alyndroid.thirdeyechecklist.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager

import com.alyndroid.thirdeyechecklist.R
import com.alyndroid.thirdeyechecklist.data.model.SettingsModel
import com.alyndroid.thirdeyechecklist.databinding.FragmentCheckListsBinding
import com.alyndroid.thirdeyechecklist.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        binding.settingsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val settingsList = listOf<SettingsModel>(
            SettingsModel(1, "create user", R.drawable.ic_add_user),
            SettingsModel(2, "create user2", R.drawable.ic_add_user),
            SettingsModel(3, "create user3", R.drawable.ic_add_user),
            SettingsModel(4, "create user4", R.drawable.ic_add_user)
            )
        val adapter = SettingsAdapter(requireContext(), SettingsAdapter.SettingsClickListener {
            when(it){
                1-> navController.navigate(R.id.action_settingsFragment_to_usersFragment)
            }
        })
        adapter.submitList(settingsList)
        binding.settingsRecyclerView.adapter = adapter

    }
}
