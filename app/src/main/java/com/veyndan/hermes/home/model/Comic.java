package com.veyndan.hermes.home.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import rx.functions.Func1;

@AutoValue
public abstract class Comic {

    public static final String TABLE = "xkcd";

    public static final String NUM = BaseColumns._ID;
    public static final String ALT = "alt";
    public static final String IMG = "img";
    public static final String IMG_DIMEN = "imgDimen";
    public static final String TITLE = "title";
    public static final String FAVORITE = "favorite";

    @ColumnName(NUM) public abstract int num();
    public abstract String alt();
    public abstract String img();
    public abstract float imgDimen();
    public abstract String title();
    public abstract boolean favorite();

    public abstract Comic withImgDimen(float imgDimen);
    public abstract Comic withFavorite(boolean favorite);

    public abstract ContentValues toContentValues();

    public static JsonAdapter<Comic> jsonAdapter(Moshi moshi) {
        return new AutoValue_Comic.MoshiJsonAdapter(moshi);
    }

    public static Comic create(Cursor cursor) {
        return AutoValue_Comic.createFromCursor(cursor);
    }

    public static final Func1<Cursor, Comic> MAPPER = Comic::create;

    public String displayNum() {
        return "#" + num();
    }
}
