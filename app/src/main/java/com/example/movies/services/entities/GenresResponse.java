package com.example.movies.services.entities;

import com.example.movies.models.entities.Genre;

import java.util.List;

public class GenresResponse {

    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
