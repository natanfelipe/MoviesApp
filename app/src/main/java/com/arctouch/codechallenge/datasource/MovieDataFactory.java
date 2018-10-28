package com.arctouch.codechallenge.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.support.annotation.NonNull;

import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.connections.RestApiFactory;
import com.arctouch.codechallenge.model.Movie;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class MovieDataFactory extends DataSource.Factory<Long,Movie> {

    private CompositeDisposable compositeDisposable;
    private TmdbApi tmdbApi;
    private MutableLiveData<MovieDataSource> movieDataSourceMutableLiveData = new MutableLiveData<>();


    public MovieDataFactory(CompositeDisposable compositeDisposable) {
        this.tmdbApi = RestApiFactory.create();
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public DataSource<Long, Movie> create() {
        MovieDataSource movieDataSource = new MovieDataSource(tmdbApi,compositeDisposable);
        movieDataSourceMutableLiveData.postValue(movieDataSource);
        return movieDataSource;
    }

    @NonNull
    public MutableLiveData<MovieDataSource> getMoviesDataSourceLiveData() {
        return movieDataSourceMutableLiveData;
    }
}
