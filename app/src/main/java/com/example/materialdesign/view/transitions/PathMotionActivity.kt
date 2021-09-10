package com.example.materialdesign.view.transitions

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.ArcMotion
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.materialdesign.databinding.ActivityAnimationsPathTransitionsBinding

//«Силы из реального мира, такие как сила гравитации, предполагают движение элементов по
//изогнутой траектории, а не по прямой линии» (Material Design guidelines).
class PathMotionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimationsPathTransitionsBinding
    private var toRightAnimation = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnimationsPathTransitionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val changeBounds = ChangeBounds()
            changeBounds.setPathMotion(ArcMotion())
            changeBounds.duration = 500
            TransitionManager.beginDelayedTransition(
                binding.transitionsContainer,
                changeBounds
            )
            toRightAnimation = !toRightAnimation
            val params = binding.button.layoutParams as FrameLayout.LayoutParams
            params.gravity =
                if (toRightAnimation) Gravity.END or Gravity.BOTTOM else
                    Gravity.START or Gravity.TOP
            binding.button.layoutParams = params
        }
    }
}