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

public class MeatMapLoc extends AppCompatActivity implements ZoomLayout.OnScaleChangedListener {

    private ImageView first_meat;
    private ImageView second_meat;
    private ZoomLayout zoomLayout;
    private TextView textView9, textView10, textView11, textView12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meatmaploc);

        first_meat = findViewById(R.id.first_meat);
        second_meat = findViewById(R.id.second_meat);

        zoomLayout = findViewById(R.id.zoom_layout);
        zoomLayout.setOnScaleChangedListener(this);

        textView9 = findViewById(R.id.textView9);
        textView10 = findViewById(R.id.textView10);
        textView11 = findViewById(R.id.textView11);
        textView12 = findViewById(R.id.textView12);

        first_meat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MeatMapLoc.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.view_store) {
                            Intent intent = new Intent(MeatMapLoc.this, FirstMeatStore.class);
                            intent.putExtra("storeName", "FirstMeatStore");
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

        second_meat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MeatMapLoc.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.view_store) {
                            Intent intent = new Intent(MeatMapLoc.this, SecondMeatStore.class);
                            intent.putExtra("storeName", "SecondMeatStore");
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
            textView9.setVisibility(View.GONE);
            textView10.setVisibility(View.GONE);
            textView11.setVisibility(View.GONE);
            textView12.setVisibility(View.GONE);
        } else {
            textView9.setVisibility(View.VISIBLE);
            textView10.setVisibility(View.VISIBLE);
            textView11.setVisibility(View.VISIBLE);
            textView12.setVisibility(View.VISIBLE);
        }
    }
}
