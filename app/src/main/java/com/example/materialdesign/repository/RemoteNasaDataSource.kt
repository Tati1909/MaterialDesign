package com.example.materialdesign.repository

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

// Результаты запроса станут обрабатываться во ViewModel — там будет находиться наш callback.
object RemoteNasaDataSource {
    private const val TAG = "@@RemoteDataSource"
    private const val BASE_URL = "https://api.nasa.gov/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }
    private val service: PictureOfTheDayApi by lazy {
        retrofit.create(PictureOfTheDayApi::class.java)
    }

    fun getNasaApiService() = service

    fun getTodayDayString(offset: Int = 0): String {
        val currentData = Calendar.getInstance().apply { add(Calendar.DATE, offset) }.time
        val currentDataString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentData)
        Log.d(TAG, "getToday() called: offset = $offset today = $currentDataString")

        return currentDataString
    }
}