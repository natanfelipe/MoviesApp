package com.arctouch.codechallenge.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;


public class MovieDataFactory extends DataSource.Factory {

    private MutableLiveData<MovieDataSource> mutableLiveData;
    private MovieDataSource movieDataSource;

    public MovieDataFactory() {
        this.mutableLiveData = new MutableLiveData<MovieDataSource>();
    }

    @Override
    public DataSource create() {
        movieDataSource = new MovieDataSource();
        mutableLiveData.postValue(movieDataSource);
        return movieDataSource;
    }


    public MutableLiveData<MovieDataSource> getMutableLiveData() {
        return mutableLiveData;
    }

}
