package com.example.coursework_com413;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    public interface RoomClickListener {
        void onRoomClick(Room room);
        void onFavouriteClick(Room room);
    }

    private List<Room> rooms;
    private Set<String> favouriteIds;
    private final RoomClickListener listener;

    public RoomAdapter(List<Room> rooms, Set<String> favouriteIds, RoomClickListener listener) {
        this.rooms = rooms;
        this.favouriteIds = favouriteIds;
        this.listener = listener;
    }

    public void updateFavourites(Set<String> newFavs) {
        this.favouriteIds = newFavs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);

        holder.roomName.setText(room.getName());
        holder.roomDescription.setText(room.getDescription());

        boolean fav = favouriteIds != null && favouriteIds.contains(room.getId());
        holder.favIcon.setImageResource( fav ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);

        holder.itemView.setOnClickListener(v -> listener.onRoomClick(room));
        holder.favIcon.setOnClickListener(v -> listener.onFavouriteClick(room));
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomName, roomDescription;
        ImageView favIcon;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.room_name);
            roomDescription = itemView.findViewById(R.id.room_description);
            favIcon = itemView.findViewById(R.id.room_fav_icon);
        }
    }
}
