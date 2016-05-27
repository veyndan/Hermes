package com.veyndan.hermes.home;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.veyndan.hermes.BaseActivity;
import com.veyndan.hermes.R;
import com.veyndan.hermes.model.Comic;
import com.veyndan.hermes.service.ComicService;
import com.veyndan.hermes.ui.AutoLayoutManager;
import com.veyndan.hermes.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private final List<Comic> comics = new ArrayList<>();
    private final HomeAdapter adapter = new HomeAdapter(comics);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(AutoLayoutManager.staggeredGridLayoutManager(
                this, UIUtils.getScreenWidth(this), 1080));
        recyclerView.setAdapter(adapter);

        ComicService comicService = new ComicService();

        comicService.fetchComics(1600)
                .compose(this.<Comic>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comic -> {
                    comics.add(comic);
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
            case R.id.action_settings:
                return true;
            default:
                return false;
        }
    }
}
