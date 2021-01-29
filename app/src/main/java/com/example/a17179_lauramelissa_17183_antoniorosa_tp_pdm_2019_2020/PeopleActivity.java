package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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

public class PeopleActivity extends AppCompatActivity {

    private PeopleAdapter peopleAdapter;
    private List<String> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        //BottomNav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setSelectedItemId(R.id.nav_people);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.people);

        //RecyclerView
        RecyclerView peopleList = findViewById(R.id.recyclerViewPeople);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.peopleAdapter = new PeopleAdapter();
        peopleList.setLayoutManager(layoutManager);
        peopleList.setAdapter(peopleAdapter);

        //Button to create new People and his listener
        FloatingActionButton addButton = findViewById(R.id.addPeopleButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePeopleActivity.class);
            startActivity(intent);
        });

    }

    /**
     * Function called on the start of the activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        refreshData();
    }

    /**
     * Function used to refresh data from firebase into list's used on activity.
     */
    private void refreshData() {
        // After declare instance of Firebase we access into peoples collection
        // and save every document information into a list of people objects.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("peoples")
                .addSnapshotListener(this, (value, error) -> {
                    if (error == null) {
                        List<People> people = value.toObjects(People.class);
                        peopleAdapter.setData(people);
                    }
                });

        // Access again into firebase to get every document id and save it into
        // a list so after we can access to a specific document and take an action on it.
        this.ids = new ArrayList<>();
        db.collection("peoples")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            this.ids.add(document.getId());
                        }
                    }
                });
    }

    public class PeopleAdapter extends RecyclerView.Adapter<PeopleViewHolder> {

        private List<People> data = new ArrayList<>();


        /**
         * Initialize the dataset of the Adapter.
         *
         * @param data List<People> containing the data to populate views to be used
         *             by recyclerView.
         */
        public void setData(List<People> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        // Create new views (invoked by the layout manager)

        /**
         * Function used to create new views
         */
        @NonNull
        @Override
        public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.people_list, parent, false);
            return new PeopleViewHolder(view);
        }

        // Replace the contents of a view
        @Override
        public void onBindViewHolder(@NonNull PeopleViewHolder holder, int position) {
            People people = this.data.get(position);
            holder.bind(people);
        }

        // Return the size of data
        @Override
        public int getItemCount() {
            return this.data.size();
        }
    }

    public class PeopleViewHolder extends RecyclerView.ViewHolder {

        private final ImageView picturePerson;
        private final TextView namePerson;
        private final TextView degreePerson;
        private int positionClicked;

        public PeopleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.picturePerson = itemView.findViewById(R.id.picturePerson);
            this.namePerson = itemView.findViewById(R.id.namePerson);
            this.degreePerson = itemView.findViewById(R.id.degreePerson);

            // Declaring buttonMap and click listener
            Button buttonMap = itemView.findViewById(R.id.buttonMap);
            buttonMap.setOnClickListener(v -> {
                // Get the position of the itemView where the button was clicked
                int position = getAdapterPosition();
                // Get the id of the document associated with this itemView
                String id = getIdDocument(position);

                // Declaring firebase, access to the document and pass the information to the
                // new activity with the intents
                FirebaseFirestore fb = FirebaseFirestore.getInstance();
                DocumentReference docRef = fb.collection("peoples").document(id);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot docSnap = task.getResult();
                        if (docSnap != null) {
                            Intent intent = new Intent(PeopleActivity.this,
                                    ShowLocationActivity.class);
                            intent.putExtra("Lat", docSnap.getString("lat"));
                            intent.putExtra("Lng", docSnap.getString("lng"));
                            intent.putExtra("Title", docSnap.getString("namePerson") + " - " + docSnap.getString("degreePerson"));
                            startActivity(intent);
                        }
                    }
                });
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
                DocumentReference docRef = fb.collection("peoples").document(id);
                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot docSnap = task.getResult();
                        if (docSnap != null) {
                            Intent intent = new Intent(PeopleActivity.this,
                                    EditPeopleActivity.class);
                            intent.putExtra("DocumentID", id);
                            intent.putExtra("Lat", docSnap.getString("lat"));
                            intent.putExtra("Lng", docSnap.getString("lng"));
                            intent.putExtra("Name", docSnap.getString("namePerson"));
                            intent.putExtra("Degree", docSnap.getString("degreePerson"));
                            intent.putExtra("ImgPath", docSnap.getString("imgPath"));
                            startActivity(intent);
                        }
                    }
                });
            });

            // Listener to itemView long click
            itemView.setOnLongClickListener(v -> {
                // Get the position of the itemView clicked
                this.positionClicked = getAdapterPosition();

                // Declare an instance of an alert dialog and customizing it
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PeopleActivity.this);
                alertDialog.setTitle(R.string.deletePeopleTitleAlertDialog);
                alertDialog.setMessage(R.string.deletePeopleMessageAlertDialog);
                alertDialog
                        .setPositiveButton(R.string.positiveButtonDeletePeopleAlertDialog,
                                (dialog, which) -> {
                                    // if the positive button is clicked we call the function
                                    // deleteItem() and pass the position of the itemView clicked
                                    deleteItem(this.positionClicked);
                                    refreshData();
                                    dialog.cancel();
                                });
                alertDialog
                        .setNegativeButton(R.string.negativeButtonDeletePeopleAlertDialog,
                                (dialog, which) -> dialog.cancel());
                alertDialog.show();

                return false;
            });
        }

        public void bind(People people) {
            // Get the img path from the object people
            String imgPath = people.getImgPath();

            // Creation of a new file with the path from the img
            File img = new File(imgPath);

            // Using thread to get a better performance.
            // Block of code responsible for verifying if the img exists,
            // decode the file into bitmap, get the Exif of the image and the orientation.
            // After getting the orientation, we use the Exif to rotate the image and save it
            // into a new Bitmap. After the new Bitmap created, we use the Glide library to
            // introduce it into an ImageView
            if (img.exists()) {
                new Thread(() -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
                    ExifInterface ei = null;
                    try {
                        ei = new ExifInterface(imgPath);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    Bitmap rotatedBitmap;
                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = bitmap;
                    }
                });
            }

            // Glide to load bitmap from thread. It waits and when the
            // resource is ready, load it into imageView
            Glide.with(getApplicationContext()).asBitmap().load(img.getAbsolutePath()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    ImageView imageView = itemView.findViewById(R.id.picturePerson);
                    imageView.setImageBitmap(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
            this.namePerson.setText(people.getNamePerson());
            this.degreePerson.setText(people.getDegreePerson());
        }
    }

    /**
     * Function responsible to get the item click from the Bottom Navigation Bar, use
     * a listener to create a new intent and move the user to a new Activity.
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Intent resultIntent = null;
                switch (item.getItemId()) {
                    case R.id.nav_tasks:
                        resultIntent = new Intent(PeopleActivity.this,
                                MainActivity.class);
                        break;
                    case R.id.nav_map:
                        resultIntent = new Intent(PeopleActivity.this,
                                LocationActivity.class);
                        break;
                }
                if (resultIntent != null) {
                    startActivity(resultIntent);
                    PeopleActivity.this.finish();
                    return true;
                }
                return false;
            };

    /**
     * Function called after listener from alertDialog positive button click.
     * This function is responsible to delete the item from the recyclerView because it deletes
     * the document based on the position from the firebase and the id from the list of ids.
     *
     * @param position of itemView clicked.
     */
    private void deleteItem(int position) {
        String id = this.ids.get(position);
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        fb.collection("peoples")
                .document(id)
                .delete();
        this.ids.remove(position);
    }

    /**
     * Function called to rotate the bitmap based on an angle.
     *
     * @param source bitmap to be rotated.
     * @param angle  to rotate the bitmap.
     * @return the new bitmap to insert into imageView.
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /**
     * Function called to return the id of the document based on the position of itemView clicked.
     *
     * @param position of itemView clicked.
     * @return the id of the document.
     */
    private String getIdDocument(int position) {
        return this.ids.get(position);
    }
}
