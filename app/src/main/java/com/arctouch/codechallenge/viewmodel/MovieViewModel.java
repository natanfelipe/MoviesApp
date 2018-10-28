package com.arctouch.codechallenge.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.arctouch.codechallenge.datasource.MovieDataFactory;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.NetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MovieViewModel extends ViewModel {

    private Executor executor;
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<Movie>> movieLiveData;

    public MovieViewModel() {
        init();
    }

    private void init() {
        executor = Executors.newFixedThreadPool(5);

        MovieDataFactory movieDataFactory = new MovieDataFactory();
        networkState = Transformations.switchMap(movieDataFactory.getMutableLiveData(),
                dataSource -> dataSource.getNetworkState());

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(10)
                        .setPageSize(20).build();

        movieLiveData = (new LivePagedListBuilder(movieDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }


    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<Movie>> getMovieLiveData() {
        return movieLiveData;
    }

}
