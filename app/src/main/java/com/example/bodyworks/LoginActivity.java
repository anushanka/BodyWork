package com.example.bodyworks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.mailTxt)
    EditText mailTxt;
    @BindView(R.id.passTxt)
    EditText passTxt;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnGoSignUp)
    TextView btnGoSignUp;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        fAuth = FirebaseAuth.getInstance();
        btnGoSignUp.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));

        btnSignIn.setOnClickListener(view -> {
            final String email = mailTxt.getText().toString().trim();
            final String password = passTxt.getText().toString().trim();

            //validation
            if (TextUtils.isEmpty(email)) {
                mailTxt.setError("Email is Required.");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passTxt.setError("Password is Required.");
                return;
            }

            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);

                }

            }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "User name or password incorrect ! " + e, Toast.LENGTH_SHORT).show());
        });
    }
}