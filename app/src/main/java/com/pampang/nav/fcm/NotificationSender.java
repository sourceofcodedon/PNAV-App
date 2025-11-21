package com.pampang.nav.fcm;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.pampang.nav.remote.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSender {

    private static final String PROJECT_ID = "pampangnav";

    // Updated to include the sender's ID
    public static void sendNotificationToGroup(Context context, String title, String body, String senderId) {
        new Thread(() -> {
            try {
                String accessToken = FCMTokenHelper.getAccessToken(context);
                if (accessToken == null) {
                    Log.e("FCM", "No access token generated");
                    return;
                }

                // Build payload
                JsonObject payload = new JsonObject();
                JsonObject message = new JsonObject();
                JsonObject notification = new JsonObject();
                JsonObject data = new JsonObject(); // Data payload for sender ID

                notification.addProperty("title", title);
                notification.addProperty("body", body);

                data.addProperty("senderId", senderId); // Add the sender's ID to the data payload

                message.addProperty("topic", "global_chat");
                message.add("notification", notification);
                message.add("data", data); // Include the data payload
                payload.add("message", message);

                // Send request
                FCMApi api = RetrofitClient.getFCMClient(context).create(FCMApi.class);
                String url = "v1/projects/" + PROJECT_ID + "/messages:send";

                api.sendMessage(url, payload).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("FCM", "✅ Notification sent successfully to group");
                        } else {
                            try {
                                Log.e("FCM", "❌ Failed: " + response.code() + " | " + response.errorBody().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("FCM", "💥 Error sending notification to group", t);
                    }
                });

            } catch (Exception e) {
                Log.e("FCM", "Error generating access token", e);
            }
        }).start();
    }
}
