package net.studymongolian.todochimee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private DatabaseHelper mHelper;

    DatabaseManager(Context context) {
        this.mHelper = new DatabaseHelper(context);
    }

    public void touchDatabase() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.close();
    }

    public List<Word> getAllWords() {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        String[] columns = {
                UserDictionaryEntry._ID,
                UserDictionaryEntry.WORD,
                UserDictionaryEntry.FOLLOWING,
                UserDictionaryEntry.FREQUENCY
        };
        Cursor cursor = db.query(UserDictionaryEntry.TABLE_NAME, columns,
                null, null,null, null, null, null);
        int indexId = cursor.getColumnIndex(UserDictionaryEntry._ID);
        int indexWord = cursor.getColumnIndex(UserDictionaryEntry.WORD);
        int indexFollowing = cursor.getColumnIndex(UserDictionaryEntry.FOLLOWING);
        int indexFrequency = cursor.getColumnIndex(UserDictionaryEntry.FREQUENCY);

        List<Word> wordList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Word wordItem = new Word(cursor.getString(indexWord));
            wordItem.setId(cursor.getLong(indexId));
            wordItem.setFollowing(cursor.getString(indexFollowing));
            wordItem.setFrequency(cursor.getInt(indexFrequency));
            wordList.add(wordItem);
        }

        cursor.close();
        db.close();

        return wordList;
    }

    Word queryWord(String word) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String[] columns = {
                UserDictionaryEntry._ID,
                UserDictionaryEntry.FOLLOWING,
                UserDictionaryEntry.FREQUENCY
        };
        String selection = UserDictionaryEntry.WORD + " =?";
        String[] selectionArgs = {word};
        Cursor cursor = db.query(UserDictionaryEntry.TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);

        int indexId = cursor.getColumnIndex(UserDictionaryEntry._ID);
        int indexFollowing = cursor.getColumnIndex(UserDictionaryEntry.FOLLOWING);
        int indexFrequency = cursor.getColumnIndex(UserDictionaryEntry.FREQUENCY);

        Word result = null;
        if (cursor.moveToFirst()) {
            result = new Word(word);
            result.setId(cursor.getLong(indexId));
            result.setFollowing(cursor.getString(indexFollowing));
            result.setFrequency(cursor.getInt(indexFrequency));
            cursor.close();
            db.close();
        }
        return result;
    }

    public List<String> queryWordsStartingWith(String prefix, int limit) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String[] columns = {
                UserDictionaryEntry.WORD
        };
        String selection = UserDictionaryEntry.WORD + " LIKE ?";
        String[] selectionArgs = {prefix + "%"};
        String limitString = String.valueOf(limit);
        Cursor cursor = db.query(UserDictionaryEntry.TABLE_NAME, columns, selection, selectionArgs,
                null, null, null, limitString);

        int indexWord = cursor.getColumnIndex(UserDictionaryEntry.WORD);

        List<String> words = new ArrayList<>();
        while (cursor.moveToNext()) {
            words.add(cursor.getString(indexWord));
        }
        cursor.close();
        db.close();
        return words;
    }
//
//    public List<String> queryWordsFollowing(String word) {
//        return null;
//    }

    public void bulkInsert(List<String> words) {

        SQLiteDatabase db = mHelper.getWritableDatabase();
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



    public int deleteWord(String word) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String whereClause = UserDictionaryEntry.WORD + " =?";
        String[] whereArgs = {word};
        int count = db.delete(UserDictionaryEntry.TABLE_NAME, whereClause, whereArgs);
        db.close();
        return count;
    }

    public void insertOrUpdateWord(String word, String previousWord) {
        Word result = queryWord(word);
        if (result == null) {
            if (insertWord(word) >= 0) {
                updateFollowing(previousWord, word);
            }
        } else {
            updateFrequency(result);
        }
    }

    private long insertWord(String word) {
        if (TextUtils.isEmpty(word))
            return -1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDictionaryEntry.WORD, word);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id = db.insert(UserDictionaryEntry.TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    private long insertWord(String word, String followingWord) {
        if (TextUtils.isEmpty(word))
            return -1;
        if (TextUtils.isEmpty(followingWord))
            return insertWord(word);
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDictionaryEntry.WORD, word);
        contentValues.put(UserDictionaryEntry.FOLLOWING, followingWord);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id = db.insert(UserDictionaryEntry.TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    private long updateFollowing(String word, String followingWord) {
        if (TextUtils.isEmpty(word)) return -1;
        Word wordData = queryWord(word);
        if (wordData == null) {
            return insertWord(word, followingWord);
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDictionaryEntry.FREQUENCY, wordData.getFrequency() + 1);
        String selection = UserDictionaryEntry.WORD + " =?";
        String[] selectionArgs = {word};
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id = db.update(UserDictionaryEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        db.close();
        return id;
    }

    private long updateFrequency(Word word) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDictionaryEntry.FREQUENCY, word.getFrequency() + 1);
        String selection = UserDictionaryEntry._ID + " =?";
        String[] selectionArgs = {String.valueOf(word.getId())};
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long id = db.update(UserDictionaryEntry.TABLE_NAME,contentValues, selection, selectionArgs);
        db.close();
        return id;
    }
}