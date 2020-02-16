package com.example.movies.ui.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movies.R;
import com.example.movies.adapters.CastPersonAdapter;
import com.example.movies.services.entities.Client;
import com.example.movies.models.entities.Movie;
import com.example.movies.models.entities.Actor;
import com.example.movies.models.entities.MovieCredits;
import com.example.movies.models.entities.Genre;
import com.example.movies.services.dao.Service;
import com.example.movies.utilities.AnimationsManager;
import com.example.movies.utilities.DialogsManager;
import com.example.movies.utilities.FilesManager;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieContainer extends AppCompatActivity {

    private Movie movie;
    private MaterialButton btnFavorite;

    private void initComponents() {
        btnFavorite = findViewById(R.id.contMovie_btnFavorite);
        findViewById(R.id.contMovie_btnFavorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite();
            }
        });
    }

    private void loadMovie() {
        setMovie(movie);
        loadMovieCasting();
    }

    private void loadMovieCasting() {
        final String apiKey = "605d12fa02b5ae40170cdc0bb6214030";
        final View view = findViewById(R.id.container);
        final Service apiService = Client.getClient().create(Service.class);
        final Resources resources = getResources();
        Call<MovieCredits> call = apiService.getMovieCredits(movie.getId(), apiKey, "es");
        call.enqueue(new Callback<MovieCredits>() {
            @Override
            public void onResponse(Call<MovieCredits> call, Response<MovieCredits> response) {
                MovieCredits movieCredits = response.body();
                List<Actor> castPeople = movieCredits.getCast();
                for (int i = castPeople.size() - 1; i >= 0; i--) {
                    if (i > 3) {
                        castPeople.remove(i);
                    }
                }
                RecyclerView recyclerView = findViewById(R.id.contMovie_rvCastPeople);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(new CastPersonAdapter(castPeople));
            }

            @Override
            public void onFailure(Call<MovieCredits> call, final Throwable t) {
                DialogsManager.showSnackBar(view, resources.getString(R.string.d_movieCreditsLoadingFailed), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogsManager().showDialog(getSupportFragmentManager(), t.toString());
                    }
                }, resources.getString(R.string.details));
            }
        });
    }

    @Override
    public void onBackPressed() {
        AnimationsManager.collapse(findViewById(R.id.contMovie_rvCastPeople));
        AnimationsManager.collapse(findViewById(R.id.contMovie_txtDescription));
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        setMovie((Movie) getIntent().getSerializableExtra("movie"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        loadMovie();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    private void removeMovie() {
        movie.setFavorite(false);
        FilesManager.getInstance().removeMovie(movie.getId());
        btnFavorite.setIconResource(R.drawable.ic_star_border_black_24dp);
    }

    private void saveMovie() {
        movie.setFavorite(true);
        FilesManager.getInstance().saveMovie(movie);
        btnFavorite.setIconResource(R.drawable.ic_star_black_24dp);
    }

    private void setFavorite() {
        View view = findViewById(R.id.container);
        final boolean favorite = movie.isFavorite();
        Resources resources = getResources();
        if (favorite) {
            removeMovie();
            DialogsManager.showSnackBar(view, resources.getString(R.string.d_removeFavorite) + " " + movie.getTitle(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveMovie();
                }
            }, resources.getString(R.string.undo));
        } else {
            saveMovie();
            DialogsManager.showSnackBar(view, resources.getString(R.string.d_addFavorite) + " " + movie.getTitle(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeMovie();
                }
            }, resources.getString(R.string.undo));
        }
    }

    private void setMovie(Movie movie) {
        this.movie = movie;
        String title = movie.getTitle();
        setTitle(title);
        Picasso.get().load(movie.getPosterPath()).into((ImageView) findViewById(R.id.contMovie_imgCover));
        ((TextView) findViewById(R.id.contMovie_txtTitle)).setText(title);
        ((TextView) findViewById(R.id.contMovie_txtDescription)).setText(movie.getOverview());
        if (movie.isFavorite()) {
            ((MaterialButton) findViewById(R.id.contMovie_btnFavorite)).setIconResource(R.drawable.ic_star_black_24dp);
        }
        List<Genre> genres = movie.getGenres();
        if (genres != null) {
            String sGenres = "";
            int nGenres = genres.size();
            for (int i = 0; i < nGenres; i++) {
                sGenres += genres.get(i).getName();
                if (i < nGenres - 1) {
                    sGenres += ", ";
                }
            }
            ((TextView) findViewById(R.id.contMovie_txtGenres)).setText(sGenres);
        }

    }
}
