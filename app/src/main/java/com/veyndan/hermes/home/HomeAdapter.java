package com.veyndan.hermes.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.veyndan.hermes.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.VH> {

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Context context = holder.itemView.getContext();

        Glide.with(context).load("https://imgs.xkcd.com//comics//woodpecker.png").into(holder.img);
        holder.title.setText("Woodpecker");
        holder.num.setText("614");
        holder.alt.setText("If you don't have an extension cord I can get that too.  Because we're friends!  Right?");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class VH extends RecyclerView.ViewHolder {
        @BindView(R.id.img) ImageView img;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.num) TextView num;
        @BindView(R.id.alt) TextView alt;

        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
