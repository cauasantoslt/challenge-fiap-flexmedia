package com.fiap.totemflexmedia.model

import com.google.gson.annotations.SerializedName

data class PerguntaRequest(
    @SerializedName("texto")
    val texto: String
)

data class RespostaResponse(
    @SerializedName("respostaIa", alternate = ["resposta_ia", "resposta_IA", "respostaIA"])
    val respostaIa: String,

    @SerializedName("clima")
    val clima: String? = null,

    @SerializedName("recomendacao")
    val recomendacao: String? = null
)

data class ClimaResponse(
    @SerializedName("clima")
    val clima: String
)

data class MetricasResponse(
    @SerializedName("status") val status: String,
    @SerializedName("interacoes") val interacoes: Int,
    @SerializedName("latenciaMedia") val latenciaMedia: String
)