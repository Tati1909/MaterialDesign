package com.example.materialdesign.repository

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

// Результаты запроса станут обрабатываться во ViewModel — там будет находиться наш callback.
object RemoteDataSource {
    private const val TAG = "@@RemoteDataSource"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.nasa.gov/")
            .build()
    }
    private val SERVICE: PictureOfTheDayApi by lazy {
        retrofit.create(PictureOfTheDayApi::class.java)
    }

    fun getNasaApiService() = SERVICE

    fun getToday(offset: Int = 0): String {
        val date = Calendar.getInstance().apply { add(Calendar.DATE, offset) }.time
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        Log.d(TAG, "getToday() called: offset = $offset today = $today")

        return today
    }
}