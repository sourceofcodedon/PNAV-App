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

public class FirstFishLoc extends AppCompatActivity {

    private ImageView first_fish;
    private ImageView second_fish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_firstfishloc);

        // 🐟 Initialize ImageViews
        first_fish = findViewById(R.id.first_fish);
        second_fish = findViewById(R.id.second_fish);

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
                        } else if (item.getItemId() == R.id.get_direction) {
                            Intent intent = new Intent(FirstFishLoc.this, MapActivityAF.class);
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
                        } else if (item.getItemId() == R.id.get_direction) {
                            Intent intent = new Intent(FirstFishLoc.this, MapActivty2.class);
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
