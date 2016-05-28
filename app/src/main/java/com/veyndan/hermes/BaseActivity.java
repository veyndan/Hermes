package com.veyndan.hermes;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends RxAppCompatActivity {

    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }
}
