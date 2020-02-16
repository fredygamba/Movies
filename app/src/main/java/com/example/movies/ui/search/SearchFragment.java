package com.example.movies.ui.search;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.adapters.MoviesAdapter;
import com.example.movies.services.dao.ServiceDAO;
import com.example.movies.services.entities.Client;
import com.example.movies.models.entities.Movie;
import com.example.movies.services.entities.MoviesResponse;
import com.example.movies.services.dao.Service;
import com.example.movies.threads.ThreadWait;
import com.example.movies.utilities.FilesManager;
import com.example.movies.utilities.KeyboardManager;
import com.example.movies.utilities.SearchInterface;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements SearchInterface {

    private SearchViewModel searchViewModel;
    private ThreadWait thread;
    private View view;
    private List<Movie> movies;
    private SearchView searchView;
    private ServiceDAO serviceDAO;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        this.view = inflater.inflate(R.layout.fragment_search, container, false);
        this.serviceDAO = serviceDAO.getInstance();
        initComponents();
        return view;
    }

    private void initComponents() {
        searchView = view.findViewById(R.id.search_sv);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if (thread.isAlive()) {
                    thread.kill();
                    search(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.matches(" *")) {
                    if (thread != null) {
                        thread.kill();
                    }
                    thread = new ThreadWait(SearchFragment.this, newText, 2000);
                    thread.start();
                } else {
                    if (thread != null) {
                        thread.kill();
                    }
                    showEmptyMode(false);
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (("" + searchView.getQuery()).matches(" *")) {
            searchView.setQuery("", false);
            searchView.requestFocus();
            KeyboardManager.openKeyboard(getActivity());
        } else {
            searchView.clearFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        KeyboardManager.closeKeyboard(getActivity());
    }

    @Override
    public void search(String query) {
        try {
            Service apiService = Client.getClient().create(Service.class);
            String apiKey = "605d12fa02b5ae40170cdc0bb6214030";
            final Resources resources = getResources();
            Call<MoviesResponse> call = apiService.searchMovies(apiKey, "es", "co", query);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                    movies = response.body().getResults();
                    List<Movie> favoriteMovies = FilesManager.getInstance().readMovies();
                    if (!movies.isEmpty()) {
                        getActivity().findViewById(R.id.search_rv).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.search_tv).setVisibility(View.GONE);
                        if (favoriteMovies != null) {
                            Movie movie = null, favoriteMovie = null;
                            for (int i = 0; i < movies.size(); i++) {
                                movie = movies.get(i);
                                serviceDAO.identifyGenreMovie(movie);
                                for (int j = 0; j < favoriteMovies.size(); j++) {
                                    favoriteMovie = favoriteMovies.get(j);
                                    if (movie.getId() == favoriteMovie.getId()) {
                                        movie.setFavorite(true);
                                    }
                                }
                            }
                        }
                        RecyclerView recyclerView = view.findViewById(R.id.search_rv);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(new MoviesAdapter(getActivity(), movies, false));
                    } else {
                        showEmptyMode(true);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Snackbar.make(view, resources.getString(R.string.d_moviesLoadingFailed), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        } catch (Exception e) {
            try {
                Snackbar.make(view, "Error " + e.getMessage(), Snackbar.LENGTH_LONG);
            } catch (IllegalArgumentException ex) {
            }
        }
    }

    private void showEmptyMode(boolean searching) {
        Activity activity = getActivity();
        activity.findViewById(R.id.search_rv).setVisibility(View.GONE);
        TextView textView = activity.findViewById(R.id.search_tv);
        textView.setVisibility(View.VISIBLE);
        textView.setText(getResources().getString(searching ? R.string.d_noMoviesFound : R.string.d_searchMovie));
    }


}