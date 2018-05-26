package net.studymongolian.todochimee;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_user_dict.db";
    private static final int DATABASE_VERSION = 1;
    //private static final String TAG = DatabaseHelper.class.getName();
    private static final String DEFAULT_WORD_LIST_FILE = "default_candidates.txt";


    private final Context context;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(UserDictionaryEntry.CREATE_USER_DICTIONARY_TABLE);
            new InsertDefaultInitialData(context).execute(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserDictionaryEntry.DROP_USER_DICTIONARY_TABLE);
        onCreate(db);
    }

//    private void insertDefaultInitialData(SQLiteDatabase db) {
//        String[] words = context.getResources().getStringArray(R.array.default_words);
//        ContentValues contentValues = new ContentValues();
//        for (String word : words) {
//            contentValues.put(UserDictionaryEntry.WORD, word);
//            db.insert(UserDictionaryEntry.TABLE_NAME, null, contentValues);
//        }
//    }



    // TODO test what happens if there are duplicate words

    private static class InsertDefaultInitialData extends AsyncTask<SQLiteDatabase, Void, Void> {

        private static final String TAG = "TAG";
        private WeakReference<Context> contextReference;

        InsertDefaultInitialData(Context context) {
            contextReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(SQLiteDatabase... params) {

            SQLiteDatabase db = params[0];
            Context context = contextReference.get();
            if (context == null) return null;

            Log.i(TAG, "doInBackground: inserting words starting");
            try {
                List<String> allWords = importFile(context);
                bulkInsert(db, allWords);
            } catch (Exception e) {
                Log.e("app", e.toString());
            }
            Log.i(TAG, "doInBackground: inserting words finished");

            return null;
        }

        private List<String> importFile(Context context) throws IOException {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(DEFAULT_WORD_LIST_FILE)));

            List<String> words = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                words.add(line);
                line = reader.readLine();
            }
            reader.close();
            return words;
        }

        private void bulkInsert(SQLiteDatabase db, List<String> words) {
            try {

                db.beginTransaction();
                String sql = "INSERT INTO " + UserDictionaryEntry.TABLE_NAME +
                        " (" + UserDictionaryEntry.WORD + ") VALUES (?)";
                SQLiteStatement statement = db.compileStatement(sql);

                for (String word : words) {
                    statement.clearBindings();
                    statement.bindString(1, word);
                    statement.executeInsert();
                }

                db.setTransactionSuccessful();

            } catch (SQLException e) {
                Log.e("DatabaseManager", "bulkInsert: ", e);
            } finally {
                db.endTransaction();
                db.close();
            }
        }

    }
}
