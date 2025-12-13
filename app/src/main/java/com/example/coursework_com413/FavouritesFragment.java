package com.example.coursework_com413;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.LayoutInflaterFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private final List<Room> favRooms = new ArrayList<>();
    private RoomAdapter adapter;
    private RoomsViewModel vm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        vm = new ViewModelProvider(requireActivity()).get(RoomsViewModel.class);

        RecyclerView rv = view.findViewById(R.id.fav_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RoomAdapter(favRooms, vm.getFavouriteIds().getValue(), new RoomAdapter.RoomClickListener() {
            @Override
            public void onRoomClick(Room room) {
                vm.selectRoom(room);
                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                bottomNav.setSelectedItemId(R.id.nav_map);
            }

            @Override
            public void onFavouriteClick(Room room) {
                vm.toggleFavourite(room.getId());
            }
        });

        rv.setAdapter(adapter);

        // Update when rooms OR favourites change
        vm.getRooms().observe(getViewLifecycleOwner(), rooms -> rebuildFavList());
        vm.getFavouriteIds().observe(getViewLifecycleOwner(), favs -> {
            adapter.updateFavourites(favs);
            rebuildFavList();
        });

        return view;
    }

    private void rebuildFavList() {
        List<Room> rooms = vm.getRooms().getValue();
        java.util.Set<String> favs = vm.getFavouriteIds().getValue();
        if (rooms == null || favs == null) return;

        favRooms.clear();
        for (Room r : rooms) {
            if (favs.contains(r.getId())) favRooms.add(r);
        }
        adapter.notifyDataSetChanged();
    }
}
