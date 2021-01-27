package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditLocationActivity extends AppCompatActivity {

    private String documentID;
    private String lat;
    private String lng;
    public static final int MAP_REQUEST_CODE = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        //Toobar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.editLocationToolbar);

        Bundle extras = getIntent().getExtras();
        this.documentID = extras.getString("DocumentID");
        String locationDescription = extras.getString("LocationName");
        this.lat = extras.getString("Lat");
        this.lng = extras.getString("Lng");

        EditText locationEditDescription = findViewById(R.id.location_edit_description);
        locationEditDescription.setText(locationDescription);

        Button locationEditMap = findViewById(R.id.edit_location_map);
        locationEditMap.setOnClickListener(v -> {
            Intent intentMap = new Intent(getApplicationContext(), EditLocationMapActivity.class);
            intentMap.putExtra("Lat", this.lat);
            intentMap.putExtra("Lng", this.lng);
            startActivityForResult(intentMap, MAP_REQUEST_CODE);
        });

        Button saveEditLocationButton = findViewById(R.id.save_edited_location_btn);
        saveEditLocationButton.setOnClickListener(v -> {
            FirebaseFirestore fb = FirebaseFirestore.getInstance();
            DocumentReference documentReference = fb.collection("locations").document(this.documentID);
            documentReference.update("locationDescription", String.valueOf(locationEditDescription.getText()));
            documentReference.update("lat", String.valueOf(this.lat));
            documentReference.update("lng", String.valueOf(this.lng));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MAP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                this.lat = data.getStringExtra("EditLat");
                this.lng = data.getStringExtra("EditLng");
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
