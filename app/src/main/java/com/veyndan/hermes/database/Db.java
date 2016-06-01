package com.veyndan.hermes.database;

import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.veyndan.hermes.home.model.Comic;

import java.util.List;

public class Db {

    public static void insert(BriteDatabase db, List<Comic> comics) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (Comic comic: comics) {
                db.insert(Comic.TABLE, comic.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

}
