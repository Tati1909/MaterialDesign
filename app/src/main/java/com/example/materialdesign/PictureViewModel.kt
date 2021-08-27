package com.example.materialdesign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.materialdesign.model.PictureDTO
import com.example.materialdesign.repository.RemoteDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PictureViewModel : ViewModel() {
    private var _pictureOfTheDay = MutableLiveData<PictureDTO>()
    val PictureDTO: LiveData<PictureDTO> = _pictureOfTheDay
    private var pictureRequestedOffset = 1
    private val service by lazy { RemoteDataSource.getNasaApiService() }

    private val cachePicture = mutableMapOf<Int, PictureDTO?>()

    fun requestPicture(offset: Int = 0) {
        if (cachePicture[offset] != null) {
            _pictureOfTheDay.postValue(cachePicture[offset])
            return
        }
        if (pictureRequestedOffset != offset) {
            pictureRequestedOffset = offset
            service.getPictureOfTheDay(RemoteDataSource.getToday(offset))
                .enqueue(object : Callback<PictureDTO> {
                    override fun onResponse(
                        call: Call<PictureDTO>,
                        response: Response<PictureDTO>,
                    ) {
                        if (response.isSuccessful) {
                            cachePicture[offset] = response.body()!!
                            _pictureOfTheDay.postValue(response.body())
                        }
                    }

                    override fun onFailure(call: Call<PictureDTO>, t: Throwable) {
                        pictureRequestedOffset = 1
                    }
                })
        }

    }
}