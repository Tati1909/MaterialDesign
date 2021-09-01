package com.example.materialdesign.view.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materialdesign.model.PictureDto
import com.example.materialdesign.repository.RemoteNasaDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PictureViewModel : ViewModel() {
    private var _pictureOfTheDay = MutableLiveData<PictureDto>()
    val pictureLiveDataDto: LiveData<PictureDto> = _pictureOfTheDay
    private var pictureRequestedOffset = 1
    private val service by lazy { RemoteNasaDataSource.getNasaApiService() }

    private val cachePicture = mutableMapOf<Int, PictureDto?>()

    fun requestPicture(offset: Int = 0) {
        if (cachePicture[offset] != null) {
            _pictureOfTheDay.postValue(cachePicture[offset])
            return
        }
        if (pictureRequestedOffset != offset) {
            pictureRequestedOffset = offset
            service.getPictureOfTheDay(RemoteNasaDataSource.getTodayDayString(offset))
                .enqueue(object : Callback<PictureDto> {
                    override fun onResponse(
                        call: Call<PictureDto>,
                        response: Response<PictureDto>,
                    ) {
                        if (response.isSuccessful) {
                            cachePicture[offset] = response.body()!!
                            _pictureOfTheDay.postValue(response.body())
                        }
                    }

                    override fun onFailure(call: Call<PictureDto>, t: Throwable) {
                        pictureRequestedOffset = 1
                    }
                })
        }

    }
}