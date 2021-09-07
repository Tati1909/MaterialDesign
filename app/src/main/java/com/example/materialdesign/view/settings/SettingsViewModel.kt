package com.example.materialdesign.view.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _textLiveData = MutableLiveData<String>().apply {
        value = "Settings"
    }
    val textLiveData: LiveData<String> = _textLiveData


}
