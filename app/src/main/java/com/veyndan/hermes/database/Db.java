package com.veyndan.hermes.database;

import com.squareup.sqlbrite.BriteDatabase;
import com.veyndan.hermes.home.model.Comic;

import java.util.List;

public class Db {

    public static void insert(BriteDatabase db, List<Comic> comics) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (Comic comic: comics) {
                db.insert(Comic.TABLE, comic.toContentValues());
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public static void update(BriteDatabase db, Comic comic) {
        db.update(Comic.TABLE, comic.toContentValues(), Comic.NUM + " = ?", String.valueOf(comic.num()));
    }

}
