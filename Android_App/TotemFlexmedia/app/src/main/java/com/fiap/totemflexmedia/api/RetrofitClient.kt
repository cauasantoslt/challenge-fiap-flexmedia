package com.fiap.totemflexmedia.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.118:5000/"

    // Criando um cliente que tem paciência para esperar a IA responder
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val instance: TotemApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Adicionamos o cliente paciente aqui!
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TotemApi::class.java)
    }
}