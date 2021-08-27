package com.example.materialdesign.repository


import com.example.materialdesign.BuildConfig
import com.example.materialdesign.model.PictureDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//Этим интерфейсом мы описываем конкретный запрос на сервер — запрос на фото дня
//Он формируется простым методом с аннотациями: указывается endpoint ссылки
//(planetary/apod?), параметр (@Query)  передается в метод как аргумент
// Возвращает метод уже готовый класс с ответом от сервера (PictureDTO).

interface PictureOfTheDayApi {
    /*Можно добавлять различные параметры и пути, а также обозначить тип запроса — GET или POST —
   и в качестве параметра указать ссылку. Аннотации используются,
   чтобы указывать параметры запроса. Метод должен возвращать generic-тип Call.
   */
    @GET("planetary/apod?api_key=${BuildConfig.NASA_API_KEY}")
    fun getPictureOfTheDay(
        @Query("date") date: String = RemoteDataSource.getToday(),
    ): Call<PictureDTO>
}
