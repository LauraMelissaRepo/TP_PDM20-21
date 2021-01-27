package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.Location;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateLocationActivity extends AppCompatActivity {

    public static final int MAP_REQUEST_CODE = 3000;
    private String lat, lng, locationDescriptionString;
    private EditText locationDescription;
    private TextView savedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);

        this.lat = "";
        this.lng = "";
        this.locationDescriptionString = "";

        Toolbar toolbar = findViewById(R.id.toolbar);
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
        saveAll.setOnClickListener(v -> {
            this.locationDescriptionString = locationDescription.getText().toString();

            if (this.lat.equals("")
                    || this.lng.equals("")
                    || this.locationDescriptionString.equals("")){
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateLocationActivity.this);

                builder.setTitle(R.string.errorCreatePeopleTitleAlertDialog);
                builder.setMessage(R.string.errorCreateLocationMessageAlertDialog);
                builder.setPositiveButton(R.string.errorCreatePeoplePositiveButtonAlertDialog,
                        (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else{
                Location location = new Location(this.locationDescriptionString,
                        this.lat,
                        this.lng);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("locations")
                        .add(location);
                finish();
            }

        });
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
