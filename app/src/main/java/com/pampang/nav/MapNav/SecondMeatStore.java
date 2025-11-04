package com.pampang.nav.MapNav;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.pampang.nav.R;

public class SecondMeatStore extends AppCompatActivity {

    private TextView storeNameTextView;
    private TextView openTimeTextView;
    private TextView closeTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second_meat_store);

        storeNameTextView = findViewById(R.id.store_name);
        openTimeTextView = findViewById(R.id.open_time_value);
        closeTimeTextView = findViewById(R.id.close_time_value);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("stores")
                .whereEqualTo("store_category", "SecondMeatStore")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String storeName = task.getResult().getDocuments().get(0).getString("store_name");
                        String openingTime = task.getResult().getDocuments().get(0).getString("opening_time");
                        String closingTime = task.getResult().getDocuments().get(0).getString("closing_time");
                        storeNameTextView.setText(storeName);
                        openTimeTextView.setText(openingTime);
                        closeTimeTextView.setText(closingTime);
                    }
                });
    }
}
