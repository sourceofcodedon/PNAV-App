package com.pampang.nav.MapNav;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.pampang.nav.R;

public class SecondMeatStore extends AppCompatActivity {

    private TextView storeNameTextView;
    private TextView openTimeTextView;
    private TextView closeTimeTextView;
    private ImageView storeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second_meat_store);

        storeNameTextView = findViewById(R.id.store_name);
        openTimeTextView = findViewById(R.id.open_time_value);
        closeTimeTextView = findViewById(R.id.close_time_value);
        storeImageView = findViewById(R.id.store_image);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("stores")
                .whereEqualTo("store_category", "SecondMeatStore")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String storeName = task.getResult().getDocuments().get(0).getString("store_name");
                        String openingTime = task.getResult().getDocuments().get(0).getString("opening_time");
                        String closingTime = task.getResult().getDocuments().get(0).getString("closing_time");
                        String imageBase64 = task.getResult().getDocuments().get(0).getString("image");

                        storeNameTextView.setText(storeName);
                        openTimeTextView.setText(openingTime);
                        closeTimeTextView.setText(closingTime);

                        if (imageBase64 != null) {
                            Bitmap bitmap = decodeImage(imageBase64);
                            storeImageView.setImageBitmap(bitmap);
                        }
                    }
                });
    }

    private Bitmap decodeImage(String base64String) {
        byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
