package com.example.coursework_com413;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private final List<Room> roomList = new ArrayList<>();
    private RoomsViewModel vm;
    private int sortMode = 0;
    private DatabaseReference databaseReference;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        vm = new ViewModelProvider(requireActivity()).get(RoomsViewModel.class);

        recyclerView = view.findViewById(R.id.rooms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RoomAdapter(roomList, vm.getFavouriteIds().getValue(),
                new RoomAdapter.RoomClickListener() {
                    @Override
                    public void onRoomClick(Room room) {
                        vm.selectRoom(room);

                        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                                requireActivity().findViewById(R.id.bottom_navigation);
                        bottomNav.setSelectedItemId(R.id.nav_map);
                    }

                    @Override
                    public void onFavouriteClick(Room room) {
                        vm.toggleFavourite(room.getId());
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        vm.getFavouriteIds().observe(getViewLifecycleOwner(), favs -> adapter.updateFavourites(favs));

        Spinner spinner = view.findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.room_sort_options,
                android.R.layout.simple_spinner_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spAdapter);

        sortMode = Prefs.getDefaultSort(requireContext());
        spinner.setSelection(sortMode);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                sortMode = position;
                sortAndRefresh();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance("https://coursework-com413-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Rooms");

        // Listen for data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    if (room != null) roomList.add(room);
                }

                vm.setRooms(new ArrayList<>(roomList));
                sortAndRefresh();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        return view;
    }

    private void sortAndRefresh() {
        Collections.sort(roomList, getComparator(sortMode));
        adapter.notifyDataSetChanged();
    }

    private Comparator<Room> getComparator(int mode) {
        switch (mode) {
            case 1: return Comparator.comparingInt(Room::getDesks);
            case 2: return (a, b) -> Integer.compare(b.getDesks(), a.getDesks());
            case 3: return Comparator.comparingInt(Room::getPcs);
            case 4: return (a, b) -> Integer.compare(b.getPcs(), a.getPcs());
            case 0:
            default:
                return (a, b) -> {
                    String n1 = a.getName();
                    String n2 = b.getName();

                    int num1 = extractTrailingNumber(n1);
                    int num2 = extractTrailingNumber(n2);

                    // If both names end in numbers, compare numbers
                    if (num1 != -1 && num2 != -1) {
                        return Integer.compare(num1, num2);
                    }

                    // Otherwise fallback to normal alphabetical
                    if (n1 == null) n1 = "";
                    if (n2 == null) n2 = "";
                    return n1.compareToIgnoreCase(n2);
                };
        }
    }

    private int extractTrailingNumber(String text) {
        if (text == null) return -1;

        int i = text.length() - 1;
        while (i >= 0 && Character.isDigit(text.charAt(i))) {
            i--;
        }

        if (i == text.length() - 1) return -1;

        try {
            return Integer.parseInt(text.substring(i + 1));
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }
    public List<Room> getRoomList() {
        return roomList;
    }
}
