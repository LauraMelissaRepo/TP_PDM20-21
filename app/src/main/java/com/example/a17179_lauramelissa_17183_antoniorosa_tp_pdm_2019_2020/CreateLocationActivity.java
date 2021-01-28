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

        // Instantiate the variables with a empty string
        this.lat = "";
        this.lng = "";
        this.locationDescriptionString = "";

        // ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.createLocation);

        // View objects declared into variables
        this.locationDescription = findViewById(R.id.location_description);
        this.savedLocation = findViewById(R.id.text_saved_location);

        //listener to go to the activity that open the map
        ExtendedFloatingActionButton mapButton = findViewById(R.id.add_location_map);
        mapButton.setOnClickListener(v -> {
            Intent intentMap = new Intent(getApplicationContext(), AddLocalToPeopleActivity.class);
            startActivityForResult(intentMap, MAP_REQUEST_CODE);
        });

        //listener to save all the information
        ExtendedFloatingActionButton saveAll = findViewById(R.id.save_location_btn);
        saveAll.setOnClickListener(v -> {
            this.locationDescriptionString = locationDescription.getText().toString();

            //listener to confirm if any information is not complete
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
            }
            // If all fields have been entered, a new location object is created and
            // this new object is saved within a new document in the firebase
            else{

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

    /**
     * Function called after certain activities are finished.
     * @param requestCode code responsible for identifying the activity.
     * @param resultCode code responsible for identifying how the activity ended,
     *                  successfully, unsuccessfully or if it was canceled.
     * @param data information returned by the closed activity.
     */
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
