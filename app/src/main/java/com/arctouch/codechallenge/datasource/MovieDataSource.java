package com.arctouch.codechallenge.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.arctouch.codechallenge.BuildConfig;
import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.connections.RestApiFactory;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.NetworkState;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MovieDataSource extends PageKeyedDataSource<Long,Movie> {

    private MutableLiveData initialLoading;
    private MutableLiveData networkState;

    private static final String TAG = MovieDataSource.class.getSimpleName();
    private static final Long FIRST_PAGE = 1L;
    private Long page;
    CompositeDisposable compositeDisposable;
    private TmdbApi restApiFactory;

    public MovieDataSource() {
        this.restApiFactory = RestApiFactory.create();

        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
    }


    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, Movie> callback) {

        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);

        //createObservable(FIRST_PAGE,FIRST_PAGE+1,callback,null);

        restApiFactory.upcomingMovies(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE, FIRST_PAGE, BuildConfig.DEFAULT_REGION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    for (Movie movie : response.results) {
                        movie.genres = new ArrayList<>();
                        for (Genre genre : Cache.getGenres()) {
                            if (movie.genreIds.contains(genre.id)) {
                                movie.genres.add(genre);
                            }
                        }
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params,
                           @NonNull LoadCallback<Long, Movie> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params,
                          @NonNull LoadCallback<Long, Movie> callback) {

        Log.i(TAG, "Loading Rang " + params.key + " Count " + params.requestedLoadSize);

        Long nextKey = (params.key == params.requestedLoadSize) ? null : params.key+1;

        networkState.postValue(NetworkState.LOADING);

        restApiFactory.upcomingMovies(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE, nextKey, BuildConfig.DEFAULT_REGION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    for (Movie movie : response.results) {
                        movie.genres = new ArrayList<>();
                        for (Genre genre : Cache.getGenres()) {
                            if (movie.genreIds.contains(genre.id)) {
                                movie.genres.add(genre);
                            }
                        }
                    }
                });
        }


    /*private void createObservable(Long requestedPage, Long nextPage, int requestedLoadSize,
                                  LoadInitialCallback<Long,Movie> initialCallback,LoadCallback<Long,Movie> callback){

        compositeDisposable.add(restApiFactory.upcomingMovies(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE, requestedPage, BuildConfig.DEFAULT_REGION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    initialCallback.onResult(response.results,null,nextPage,callback.onResult(response.results,nextPage),
                            {
                                    t-> {
                                        Log.d(TAG, "Error loading page:"+requestedLoadSize , t);
                                    }
                            });
                    for (Movie movie : response.results) {
                        movie.genres = new ArrayList<>();
                        for (Genre genre : Cache.getGenres()) {
                            if (movie.genreIds.contains(genre.id)) {
                                movie.genres.add(genre);
                            }
                        }
                    }
                }));
    }*/
}
