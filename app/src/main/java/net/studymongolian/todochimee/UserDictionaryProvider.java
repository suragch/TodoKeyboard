package net.studymongolian.todochimee;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Provides access to a database of user defined words for the Chimee app. Each
 * item has a word, a frequency, and a following word list.
 */
public class UserDictionaryProvider extends ContentProvider {

    private static final String AUTHORITY = UserDictionary.AUTHORITY;
    private static final String DATABASE_NAME = "todo_user_dict.db";
    private static final String DEFAULT_WORD_LIST_FILE = "default_candidates.txt";
    private static final int DATABASE_VERSION = 1;
    private static final String USERDICT_TABLE_NAME = "words";
    private static HashMap<String, String> sDictProjectionMap;
    private static final UriMatcher sUriMatcher;
    private static final int WORDS = 1;
    private static final int WORD_ID = 2;


    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {


        private static final String TAG = "tag";

        Context context;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + USERDICT_TABLE_NAME + " ("
                    + UserDictionary.Words._ID + " INTEGER PRIMARY KEY,"
                    + UserDictionary.Words.WORD + " TEXT NOT NULL UNIQUE,"
                    + UserDictionary.Words.FREQUENCY + " INTEGER DEFAULT 1,"
                    + UserDictionary.Words.FOLLOWING + " TEXT NOT NULL DEFAULT ''"
                    + ");");
            initWithData(db);
        }

        private void initWithData(SQLiteDatabase db) {
            try {
                List<String> allWords = importFile(context);
                bulkInsert(db, allWords);
            } catch (Exception e) {
                Log.e("app", e.toString());
            }
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
                String sql = "INSERT OR IGNORE INTO " + USERDICT_TABLE_NAME +
                        " (" + UserDictionary.Words.WORD + ") VALUES (?)";
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
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USERDICT_TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case WORDS:
                qb.setTables(USERDICT_TABLE_NAME);
                qb.setProjectionMap(sDictProjectionMap);
                break;
            case WORD_ID:
                qb.setTables(USERDICT_TABLE_NAME);
                qb.setProjectionMap(sDictProjectionMap);
                qb.appendWhere("_id" + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = UserDictionary.Words.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null,
                null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        Context context = getContext();
        if (context != null)
            cursor.setNotificationUri(context.getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case WORDS:
                return UserDictionary.Words.CONTENT_TYPE;
            case WORD_ID:
                return UserDictionary.Words.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != WORDS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        if (!values.containsKey(UserDictionary.Words.WORD)) {
            throw new SQLException("Word must be specified");
        }
        if (!values.containsKey(UserDictionary.Words.FREQUENCY)) {
            values.put(UserDictionary.Words.FREQUENCY, String.valueOf(UserDictionary.Words.FREQUENCY));
        }
        if (!values.containsKey(UserDictionary.Words.FOLLOWING)) {
            values.put(UserDictionary.Words.FOLLOWING, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(USERDICT_TABLE_NAME,
                UserDictionary.Words.WORD, values);
        if (rowId > 0) {
            Uri wordUri = ContentUris.withAppendedId(
                    UserDictionary.Words.CONTENT_URI, rowId);
            Context context = getContext();
            if (context != null)
                context.getContentResolver().notifyChange(wordUri, null);
            return wordUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        // Validate the requested uri
        int uriType = sUriMatcher.match(uri);
        if (uriType != WORDS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int insertCount = 0;
        try {

            SQLiteDatabase db = mOpenHelper.getWritableDatabase();

            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(USERDICT_TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("ContentProvider", e.toString());
            } finally {
                db.endTransaction();
            }
            Context context = getContext();
            if (context != null)
                context.getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            Log.e("ContentProvider", e.toString());
        }

        return insertCount;
    }

    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case WORDS:
                count = db.delete(USERDICT_TABLE_NAME, where, whereArgs);
                break;
            case WORD_ID:
                String wordId = uri.getPathSegments().get(1);
                count = db.delete(
                        USERDICT_TABLE_NAME,
                        UserDictionary.Words._ID
                                + "="
                                + wordId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Context context = getContext();
        if (context != null)
            context.getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where,
                      String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case WORDS:
                count = db.update(USERDICT_TABLE_NAME, values, where, whereArgs);
                break;
            case WORD_ID:
                String wordId = uri.getPathSegments().get(1);
                count = db.update(
                        USERDICT_TABLE_NAME,
                        values,
                        UserDictionary.Words._ID
                                + "="
                                + wordId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Context context = getContext();
        if (context != null)
            context.getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "words", WORDS);
        sUriMatcher.addURI(AUTHORITY, "words/#", WORD_ID);
        sDictProjectionMap = new HashMap<>();
        sDictProjectionMap.put(UserDictionary.Words._ID,
                UserDictionary.Words._ID);
        sDictProjectionMap.put(UserDictionary.Words.WORD,
                UserDictionary.Words.WORD);
        sDictProjectionMap.put(UserDictionary.Words.FREQUENCY,
                UserDictionary.Words.FREQUENCY);
        sDictProjectionMap.put(UserDictionary.Words.FOLLOWING,
                UserDictionary.Words.FOLLOWING);
    }
}
