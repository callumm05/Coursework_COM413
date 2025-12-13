package com.example.coursework_com413;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomsViewModel extends ViewModel {

    private final MutableLiveData<List<Room>> rooms = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Room> selectedRoom = new MutableLiveData<>(null);
    private final MutableLiveData<Set<String>> favouriteIds = new MutableLiveData<>(new HashSet<>());

    // ✅ logged-in username (from your login system)
    private String username;

    // ✅ Firebase reference + listener for this user's favourites
    private DatabaseReference favRef;
    private ValueEventListener favListener;

    // ✅ Rooms
    public LiveData<List<Room>> getRooms() { return rooms; }
    public void setRooms(List<Room> list) { rooms.setValue(list); }

    // ✅ Selected room
    public LiveData<Room> getSelectedRoom() { return selectedRoom; }
    public void selectRoom(Room room) { selectedRoom.setValue(room); }

    // ✅ Favourites
    public LiveData<Set<String>> getFavouriteIds() { return favouriteIds; }

    // ✅ Call this after login / in MenuActivity
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Listens to: Users/<username>/favourites
     * and keeps favouriteIds LiveData in sync.
     */
    public void startFavouritesListener() {
        if (username == null || username.trim().isEmpty()) return;

        // avoid double listeners if called twice
        stopFavouritesListener();

        favRef = FirebaseDatabase
                .getInstance("https://coursework-com413-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
                .child(username)
                .child("favourites");

        favListener = favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    set.add(child.getKey()); // roomId
                }
                favouriteIds.setValue(set);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void stopFavouritesListener() {
        if (favRef != null && favListener != null) {
            favRef.removeEventListener(favListener);
            favListener = null;
        }
    }

    /**
     * Writes to: Users/<username>/favourites/<roomId>
     */
    public void toggleFavourite(String roomId) {
        if (username == null || username.trim().isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://coursework-com413-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
                .child(username)
                .child("favourites")
                .child(roomId);

        if (isFavourite(roomId)) {
            ref.removeValue();
        } else {
            ref.setValue(true);
        }
    }

    public boolean isFavourite(String roomId) {
        Set<String> current = favouriteIds.getValue();
        return current != null && current.contains(roomId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopFavouritesListener();
    }
}
