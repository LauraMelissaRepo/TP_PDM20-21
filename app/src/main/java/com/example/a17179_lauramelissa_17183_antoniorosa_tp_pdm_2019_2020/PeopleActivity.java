package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PeopleActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.people);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent resultIntent = null;
                switch (item.getItemId()) {
                    case R.id.nav_tasks:
                        resultIntent = new Intent(PeopleActivity.this, MainActivity.class);
                        break;
                    case R.id.nav_map:
                        resultIntent = new Intent(PeopleActivity.this, LocationActivity.class);
                        break;
                }
                startActivity(resultIntent);
                PeopleActivity.this.finish();
                return true;
            }
        };
}
