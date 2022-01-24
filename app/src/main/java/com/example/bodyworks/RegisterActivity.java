package com.example.bodyworks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.mailTxt)
    EditText mailTxt;
    @BindView(R.id.passTxt)
    EditText passTxt;
    @BindView(R.id.rePassTxt)
    EditText rePassTxt;
    @BindView(R.id.btnSignup)
    Button btnSignup;
    @BindView(R.id.btnGotoSignIn)
    TextView btnGotoSignIn;

    FirebaseAuth fAuth;
    String userID;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        btnGotoSignIn.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this,LoginActivity.class)));
        register();
    }

    private void register() {


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mailTxt.getText().toString().trim();
                final String password = passTxt.getText().toString().trim();
                final String rePass = rePassTxt.getText().toString();


                //validation
                if (TextUtils.isEmpty(email)) {
                    mailTxt.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passTxt.setError("Password is Required.");
                    return;
                }
                if (password.length() < 8) {
                    passTxt.setError("Password 8 character length");
                    return;
                }
                if (TextUtils.isEmpty(rePass)) {
                    rePassTxt.setError("Re enter password..");
                    return;
                }
                if (!rePass.equals(password)) {
                    rePassTxt.setError("Password mismatch");
                    return;
                }


                // register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // send verification link

                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                    userRegister(email,password);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                                }
                            });




                        } else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }
    private void userRegister(String email, String password) {

        userID = fAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userID);

        Map<String, Object> user = new HashMap<>();
        user.put("id",userID);
        user.put("email", email);
        user.put("password",password);
        user.put("name","");
        user.put("age","");


        userRef.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "User create failed !", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                });

    }
}