package com.arctouch.codechallenge.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arctouch.codechallenge.BuildConfig;
import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.TmdbApi;
import com.arctouch.codechallenge.connections.RestApiFactory;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.databinding.MovieItemBinding;
import com.arctouch.codechallenge.datasource.MovieDataFactory;
import com.arctouch.codechallenge.interfaces.OnItemClickListenerInterface;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.model.UpcomingMoviesResponse;
import com.arctouch.codechallenge.util.NetworkState;
import com.arctouch.codechallenge.view.adapters.HomeAdapter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class MovieViewModelTest extends Assert {

    RestApiFactory restApiFactory;
    TmdbApi api;
    CompositeDisposable compositeDisposable;

    @Mock
    HomeAdapter adapter;

    @Mock
    Context context;

    @Mock
    ViewGroup parent;

    @Mock
    MovieViewModel viewModel;

    LayoutInflater layoutInflater;

    @Mock
    OnItemClickListenerInterface onItemClickListener;

    HomeAdapter.ViewHolder holder;

    @Mock
    DiffUtil.ItemCallback<Movie> callback;

    String code = "";

    List<Movie> result;



    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        compositeDisposable = new CompositeDisposable();
        restApiFactory = new RestApiFactory();
        api = restApiFactory.create();
        result = new ArrayList<>();


        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                // this prevents StackOverflowErrors when scheduling with a delay
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };


        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);

    }

    @Test
    public void loadMovieSuccesfully() {


        api.upcomingMovies(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE, 1L)
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

                        result = response.results;
                    }
                },throwable -> {
                    code = throwable.getMessage();
                });

        assertTrue(result.size() > 0);

    }

    @Test
    public void tryToLoadMovieFromPageWithoutMovies() {
        api.upcomingMovies(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE, 0L)
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
                },throwable -> {
                    code = throwable.getMessage();
                });

        assertEquals(true,code.contains("422"));
    }

    @Test
    public void cacheGenreSuccesfully() {

        api.genres(BuildConfig.API_KEY, BuildConfig.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Cache.setGenres(response.genres);
                });
        assertEquals(19,Cache.getGenres().size());
    }



    @After
    public void tearDown() throws Exception {
    }
}