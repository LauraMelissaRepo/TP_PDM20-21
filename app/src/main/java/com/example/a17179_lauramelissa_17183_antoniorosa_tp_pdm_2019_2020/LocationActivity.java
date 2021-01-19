package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
