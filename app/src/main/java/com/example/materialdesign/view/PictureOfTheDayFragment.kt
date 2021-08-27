package com.example.materialdesign.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.materialdesign.databinding.FragmentPictureOfTheDayBinding
import com.example.materialdesign.PictureViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PictureOfTheDayFragment : Fragment() {
    private var _binding: FragmentPictureOfTheDayBinding? = null
    private val binding: FragmentPictureOfTheDayBinding get() = _binding!!

    private val viewModel by lazy { ViewModelProvider(this).get(PictureViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPictureOfTheDayBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.requestPicture()
        binding.apply {
            viewModel.PictureDTO.observe(viewLifecycleOwner) { picture ->
                textView.text = picture.explanation

                if (picture.isImage) {
                    Glide.with(root)
                        .load(picture.url)
                        .centerCrop()
                        .into(imageView)
                }
                imageView.isVisible = picture.isImage
            }

            chipGroup.setOnCheckedChangeListener { _, _ ->
                viewModel.requestPicture(
                    when {
                        todayChip.isChecked -> 0
                        yesterdayChip.isChecked -> -1
                        beforeYesterdayChip.isChecked -> -2
                        else -> 0
                    }
                )
            }

/*            val bottomSheetBehavior: BottomSheetBehavior<FrameLayout> = BottomSheetBehavior.from(bottomSheetFrameLayout)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetFrameLayout.setOnClickListener {
                bottomSheetBehavior.state = when (bottomSheetBehavior.state) {
                    BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
                    else -> BottomSheetBehavior.STATE_EXPANDED
                }

                Log.d(TAG, "onViewCreated() called state = ${bottomSheetBehavior.state}")
            } */
        }
    }

    companion object{
        const val TAG = "@@PictureFragment"
    }
}