package com.pampang.nav.MapNav;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pampang.nav.R;
import com.pampang.nav.screens.seller.EditStoreActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class FirstMeatStore extends AppCompatActivity {

    private TextView storeNameTextView;
    private TextView openTimeTextView;
    private TextView closeTimeTextView;
    private TextView storeStatusIndicator;
    private ImageView storeImageView;
    private Button editButton;
    private Button deleteButton;
    private Button getDirectionButton;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_meat_store);

        storeNameTextView = findViewById(R.id.store_name);
        openTimeTextView = findViewById(R.id.open_time_value);
        closeTimeTextView = findViewById(R.id.close_time_value);
        storeStatusIndicator = findViewById(R.id.store_status_indicator);
        storeImageView = findViewById(R.id.store_image);
        editButton = findViewById(R.id.edit_button);
        deleteButton = findViewById(R.id.delete_button);
        getDirectionButton = findViewById(R.id.get_direction_button);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        getDirectionButton.setOnClickListener(v -> {
            if (currentUser != null) {
                String storeName = storeNameTextView.getText().toString();
                String userId = currentUser.getUid();

                Map<String, Object> navigationHistory = new HashMap<>();
                navigationHistory.put("user_id", userId);
                navigationHistory.put("store_name", storeName);
                navigationHistory.put("timestamp", Timestamp.now());

                db.collection("navigationHistory")
                        .add(navigationHistory)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(FirstMeatStore.this, "Added to history", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreError", "Failed to add navigation history", e);
                            Toast.makeText(FirstMeatStore.this, "Failed to add to history: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }

            Intent intent = new Intent(FirstMeatStore.this, FirstMeatMapView.class);
            startActivity(intent);
        });

        db.collection("stores")
                .whereEqualTo("store_category", "FirstMeatStore")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        storeId = task.getResult().getDocuments().get(0).getId();
                        String storeName = task.getResult().getDocuments().get(0).getString("store_name");
                        String openingTime = task.getResult().getDocuments().get(0).getString("opening_time");
                        String closingTime = task.getResult().getDocuments().get(0).getString("closing_time");
                        String imageUrl = task.getResult().getDocuments().get(0).getString("image");
                        String ownerId = task.getResult().getDocuments().get(0).getString("owner_id");

                        storeNameTextView.setText(storeName);
                        openTimeTextView.setText(openingTime);
                        closeTimeTextView.setText(closingTime);

                        updateStoreStatus(openingTime, closingTime);

                        if (imageUrl != null) {
                            Glide.with(this).load(imageUrl).into(storeImageView);
                        }

                        if (currentUser != null && currentUser.getUid().equals(ownerId)) {
                            editButton.setVisibility(View.VISIBLE);
                            deleteButton.setVisibility(View.VISIBLE);
                            editButton.setOnClickListener(v1 -> {
                                Intent intent = new Intent(FirstMeatStore.this, EditStoreActivity.class);
                                intent.putExtra("store_id", storeId);
                                intent.putExtra("store_name", storeName);
                                intent.putExtra("store_category", "FirstMeatStore");
                                intent.putExtra("opening_time", openingTime);
                                intent.putExtra("closing_time", closingTime);
                                intent.putExtra("image_url", imageUrl);
                                startActivity(intent);
                            });
                            deleteButton.setOnClickListener(v1 -> showDeleteConfirmationDialog());
                        }
                    }
                });
    }

    private void updateStoreStatus(String openingTime, String closingTime) {
        if (openingTime == null || closingTime == null || openingTime.isEmpty() || closingTime.isEmpty()) {
            storeStatusIndicator.setText("");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);

            Date openTimeParsed = sdf.parse(openingTime);
            Date closeTimeParsed = sdf.parse(closingTime);

            Calendar cal = Calendar.getInstance();
            cal.setTime(openTimeParsed);
            int openMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

            cal.setTime(closeTimeParsed);
            int closeMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

            Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
            int nowMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

            boolean isOpen;
            if (openMinutes < closeMinutes) {
                // Same day opening (e.g. 9am to 5pm)
                isOpen = nowMinutes >= openMinutes && nowMinutes < closeMinutes;
            } else if (openMinutes > closeMinutes) {
                // Overnight opening (e.g. 10pm to 6am)
                isOpen = nowMinutes >= openMinutes || nowMinutes < closeMinutes;
            } else {
                // open 24 hours
                isOpen = true;
            }

            if (isOpen) {
                storeStatusIndicator.setText(R.string.store_status_open);
                storeStatusIndicator.setTextColor(Color.GREEN);
            } else {
                storeStatusIndicator.setText(R.string.store_status_closed);
                storeStatusIndicator.setTextColor(Color.RED);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            storeStatusIndicator.setText(""); // Clear on error
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Store")
                .setMessage("Are you sure you want to delete this store?")
                .setPositiveButton("Delete", (dialog, which) -> deleteStore())
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteStore() {
        if (storeId != null) {
            db.collection("stores").document(storeId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(FirstMeatStore.this, "Store deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(FirstMeatStore.this, "Error deleting store", Toast.LENGTH_SHORT).show());
        }
    }
}
