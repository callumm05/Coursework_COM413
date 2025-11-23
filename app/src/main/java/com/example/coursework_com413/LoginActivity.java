package com.example.coursework_com413;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTxtUser, editTxtPass;
    private TextInputLayout layoutUser, layoutPassword;
    private Button btnLogin;
    private TextView txtForgotPass;
    private TextView txtRegister;
    private DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usersRef = FirebaseDatabase.getInstance("https://coursework-com413-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users");

        editTxtUser = findViewById(R.id.editTxtUser);
        editTxtPass = findViewById(R.id.editTxtPass);
        layoutUser = findViewById(R.id.layoutUser);
        layoutPassword = findViewById(R.id.layoutPassword);
        txtForgotPass = findViewById(R.id.txtViewForgotPass);
        txtRegister = findViewById(R.id.txtViewRegister);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {
            boolean valid = true;

            String username = editTxtUser.getText().toString().trim();
            String password = editTxtPass.getText().toString().trim();

            layoutUser.setError(null);
            layoutPassword.setError(null);

            if (TextUtils.isEmpty(username)) {
                layoutUser.setError("Username cannot be empty.");
                valid = false;
            }

            if (TextUtils.isEmpty(password)) {
                layoutPassword.setError("Password cannot be empty.");
                valid = false;
            }

            if (!valid) return;

            usersRef.child(username).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (!snapshot.exists()) {
                        layoutUser.setError("Username does not exist.");
                    }
                    else {
                        String storedPassword = snapshot.getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            layoutPassword.setError("Incorrect password.");
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        txtForgotPass.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Forgot Password.");

            final EditText input = new EditText(LoginActivity.this);
            input.setHint("Enter your username.");
            builder.setView(input);

            builder.setPositiveButton("Retrieve", (dialog, which) -> {
                String username = input.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(LoginActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                usersRef.child(username).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (!snapshot.exists()) {
                            Toast.makeText(LoginActivity.this, "Username not found.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String storedPassword = snapshot.getValue(String.class);
                            Toast.makeText(LoginActivity.this, "Your password: " + storedPassword, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.show();
        });

        txtRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}