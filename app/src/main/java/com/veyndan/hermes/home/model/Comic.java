package com.veyndan.hermes.home.model;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Comic {

    public static final String TABLE = "xkcd";

    public static final String NUM = BaseColumns._ID;
    public static final String ALT = "alt";
    public static final String IMG = "img";
    public static final String TITLE = "title";

    public abstract int num();
    public abstract String alt();
    public abstract String img();
    public abstract String title();

    public static JsonAdapter<Comic> jsonAdapter(Moshi moshi) {
        return new AutoValue_Comic.MoshiJsonAdapter(moshi);
    }

    public String displayNum() {
        return "#" + num();
    }

    @NonNull
    public ContentValues toContentValues(@NonNull Comic comic) {
        ContentValues values = new ContentValues();
        values.put(NUM, comic.num());
        values.put(ALT, comic.alt());
        values.put(IMG, comic.img());
        values.put(TITLE, comic.title());
        return values;
    }
}