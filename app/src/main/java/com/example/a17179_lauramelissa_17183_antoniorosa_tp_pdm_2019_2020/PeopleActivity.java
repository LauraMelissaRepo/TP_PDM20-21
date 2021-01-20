package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.People;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends AppCompatActivity {

    private PeopleAdapter peopleAdapter;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("peoples")
                .addSnapshotListener(this, (value, error) -> {
                    if(error == null){
                        List<People> people = value.toObjects(People.class);
                        String string = "" + people.size();
                        Toast.makeText(getApplicationContext(), string + " Existe e chegou", Toast.LENGTH_LONG).show();
                        peopleAdapter.setData(people);
                    }else{
                        Toast.makeText(getApplicationContext(), "Não há nada", Toast.LENGTH_LONG).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_list, parent, false);
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
        private People people;

        public PeopleViewHolder(@NonNull View itemView){
            super(itemView);
            this.picturePerson = itemView.findViewById(R.id.picturePerson);
            this.namePerson = itemView.findViewById(R.id.namePerson);
            this.degreePerson = itemView.findViewById(R.id.degreePerson);
            Button buttonMap = itemView.findViewById(R.id.buttonMap);

            buttonMap.setOnClickListener(v -> {
                String string = "ButtonMap clicked: " + people.getId();
                Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
            });
        }

        public void bind(People people){
            this.people = people;
            String imgPath = people.getImgPath();

            File img = new File(imgPath);
            if (img.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
                this.picturePerson.setImageBitmap(bitmap);
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
                        resultIntent = new Intent(PeopleActivity.this, MainActivity.class);
                        break;
                    case R.id.nav_map:
                        resultIntent = new Intent(PeopleActivity.this, LocationActivity.class);
                        break;
                }
                if (resultIntent != null){
                    startActivity(resultIntent);
                    PeopleActivity.this.finish();
                    return true;
                }
                return false;
            };
}
