package com.example.movies.models.entities;

public class Actor extends Person {

    private String character;

    public Actor(int id, String name, String character) {
        super(id, name);
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

}