package com.veyndan.hermes.home;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.ryanharter.auto.value.moshi.AutoValueMoshiAdapterFactory;
import com.squareup.moshi.Moshi;
import com.veyndan.hermes.BaseActivity;
import com.veyndan.hermes.Comic;
import com.veyndan.hermes.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private final List<Comic> comics = new ArrayList<>();
    private HomeAdapter adapter;

    private Moshi moshi = new Moshi.Builder().add(new AutoValueMoshiAdapterFactory()).build();

    private Retrofit retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://xkcd.com/")
            .build();

    private interface XKCDService {
        @GET("info.0.json")
        Observable<Comic> latest();

        @GET("{num}/info.0.json")
        Observable<Comic> num(@Path("num") int num);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);

        adapter = new HomeAdapter(comics);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        final XKCDService xkcdService = retrofit.create(XKCDService.class);

        xkcdService.latest()
                .compose(this.<Comic>bindToLifecycle())
                .flatMap(new Func1<Comic, Observable<Comic>>() {
                    @Override
                    public Observable<Comic> call(Comic comic) {
                        Observable<Comic> observable = Observable.empty();
                        for (int num = comic.num(); num > comic.num() - 100; num--) {
                            observable = Observable.concat(observable, xkcdService.num(num));
                        }
                        return observable;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Comic>() {
                    @Override
                    public void call(Comic comic) {
                        comics.add(comic);
                        adapter.notifyDataSetChanged();
                    }
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
