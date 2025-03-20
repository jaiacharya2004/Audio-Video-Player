package com.example.audiovideoplayer.ui.audio

import android.content.Context
import android.provider.MediaStore

class AudioRepository(private val context: Context) {

    fun getAudioFiles(): List<AudioModel> {
        val audioList = mutableListOf<AudioModel>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA
        )

        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val pathColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val path = it.getString(pathColumn)

                audioList.add(AudioModel(title, artist, path))
            }
        }
        return audioList
    }
}
