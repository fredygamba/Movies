package com.example.movies.ui.movies;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
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
import com.example.movies.models.entities.Genre;
import com.example.movies.services.dao.ServiceDAO;
import com.example.movies.services.entities.Client;
import com.example.movies.models.entities.Movie;
import com.example.movies.services.entities.MoviesResponse;
import com.example.movies.services.dao.Service;
import com.example.movies.utilities.DialogsManager;
import com.example.movies.utilities.FilesManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesFragment extends Fragment {

    private MoviesViewModel moviesViewModel;
    private ServiceDAO serviceDAO;
    private List<Movie> movies;
    private List<Movie> upcomingMovies;
    private List<Movie> popularMovies;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        moviesViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_movies, container, false);
        initComponents(root);
        FilesManager filesManager = FilesManager.getInstance();
        filesManager.setActivity(getActivity());
        filesManager.setContext(getContext());
        serviceDAO = ServiceDAO.getInstance();
        loadMovies(root, getResources().getString(R.string.billboard));
        return root;
    }

    private void initComponents(final View view) {
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadMovies(view, "" + tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void loadMovies(final View view, String category) {
        try {
            Service apiService = Client.getClient().create(Service.class);
            String apiKey = "605d12fa02b5ae40170cdc0bb6214030";
            final Resources resources = getResources();
            Call<MoviesResponse> call = null;
            if (category.equals(resources.getString(R.string.upcoming))) {
                call = apiService.getUpcomingMovies(apiKey, "es");
            } else if (category.equals(resources.getString(R.string.popular))) {
                call = apiService.getPopularMovies(apiKey, "es", "co");
            } else if (category.equals(resources.getString(R.string.topRated))) {
                call = apiService.getTopRatedMovies(apiKey, "es", "co");
            } else {
                call = apiService.getNowPlayingMovies(apiKey, "es", "us");
            }
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                    movies = response.body().getResults();
                    List<Movie> favoriteMovies = FilesManager.getInstance().readMovies();
                    List<Genre> genres = serviceDAO.getGenres();
                    Log.d("", genres != null ? "Instance" : "No instance");
                    if (favoriteMovies != null) {
                        Movie favoriteMovie = null;
                        for (Movie movie : movies) {
                            identifyFavoriteMovie(movie, favoriteMovies);
                            identifyGenreMovie(movie, genres);
                        }
                    }
                    RecyclerView recyclerView = view.findViewById(R.id.movies_rv);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(new MoviesAdapter(getActivity(), movies, false));
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, final Throwable t) {
                    DialogsManager.showSnackBar(view, resources.getString(R.string.d_moviesLoadingFailed), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DialogsManager().showDialog(getFragmentManager(), t.toString());
                        }
                    }, resources.getString(R.string.details));
                }
            });
        } catch (Exception e) {
            Snackbar.make(view, "Error " + e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void identifyFavoriteMovie(Movie movie, List<Movie> favoriteMovies) {
        for (Movie favoriteMovie : favoriteMovies) {
            if (movie.getId() == favoriteMovie.getId()) {
                movie.setFavorite(true);
            }
        }
    }

    private void identifyGenreMovie(Movie movie, List<Genre> genres) {
        if (genres != null){
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
    }
}