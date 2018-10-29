package com.arctouch.codechallenge.datasource;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.arctouch.codechallenge.BuildConfig;
import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.connections.RestApiFactory;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.NetworkState;
import com.arctouch.codechallenge.view.ui.HomeActivity;

import java.util.ArrayList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MovieDataSource extends PageKeyedDataSource<Long,Movie> {

    private MutableLiveData<NetworkState> initialLoading = new MutableLiveData<>();
    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private static final String TAG = MovieDataSource.class.getSimpleName();
    private static final Long FIRST_PAGE = 1L;
    CompositeDisposable compositeDisposable;
    private TmdbApi restApiFactory;



    public MovieDataSource(TmdbApi restApiFactory, CompositeDisposable compositeDisposable) {
        this.restApiFactory = RestApiFactory.create();
        this.compositeDisposable = compositeDisposable;
        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
    }


    public MutableLiveData<NetworkState> getNetworkState(){
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, Movie> callback) {
                    initialLoading.postValue(NetworkState.LOADING);
                    networkState.postValue(NetworkState.LOADING);
                    createObservable(FIRST_PAGE,FIRST_PAGE+1,params.requestedLoadSize,callback,null);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params,
                           @NonNull LoadCallback<Long, Movie> callback) {
                    initialLoading.postValue(NetworkState.LOADING);
                    networkState.postValue(NetworkState.LOADING);
                    createObservable(params.key,params.key-1,params.requestedLoadSize,null,callback);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params,
                          @NonNull LoadCallback<Long, Movie> callback) {
                    initialLoading.postValue(NetworkState.LOADING);
                    networkState.postValue(NetworkState.LOADING);
                    createObservable(params.key,params.key+1,params.requestedLoadSize,null,callback);
                }

        private void createObservable(Long requestedPage, Long nextPage, int requestedLoadSize,
                                      LoadInitialCallback<Long,Movie> initialCallback,LoadCallback<Long,Movie> callback){

            if(requestedPage == FIRST_PAGE) {
                restApiFactory.genres(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            Cache.setGenres(response.genres);
                        });
            }


            compositeDisposable.add(restApiFactory.upcomingMovies(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE,
                        requestedPage).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                upcomingMoviesResponse -> {
                                    for (Movie movie : upcomingMoviesResponse.results) {
                                        movie.genres = new ArrayList<>();
                                        for (Genre genre : Cache.getGenres()) {
                                            if (movie.genreIds.contains(genre.id)) {
                                                movie.genres.add(genre);
                                            }
                                        }
                                    }
                                    if(initialCallback != null)
                                      initialCallback.onResult(upcomingMoviesResponse.results,null,nextPage);
                                    if(callback != null)
                                      callback.onResult(upcomingMoviesResponse.results,nextPage);
                                    networkState.postValue(NetworkState.LOADED);
                                    initialLoading.postValue(NetworkState.LOADED);
                                }, throwable -> {
                                }
                        ));
        }

}
