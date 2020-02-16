package com.example.movies.utilities;

import android.app.Activity;
import android.content.Context;

import com.example.movies.models.entities.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FilesManager {

    private static final String FAVORITES_FILENAME = "favorites.json";
    private static FilesManager filesManager;
    private Activity activity;
    private Context context;

    private FilesManager() {

    }

    public static FilesManager getInstance() {
        if (filesManager == null) {
            filesManager = new FilesManager();
        }
        return filesManager;
    }

    public List<Movie> readMovies() {
        String jsonMovies = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput(FAVORITES_FILENAME)));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                jsonMovies += line + "\n";
            }
            bufferedReader.close();
        } catch (IOException e) {
            writeMovies(new ArrayList<Movie>());
        }
        Type type = new TypeToken<List<Movie>>() {
        }.getType();
        return new Gson().fromJson(jsonMovies, type);
    }

    public void removeMovie(int id) {
        List<Movie> movies = readMovies();
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getId() == id) {
                movies.remove(i);
            }
        }
        writeMovies(movies);
    }

    public void saveMovie(Movie movie) {
        List<Movie> movies = readMovies();
        if (movies == null) {
            movies = new ArrayList<>();
        }
        movies.add(movie);
        writeMovies(movies);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void writeMovies(List<Movie> movies) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Movie>>() {
        }.getType();
        String json = gson.toJson(movies, type);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FAVORITES_FILENAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}