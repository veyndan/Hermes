package com.veyndan.hermes;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.veyndan.hermes.database.DbHelper;
import com.veyndan.hermes.home.HomeAdapter;
import com.veyndan.hermes.home.model.Comic;
import com.veyndan.hermes.service.ComicService;
import com.veyndan.hermes.ui.AutoStaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeedFragment extends BaseFragment {

    private static final String TAG = "veyndan_FeedFragment";

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
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(new DbHelper(getActivity()), Schedulers.io());

//        String sql = SQLiteQueryBuilder.buildQueryString(false, Comic.TABLE, null, null, null, null, Comic.NUM + " DESC", "1");
//        db.createQuery(Comic.TABLE, sql)
//                .compose(this.<SqlBrite.Query>bindToLifecycle())
//                .flatMap(query -> query.asRows(Comic.MAPPER))
//                .flatMap(comic -> comicService.fetchComics(comic.num()))
//                .switchIfEmpty(comicService.fetchComics())
//                .observeOn(Schedulers.io())
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(comic -> Log.d(TAG, "onCreateView:" + comic));

//        db.createQuery(Comic.TABLE, "SELECT * FROM " + Comic.TABLE)
//                .compose(this.<SqlBrite.Query>bindToLifecycle())
//                .concatMap(query -> query.asRows(Comic.MAPPER))
//                .concatWith(db.createQuery(Comic.TABLE, "SELECT MAX(_id) FROM " + Comic.TABLE)
//                        .flatMap(query -> query.asRows(Comic.MAPPER))
//                        .flatMap(comic -> comicService.fetchComics(comic.num())))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(comic -> {
//                    comics.add(comic);
//                    adapter.notifyDataSetChanged();
//                });
//
        comicService.fetchComics(1600, 1660)
                .compose(this.<Comic>bindToLifecycle())
                .buffer(20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comics -> {

                    BriteDatabase.Transaction transaction = db.newTransaction();
                    try {
                        for (Comic comic : comics) {
                            db.insert(Comic.TABLE, comic.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE);
                        }
                        transaction.markSuccessful();
                    } finally {
                        transaction.end();
                    }

                    this.comics.addAll(comics);
                    adapter.notifyDataSetChanged();
                });

        return recyclerView;
    }
}
