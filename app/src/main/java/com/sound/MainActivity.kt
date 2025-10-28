package com.sound

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private var detector: NoiseDetector? = null
    private lateinit var statusTv: TextView
    private lateinit var checkBtn: Button
    private lateinit var libraryBtn: Button
    private var mediaPlayer: MediaPlayer? = null

    private val noiseThresholdDb = 65.0

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                statusTv.text = "Permiso micrófono concedido."
                enableControls(true)
            } else {
                statusTv.text = "Permiso micrófono rechazado."
                enableControls(false)
                AlertDialog.Builder(this)
                    .setTitle("Permiso necesario")
                    .setMessage("La app necesita acceso al micrófono para medir el ruido.")
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTv = findViewById(R.id.statusText)
        checkBtn = findViewById(R.id.checkNoiseBtn)
        libraryBtn = findViewById(R.id.openLibraryBtn)

        checkBtn.setOnClickListener { startNoiseCheck() }
        libraryBtn.setOnClickListener { startActivity(Intent(this, SoundLibraryActivity::class.java)) }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            statusTv.text = "Permiso micrófono concedido."
            enableControls(true)
        }
    }

    private fun enableControls(enabled: Boolean) {
        checkBtn.isEnabled = enabled
        libraryBtn.isEnabled = true
    }

    private fun startNoiseCheck() {
        statusTv.text = "Midiendo ruido 3 segundos..."
        val measures = mutableListOf<Double>()
        detector = NoiseDetector(callback = { db -> synchronized(measures) { measures.add(db) } })
        detector?.start()

        checkBtn.isEnabled = false
        checkBtn.postDelayed({
            detector?.stop()
            detector = null
            val avgDb = synchronized(measures) { if (measures.isEmpty()) 0.0 else measures.average() }
            val rounded = String.format("%.1f", avgDb)
            if (avgDb >= noiseThresholdDb) {
                statusTv.text = "Hay bastante ruido (~$rounded dB). Reproduzco música relajante."
                playRelaxing()
            } else {
                statusTv.text = "Ruido bajo (~$rounded dB). Puedes abrir la biblioteca de sonidos."
            }
            checkBtn.isEnabled = true
        }, 3000)
    }

    private fun playRelaxing() {
        stopPlayer()
        val resId = resources.getIdentifier("relax_1", "raw", packageName)
        if (resId == 0) {
            statusTv.append("\nNo hay audio 'relax_1' en res/raw. Ejecuta fetch_samples.ps1 o añade audios.")
            return
        }
        mediaPlayer = MediaPlayer.create(this, resId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun stopPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        detector?.stop()
        stopPlayer()
    }
}