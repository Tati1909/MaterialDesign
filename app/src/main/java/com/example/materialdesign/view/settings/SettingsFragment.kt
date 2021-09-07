package com.example.materialdesign.view.settings

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.materialdesign.R
import com.example.materialdesign.databinding.FragmentSettingsBinding
import com.example.materialdesign.view.MainActivity
import com.google.android.material.radiobutton.MaterialRadioButton

open class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioButtonSetOnClickListener(binding.indigoThemeRadioButton,R.style.IndigoTheme)
        radioButtonSetOnClickListener(binding.pinkThemeRadioButton,R.style.PinkTheme)
        radioButtonSetOnClickListener(binding.purpleThemeRadioButton,R.style.PurpleTheme)
    }

    private fun radioButtonSetOnClickListener(button: MaterialRadioButton, theme: Int) {
        button.setOnClickListener {
            MainActivity.ThemeHolder.theme = theme
            requireActivity().recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}