package com.example.coursework_com413;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends Fragment {

    private TextView txtUsername;
    private Button btnLogout, btnChangePassword;
    private Spinner spinnerDefaultSort;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        txtUsername = view.findViewById(R.id.txt_username);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        spinnerDefaultSort = view.findViewById(R.id.spinner_default_sort);

        String username = SessionManager.getUsername(requireContext());
        txtUsername.setText("Logged in as: " + (username == null ? "Unknown" : username));

        // Spinner options (reuse your existing array if you want)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.room_sort_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDefaultSort.setAdapter(adapter);

        // Load saved default sort
        int savedSort = Prefs.getDefaultSort(requireContext());
        spinnerDefaultSort.setSelection(savedSort);

        // Save when changed
        spinnerDefaultSort.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                Prefs.setDefaultSort(requireContext(), position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        btnLogout.setOnClickListener(v -> {
            SessionManager.clear(requireContext());

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog(username));

        return view;
    }

    private void showChangePasswordDialog(String username) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(requireContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText input = new EditText(requireContext());
        input.setHint("Enter new password");

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newPass = input.getText().toString().trim();
                    if (TextUtils.isEmpty(newPass)) {
                        Toast.makeText(requireContext(), "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference ref = FirebaseDatabase
                            .getInstance("https://coursework-com413-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Users")
                            .child(username)
                            .child("password");

                    ref.setValue(newPass).addOnSuccessListener(aVoid ->
                            Toast.makeText(requireContext(), "Password updated!", Toast.LENGTH_SHORT).show()
                    ).addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
