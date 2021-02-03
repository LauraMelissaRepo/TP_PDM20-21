package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.database.TaskDatabase;
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

/**
 * This class and those derived from it, implement the requirements 2, 3 and 6.
 */
public class LocationActivity extends AppCompatActivity {

    private LocationAdapter locationAdapter;
    private List<String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        //BottomNav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setSelectedItemId(R.id.nav_map);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.locations);


        RecyclerView locationsList = findViewById(R.id.recyclerViewLocation);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.locationAdapter = new LocationAdapter();

        locationsList.setLayoutManager(layoutManager);
        locationsList.setAdapter(this.locationAdapter);

        //listener to go to the activity that creates a new location
        FloatingActionButton createLocationButton = findViewById(R.id.create_location_btn);
        createLocationButton.setOnClickListener(v -> {
            Intent intent = new Intent(LocationActivity.this, CreateLocationActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Function called on the start of the activity.
     */
    @Override
    protected void onStart(){
        super.onStart();
        refreshData();
    }

    /**
     * Function used to refresh data from database into list's used on activity.
     */
    private void refreshData() {
        // After declare instance of Firebase we access into locations collection
        // and save every document information into a list of people objects.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations")
                .addSnapshotListener(this, (value, error) -> {
                    if(error == null){
                        List<Location> location = value.toObjects(Location.class);
                        locationAdapter.setData(location);
                    }
                });

        // Access again into firebase to get every document id and save it into
        // a list so after we can access to a specific document and take an action on it.
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

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param data List<Location> containing the data to populate views to be used
         *             by recyclerView.
         */
        public void setData(List<Location> data){
            this.data = data;
            notifyDataSetChanged();
        }

        /**
         * Function used to create new views
         */
        @NonNull
        @Override
        public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.location_list, parent, false);
            return  new LocationViewHolder(view);
        }

        // Replace the contents of a view
        @Override
        public void onBindViewHolder(@NonNull LocationViewHolder holder, int position){
            Location location = this.data.get(position);
            holder.bind(location);
        }

        // Return the size of data
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

            // Declaring buttonMap and click listener
            Button buttonMap = itemView.findViewById(R.id.buttonMapLocationList);
            buttonMap.setOnClickListener(v -> {
                // Get the position of the itemView where the button was clicked
                int position = getAdapterPosition();
                // Get the id of the document associated with this itemView
                String id = getIdDocument(position);

                // Declaring firebase, access to the document and pass the information to the
                // new activity with the intents
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

            // Listener to itemView click
            itemView.setOnLongClickListener(v -> {
                // Get the position of the itemView clicked
                this.positionClicked = getAdapterPosition();

                // Declare an instance of an alert dialog and customizing it
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

            // Listener to itemView click
            itemView.setOnClickListener(v -> {
                // Get the position of the itemView clicked
                int position = getAdapterPosition();
                // Get the id of the document associated with this itemView
                String id = getIdDocument(position);

                // Declaring firebase, access to the document and pass the information to the
                // new activity with the intents
                FirebaseFirestore fb = FirebaseFirestore.getInstance();
                DocumentReference docRef = fb.collection("locations").document(id);
                docRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot docSnap = task.getResult();
                        if (docSnap != null){
                            Intent intent = new Intent(LocationActivity.this,
                                EditLocationActivity.class);
                            intent.putExtra("DocumentID", id);
                            intent.putExtra("LocationName", docSnap.getString("locationDescription"));
                            intent.putExtra("Lat", docSnap.getString("lat"));
                            intent.putExtra("Lng", docSnap.getString("lng"));
                            startActivity(intent);
                        }
                    }
                });
            });
        }

        public void bind(Location location){
            this.locationDescription.setText(location.getLocationDescription());
        }
    }

    /**
     * Function called after listener from alertDialog positive button click.
     * This function is responsible to delete the item from the recyclerView because it deletes
     * the document based on the position from the firebase and the id from the list of ids.
     * @param position of itemView clicked.
     */
    private void deleteItem(int position){
        String id = this.ids.get(position);
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        fb.collection("locations")
                .document(id)
                .delete();
        this.ids.remove(position);
    }

    /**
     * Function called to return the id of the document based on the position of itemView clicked.
     * @param position of itemView clicked.
     * @return the id of the document.
     */
    private String getIdDocument(int position){
        return this.ids.get(position);
    }

    /**
     * Function responsible to get the item click from the Bottom Navigation Bar, use
     * a listener to create a new intent and move the user to a new Activity.
     */
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
