package com.example.materialdesign.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    //Определим переменную типа BottomSheetBehaviour. В качестве generic передаём тип контейнера
    //нашего BottomSheet. Этот instance будет управлять нашей нижней панелью.
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

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
            //объяснение (описание галактики) будет загружаться в bottomSheet
            viewModel.PictureDTO.observe(viewLifecycleOwner)
            { picture ->
                textViewBottomSheet.text = picture.explanation

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

            //вводим слово в inputLayout и посылаем запрос в википедию, нажав на иконку W
            inputLayout.setEndIconOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data =
                        Uri.parse("https://en.wikipedia.org/wiki/${inputEditText.text.toString()}")
                })
            }

           //Здесь мы передаем наш bottomSheetFrameLayout
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetFrameLayout)
            // Сразу укажем его состояние (свёрнутое, но не скрытое):
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            bottomSheetFrameLayout.setOnClickListener {
                bottomSheetBehavior.state = when (bottomSheetBehavior.state) {
                    BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
                    else -> BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    companion object{
        const val TAG = "@@PictureOfTheDayFragment"
    }
}