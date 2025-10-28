package com.sound

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.concurrent.thread
import kotlin.math.log10
import kotlin.math.sqrt

class NoiseDetector(
    private val sampleRate: Int = 44100,
    private val bufferIntervalMs: Long = 200,
    private val callback: (db: Double) -> Unit
) {
    private var audioRecord: AudioRecord? = null
    @Volatile private var running = false

    fun start() {
        if (running) return
        val minBuf = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBuf
        )
        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            return
        }
        running = true
        audioRecord?.startRecording()

        thread(start = true) {
            val buffer = ShortArray(minBuf)
            while (running) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    var sum = 0.0
                    for (i in 0 until read) {
                        val v = buffer[i].toDouble()
                        sum += v * v
                    }
                    val mean = if (read > 0) sum / read else 0.0
                    val rms = sqrt(mean)
                    val db = if (rms > 0) 20 * log10(rms / 32768.0) + 90 else 0.0
                    callback(db)
                }
                try { Thread.sleep(bufferIntervalMs) } catch (e: InterruptedException) { }
            }
        }
    }

    fun stop() {
        running = false
        try { audioRecord?.stop() } catch (e: Exception) { }
        try { audioRecord?.release() } catch (e: Exception) { }
        audioRecord = null
    }
}