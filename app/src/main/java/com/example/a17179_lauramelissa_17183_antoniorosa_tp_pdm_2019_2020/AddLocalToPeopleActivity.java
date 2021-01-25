package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import android.content.Context;
import android.widget.Button;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class AddLocalToPeopleActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentLatLng;
    private Marker lastMarker;
    private String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_local_to_contact);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button moveCamera = findViewById(R.id.moveCamera);
        moveCamera.setOnClickListener(v -> moveCameraCurrentLocation());
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

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        addCurrentLocationMarker();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        mMap.setOnMapClickListener(latLng -> {
            if (this.lastMarker != null) {
                this.lastMarker.remove();
            }
            this.lastMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            this.lat = "" + this.lastMarker.getPosition().latitude;
            this.lng = "" + this.lastMarker.getPosition().longitude;
        });

        Button saveButton = findViewById(R.id.saveLocation);
        saveButton.setOnClickListener(v -> {
            if (this.lat == null && this.lng == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(AddLocalToPeopleActivity.this);

                builder.setTitle(R.string.errorCreatePeopleTitleAlertDialog);
                builder.setMessage(R.string.errorCreateLocationMessage);
                builder.setPositiveButton(R.string.errorCreatePeoplePositiveButtonAlertDialog,
                        (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else{
                Intent intent = getIntent();
                intent.putExtra("lastMarkerLat", this.lat);
                intent.putExtra("lastMarkerLng", this.lng);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddLocalToPeopleActivity.this);
        builder.setTitle(R.string.showSettingDialogTitle);
        builder.setMessage(R.string.showSettingsDialogMessage);
        builder.setPositiveButton(R.string.showSettingsDialogPositiveButton, (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(R.string.showSettingsDialogNegativeButton,
                (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_my_location_24);
        vectorDrawable.setBounds(0,
                0,
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getMinimumHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void addCurrentLocationMarker() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.
                getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            this.currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(this.currentLatLng)
                    .title(getString(R.string.markerTitleAtualLocation))
                    .icon(bitmapDescriptorFromVector(getApplicationContext()
                    )));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(this.currentLatLng, 17f);
            mMap.animateCamera(cameraUpdate);
        });
    }

    private void moveCameraCurrentLocation() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(this.currentLatLng, 17f);
        mMap.animateCamera(cameraUpdate);
    }
}
