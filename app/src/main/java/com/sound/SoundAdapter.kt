package com.sound

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SoundAdapter(
    private val items: List<SoundItem>,
    private val listener: OnPlayClick
) : RecyclerView.Adapter<SoundAdapter.VH>() {

    interface OnPlayClick { fun onPlay(sound: SoundItem); fun onStop() }

    private var currentlyPlaying: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_sound, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.name
        holder.playBtn.text = if (currentlyPlaying == position) "Detener" else "Reproducir"
        holder.playBtn.setOnClickListener {
            if (currentlyPlaying == position) {
                listener.onStop()
                currentlyPlaying = null
                notifyItemChanged(position)
            } else {
                val previous = currentlyPlaying
                currentlyPlaying = position
                previous?.let { notifyItemChanged(it) }
                notifyItemChanged(position)
                listener.onPlay(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.soundTitle)
        val playBtn: Button = view.findViewById(R.id.playBtn)
    }
}