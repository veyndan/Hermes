package com.veyndan.hermes;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public class BaseActivity extends RxAppCompatActivity {

    @Nullable private Toolbar toolbar;

    @Nullable
    protected Toolbar getToolbar() {
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }
        }
        return toolbar;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        getToolbar();
    }
}
