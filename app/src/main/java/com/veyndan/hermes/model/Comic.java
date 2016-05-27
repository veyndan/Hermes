package com.veyndan.hermes.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Comic {
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
}