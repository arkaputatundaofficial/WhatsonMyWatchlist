package com.deluxedevelopment.whatsonmywatchlist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WatchlistDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "watchlist.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_WATCHLIST = "watchlist";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_LIKED = "liked";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_WATCHLIST + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_TYPE + " text not null, "
            + COLUMN_STATUS + " text not null, "
            + COLUMN_NOTES + " text, "
            + COLUMN_LIKED + " integer);";

    public WatchlistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_WATCHLIST +
                    " ADD COLUMN " + COLUMN_LIKED + " integer;");
        }
    }
}