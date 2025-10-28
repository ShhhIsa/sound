package com.sound

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SoundLibraryActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var recycler: RecyclerView

    private val sounds = listOf(
        SoundItem("Relax 1", R.raw.relax_1),
        SoundItem("Relax 2", R.raw.relax_2),
        SoundItem("Rain", R.raw.rain),
        SoundItem("Ocean", R.raw.ocean)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_library)
        recycler = findViewById(R.id.soundsRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = SoundAdapter(sounds, object : SoundAdapter.OnPlayClick {
            override fun onPlay(sound: SoundItem) { playSound(sound.resId) }
            override fun onStop() { stopSound() }
        })
        recycler.adapter = adapter
    }

    private fun playSound(resId: Int) {
        stopSound()
        mediaPlayer = MediaPlayer.create(this, resId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSound()
    }
}