package com.example.movies.services.dao;

import com.example.movies.models.entities.Movie;
import com.example.movies.models.entities.MovieCredits;
import com.example.movies.services.entities.GenresResponse;
import com.example.movies.services.entities.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @GET("genre/movie/list")
    Call<GenresResponse> getGenres(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/{id}")
    Call<Movie> getMovie(@Path("id") int movieId, @Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/{id}/credits")
    Call<MovieCredits> getMovieCredits(@Path("id") int movieId, @Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/now_playing")
    Call<MoviesResponse> getNowPlayingMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region);

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region);

    @GET("movie/upcoming")
    Call<MoviesResponse> getUpcomingMovies(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("search/movie")
    Call<MoviesResponse> searchMovies(@Query("api_key") String apiKey, @Query("language") String language, @Query("region") String region, @Query("query") String query);

}