package com.pampang.nav.fcm;

import com.google.gson.JsonObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface FCMApi {
    @POST
    Call<ResponseBody> sendMessage(
            @Url String url,
            @Body JsonObject body
    );
}
