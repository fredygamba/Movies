package com.example.movies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies.R;
import com.example.movies.services.entities.Client;
import com.example.movies.models.entities.Actor;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CastPersonAdapter extends RecyclerView.Adapter<CastPersonAdapter.ViewHolderMovies> {

    private List<Actor> cast;

    public CastPersonAdapter(List<Actor> cast) {
        this.cast = cast;
    }

    @Override
    public int getItemCount() {
        return cast.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMovies holder, int position) {
        holder.setCastPerson(cast.get(position));
    }

    @NonNull
    @Override
    public ViewHolderMovies onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderMovies(LayoutInflater.from(parent.getContext()).inflate(R.layout.component_person, null, false));
    }

    public class ViewHolderMovies extends RecyclerView.ViewHolder {

        private ImageView ivPhoto;
        private TextView txtName;
        private TextView txtCharacter;

        public ViewHolderMovies(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.person_ivPhoto);
            txtName = itemView.findViewById(R.id.person_txtName);
            txtCharacter = itemView.findViewById(R.id.person_txtCharacter);
        }

        public void setCastPerson(Actor actor) {
            String profilePath = actor.getProfilePath();
            Picasso.get()
                    .load(profilePath == null ? "https://image.flaticon.com/icons/png/512/2184/premium/2184718.png" : Client.IMAGE_BASE_URL + profilePath)
                    .into(ivPhoto);
            txtName.setText(actor.getName());
            txtCharacter.setText(actor.getCharacter());
        }
    }
}