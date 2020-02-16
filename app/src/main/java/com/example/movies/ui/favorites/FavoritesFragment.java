package com.example.movies.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.adapters.MoviesAdapter;
import com.example.movies.models.entities.Movie;
import com.example.movies.utilities.FilesManager;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel favoritesViewModel;

    private void initComponents(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.favorites_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        List<Movie> favoriteMovies = favoritesViewModel.getFavoriteMovies();
        if (!favoriteMovies.isEmpty()) {
            recyclerView.setAdapter(new MoviesAdapter(getActivity(), favoriteMovies, true));
        } else {
            view.findViewById(R.id.favorites_rv).setVisibility(View.GONE);
            view.findViewById(R.id.favorites_tv).setVisibility(View.VISIBLE);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        favoritesViewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        favoritesViewModel.setFilesManager(FilesManager.getInstance());
        initComponents(root);
        return root;
    }

}