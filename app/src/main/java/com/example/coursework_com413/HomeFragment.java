package com.example.coursework_com413;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private List<Room> roomList;
    private DatabaseReference databaseReference;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.rooms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        roomList = new ArrayList<>();
        adapter = new RoomAdapter(roomList);
        recyclerView.setAdapter(adapter);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance("https://coursework-com413-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Rooms");

        // Listen for data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    roomList.add(room);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        return view;
    }

    // Optional: method to return list of rooms to other fragments
    public List<Room> getRoomList() {
        return roomList;
    }
}
