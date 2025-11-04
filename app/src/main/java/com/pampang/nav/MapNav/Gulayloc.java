package com.pampang.nav.MapNav;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.pampang.nav.R;

public class Gulayloc extends AppCompatActivity {

    private ImageView first_gulay;
    private ImageView second_gulay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gulayloc);

        // 🐟 Initialize ImageViews
        first_gulay = findViewById(R.id.first_gulay);
        second_gulay = findViewById(R.id.second_gulay);

        // 🐠 First gulay click → show popup menu
        first_gulay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Gulayloc.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.view_store) {
                            Intent intent = new Intent(Gulayloc.this, FirstGulayStore.class);
                            intent.putExtra("storeName", "FirstGulayStore");
                            startActivity(intent);
                            return true;
                        } else if (item.getItemId() == R.id.get_direction) {
                            Intent intent = new Intent(Gulayloc.this, GulayMapOne.class);
                            startActivity(intent);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popup.show();
            }
        });

        // 🐡 Second gulay click → show popup menu
        second_gulay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Gulayloc.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.view_store) {
                            Intent intent = new Intent(Gulayloc.this, SecondGulayStore.class);
                            intent.putExtra("storeName", "SecondGulayStore");
                            startActivity(intent);
                            return true;
                        } else if (item.getItemId() == R.id.get_direction) {
                            Intent intent = new Intent(Gulayloc.this, GulayMapTwo.class);
                            startActivity(intent);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }
}
