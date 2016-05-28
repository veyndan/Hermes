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

        comicService.fetchComics(1600)
                .compose(this.<Comic>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comic -> {
                    db.insert(Comic.TABLE, comic.toContentValues(comic), SQLiteDatabase.CONFLICT_IGNORE);
                    comics.add(comic);
                    adapter.notifyDataSetChanged();
                });

        return recyclerView;
    }
}
