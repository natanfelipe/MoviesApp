package com.arctouch.codechallenge.view.ui;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import com.arctouch.codechallenge.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    Movie movie;
    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;
    @BindView(R.id.tv_overview)
    TextView tvOverview;
    @BindView(R.id.tv_genre)
    TextView tvGenre;
    @BindView(R.id.img)
    ImageView ivPoster;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ct)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.iv_backdrop)
    ImageView ivBackdrop;

    private final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
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
                        .into(ivPoster);
            }

            if (TextUtils.isEmpty(posterPath) == false) {
                Glide.with(this)
                        .load(movieImageUrlBuilder.buildPosterUrl(posterPath))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                        .into(ivBackdrop);
            }

            tvGenre.setText(TextUtils.join(", ", movie.genres));
            collapsingToolbar.setTitle(movie.title);
            tvReleaseDate.setText(movie.releaseDate);
            tvOverview.setText(movie.overview);

        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
