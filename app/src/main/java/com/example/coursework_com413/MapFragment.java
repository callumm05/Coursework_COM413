package com.example.coursework_com413;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private RoomsViewModel vm;
    private final java.util.Map<String, com.google.android.gms.maps.model.Marker> markers = new java.util.HashMap<>();

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        vm = new ViewModelProvider(requireActivity()).get(RoomsViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        vm.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            if (mMap == null) return;
            drawMarkers(rooms);
        });

        vm.getSelectedRoom().observe(getViewLifecycleOwner(), room -> {
            if (mMap == null || room == null) return;
            focusRoom(room);
        });

        databaseReference = FirebaseDatabase.getInstance("https://coursework-com413-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Rooms");

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Center map on Ulster University campus
        LatLng campus = new LatLng(55.0062, -7.3236);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campus, 16f));

        List<Room> rooms = vm.getRooms().getValue();
        if (rooms != null) drawMarkers(rooms);

        Room selected = vm.getSelectedRoom().getValue();
        if (selected != null) focusRoom(selected);

        // Load rooms from Firebase and add markers
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMap.clear(); // Clear existing markers
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    if (room != null) {
                        LatLng position = new LatLng(room.getLatitude(), room.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(room.getName())
                                .snippet(room.getDescription()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void drawMarkers(List<Room> rooms) {
        mMap.clear();
        markers.clear();

        for (Room r  : rooms) {
            LatLng pos = new LatLng(r.getLatitude(), r.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(r.getName()).snippet(r.getDescription()));
            markers.put(r.getId(), marker);
        }
    }

    private void focusRoom(Room room) {
        LatLng pos = new LatLng(room.getLatitude(), room.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18f));

        Marker marker = markers.get(room.getId());
        if (marker != null) marker.showInfoWindow();
    }
}
