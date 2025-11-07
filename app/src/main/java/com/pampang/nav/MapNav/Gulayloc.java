package com.pampang.nav.MapNav;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pampang.nav.R;
import com.pampang.nav.utils.ZoomLayout;

public class Gulayloc extends AppCompatActivity implements ZoomLayout.OnScaleChangedListener {

    private ImageView first_gulay;
    private ImageView second_gulay;
    private ZoomLayout zoomLayout;
    private TextView textView5, textView6, textView7, textView8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gulayloc);

        // 🐟 Initialize ImageViews
        first_gulay = findViewById(R.id.first_gulay);
        second_gulay = findViewById(R.id.second_gulay);

        zoomLayout = findViewById(R.id.zoom_layout);
        zoomLayout.setOnScaleChangedListener(this);

        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);

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

    @Override
    public void onScaleChanged(float scaleFactor) {
        if (scaleFactor > 1) {
            textView5.setVisibility(View.GONE);
            textView6.setVisibility(View.GONE);
            textView7.setVisibility(View.GONE);
            textView8.setVisibility(View.GONE);
        } else {
            textView5.setVisibility(View.VISIBLE);
            textView6.setVisibility(View.VISIBLE);
            textView7.setVisibility(View.VISIBLE);
            textView8.setVisibility(View.VISIBLE);
        }
    }
}
