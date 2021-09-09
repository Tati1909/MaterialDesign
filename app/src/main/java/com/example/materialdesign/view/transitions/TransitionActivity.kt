package com.example.materialdesign.view.transitions

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import com.example.materialdesign.R
import com.example.materialdesign.databinding.ActivityExplodeBinding
import com.example.materialdesign.databinding.ActivityTransitionsBinding

//Нажимаем на кнопку и внизу появляется текст, кнопка сдвигается выше
class TransitionActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTransitionsBinding
    private var textIsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransitionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.transitionsButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.transitionsContainer)
            textIsVisible = !textIsVisible
            binding.text.visibility = if (textIsVisible) View.VISIBLE else View.GONE
        }
    }
}