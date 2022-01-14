package com.why.arcgisdevutils.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private const val ROOT_URL = "https://www.baidu.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(ROOT_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)
}