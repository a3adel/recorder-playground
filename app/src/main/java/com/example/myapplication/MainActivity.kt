package com.example.myapplication

import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : Activity() {
    private companion object {
        const val RECORD_AUDIO_PERMISSION_CODE = 1
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var transcriptTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        transcriptTextView = findViewById(R.id.transcriptTextView)
        val startButton: Button = findViewById(R.id.startButton)
        val stopButton: Button = findViewById(R.id.stopButton)
        startButton.setOnClickListener { startSpeechRecognition() }

        stopButton.setOnClickListener { stopSpeechRecognition() }

        checkPermission()

    }
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_CODE
            )
        } else {
            initializeSpeechRecognizer()
        }
    }
    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle) {
                    transcriptTextView.text = "Listening..."
                }

                override fun onSegmentResults(segmentResults: Bundle) {
                    super.onSegmentResults(segmentResults)
                    println("wwww")
                    val partialMatches =
                        segmentResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (partialMatches != null && partialMatches.isNotEmpty()) {
                        val partialText = partialMatches[0]
                        transcriptTextView.text = partialText
                        println(partialText)
                    }
                }

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray) {}

                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    transcriptTextView.text = "Error: $error"
                }

                override fun onResults(results: Bundle) {
                    println("vvvvasd")
                    val matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        val recognizedText = matches[0]
                        transcriptTextView.text = recognizedText
                    }
                }

                override fun onPartialResults(partialResults: Bundle) {
                    println("aasd")
                    val partialMatches =
                        partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (partialMatches != null && partialMatches.isNotEmpty()) {
                        val partialText = partialMatches[0]
                        transcriptTextView.text = partialText
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle) {}
            })
        } else {
            Toast.makeText(
                this,
                "Speech recognition not available on this device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun startSpeechRecognition() {
        speechRecognizer?.let {
            val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,

            )
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA") // Use "ar-SA" for Arabic
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SEGMENTED_SESSION, true)
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

            it.startListening(recognizerIntent)
        }
    }

    private fun stopSpeechRecognition() {
        speechRecognizer?.stopListening()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSpeechRecognizer()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. App cannot listen to your speech.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}

