package com.example.movies.models.entities;

import java.util.List;

public class MovieCredits {

    private int id;
    private List<Actor> cast;

    public MovieCredits(int id, List<Actor> cast) {
        this.id = id;
        this.cast = cast;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Actor> getCast() {
        return cast;
    }

    public void setCast(List<Actor> cast) {
        this.cast = cast;
    }
}
