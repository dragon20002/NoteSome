package com.haruu.notesome.service

import android.util.Log
import com.haruu.notesome.model.Some

object FileManager {

    fun readTexts(callback: (someList: List<Some>) -> Unit) {
        Log.i("debug", "load text from local")
        // TODO
    }

    fun readAudios(callback: (someList: List<Some>) -> Unit) {
        Log.i("debug", "load audio from local")
        // TODO
    }

    fun writeText(text: String, callback: (some: Some) -> Unit) {
        Log.i("debug", "save text $text to local")
        // TODO
    }

    fun writeAudio(filename: String, callback: (some: Some) -> Unit) {
        Log.i("debug", "save audio : $filename to local")
        // TODO
    }

    fun deleteText(id: Long, callback: (some: Some) -> Unit) {
        Log.i("debug", "delete text $id from local")
        // TODO
    }

    fun deleteAudio(id: Long, callback: (some: Some) -> Unit) {
        Log.i("debug", "delete audio : $id from local")
        // TODO
    }

}