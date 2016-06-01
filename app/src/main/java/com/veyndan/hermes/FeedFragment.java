package com.veyndan.hermes;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.veyndan.hermes.database.Db;
import com.veyndan.hermes.database.DbHelper;
import com.veyndan.hermes.home.HomeAdapter;
import com.veyndan.hermes.home.model.Comic;
import com.veyndan.hermes.service.ComicService;
import com.veyndan.hermes.ui.AutoStaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeedFragment extends BaseFragment {

    private static final String TAG = "veyndan_FeedFragment";

    private static final int BUFFER_SIZE = 20;

    private final List<Comic> comics = new ArrayList<>();
    private final HomeAdapter adapter = new HomeAdapter(comics);

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView =
                (RecyclerView) inflater.inflate(R.layout.feed_fragment, container, false);
        recyclerView.setLayoutManager(new AutoStaggeredGridLayoutManager(700));
        recyclerView.setAdapter(adapter);

        ComicService comicService = new ComicService();

        SqlBrite sqlBrite = SqlBrite.create();
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(new DbHelper(getContext()), Schedulers.io());

        Observable<List<Comic>> network = comicService.fetchComics(1, 10)
                .buffer(BUFFER_SIZE)
                .doOnNext(comics -> {
                    Db.insert(db, comics);
                    Log.d(TAG, "Network request: " + comics.get(0).num() + " to " + comics.get(comics.size() - 1).num());
                });

        db.createQuery(Comic.TABLE, "SELECT * FROM " + Comic.TABLE + " ORDER BY " + Comic.NUM + " DESC")
                .compose(this.<SqlBrite.Query>bindToLifecycle())
                .concatMap(query -> query.asRows(Comic.MAPPER))
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comic -> {
                    this.comics.add(comic);
                    adapter.notifyDataSetChanged();
                });

//        network
//                .compose(this.<List<Comic>>bindToLifecycle())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(comics -> {
//                    this.comics.addAll(comics);
//                    adapter.notifyDataSetChanged();
//                });

        return recyclerView;
    }
}
