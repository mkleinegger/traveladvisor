package com.example.traveladvisor;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final TextInputEditText txtEmail = findViewById(R.id.txtEmail);
        final TextInputEditText txtPassword = findViewById(R.id.txtPassword);
        final TextInputEditText txtRepeatPassword = findViewById(R.id.txtRepeatPassword);
        final TextInputEditText txtFirstname = findViewById(R.id.txtFirstname);
        final TextInputEditText txtLastname = findViewById(R.id.txtLastname);
        final Button btnRegister = findViewById(R.id.btnRegister);

        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();
                String repeatPassword = txtRepeatPassword.getText().toString();
                final String firstname = txtFirstname.getText().toString();
                final String lastname = txtLastname.getText().toString();

                boolean error = false;


                if (TextUtils.isEmpty(email)) {
                    txtEmail.setError("Please fill in your email address.");
                    Toast.makeText(getApplicationContext(), "Please fill in your email address.", Toast.LENGTH_SHORT).show();
                    error = true;
                }
                if (TextUtils.isEmpty(password)) {
                    txtPassword.setError("Please fill in your password.");
                    Toast.makeText(getApplicationContext(), "Please fill in your password.", Toast.LENGTH_SHORT).show();
                    error = true;
                }
                if (password.length() < 7) {
                    txtPassword.setError("Your password must be at least 7 characters.");
                    Toast.makeText(getApplicationContext(), "Your password must be at least 7 characters.", Toast.LENGTH_SHORT).show();
                    error = true;
                }
                if (!TextUtils.equals(password, repeatPassword)) {
                    txtRepeatPassword.setError("The passwords do not match.");
                    Toast.makeText(getApplicationContext(), "The passwords do not match.", Toast.LENGTH_SHORT).show();
                    error = true;
                }
                if (TextUtils.isEmpty(firstname)) {
                    txtFirstname.setError("Please fill in your First Name.");
                    Toast.makeText(getApplicationContext(), "Please fill in your first Name.", Toast.LENGTH_SHORT).show();
                    error = true;
                }
                if (TextUtils.isEmpty(lastname)) {
                    txtFirstname.setError("Please fill in your last Name.");
                    Toast.makeText(getApplicationContext(), "Please fill in your last Name.", Toast.LENGTH_SHORT).show();
                    error = true;
                }
                if (!error) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                String userId = firebaseUser.getUid();
                                //database = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                FirebaseFirestore db = FirebaseFirestore.getInstance();


                                HashMap<String, String> newUser = new HashMap<>();
                                newUser.put("uid", userId);
                                newUser.put("email", email);
                                newUser.put("typ", "besucher");
                                newUser.put("vorname", firstname);
                                //newUser.put("token", FirebaseInstanceId.getInstance().getToken());
                                newUser.put("nachname", lastname);

                                db.collection("users").document(userId)
                                        .set(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Benutzer in SQL DB speichern

                                                try {
                                                    User.getInstance().saveToSQLDB();
                                                    firebaseAuth.signInWithEmailAndPassword(email, password);
                                                    startNewIntent();
                                                }
                                                catch(Exception ex) {
                                                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG);
                                                }

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Register not successful: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "Register not successful: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void startNewIntent() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
