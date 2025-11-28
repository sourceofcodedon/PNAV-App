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

public class FirstFishLoc extends AppCompatActivity implements ZoomLayout.OnScaleChangedListener {

    private ImageView first_fish;
    private ImageView second_fish;
    private ZoomLayout zoomLayout;
    private TextView textView, textView2, textView3, textView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstfishloc);

        // 🐟 Initialize ImageViews
        first_fish = findViewById(R.id.first_fish);
        second_fish = findViewById(R.id.second_fish);

        zoomLayout = findViewById(R.id.zoom_layout);
        zoomLayout.setOnScaleChangedListener(this);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);

        // 🐠 First fish click → show popup menu
        first_fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(FirstFishLoc.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.view_store) {
                            Intent intent = new Intent(FirstFishLoc.this, FirstFishStore.class);
                            intent.putExtra("storeName", "FirstFishStore");
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

        // 🐡 Second fish click → show popup menu
        second_fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(FirstFishLoc.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.view_store) {
                            Intent intent = new Intent(FirstFishLoc.this, SecondFishStore.class);
                            intent.putExtra("storeName", "SecondFishStore");
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
            textView.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView4.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            textView4.setVisibility(View.VISIBLE);
        }
    }
}
