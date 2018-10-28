package com.arctouch.codechallenge.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.arctouch.codechallenge.datasource.MovieDataFactory;
import com.arctouch.codechallenge.datasource.MovieDataSource;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.NetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.disposables.CompositeDisposable;

public class MovieViewModel extends ViewModel {

    private Executor executor;
    private LiveData movieLiveData;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MovieDataFactory movieDataFactory;
    private int pageSize = 20;

    public MovieViewModel() {
        init();
    }

    private void init() {
        executor = Executors.newFixedThreadPool(5);

        movieDataFactory = new MovieDataFactory(compositeDisposable);

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(10)
                        .setPageSize(pageSize)
                        .setPrefetchDistance(5)
                        .build();

        movieLiveData = (new LivePagedListBuilder(movieDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(movieDataFactory.getMoviesDataSourceLiveData(), MovieDataSource::getNetworkState);
    }

    public LiveData<PagedList<Movie>> getMovieLiveData() {
        return movieLiveData;
    }

    public void retry() {
        movieDataFactory.getMoviesDataSourceLiveData().getValue().retry();
    }

    public void refresh() {
        movieDataFactory.getMoviesDataSourceLiveData().getValue().invalidate();
    }

}
