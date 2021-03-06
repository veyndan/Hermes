package com.veyndan.hermes.home;

import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "veyndan_HomeActivity";

    private static final int BUFFER_SIZE = 20;

    private static final String SELECT_FAVORITES = String.format("%s = 1", Comic.FAVORITE);

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private final List<Comic> comics = new ArrayList<>();
    private HomeAdapter adapter;

    private BriteDatabase db;
    private List<String> filters = new ArrayList<>(2);
    private Subscription databaseSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);

        SqlBrite sqlBrite = SqlBrite.create();
        db = sqlBrite.wrapDatabaseHelper(new DbHelper(this), Schedulers.io());

        adapter = new HomeAdapter(comics, db);

        recyclerView.setLayoutManager(new AutoStaggeredGridLayoutManager(700));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
                // Allows animations to persist through notification of data set change.
                return true;
            }
        });

        ComicService comicService = new ComicService();

        query();

        Observable<List<Comic>> network = comicService.fetchComics(1, 40)
                .buffer(BUFFER_SIZE)
                .doOnNext(comics -> {
                    Db.insert(db, comics);
                    Log.d(TAG, "Network request: " + comics.get(0).num() + " to " + comics.get(comics.size() - 1).num());
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

    private void query() {
        if (databaseSubscription != null) databaseSubscription.unsubscribe();

        String sql = SQLiteQueryBuilder.buildQueryString(false, Comic.TABLE, null,
                TextUtils.join(" ", filters), null, null, Comic.NUM + " DESC", null);

        databaseSubscription = db.createQuery(Comic.TABLE, sql)
                .compose(this.<SqlBrite.Query>bindToLifecycle())
                .concatMap(query -> query.asRows(Comic.MAPPER).toList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comics -> {
                    this.comics.clear();
                    this.comics.addAll(comics);
                    adapter.notifyDataSetChanged();
                });
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
                item.setChecked(!item.isChecked());
                if (item.isChecked()) filters.add(SELECT_FAVORITES);
                else filters.remove(SELECT_FAVORITES);
                query();
                return true;
            case R.id.action_filter_unread:
                item.setChecked(!item.isChecked());
                return true;
            case R.id.action_sort_new:
            case R.id.action_sort_old:
                // If the item is already checked, then the direction of the list is correct as is.
                if (item.isChecked()) return false;
                item.setChecked(true);
                Collections.reverse(comics);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return false;
        }
    }
}
