package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data.People;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreatePeopleActivity extends AppCompatActivity {

    private EditText namePersonCreate;
    private EditText degreePersonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_people);

        this.namePersonCreate = findViewById(R.id.namePersonCreateEdit);
        this.degreePersonCreate = findViewById(R.id.degreePersonCreateEdit);

        Button addButton = findViewById(R.id.createPeopleButton);

        addButton.setOnClickListener(v -> {
            String name = this.namePersonCreate.getText().toString();
            String degree = this.degreePersonCreate.getText().toString();
            String path = "path";

            People people = new People(0, name, degree);

            Toast.makeText(getApplicationContext(), "Chegou", Toast.LENGTH_LONG).show();
            FirebaseFirestore
                    .getInstance()
                    .collection("peoples")
                    .add(people)
                    .addOnSuccessListener(this, documentReference -> {
                        finish();
                    } );
        });
    }
}