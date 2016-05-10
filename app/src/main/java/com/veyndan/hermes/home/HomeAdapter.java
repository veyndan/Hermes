package com.veyndan.hermes.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.VH> {

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class VH extends RecyclerView.ViewHolder {
        public VH(View itemView) {
            super(itemView);
        }
    }
}
