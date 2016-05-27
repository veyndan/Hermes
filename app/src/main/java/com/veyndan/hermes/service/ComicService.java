package com.veyndan.hermes.service;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.ryanharter.auto.value.moshi.AutoValueMoshiAdapterFactory;
import com.squareup.moshi.Moshi;
import com.veyndan.hermes.model.Comic;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public class ComicService {

    private static final String XKCD_BASE_URL = "https://xkcd.com";

    private final XKCDService xkcdService;

    private interface XKCDService {
        @GET("info.0.json")
        Observable<Comic> latest();

        @GET("{num}/info.0.json")
        Observable<Comic> num(@Path("num") int num);
    }

    public ComicService() {

        final Moshi moshi = new Moshi.Builder().add(new AutoValueMoshiAdapterFactory()).build();

        final Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(XKCD_BASE_URL)
                .build();

        xkcdService = retrofit.create(XKCDService.class);
    }

    /**
     * Fetches every xkcd comic ever made.
     *
     * @return An Observable which emits each comic individually.
     */
    @NonNull
    public Observable<Comic> fetchComics() {
        return fetchComics(1);
    }

    /**
     * Fetches every xkcd comic from the desired comic up to and including the most recent comic.
     *
     * @param from The earliest comic to fetch from (inclusive).
     * @return An Observable which emits each comic individually.
     */
    @NonNull
    public Observable<Comic> fetchComics(@IntRange(from = 1) int from) {
        return xkcdService.latest()
                .flatMap(comic -> fetchComics(from, comic.num()));
    }

    /**
     * Fetches every xkcd comic from the desired comic up to the desired comic.
     *
     * @param from The earliest comic to fetch from (inclusive).
     * @param to   The latest comic to fetch to (inclusive).
     * @return An Observable which emits each comic individually.
     */
    @NonNull
    public Observable<Comic> fetchComics(@IntRange(from = 1) int from, @IntRange(from = 1) int to) {
        Observable<Comic> observable = Observable.empty();
        for (int num = to; num >= from; num--) {
            observable = observable.concatWith(xkcdService.num(num));
        }
        return observable;
    }

}
