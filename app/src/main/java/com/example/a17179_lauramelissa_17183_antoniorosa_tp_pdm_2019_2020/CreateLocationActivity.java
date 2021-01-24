package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.exifinterface.media.ExifInterface;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class CreateLocationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public static final int MAP_REQUEST_CODE = 3000;
    private String lat, lng;
    private EditText locationDescription;
    private TextView savedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.createLocation);

        this.locationDescription = findViewById(R.id.location_description);
        this.savedLocation = findViewById(R.id.text_saved_location);



        ExtendedFloatingActionButton mapButton = findViewById(R.id.add_location_map);
        mapButton.setOnClickListener(v -> {
            Intent intentMap = new Intent(getApplicationContext(), AddLocalToPeopleActivity.class);
            startActivityForResult(intentMap, MAP_REQUEST_CODE);
        });

        ExtendedFloatingActionButton saveAll = findViewById(R.id.save_location_btn);

//        saveAll.setOnClickListener(v -> {
//            String description = locationDescription.getText().toString();
//        }
    }


    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == this.MAP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                this.lat = data.getStringExtra("lastMarkerLat");
                this.lng = data.getStringExtra("lastMarkerLng");
                this.savedLocation.setText("A sua localização foi guardada!");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
