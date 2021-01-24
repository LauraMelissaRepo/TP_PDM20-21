package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LocationActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setSelectedItemId(R.id.nav_map);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.locations);


        RecyclerView locationsList = findViewById(R.id.locations_list);
        FloatingActionButton createLocationButton = findViewById(R.id.create_location_btn);

        createLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationActivity.this, CreateLocationActivity.class);
                startActivity(intent);
            }
        });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Intent resultIntent = null;
                switch (item.getItemId()) {
                    case R.id.nav_tasks:
                        resultIntent = new Intent(LocationActivity.this, MainActivity.class);
                        break;
                    case R.id.nav_people:
                        resultIntent = new Intent(LocationActivity.this, PeopleActivity.class);
                        break;
                }
                if (resultIntent != null){
                    startActivity(resultIntent);
                    LocationActivity.this.finish();
                    return true;
                }
                return false;
            };
}
