package com.veyndan.hermes.ui;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

public class AutoLayoutManager {

    @NonNull
    public static RecyclerView.LayoutManager staggeredGridLayoutManager(
            @Nullable Context context, @IntRange(from = 0) int recyclerViewWidth,
            @IntRange(from = 0) int maxCardWidth) {
        int spanCount = (int) Math.ceil((float) recyclerViewWidth / maxCardWidth);
        if (spanCount == 1) {
            return new LinearLayoutManager(context);
        }
        return new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
    }
}
