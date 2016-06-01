package com.veyndan.hermes.home;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.veyndan.hermes.BaseActivity;
import com.veyndan.hermes.R;
import com.veyndan.hermes.database.Db;
import com.veyndan.hermes.database.DbHelper;
import com.veyndan.hermes.home.model.Comic;
import com.veyndan.hermes.service.ComicService;
import com.veyndan.hermes.ui.AutoStaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "veyndan_HomeActivity";

    private static final int BUFFER_SIZE = 20;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private final List<Comic> comics = new ArrayList<>();
    private final HomeAdapter adapter = new HomeAdapter(comics);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new AutoStaggeredGridLayoutManager(700));
        recyclerView.setAdapter(adapter);

        ComicService comicService = new ComicService();

        SqlBrite sqlBrite = SqlBrite.create();
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(new DbHelper(this), Schedulers.io());

        Observable<List<Comic>> database = db.createQuery(Comic.TABLE, "SELECT * FROM " + Comic.TABLE + " ORDER BY " + Comic.NUM + " DESC")
                .concatMap(query -> query.asRows(Comic.MAPPER))
                .onBackpressureBuffer()
                .buffer(BUFFER_SIZE);

        Observable<List<Comic>> network = comicService.fetchComics(1, 40)
                .buffer(BUFFER_SIZE)
                .doOnNext(comics -> {
                    Db.insert(db, comics);
                    Log.d(TAG, "Network request: " + comics.get(0).num() + " to " + comics.get(comics.size() - 1).num());
                });

        database
                .compose(this.<List<Comic>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comics -> {
                    this.comics.addAll(comics);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_favorites:
            case R.id.action_filter_unread:
                item.setChecked(!item.isChecked());
                return true;
            case R.id.action_settings:
                return true;
            default:
                return false;
        }
    }
}
