package com.veyndan.hermes.ui;

import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

// Modified: http://stackoverflow.com/a/30256880
public class AutoStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    private int columnWidth;

    public AutoStaggeredGridLayoutManager(@IntRange(from = 1) int columnWidth) {
        super(1, VERTICAL);
        this.columnWidth = columnWidth;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int width = getWidth();
        int height = getHeight();
        if (width > 0 && height > 0) {
            int totalSpace = width - getPaddingRight() - getPaddingLeft();
            int spanCount = Math.max(1, totalSpace / columnWidth);
            new Handler().post(() -> setSpanCount(spanCount));
        }
        super.onLayoutChildren(recycler, state);
    }
}
