package com.fiap.totemflexmedia.api

import com.fiap.totemflexmedia.model.ClimaResponse
import com.fiap.totemflexmedia.model.MetricasResponse
import com.fiap.totemflexmedia.model.PerguntaRequest
import com.fiap.totemflexmedia.model.RespostaResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TotemApi {

    @GET("api/clima")
    fun obterClimaAtual(): Call<ClimaResponse>

    @POST("api/interacao")
    fun enviarPergunta(@Body request: PerguntaRequest): Call<RespostaResponse>

    @GET("api/metricas")
    fun obterMetricas(): Call<MetricasResponse>

}