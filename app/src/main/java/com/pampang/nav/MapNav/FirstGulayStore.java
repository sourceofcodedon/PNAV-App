package com.pampang.nav.MapNav;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pampang.nav.R;
import com.pampang.nav.screens.seller.EditStoreActivity;

public class FirstGulayStore extends AppCompatActivity {

    private TextView storeNameTextView;
    private TextView openTimeTextView;
    private TextView closeTimeTextView;
    private ImageView storeImageView;
    private Button editButton;
    private Button deleteButton;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_gulay_store);

        storeNameTextView = findViewById(R.id.store_name);
        openTimeTextView = findViewById(R.id.open_time_value);
        closeTimeTextView = findViewById(R.id.close_time_value);
        storeImageView = findViewById(R.id.store_image);
        editButton = findViewById(R.id.edit_button);
        deleteButton = findViewById(R.id.delete_button);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("stores")
                .whereEqualTo("store_category", "FirstGulayStore")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        storeId = task.getResult().getDocuments().get(0).getId();
                        String storeName = task.getResult().getDocuments().get(0).getString("store_name");
                        String openingTime = task.getResult().getDocuments().get(0).getString("opening_time");
                        String closingTime = task.getResult().getDocuments().get(0).getString("closing_time");
                        String imageUrl = task.getResult().getDocuments().get(0).getString("image");
                        String creatorId = task.getResult().getDocuments().get(0).getString("creatorId");

                        storeNameTextView.setText(storeName);
                        openTimeTextView.setText(openingTime);
                        closeTimeTextView.setText(closingTime);

                        if (imageUrl != null) {
                            Glide.with(this).load(imageUrl).into(storeImageView);
                        }

                        if (currentUser != null && currentUser.getUid().equals(creatorId)) {
                            editButton.setVisibility(View.VISIBLE);
                            deleteButton.setVisibility(View.VISIBLE);
                            editButton.setOnClickListener(v -> {
                                Intent intent = new Intent(FirstGulayStore.this, EditStoreActivity.class);
                                intent.putExtra("store_id", storeId);
                                intent.putExtra("store_name", storeName);
                                intent.putExtra("store_category", "FirstGulayStore");
                                intent.putExtra("opening_time", openingTime);
                                intent.putExtra("closing_time", closingTime);
                                intent.putExtra("image_url", imageUrl);
                                startActivity(intent);
                            });
                            deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
                        }
                    }
                });
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
                        Toast.makeText(FirstGulayStore.this, "Store deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(FirstGulayStore.this, "Error deleting store", Toast.LENGTH_SHORT).show());
        }
    }
}
