package com.example.bodyworks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileAcivity extends AppCompatActivity {

    @BindView(R.id.loginNavigate)
    Button loginNavigate;
    @BindView(R.id.registerNavigate)
    Button registerNavigate;
    @BindView(R.id.constraintLayout6)
    ConstraintLayout datalayout;
    @BindView(R.id.loginlayout)
    ConstraintLayout loginlayout;
    @BindView(R.id.deleteBtn)
    ImageView deleteBtn;
    @BindView(R.id.editBtn)
    ImageView editBtn;
    @BindView(R.id.logOutBtn)
    ImageView logOutBtn;
    @BindView(R.id.nameTxt)
    EditText nameTxt;
    @BindView(R.id.ageTxt)
    EditText ageTxt;
    @BindView(R.id.saveBtn)
    Button saveBtn;


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

    FirebaseAuth fAuth;
    String userID;
    FirebaseFirestore fStore;

    private  String mail,name,age,password,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_acivity);

        ButterKnife.bind(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = fAuth.getCurrentUser();


        loginNavigate.setOnClickListener(view -> startActivity(new Intent(ProfileAcivity.this,LoginActivity.class)));
        registerNavigate.setOnClickListener(view -> startActivity(new Intent(ProfileAcivity.this,RegisterActivity.class)));

        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() == null){
           loginlayout.setVisibility(View.VISIBLE);
           datalayout.setVisibility(View.GONE);
        }else{
            loginlayout.setVisibility(View.GONE);
            datalayout.setVisibility(View.VISIBLE);
            loadUserFromFireBase(firebaseUser);
        }

        navigate();


        logOutBtn.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Sign out")
                    .setMessage("Do you really want to sign out ?")
                    .setNegativeButton("NO", (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton("YES", (dialog12, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(this,HomeActivity.class));
                        dialog12.dismiss();
                    }).create();
            dialog.show();
        });

        deleteBtn.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Do you really want to delete account ?")
                    .setNegativeButton("NO", (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton("DELETE", (dialog12, which) -> {

                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d("TAG", "Account deleted " + userID);
                                }
                                startActivity(new Intent(ProfileAcivity.this,LoginActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "Account delete fail  " + e);
                            }
                        });

                        dialog12.dismiss();
                    }).create();
            dialog.show();
        });

        editBtn.setOnClickListener(view -> {
                nameTxt.setEnabled(true);
                ageTxt.setEnabled(true);
        });

        saveBtn.setOnClickListener(view -> {
            nameTxt.setEnabled(false);
            ageTxt.setEnabled(false);

            final String name = nameTxt.getText().toString().trim();
            final String age = ageTxt.getText().toString().trim();


            //validation
            if (TextUtils.isEmpty(name)) {
                nameTxt.setError("Name is Required.");
                return;
            }
            if (TextUtils.isEmpty(age)) {
                ageTxt.setError("Age is Required.");
                return;
            }

            userID = fAuth.getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userID);

            Map<String, Object> user = new HashMap<>();
            user.put("id",userID);
            user.put("email", mail);
            user.put("password",password);
            user.put("name", name);
            user.put("age",age);


            userRef.setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Profile update failed !", Toast.LENGTH_SHORT).show();
                    });
        });

    }
    private void loadUserFromFireBase(FirebaseUser firebaseUser) {

        userID = firebaseUser.getUid();
        FirebaseDatabase.getInstance().getReference("User")
                .child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    mail = snapshot.child("email").getValue().toString();
                    name = snapshot.child("name").getValue().toString();
                    userId = snapshot.child("id").getValue().toString();
                    password = snapshot.child("password").getValue().toString();
                    age = snapshot.child("age").getValue().toString();


                    nameTxt.setText(name);
                    ageTxt.setText(age);

                    Log.d("TAG", "Got user profile data of " + userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileAcivity.this, "User not found!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void navigate() {
        homeBtn.setOnClickListener(view -> startActivity(new Intent(ProfileAcivity.this,HomeActivity.class)));
        addBtn.setOnClickListener(view -> startActivity(new Intent(ProfileAcivity.this,AddWorkoutActivity.class)));
        planBtn.setOnClickListener(view -> startActivity(new Intent(ProfileAcivity.this,ShaduleActivity.class)));
        notificationBtn.setOnClickListener(view -> startActivity(new Intent(ProfileAcivity.this,NotificationActivity.class)));
    }
}