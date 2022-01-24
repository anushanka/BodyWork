package com.example.bodyworks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.bodyworks.Modals.WorkOut;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddWorkoutActivity extends AppCompatActivity {

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

    @BindView(R.id.loginNavigate)
    Button loginNavigate;
    @BindView(R.id.registerNavigate)
    Button registerNavigate;
    @BindView(R.id.scrollView3)
    ScrollView datalayout;
    @BindView(R.id.loginlayout)
    ConstraintLayout loginlayout;


    @BindView(R.id.wNameTxt)
    EditText wNameTxt;
    @BindView(R.id.wDescTxt)
    EditText wDescTxt;
    @BindView(R.id.countTxt)
    EditText countTxt;
    @BindView(R.id.wDurationTxt)
    EditText wDurationTxt;
    @BindView(R.id.addWorkOutBtn)
    Button addWorkOutBtn;


    FirebaseAuth fAuth;
    String userID;
    FirebaseFirestore fStore;

    WorkOut workOut = new WorkOut();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        ButterKnife.bind(this);
        navigate();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = fAuth.getCurrentUser();

        loginNavigate.setOnClickListener(view -> startActivity(new Intent(AddWorkoutActivity.this,LoginActivity.class)));
        registerNavigate.setOnClickListener(view -> startActivity(new Intent(AddWorkoutActivity.this,RegisterActivity.class)));

        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() == null){
            loginlayout.setVisibility(View.VISIBLE);
            datalayout.setVisibility(View.GONE);
        }else{
            loginlayout.setVisibility(View.GONE);
            datalayout.setVisibility(View.VISIBLE);
        }

        addWorkOutBtn.setOnClickListener(view -> {
            final String name = wNameTxt.getText().toString().trim();
            final String duration = wDurationTxt.getText().toString().trim();
            final String count = countTxt.getText().toString().trim();
            final String desc = wDescTxt.getText().toString().trim();

            //validation
            if (TextUtils.isEmpty(name)) {
                wNameTxt.setError("Workout Name is Required.");
                return;
            }

            if (TextUtils.isEmpty(duration)) {
                wDurationTxt.setError("Workout duration is Required.");
                return;
            }
            if (TextUtils.isEmpty(count)) {
                countTxt.setError("Workout Count is Required.");
                return;
            }
            if (TextUtils.isEmpty(desc)) {
                wDescTxt.setError("Workout Description is Required.");
                return;
            }

            userID = fAuth.getCurrentUser().getUid();
            DatabaseReference workoutRef = FirebaseDatabase.getInstance().getReference("User").child(userID).child("WorkOut");
            String key = workoutRef.push().getKey();

            workOut.setCount(count);
            workOut.setName(name);
            workOut.setDescription(desc);
            workOut.setId(key);
            workOut.setDuration(duration);

            workoutRef.child(key).setValue(workOut).addOnSuccessListener(aVoid ->{
                Toast.makeText(this, "New Workout added.", Toast.LENGTH_SHORT).show();
                wDurationTxt.setText("");
                wNameTxt.setText("");
                countTxt.setText("");
                wDescTxt.setText("");
            }).addOnFailureListener(e -> Toast.makeText(this, "Workout add failed!", Toast.LENGTH_SHORT).show());

        });
    }

    private void navigate() {
        profileBtn.setOnClickListener(view -> startActivity(new Intent(AddWorkoutActivity.this,ProfileAcivity.class)));
        homeBtn.setOnClickListener(view -> startActivity(new Intent(AddWorkoutActivity.this,HomeActivity.class)));
        planBtn.setOnClickListener(view -> startActivity(new Intent(AddWorkoutActivity.this,ShaduleActivity.class)));
        notificationBtn.setOnClickListener(view -> startActivity(new Intent(AddWorkoutActivity.this,NotificationActivity.class)));
    }
}