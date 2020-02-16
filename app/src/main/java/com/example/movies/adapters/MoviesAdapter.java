package com.example.movies.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.models.entities.Genre;
import com.example.movies.ui.movies.MovieContainer;
import com.example.movies.R;
import com.example.movies.models.entities.Movie;
import com.example.movies.utilities.AnimationsManager;
import com.example.movies.utilities.FilesManager;
import com.example.movies.utilities.DialogsManager;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolderMovies> {

    private Activity activity;
    private List<Movie> movies;
    private boolean favoriteFragment;

    public MoviesAdapter(Activity activity, List<Movie> movies, boolean favoriteFragment) {
        this.activity = activity;
        this.movies = movies;
        this.favoriteFragment = favoriteFragment;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMovies holder, int position) {
        holder.setMovie(movies.get(position));
    }

    @NonNull
    @Override
    public ViewHolderMovies onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_movie, null, false);
        return new ViewHolderMovies(view);
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public class ViewHolderMovies extends RecyclerView.ViewHolder {

        private View view;
        private CardView cardView;
        private ImageView imgCover;
        private TextView txtTitle;
        private TextView txtGenres;
        private TextView txtDescription;
        private ImageButton btnExpand;
        private MaterialButton btnFavorite;
        private Movie movie;

        private ViewHolderMovies(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.imgCover = itemView.findViewById(R.id.movie_imgCover);
            this.btnExpand = itemView.findViewById(R.id.movie_btnExpand);
            this.txtTitle = itemView.findViewById(R.id.movie_txtTitle);
            this.txtGenres = itemView.findViewById(R.id.movie_txtGenres);
            this.txtDescription = itemView.findViewById(R.id.movie_txtDescription);
            this.btnFavorite = itemView.findViewById(R.id.movie_btnFavorite);
            this.cardView = itemView.findViewById(R.id.container);
            initProperties();
        }

        public String chainGenres(List<Genre> genres) {
            String sGenres = null;
            if (genres != null) {
                sGenres = "";
                int nGenres = genres.size();
                for (int i = 0; i < nGenres; i++) {
                    sGenres += genres.get(i).getName();
                    if (i < nGenres - 1) {
                        sGenres += ", ";
                    }
                }
            }
            return sGenres;
        }

        private void initProperties() {
            txtDescription.setVisibility(View.GONE);
            btnExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txtDescription.getVisibility() == View.VISIBLE) {
                        AnimationsManager.collapse(txtDescription);
                        btnExpand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    } else {
                        AnimationsManager.expand(txtDescription);
                        btnExpand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    }
                }
            });
            this.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MovieContainer.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("movie", movie);
                    intent.putExtras(bundle);
                    String transitionName = activity.getResources().getString(R.string.transition_string);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, transitionName);
                    ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());
                }
            });
            btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFavorite();
                }
            });
        }

        private void removeAt(int position) {
            if (favoriteFragment) {
                movies.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, movies.size());
                if (movies.isEmpty()) {
                    activity.findViewById(R.id.favorites_rv).setVisibility(View.GONE);
                    activity.findViewById(R.id.favorites_tv).setVisibility(View.VISIBLE);
                }
            }
        }

        private void removeMovie() {
            movie.setFavorite(false);
            FilesManager.getInstance().removeMovie(movie.getId());
            btnFavorite.setIconResource(R.drawable.ic_star_border_black_24dp);
            removeAt(getAdapterPosition());
        }

        private void saveMovie() {
            movie.setFavorite(true);
            FilesManager.getInstance().saveMovie(movie);
            btnFavorite.setIconResource(R.drawable.ic_star_black_24dp);
            if (favoriteFragment) {
                if (movies.isEmpty()) {
                    activity.findViewById(R.id.favorites_rv).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.favorites_tv).setVisibility(View.GONE);
                }
                movies.add(movie);
                notifyItemInserted(movies.size() - 1);
                notifyDataSetChanged();
            }
        }

        private void setFavorite() {
            boolean favorite = movie.isFavorite();
            Resources resources = view.getResources();
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

        public void setMovie(Movie movie) {
            this.movie = movie;
            Picasso.get()
                    .load(movie.getPosterPath())
                    .into(imgCover);
            txtTitle.setText(movie.getTitle());
            txtDescription.setText(movie.getOverview());
            if (movie.isFavorite()) {
                btnFavorite.setIconResource(R.drawable.ic_star_black_24dp);
            }
            txtGenres.setText(chainGenres(movie.getGenres()));
        }
    }
}