package com.example.movies.services.dao;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.movies.models.entities.Genre;
import com.example.movies.models.entities.Movie;
import com.example.movies.services.entities.Client;
import com.example.movies.services.entities.GenresResponse;
import com.example.movies.services.entities.MoviesResponse;
import com.example.movies.utilities.DialogsManager;
import com.example.movies.utilities.FilesManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDAO {

    private static final String API_KEY = "605d12fa02b5ae40170cdc0bb6214030";
    private static final String LANGUAGE = "es";
    private static final String REGION = "co";
    private static ServiceDAO serviceDAO;
    private Service apiService;
    private List<Genre> genres;
    private List<Movie> popularMovies;

    private ServiceDAO() {
        this.apiService = Client.getClient().create(Service.class);
        loadGenres();
        loadPopularMovies();
    }

    private void identifyFavoriteMovie(Movie movie, List<Movie> favoriteMovies) {
        for (Movie favoriteMovie : favoriteMovies) {
            if (movie.getId() == favoriteMovie.getId()) {
                movie.setFavorite(true);
            }
        }
    }

    /**
     * Identifica y relaciona los géneros de la película pedida por parámetro.
     * Los géneros de la película ya se encuentra guardados en la clase {@link ServiceDAO}
     * y son cargados automáticamente al momento de instanciarse {@link ServiceDAO} (véase
     * {@link ServiceDAO}.loadGenres()).
     * Las películas cargadas a través del servicio web de TheMovieDb, llegan con
     * una lista de identificadores de géneros de películas. Este método, toma esos
     * identificadores y busca los géneros que posee una identificación similar.
     * De este modo, relaciona el género ({@link Genre} completo con la película ({@link Movie}).
     * @param movie Película a identificar y relacionar con los géneros a los que pertenece dicha película.
     */
    public void identifyGenreMovie(Movie movie) {
        int[] movieGenreIds = movie.getGenreIds();
        List<Genre> movieGenres = new ArrayList<>();
        for (int i = 0; i < movieGenreIds.length; i++) {
            for (Genre genre : genres) {
                if (movieGenreIds[i] == genre.getId()) {
                    movieGenres.add(genre);
                    break;
                }
            }
        }
        if (!movieGenres.isEmpty()) {
            movie.setGenres(movieGenres);
        }
    }


    /**
     * Identifica y relaciona los géneros de la película pedida por parámetro.
     * Los géneros de la película ya se encuentra guardados en la clase {@link ServiceDAO}
     * y son cargados automáticamente al momento de instanciarse {@link ServiceDAO} (véase
     * {@link ServiceDAO}.loadGenres()).
     * Las películas cargadas a través del servicio web de TheMovieDb, llegan con
     * una lista de identificadores de géneros de películas. Este método, toma esos
     * identificadores y busca los géneros que posee una identificación similar.
     * De este modo, relaciona el género ({@link Genre} completo con la película ({@link Movie}).
     * @param movie Película a identificar y relacionar con los géneros a los que pertenece dicha película.
     * @param genres Lista de géneros que se utilizará para relacionar las películas ({@link Movie}
     *               con los géneros ({@link Genre}.
     */
    public void identifyGenreMovie(Movie movie, List<Genre> genres) {
        int[] movieGenreIds = movie.getGenreIds();
        List<Genre> movieGenres = new ArrayList<>();
        for (int i = 0; i < movieGenreIds.length; i++) {
            for (Genre genre : genres) {
                if (movieGenreIds[i] == genre.getId()) {
                    movieGenres.add(genre);
                    break;
                }
            }
        }
        if (!movieGenres.isEmpty()) {
            movie.setGenres(movieGenres);
        }
    }

    /**
     * Carga una lista de géneros de películas almacenados en TheMovieDb, a partir de un servicio web.
     * Esta lista es guardada en la clase {@link ServiceDAO}. Para obtener esta lista, es necesario
     * utilizar el método {@link ServiceDAO}.getInstance().getGenres().
     */
    synchronized public void loadGenres() {
        Call<GenresResponse> call = apiService.getGenres(API_KEY, LANGUAGE);
        call.enqueue(new Callback<GenresResponse>() {
            @Override
            public void onResponse(Call<GenresResponse> call, Response<GenresResponse> response) {
                GenresResponse genresResponse = response.body();
                if (genresResponse != null) {
                    genres = response.body().getGenres();
                }
            }

            @Override
            public void onFailure(Call<GenresResponse> call, Throwable t) {
            }
        });
        while (genres != null) {
            Log.d(">>", "Buscando");
        }
    }

    public void loadPopularMovies() {
        Call<MoviesResponse> call = apiService.getPopularMovies(API_KEY, LANGUAGE, REGION);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                popularMovies = response.body().getResults();
                List<Movie> favoriteMovies = FilesManager.getInstance().readMovies();
                for (Movie movie : popularMovies) {
                    if (favoriteMovies != null) {
                        identifyFavoriteMovie(movie, favoriteMovies);
                    }
                    if (genres != null) {
                        identifyGenreMovie(movie, genres);
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {

            }
        });
    }

    public static ServiceDAO getInstance() {
        if (serviceDAO == null) {
            serviceDAO = new ServiceDAO();
        }
        return serviceDAO;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<Movie> getPopularMovies() {
        return popularMovies;
    }

    public void setPopularMovies(List<Movie> popularMovies) {
        this.popularMovies = popularMovies;
    }
}
