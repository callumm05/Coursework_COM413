package com.example.coursework_com413;

import android.os.Bundle;

import androidx.core.view.LayoutInflaterFactory;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FavouritesFragment extends Fragment {

    public FavouritesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.fav_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

}