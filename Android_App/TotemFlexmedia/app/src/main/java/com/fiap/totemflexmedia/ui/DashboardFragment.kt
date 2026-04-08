package com.fiap.totemflexmedia.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fiap.totemflexmedia.R
import com.fiap.totemflexmedia.api.RetrofitClient
import com.fiap.totemflexmedia.model.MetricasResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    private lateinit var tvStatusSistema: TextView
    private lateinit var tvInteracoesDia: TextView
    private lateinit var tvTempoResposta: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mapeando os textos na tela
        tvStatusSistema = view.findViewById(R.id.tvStatusSistema)
        tvInteracoesDia = view.findViewById(R.id.tvInteracoesDia)
        tvTempoResposta = view.findViewById(R.id.tvTempoResposta)

        // Botão de sair do Admin
        view.findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Busca os dados reais assim que a tela abre
        carregarMetricas()
    }

    private fun carregarMetricas() {
        RetrofitClient.instance.obterMetricas().enqueue(object : Callback<MetricasResponse> {

            override fun onResponse(call: Call<MetricasResponse>, response: Response<MetricasResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val metricas = response.body()!!

                    activity?.runOnUiThread {
                        tvStatusSistema.text = metricas.status
                        tvStatusSistema.setTextColor(Color.parseColor("#9eb84e")) // Verde
                        tvInteracoesDia.text = metricas.interacoes.toString()
                        tvTempoResposta.text = metricas.latenciaMedia
                    }
                }
            }

            override fun onFailure(call: Call<MetricasResponse>, t: Throwable) {
                activity?.runOnUiThread {
                    tvStatusSistema.text = "OFFLINE"
                    tvStatusSistema.setTextColor(Color.parseColor("#FF4C4C")) // Vermelho
                    tvInteracoesDia.text = "Erro"
                    tvTempoResposta.text = "Erro"
                }
            }
        })
    }
}