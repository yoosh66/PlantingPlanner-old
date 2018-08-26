package com.yoonbae.plantingplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.yoonbae.plantingplanner.com.yoonbae.plantingplanner.adapter.MyRecyclerViewAdapter;
import com.yoonbae.plantingplanner.com.yoonbae.plantingplanner.vo.Plant;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private List<Plant> plantList;
    private List<String> keyList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        //BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.action_calendar:
                        intent = new Intent(ListActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_myInfo:
                        intent = new Intent(ListActivity.this, MyInfoActivity.class);
                        startActivity(intent);
                        break;
                }

                return false;
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.main_recyclerView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("users").child("plant").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                plantList = new ArrayList<Plant>();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Plant value = snapshot.getValue(Plant.class);

                    Plant plant = new Plant();
                    plant.setName(value.getName());
                    plant.setKind(value.getKind());
                    plant.setImageName(value.getImageName());
                    plant.setImageUrl(value.getImageUrl());
                    plant.setIntro(value.getIntro());
                    plant.setUid(value.getUid());
                    plant.setUserId(value.getUserId());

                    plantList.add(plant);
                    keyList.add(snapshot.getKey());
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
                MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(plantList, keyList,ListActivity.this);
                recyclerView.setAdapter(myRecyclerViewAdapter);
                myRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
