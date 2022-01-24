package com.example.bodyworks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bodyworks.Modals.WorkOut;
import com.example.bodyworks.adapter.MyWorkOutAdapter;
import com.example.bodyworks.listner.IWorkoutListner;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShaduleActivity extends AppCompatActivity implements IWorkoutListner {
    @BindView(R.id.homeBtn)
    ImageView homeBtn;
    @BindView(R.id.addBtn)
    ImageView addBtn;
    @BindView(R.id.planBtn)
    ImageView planBtn;
    @BindView(R.id.profileBtn)
    ImageView profileBtn;
    @BindView(R.id.notificationBtn)
    ImageView notificationBtn;

    @BindView(R.id.workout_recycle)
    RecyclerView workout_recycle;

    FirebaseAuth fAuth;
    String userID;
    FirebaseFirestore fStore;
    IWorkoutListner iWorkoutListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadule);

        ButterKnife.bind(this);
        navigate();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = fAuth.getCurrentUser();

        iWorkoutListner =this;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        workout_recycle.setLayoutManager(linearLayoutManager);
        workout_recycle.addItemDecoration(new DividerItemDecoration(this,linearLayoutManager.getOrientation()));
        loadFromFirebase();

    }

    private void loadFromFirebase() {
        List<WorkOut> workoutList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("User")
                .child(fAuth.getCurrentUser().getUid()).child("WorkOut").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        WorkOut workOut = dataSnapshot.getValue(WorkOut.class);
                        workOut.setId(dataSnapshot.getKey());
                        workoutList.add(workOut);
                    }
                    iWorkoutListner.onLoadSuccess(workoutList,fAuth.getCurrentUser().getUid());
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "workout list empty !",
                            Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                iWorkoutListner.onLoadFail(error.getMessage());
            }
        });
    }

    private void navigate() {
        profileBtn.setOnClickListener(view -> startActivity(new Intent(ShaduleActivity.this,ProfileAcivity.class)));
        homeBtn.setOnClickListener(view -> startActivity(new Intent(ShaduleActivity.this,HomeActivity.class)));
        addBtn.setOnClickListener(view -> startActivity(new Intent(ShaduleActivity.this,AddWorkoutActivity.class)));
        notificationBtn.setOnClickListener(view -> startActivity(new Intent(ShaduleActivity.this,NotificationActivity.class)));
    }

    @Override
    public void onLoadSuccess(List<WorkOut> workout,String Uid) {
        MyWorkOutAdapter adapter = new MyWorkOutAdapter(this,workout,this,Uid);
        workout_recycle.setAdapter(adapter);
    }

    @Override
    public void onLoadFail(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}