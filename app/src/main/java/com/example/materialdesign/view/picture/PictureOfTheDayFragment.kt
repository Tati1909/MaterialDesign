package com.example.materialdesign.view.picture

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
import com.example.materialdesign.databinding.FragmentPictureOfTheDayStartBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PictureOfTheDayFragment : Fragment() {

    //заменили фрагмент fragment_picture_of_the_day.xml на fragment_picture_of_the_day_start.xml
    private var _binding: FragmentPictureOfTheDayStartBinding? = null
    private val binding: FragmentPictureOfTheDayStartBinding get() = _binding!!

    private lateinit var pictureViewModel: PictureViewModel


    //Определим переменную типа BottomSheetBehaviour. В качестве generic передаём тип контейнера
    //нашего BottomSheet. Этот instance будет управлять нашей нижней панелью.
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        pictureViewModel = ViewModelProvider(this).get(PictureViewModel::class.java)
        _binding = FragmentPictureOfTheDayStartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pictureViewModel.requestPicture()

        binding.apply {
            //объяснение (описание галактики) будет загружаться в bottomSheet
            pictureViewModel.pictureLiveDataDto.observe(viewLifecycleOwner)
            { picture ->
                bottomSheetTextView.text = picture.explanation
                /*
            //применяем шрифт(скачали и скопировали в папку) из папки assets к описанию картинки
            activity?.let {
                bottomSheetTextView.typeface = Typeface.createFromAsset(it.assets,"AndroidInsomniaRegular-RLxW.ttf")
            } */

                if (picture.isImage) {
                    Glide.with(root)
                        .load(picture.url)
                        .centerCrop()
                        .into(imageView)
                }
                imageView.isVisible = picture.isImage
            }

            chipGroup.setOnCheckedChangeListener { _, _ ->
                pictureViewModel.requestPicture(
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
                        Uri.parse(WIKIPEDIA+"${inputEditText.text.toString()}")
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

    companion object {
        const val TAG = "@@PictureOfTheDayFragment"
        const val WIKIPEDIA = "https://en.wikipedia.org/wiki/"

    }
}