package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.Location;
import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.People;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LocationAdapter locationAdapter;
    private List<String> ids;

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


        RecyclerView locationsList = findViewById(R.id.recyclerViewLocation);
        FloatingActionButton createLocationButton = findViewById(R.id.create_location_btn);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.locationAdapter = new LocationAdapter();

        locationsList.setLayoutManager(layoutManager);
        locationsList.setAdapter(this.locationAdapter);

        createLocationButton.setOnClickListener(v -> {
            Intent intent = new Intent(LocationActivity.this, CreateLocationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        refreshData();
    }

    private void refreshData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations")
                .addSnapshotListener(this, (value, error) -> {
                    if(error == null){
                        List<Location> location = value.toObjects(Location.class);
                        locationAdapter.setData(location);
                    }
                });

        this.ids = new ArrayList<>();
        db.collection("locations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            this.ids.add(document.getId());
                        }
                    }
                });
    }

    public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder>{

        private List<Location> data = new ArrayList<>();

        public void setData(List<Location> data){
            this.data = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.location_list, parent, false);
            return  new LocationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LocationViewHolder holder, int position){
            Location location = this.data.get(position);
            holder.bind(location);
        }

        @Override
        public int getItemCount(){
            return this.data.size();
        }
    }


    public class LocationViewHolder extends RecyclerView.ViewHolder{

        private final TextView locationDescription;
        private int positionClicked;

        public LocationViewHolder(@NonNull View itemView){
            super(itemView);
            this.locationDescription = itemView.findViewById(R.id.location_name);

            Button buttonMap = itemView.findViewById(R.id.buttonMapLocationList);
            buttonMap.setOnClickListener(v -> {
                int position = getAdapterPosition();
                String id = getIdDocument(position);

                FirebaseFirestore fb = FirebaseFirestore.getInstance();
                DocumentReference docRef = fb.collection("locations").document(id);
                docRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot docSnap = task.getResult();
                        if (docSnap != null){
                            Intent intent = new Intent(LocationActivity.this,
                                    ShowLocationActivity.class);
                            intent.putExtra("Lat", docSnap.getString("lat"));
                            intent.putExtra("Lng", docSnap.getString("lng"));
                            intent.putExtra("Title", docSnap.getString("locationDescription"));
                            startActivity(intent);
                        }
                    }
                });
            });

            itemView.setOnLongClickListener(v -> {
                this.positionClicked = getAdapterPosition();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationActivity.this);
                alertDialog.setTitle(R.string.deleteLocationTitleAlertDialog);
                alertDialog.setMessage(R.string.deleteLocationMessageAlertDialog);
                alertDialog
                        .setPositiveButton(R.string.positiveButtonDeletePeopleAlertDialog,
                                (dialog, which) -> {
                                    deleteItem(this.positionClicked);
                                    dialog.cancel();
                                });
                alertDialog
                        .setNegativeButton(R.string.negativeButtonDeletePeopleAlertDialog,
                                (dialog, which) -> dialog.cancel());
                alertDialog.show();

                return false;
            });
        }

        public void bind(Location location){
            this.locationDescription.setText(location.getLocationDescription());
        }
    }

    private void deleteItem(int position){
        String id = this.ids.get(position);
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        fb.collection("locations")
                .document(id)
                .delete();
        this.ids.remove(position);
    }

    private String getIdDocument(int position){
        return this.ids.get(position);
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
