package com.arctouch.codechallenge.view.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.databinding.ActivityDetailBinding;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    Movie movie;
    ActivityDetailBinding binding;

    private final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent dataFromPreviousIntent = getIntent();
        if(dataFromPreviousIntent.getSerializableExtra("movieItem") != null){
            movie = (Movie) dataFromPreviousIntent.getSerializableExtra("movieItem");
        }

        if(movie != null){
            String backdropPath = movie.backdropPath;
            String posterPath = movie.posterPath;
            if (TextUtils.isEmpty(backdropPath) == false) {
                Glide.with(this)
                        .load(movieImageUrlBuilder.buildBackdropUrl(backdropPath))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                        .into(binding.img);
            }

            if (TextUtils.isEmpty(posterPath) == false) {
                Glide.with(this)
                        .load(movieImageUrlBuilder.buildPosterUrl(posterPath))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                        .into(binding.detail.ivBackdrop);
            }

            binding.detail.tvGenre.setText(TextUtils.join(", ", movie.genres));
            binding.ct.setTitle(movie.title);
            binding.detail.tvReleaseDate.setText(movie.releaseDate);
            binding.detail.tvOverview.setText(movie.overview);

        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
