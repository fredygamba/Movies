package com.example.movies.ui.favorites;

import androidx.lifecycle.ViewModel;

import com.example.movies.models.entities.Movie;
import com.example.movies.utilities.FilesManager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesViewModel extends ViewModel {

    private FilesManager filesManager;

    public FavoritesViewModel() {
    }

    public List<Movie> getFavoriteMovies() {
        List<Movie> movies = filesManager.readMovies();
        return movies != null ? movies : new ArrayList<Movie>();
    }

    public void setFilesManager(FilesManager filesManager) {
        this.filesManager = filesManager;
    }
}