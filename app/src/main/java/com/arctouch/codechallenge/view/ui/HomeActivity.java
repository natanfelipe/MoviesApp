package com.arctouch.codechallenge.view.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.databinding.HomeActivityBinding;
import com.arctouch.codechallenge.interfaces.OnItemClickListenerInterface;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.view.adapters.HomeAdapter;
import com.arctouch.codechallenge.viewmodel.MovieViewModel;



public class HomeActivity extends AppCompatActivity {

    MovieViewModel movieViewModel;
    HomeActivityBinding binding;
    HomeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.home_activity);
        movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        initAdapter();
    }

    private void initAdapter() {
        adapter = new HomeAdapter(this, new OnItemClickListenerInterface() {
            @Override
            public void onItemClick(Movie movie) {
                Intent detailIntent = new Intent(HomeActivity.this,DetailActivity.class);
                detailIntent.putExtra("movieItem",movie);
                startActivity(detailIntent);
            }
        });
        binding.recyclerView.setAdapter(adapter);
        movieViewModel.getMovieLiveData().observe(this, adapter::submitList);
        movieViewModel.getNetworkState().observe(this, adapter::setNetworkState);
    }

    public ProgressBar getProgressBar(){
        return binding.progressBar;
    }
}
