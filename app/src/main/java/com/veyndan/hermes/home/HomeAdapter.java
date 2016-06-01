package com.veyndan.hermes.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.veyndan.hermes.R;
import com.veyndan.hermes.home.model.Comic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.VH> {

    private final List<Comic> comics;

    public HomeAdapter(List<Comic> comics) {
        this.comics = comics;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Context context = holder.itemView.getContext();
        Comic comic = comics.get(position);

        Glide.with(context).load(comic.img()).into(holder.img);
        holder.title.setText(comic.title());
        holder.num.setText(comic.displayNum());
        holder.alt.setText(comic.alt());
    }

    @Override
    public int getItemCount() {
        return comics.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        @BindView(R.id.img) ImageView img;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.num) TextView num;
        @BindView(R.id.alt) TextView alt;

        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            RxView.layoutChangeEvents(img)
                    .take(1)
                    .subscribe(viewLayoutChangeEvent -> {
                        img.getLayoutParams().height = (int) (img.getWidth() * (9f / 16f));
                    });
        }
    }
}
