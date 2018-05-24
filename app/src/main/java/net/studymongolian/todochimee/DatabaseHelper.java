package net.studymongolian.todochimee;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_user_dict.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = DatabaseHelper.class.getName();


    private final Context context;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(UserDictionaryEntry.CREATE_USER_DICTIONARY_TABLE);
            insertDefaultInitialData(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserDictionaryEntry.DROP_USER_DICTIONARY_TABLE);
        onCreate(db);
    }

    private void insertDefaultInitialData(SQLiteDatabase db) {
        String[] words = context.getResources().getStringArray(R.array.default_words);
        ContentValues contentValues = new ContentValues();
        for (String word : words) {
            contentValues.put(UserDictionaryEntry.WORD, word);
            db.insert(UserDictionaryEntry.TABLE_NAME, null, contentValues);
        }
    }
    // TODO test what happens if there are duplicate words
}
