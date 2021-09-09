package com.example.materialdesign.view.transitions

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.materialdesign.databinding.ActivityAnimationsBinding


class AnimationsActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAnimationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnimationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.explodeButton.setOnClickListener {
            val explode = Intent(this, ExplodeActivity::class.java)
            startActivity(explode)
        }
        binding.transitionsButton.setOnClickListener {
            val transition = Intent(this, TransitionActivity::class.java)
            startActivity(transition)
        }
        binding.transformButton.setOnClickListener {
            val transform = Intent(this, TransformActivity::class.java)
            startActivity(transform)
        }
        binding.pathmotion.setOnClickListener {
            val pathmotion = Intent(this, PathMotionActivity::class.java)
            startActivity(pathmotion)
        }
        binding.animationsFabButton.setOnClickListener {
            val animationsFab = Intent(this, AnimationsFabActivity::class.java)
            startActivity(animationsFab)
        }
    }




}