package com.arctouch.codechallenge.view.adapters;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.databinding.MovieItemBinding;
import com.arctouch.codechallenge.interfaces.OnItemClickListenerInterface;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;
import com.arctouch.codechallenge.util.NetworkState;
import com.arctouch.codechallenge.view.ui.HomeActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;


public class HomeAdapter extends PagedListAdapter<Movie,HomeAdapter.ViewHolder>  {

    private NetworkState networkState;
    private LayoutInflater layoutInflater;
    private final OnItemClickListenerInterface onItemClickListener;
    Context context;



    public HomeAdapter(Context context, OnItemClickListenerInterface onItemClickListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MovieItemBinding binding;
        private final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();


        public ViewHolder(MovieItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Movie movie, OnItemClickListenerInterface onItemClickListener) {
            binding.titleTextView.setText(movie.title);
            binding.genresTextView.setText(TextUtils.join(", ", movie.genres));
            binding.releaseDateTextView.setText(movie.releaseDate);

            String posterPath = movie.posterPath;
                Glide.with(itemView)
                        .load(movieImageUrlBuilder.buildPosterUrl(posterPath != null ? posterPath : "" ))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                        .into(binding.posterImageView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(movie);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        MovieItemBinding binding = DataBindingUtil.inflate(layoutInflater,R.layout.movie_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position),onItemClickListener);
    }

    private static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Movie>() {
                @Override
                public boolean areItemsTheSame(Movie oldItem, Movie newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(Movie oldItem, Movie newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }


    public void setNetworkState(NetworkState newNetworkState) {
        if (getCurrentList() != null) {
            if (getCurrentList().size() != 0) {
                NetworkState previousState = this.networkState;
                boolean hadExtraRow = hasExtraRow();
                this.networkState = newNetworkState;
                boolean hasExtraRow = hasExtraRow();
                if (hadExtraRow != hasExtraRow) {
                    if (hadExtraRow) {
                        notifyItemRemoved(super.getItemCount());
                    } else {
                        notifyItemInserted(super.getItemCount());
                    }
                } else if (hasExtraRow && previousState != newNetworkState) {
                    notifyItemChanged(getItemCount() - 1);
                }

                if(!newNetworkState.getStatus().equals(NetworkState.Status.FAILED)){
                    ((HomeActivity)context).getProgressBar().setVisibility(View.GONE);
                } else {
                    ((HomeActivity)context).getProgressBar().setVisibility(View.VISIBLE);
                }

            }
        }
    }





}
