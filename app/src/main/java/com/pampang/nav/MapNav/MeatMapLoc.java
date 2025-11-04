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

public class MeatMapLoc extends AppCompatActivity {

    private ImageView first_meat;
    private ImageView second_meat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.meatmaploc);


        first_meat = findViewById(R.id.first_meat);
        second_meat = findViewById(R.id.second_meat);


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
                        } else if (item.getItemId() == R.id.get_direction) {
                            Intent intent = new Intent(MeatMapLoc.this, FirstMeatMapView.class);
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
                        } else if (item.getItemId() == R.id.get_direction) {
                            Intent intent = new Intent(MeatMapLoc.this, SecondMeatMapView.class);
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
