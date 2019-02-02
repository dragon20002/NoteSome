package com.haruu.notesome.service

import android.util.Log
import com.haruu.notesome.model.ShortText
import com.haruu.notesome.model.Sound
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.InputStream

private const val BASE_URL = "http://13.209.193.98:50001"

internal interface Server {
    @GET("/api/texts")
    fun getTexts(): Call<List<ShortText>>

    @GET("/api/sounds")
    fun getAudios(): Call<List<Sound>>

    @POST("/api/texts")
    fun addText(@Body shortText: ShortText): Call<ShortText>

    @POST("/api/sounds")
    fun addAudio(@Body some: Sound): Call<Sound>

    @POST("/api/sounds/file")
    @Multipart
    fun uploadAudio(@Part soundFile: MultipartBody.Part): Call<String>

    @DELETE("/api/texts/{id}")
    fun removeText(@Path("id") id: Long): Call<Long>

    @DELETE("/api/sounds/{id}")
    fun removeAudio(@Path("id") id: Long): Call<Long>
}

object ServerManager {
    private val server: Server = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Server::class.java)

    fun loadTexts(callback: (shortTextList: List<ShortText>) -> Unit) {
        Log.i("debug", "load text from remote")
        server.getTexts().enqueue(
                object : Callback<List<ShortText>> {
                    override fun onResponse(call: Call<List<ShortText>>, response: Response<List<ShortText>>) {
                        Log.i("debug", "load text from remote [complete]")
                        response.body()?.let(callback)
                    }

                    override fun onFailure(call: Call<List<ShortText>>, t: Throwable) {
                        Log.i("debug", "load text from remote [fail]")
                    }
                })
    }

    fun loadAudios(callback: (someList: List<Sound>) -> Unit) {
        Log.i("debug", "load audio from remote")
        server.getAudios().enqueue(
                object : Callback<List<Sound>> {
                    override fun onResponse(call: Call<List<Sound>>, response: Response<List<Sound>>) {
                        Log.i("debug", "load audio from remote [complete]")
                        response.body()?.let(callback)
                    }

                    override fun onFailure(call: Call<List<Sound>>, t: Throwable) {
                        Log.i("debug", "load audio from remote [fail]")
                    }
                })
    }

    fun saveText(text: String, callback: (shortText: ShortText) -> Unit) {
        Log.i("debug", "save text $text")
        server.addText(ShortText(title = text)).enqueue(
                object : Callback<ShortText> {
                    override fun onResponse(call: Call<ShortText>, response: Response<ShortText>) {
                        Log.i("debug", "save text $text [complete]")
                        response.body()?.let(callback)
                    }

                    override fun onFailure(call: Call<ShortText>, t: Throwable) {
                        Log.i("debug", "save text $text [fail]")
                    }
                })
    }

    fun saveAudio(filename: String, callback: (some: Sound) -> Unit) {
        Log.i("debug", "save audio : $filename")
        server.addAudio(Sound(title = filename)).enqueue(
                object : Callback<Sound> {
                    override fun onResponse(call: Call<Sound>, response: Response<Sound>) {
                        Log.i("debug", "save audio : $filename [complete]")
                        response.body()?.let(callback)
                    }

                    override fun onFailure(call: Call<Sound>, t: Throwable) {
                        Log.i("debug", "save audio : $filename [fail]")
                    }
                })
    }

    fun uploadAudio(
            filename: String,
            inputStream: InputStream,
            callback: (filename: String) -> Unit
    ) {
        val body: MultipartBody.Part =
                RequestBody.create(MediaType.parse("audio/*"), inputStream.readBytes()).let {
                    MultipartBody.Part.createFormData("audioFile", filename, it)
                }
        Log.i(
                "debug",
                "upload audio : $filename (${body.body().contentType()} ${body.body().contentLength() / 1024f} KB)"
        )

        server.uploadAudio(body).enqueue(
                object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Log.i("debug", "upload audio : $filename [complete]")
                        response.body()?.let(callback)
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.i("debug", "upload audio : $filename [fail]")
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
                        Log.i("debug", "delete text : $id [fail]")
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
                        Log.i("debug", "delete audio : $id [fail]")
                    }
                })
    }

}