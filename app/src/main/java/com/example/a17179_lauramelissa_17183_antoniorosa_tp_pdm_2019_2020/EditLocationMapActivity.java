package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class EditLocationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location_people);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        this.lat = extras.getString("Lat");
        this.lng = extras.getString("Lng");

        Button editButton = findViewById(R.id.editLocation);
        editButton.setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.putExtra("EditLat", this.lat);
            intent.putExtra("EditLng", this.lng);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Localização"));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f);
            mMap.animateCamera(cameraUpdate);
            this.lat = String.valueOf(latLng.latitude);
            this.lng = String.valueOf(latLng.longitude);
        });

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(Double.parseDouble(this.lat), Double.parseDouble(this.lng));
        mMap.addMarker(new MarkerOptions().position(location).title("Localização"));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 17f);
        mMap.animateCamera(cameraUpdate);
    }
}
