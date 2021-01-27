package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String lat, lng, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Obtain the information passed from the activity that called this Map activity to show
        // the marker
        Bundle extras = getIntent().getExtras();
        this.lat = extras.getString("Lat");
        this.lng = extras.getString("Lng");
        this.title = extras.getString("Title");
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

        // Add a marker in the location of the itemView clicked and move the camera with zoom on it
        LatLng location = new LatLng(Double.parseDouble(this.lat), Double.parseDouble(this.lng));
        mMap.addMarker(new MarkerOptions().position(location).title(this.title));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 17f);
        mMap.animateCamera(cameraUpdate);
    }
}