package com.fiap.totemflexmedia.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fiap.totemflexmedia.R
import com.fiap.totemflexmedia.api.RetrofitClient
import com.fiap.totemflexmedia.model.ClimaResponse
import com.fiap.totemflexmedia.model.PerguntaRequest
import com.fiap.totemflexmedia.model.RespostaResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tts: TextToSpeech

    private lateinit var tvRespostaIA: TextView
    private lateinit var tvClima: TextView
    private lateinit var tvSaudacao: TextView
    private lateinit var tvIdioma: TextView
    private lateinit var btnMic: FloatingActionButton
    private lateinit var layoutPrincipal: View

    private var isAltoContraste = false
    private var isTextoGrande = false

    private var idiomaAtual = "PT-BR"
    private var sttLanguage = "pt-BR"
    private var cmdMapa = "Onde fica o mapa do local?"
    private var cmdAgenda = "Quais os eventos de hoje?"
    private var cmdAjuda = "Preciso de suporte humano."

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startListening()
        else Toast.makeText(context, "Permissão de áudio negada.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutPrincipal = view.findViewById(R.id.layoutPrincipal)
        tvRespostaIA = view.findViewById(R.id.tvRespostaIA)
        tvClima = view.findViewById(R.id.tvClima)
        tvSaudacao = view.findViewById(R.id.tvSaudacao)
        tvIdioma = view.findViewById(R.id.tvIdioma)
        btnMic = view.findViewById(R.id.btnMic)

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("pt", "BR")

                // 1. Ajuste de Velocidade e Tom (Deixa menos robótico)
                tts.setSpeechRate(0.95f) // 5% mais lenta (soa mais calma e clara)
                tts.setPitch(1.05f)      // 5% mais aguda (tira o som abafado)

                // 2. Caça por vozes "Premium" (Network) instaladas no aparelho
                try {
                    val vozesDisponiveis = tts.voices
                    for (voz in vozesDisponiveis) {
                        // Tenta pegar a voz gerada por IA na nuvem (muito mais natural)
                        if (voz.locale.language == "pt" && voz.name.contains("network", ignoreCase = true)) {
                            tts.voice = voz
                            break // Achou uma voz boa, para de procurar
                        }
                    }
                } catch (e: Exception) {
                    // Se der erro ao listar as vozes, o Android usa a padrão tranquilamente
                }
            }
        }

        fetchWeather()
        setupSpeechRecognizer()
        btnMic.setOnClickListener { checkPermissionAndListen() }
        tvIdioma.setOnClickListener { alternarIdioma() }

        view.findViewById<MaterialButton>(R.id.btnCardMapa).setOnClickListener { enviarComandoIA(cmdMapa) }
        view.findViewById<MaterialButton>(R.id.btnCardEventos).setOnClickListener { enviarComandoIA(cmdAgenda) }
        view.findViewById<MaterialButton>(R.id.btnCardAjuda).setOnClickListener { enviarComandoIA(cmdAjuda) }

        view.findViewById<MaterialButton>(R.id.btnContraste).setOnClickListener { alternarAltoContraste() }
        view.findViewById<MaterialButton>(R.id.btnTamanhoTexto).setOnClickListener { alternarTamanhoTexto() }

        tvSaudacao.setOnLongClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DashboardFragment())
                .addToBackStack(null).commit()
            true
        }
    }

    // ================= VERIFICADOR DE INTERNET =================
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
    // ==========================================================

    private fun alternarAltoContraste() {
        isAltoContraste = !isAltoContraste

        val corFundo = if (isAltoContraste) Color.parseColor("#000000") else Color.parseColor("#F7F9FA")
        val corTextoDestaque = if (isAltoContraste) Color.parseColor("#FFFF00") else Color.parseColor("#2D3142")
        val corTextoSecundario = if (isAltoContraste) Color.parseColor("#FFFFFF") else Color.parseColor("#7c7e7f")
        val corRodape = if (isAltoContraste) Color.parseColor("#121212") else Color.parseColor("#FFFFFF")

        layoutPrincipal.setBackgroundColor(corFundo)
        view?.findViewById<View>(R.id.footerBar)?.setBackgroundColor(corRodape)

        tvSaudacao.setTextColor(corTextoDestaque)
        tvRespostaIA.setTextColor(corTextoSecundario)
        tvLocalizacao.setTextColor(corTextoSecundario)
        tvIdioma.setTextColor(corTextoSecundario)
        tvClima.setTextColor(corTextoSecundario)
        view?.findViewById<TextView>(R.id.clockTotem)?.setTextColor(corTextoDestaque)

        val btnMapa = view?.findViewById<MaterialButton>(R.id.btnCardMapa)
        val btnEventos = view?.findViewById<MaterialButton>(R.id.btnCardEventos)
        val btnAjuda = view?.findViewById<MaterialButton>(R.id.btnCardAjuda)

        val corTextoBotao = if (isAltoContraste) Color.parseColor("#FFFF00") else Color.parseColor("#4A4A4A")
        btnMapa?.setTextColor(corTextoBotao)
        btnEventos?.setTextColor(corTextoBotao)
        btnAjuda?.setTextColor(corTextoBotao)
    }

    private fun alternarTamanhoTexto() {
        isTextoGrande = !isTextoGrande
        val multiplicador = if (isTextoGrande) 1.4f else 1.0f

        tvSaudacao.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f * multiplicador)
        tvRespostaIA.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f * multiplicador)

        val btnTamanho = view?.findViewById<MaterialButton>(R.id.btnTamanhoTexto)
        btnTamanho?.text = if (isTextoGrande) "A-" else "A+"
    }

    private fun fetchWeather() {
        if (!isNetworkAvailable()) {
            tvClima.text = "🌤️ Off"
            return
        }

        tvClima.text = "🌤️ Buscando..."
        RetrofitClient.instance.obterClimaAtual().enqueue(object : Callback<ClimaResponse> {
            override fun onResponse(call: Call<ClimaResponse>, response: Response<ClimaResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val climaReal = response.body()!!.clima
                    activity?.runOnUiThread { tvClima.text = climaReal }
                } else {
                    activity?.runOnUiThread { tvClima.text = "🌤️ --" }
                }
            }

            override fun onFailure(call: Call<ClimaResponse>, t: Throwable) {
                activity?.runOnUiThread { tvClima.text = "🌤️ Erro" }
            }
        })
    }

    private fun enviarComandoIA(texto: String) {
        if (!isNetworkAvailable()) {
            val erroMsg = "Estou sem conexão com a internet."
            tvRespostaIA.text = erroMsg
            tts.speak(erroMsg, TextToSpeech.QUEUE_FLUSH, null, null)
            return
        }

        tvRespostaIA.text = "Analisando: \"$texto\"..."
        RetrofitClient.instance.enviarPergunta(PerguntaRequest(texto)).enqueue(object : Callback<RespostaResponse> {

            override fun onResponse(call: Call<RespostaResponse>, response: Response<RespostaResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val respostaDaIA = response.body()!!.respostaIa
                    tvRespostaIA.text = respostaDaIA
                    tts.speak(respostaDaIA, TextToSpeech.QUEUE_FLUSH, null, null)
                    response.body()!!.clima?.let { tvClima.text = it }
                } else {
                    val erroMsg = "Erro de Resposta: Código ${response.code()}"
                    tvRespostaIA.text = erroMsg
                }
            }

            override fun onFailure(call: Call<RespostaResponse>, t: Throwable) {
                val erroMsg = "Erro na Comunicação: ${t.message}"
                tvRespostaIA.text = erroMsg
                tts.speak("Desculpe, tive um problema de comunicação.", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        })
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { tvRespostaIA.text = "Pode falar, estou ouvindo..." }
            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)?.let { enviarComandoIA(it) }
            }
            override fun onEndOfSpeech() { tvRespostaIA.text = "Processando voz..." }
            override fun onError(error: Int) { tvRespostaIA.text = "Não entendi. Toque para tentar novamente." }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun checkPermissionAndListen() {
        if (tts.isSpeaking) tts.stop()

        // O ESCUDO: Se não tiver internet, ele nem tenta abrir o microfone
        if (!isNetworkAvailable()) {
            val erroMsg = "Sem conexão com a rede."
            tvRespostaIA.text = erroMsg
            tts.speak("Desculpe, estou temporariamente sem conexão com a rede.", TextToSpeech.QUEUE_FLUSH, null, null)
            return
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, sttLanguage)
        }
        speechRecognizer.startListening(intent)
    }

    private fun alternarIdioma() {
        when (idiomaAtual) {
            "PT-BR" -> configurarIdioma("EN-US", Locale.US, "en-US", "How can I help you?", "Tap the microphone below to speak.", "Map", "Schedule", "Support", "Where is the local map?", "What are the events today?", "I need human support.")
            "EN-US" -> configurarIdioma("ES-ES", Locale("es", "ES"), "es-ES", "¿Cómo puedo ayudarte?", "Toca el micrófono abajo para hablar.", "Mapa", "Agenda", "Soporte", "¿Dónde está el mapa local?", "¿Cuáles son los eventos de hoy?", "Necesito soporte humano.")
            "ES-ES" -> configurarIdioma("PT-BR", Locale("pt", "BR"), "pt-BR", "Como posso ajudar você?", "Toque no microfone abaixo para falar.", "Mapa", "Agenda", "Suporte", "Onde fica o mapa do local?", "Quais os eventos de hoje?", "Preciso de suporte humano.")
        }
    }

    private fun configurarIdioma(sigla: String, ttsLocale: Locale, sttLang: String, saudacao: String, dicaMic: String, txtMapa: String, txtAgenda: String, txtAjuda: String, cMapa: String, cAgenda: String, cAjuda: String) {
        idiomaAtual = sigla
        sttLanguage = sttLang
        tvIdioma.text = sigla
        tvSaudacao.text = saudacao
        tvRespostaIA.text = dicaMic
        view?.findViewById<MaterialButton>(R.id.btnCardMapa)?.text = txtMapa
        view?.findViewById<MaterialButton>(R.id.btnCardEventos)?.text = txtAgenda
        view?.findViewById<MaterialButton>(R.id.btnCardAjuda)?.text = txtAjuda
        cmdMapa = cMapa
        cmdAgenda = cAgenda
        cmdAjuda = cAjuda
        tts.language = ttsLocale
        tts.speak(saudacao, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private val tvLocalizacao: TextView get() = view?.findViewById(R.id.tvLocalizacao)!!

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        if (::tts.isInitialized) { tts.stop(); tts.shutdown() }
    }
}