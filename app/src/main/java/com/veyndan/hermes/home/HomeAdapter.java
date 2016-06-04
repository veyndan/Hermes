package com.veyndan.hermes.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.sqlbrite.BriteDatabase;
import com.veyndan.hermes.R;
import com.veyndan.hermes.home.model.Comic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.VH> {

    private static final String TAG = "veyndan_HomeAdapter";

    private final List<Comic> comics;
    private final BriteDatabase db;

    public HomeAdapter(List<Comic> comics, BriteDatabase db) {
        this.comics = comics;
        this.db = db;
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

        Glide.with(context).load(comic.img()).into(new ImageViewTarget<GlideDrawable>(holder.img) {
            @Override
            protected void setResource(GlideDrawable resource) {
                float ratio = (float) resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
                float height = holder.img.getWidth() * ratio;
                holder.img.getLayoutParams().height = (int) height;
                setDrawable(resource);
            }
        });
        holder.title.setText(comic.title());
        holder.num.setText(comic.displayNum());
        holder.alt.setText(comic.alt());
        holder.favorite.setChecked(comic.favorite());

        RxView.clicks(holder.favorite)
                .subscribe(aVoid -> {
                    db.update(Comic.TABLE, comic.withFavorite(holder.favorite.isChecked()).toContentValues(), Comic.NUM + " = ?", String.valueOf(comic.num()));
                });
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
        @BindView(R.id.favorite) ToggleButton favorite;

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
