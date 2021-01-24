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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setSelectedItemId(R.id.nav_people);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.people);

        RecyclerView peopleList = findViewById(R.id.recyclerViewPeople);
        FloatingActionButton addButton = findViewById(R.id.addPeopleButton);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.peopleAdapter = new PeopleAdapter();

        peopleList.setLayoutManager(layoutManager);
        peopleList.setAdapter(peopleAdapter);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePeopleActivity.class);
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
        db.collection("peoples")
                .addSnapshotListener(this, (value, error) -> {
                    if(error == null){
                        List<People> people = value.toObjects(People.class);
                        peopleAdapter.setData(people);
                    }
                });

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

    public class PeopleAdapter extends RecyclerView.Adapter<PeopleViewHolder>{

        private List<People> data = new ArrayList<>();

        public void setData(List<People> data){
            this.data = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.people_list, parent, false);
            return  new PeopleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PeopleViewHolder holder, int position){
            People people = this.data.get(position);
            holder.bind(people);
        }

        @Override
        public int getItemCount(){
            return this.data.size();
        }
    }

    public class PeopleViewHolder extends RecyclerView.ViewHolder{

        private final ImageView picturePerson;
        private final TextView namePerson;
        private final TextView degreePerson;
        private int positionClicked;

        public PeopleViewHolder(@NonNull View itemView){
            super(itemView);
            this.picturePerson = itemView.findViewById(R.id.picturePerson);
            this.namePerson = itemView.findViewById(R.id.name_person);
            this.degreePerson = itemView.findViewById(R.id.degreePerson);
            Button buttonMap = itemView.findViewById(R.id.button_map);

            buttonMap.setOnClickListener(v -> {
                int position = getAdapterPosition();
                String id = getIdDocument(position);

                FirebaseFirestore fb = FirebaseFirestore.getInstance();
                DocumentReference docRef = fb.collection("peoples").document(id);
                docRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot docSnap = task.getResult();
                        if (docSnap != null){
                            Intent intent = new Intent(PeopleActivity.this,
                                    ShowLocationActivity.class);
                            intent.putExtra("Lat", docSnap.getString("lat"));
                            intent.putExtra("Lng", docSnap.getString("lng"));
                            intent.putExtra("Name", docSnap.getString("namePerson"));
                            intent.putExtra("Degree", docSnap.getString("degreePerson"));
                            startActivity(intent);
                        }
                    }
                });
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                String id = getIdDocument(position);

                FirebaseFirestore fb = FirebaseFirestore.getInstance();
                DocumentReference docRef = fb.collection("peoples").document(id);
                docRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot docSnap = task.getResult();
                        if (docSnap != null){
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

            itemView.setOnLongClickListener(v -> {
                this.positionClicked = getAdapterPosition();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PeopleActivity.this);
                alertDialog.setTitle(R.string.deletePeopleTitleAlertDialog);
                alertDialog.setMessage(R.string.deletePeopleMessageAlertDialog);
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

        public void bind(People people){
            String imgPath = people.getImgPath();

            File img = new File(imgPath);
            if (img.exists()){
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
                switch(orientation) {

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


                this.picturePerson.setImageBitmap(rotatedBitmap);
            }
            this.namePerson.setText(people.getNamePerson());
            this.degreePerson.setText(people.getDegreePerson());
        }
    }

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
                if (resultIntent != null){
                    startActivity(resultIntent);
                    PeopleActivity.this.finish();
                    return true;
                }
                return false;
            };

    private void deleteItem(int position){
        String id = this.ids.get(position);
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        fb.collection("peoples")
                .document(id)
                .delete();
        this.ids.remove(position);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private String getIdDocument(int position){
        return this.ids.get(position);
    }
}
