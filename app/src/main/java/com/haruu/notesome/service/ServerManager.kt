package com.haruu.notesome.service

import android.util.Log
import com.haruu.notesome.model.Some
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

private const val BASE_URL = "http://13.209.193.98:50001"

internal interface Server {
    @GET("/api/texts")
    fun getTexts(): Call<List<Some>>

    @GET("/api/audios")
    fun getAudios(): Call<List<Some>>

    @POST("/api/texts")
    fun addText(@Body some: Some): Call<Some>

    @POST("/api/audios")
    fun addAudio(@Body some: Some): Call<Some>

    @POST("/api/audios/file")
    fun uploadAudio(@Part audioFile: MultipartBody.Part): Call<Some>

    @DELETE("/api/texts/{id}")
    fun removeText(@Path("id") id: Long): Call<Long>

    @DELETE("/api/audios/{id}")
    fun removeAudio(@Path("id") id: Long): Call<Long>
}

object ServerManager {
    private val server: Server = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(Server::class.java)

    fun loadTexts(callback: (someList: List<Some>) -> Unit) {
        Log.i("debug", "load text from remote")
        server.getTexts().enqueue(
            object : Callback<List<Some>> {
                override fun onResponse(call: Call<List<Some>>, response: Response<List<Some>>) {
                    Log.i("debug", "load text from remote [complete]")
                    response.body()?.let(callback)
                }

                override fun onFailure(call: Call<List<Some>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    fun loadAudios(callback: (someList: List<Some>) -> Unit) {
        Log.i("debug", "load audio from remote")
        server.getAudios().enqueue(
            object : Callback<List<Some>> {
                override fun onResponse(call: Call<List<Some>>, response: Response<List<Some>>) {
                    Log.i("debug", "load audio from remote [complete]")
                    response.body()?.let(callback)
                }

                override fun onFailure(call: Call<List<Some>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    fun saveText(text: String, callback: (some: Some) -> Unit) {
        Log.i("debug", "save text $text")
        server.addText(Some(title = text)).enqueue(
            object : Callback<Some> {
                override fun onResponse(call: Call<Some>, response: Response<Some>) {
                    Log.i("debug", "save text $text [complete]")
                    response.body()?.let(callback)
                }

                override fun onFailure(call: Call<Some>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    fun saveAudio(filename: String, callback: (some: Some) -> Unit) {
        Log.i("debug", "save audio : $filename")
        server.addAudio(Some(title = filename)).enqueue(
            object : Callback<Some> {
                override fun onResponse(call: Call<Some>, response: Response<Some>) {
                    Log.i("debug", "save audio : $filename [complete]")
                    response.body()?.let(callback)
                }

                override fun onFailure(call: Call<Some>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    fun uploadAudio(audioFile: File, callback: (some: Some) -> Unit) {
        Log.i("debug", "upload audio : ${audioFile.absolutePath}")
        val body: MultipartBody.Part = RequestBody.create(MediaType.parse("audio/*"), audioFile).let {
            MultipartBody.Part.createFormData("audioFile", audioFile.name, it)
        }

        server.uploadAudio(body).enqueue(
            object : Callback<Some> {
                override fun onResponse(call: Call<Some>, response: Response<Some>) {
                    Log.i("debug", "upload audio : ${audioFile.absolutePath} [complete]")
                    response.body()?.let(callback)
                }

                override fun onFailure(call: Call<Some>, t: Throwable) {
                    t.printStackTrace()
                }
            }
        )
    }

    fun deleteText(id: Long, callback: (id: Long) -> Unit) {
        Log.i("debug", "delete text $id")
        server.removeText(id).enqueue(
            object : Callback<Long> {
                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    Log.i("debug", "delete text : $id [complete]")
                    response.body()?.let(callback)
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    fun deleteAudio(id: Long, callback: (id: Long) -> Unit) {
        Log.i("debug", "delete audio : $id")
        server.removeAudio(id).enqueue(
            object : Callback<Long> {
                override fun onResponse(call: Call<Long>, response: Response<Long>) {
                    Log.i("debug", "delete audio : $id [complete]")
                    response.body()?.let(callback)
                }

                override fun onFailure(call: Call<Long>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

}