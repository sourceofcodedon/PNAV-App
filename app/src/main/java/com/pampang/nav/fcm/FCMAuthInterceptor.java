package com.pampang.nav.fcm;

import android.content.Context;
import androidx.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FCMAuthInterceptor implements Interceptor {
    private final Context context;

    public FCMAuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = FCMTokenHelper.getAccessToken(context);

        if (token == null) {
            throw new IOException("Failed to get Firebase access token");
        }

        Request originalRequest = chain.request();
        Request newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json; UTF-8")
                .build();

        return chain.proceed(newRequest);
    }
}
