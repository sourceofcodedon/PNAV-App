package com.pampang.nav.network

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface FCMApi {
    @POST
    fun sendMessage(
            @Url url: String,
            @Body body: JsonObject
    ): Call<ResponseBody>
}
