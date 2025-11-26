package com.example.coursework_com413;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTxtUser, editTxtPass, editTxtConfirmPass;
    private TextInputLayout layoutUserR, layoutPassR, layoutConfirmPassR;
    private Button btnRegister;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usersRef = FirebaseDatabase.getInstance("https://coursework-com413-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users");

        editTxtUser = findViewById(R.id.editTxtUserR);
        editTxtPass = findViewById(R.id.editTxtPassR);
        editTxtConfirmPass = findViewById(R.id.editTxtConfirmPassR);

        layoutUserR = findViewById(R.id.layoutUserR);
        layoutPassR = findViewById(R.id.layoutPasswordR);
        layoutConfirmPassR = findViewById(R.id.layoutConfirmPasswordR);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> {

            boolean valid = true;
            String username = editTxtUser.getText().toString().trim();
            String password = editTxtPass.getText().toString().trim();
            String confirmPassword = editTxtConfirmPass.getText().toString().trim();

            layoutUserR.setError(null);
            layoutPassR.setError(null);
            layoutConfirmPassR.setError(null);

            if (TextUtils.isEmpty(username)) {
                layoutUserR.setError("Username cannot be empty.");
                valid = false;
            }

            if (TextUtils.isEmpty(password)) {
                layoutPassR.setError("Password cannot be empty.");
                valid = false;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                layoutConfirmPassR.setError("Confirm Password cannot be empty.");
                valid = false;
            }

            if (!password.equals(confirmPassword)) {
                layoutConfirmPassR.setError("Passwords do not match.");
                valid = false;
            }
            if (!valid) return;

            usersRef.child(username).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        layoutUserR.setError("Username already exists.");
                    }
                    else {
                        usersRef.child(username).setValue(password)
                                .addOnCompleteListener(saveTask -> {
                                    if (saveTask.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Error: " + saveTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                        });
                    }
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}