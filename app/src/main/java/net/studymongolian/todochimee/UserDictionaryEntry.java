package net.studymongolian.todochimee;

import android.provider.BaseColumns;

public class UserDictionaryEntry implements BaseColumns {

    // table
    static final String TABLE_NAME = "words";

    // Column names
    static final String WORD = "word";
    static final String FREQUENCY = "frequency";
    static final String FOLLOWING = "following";

    // SQL statements
    static final String CREATE_USER_DICTIONARY_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY,"
            + WORD + " TEXT NOT NULL UNIQUE,"
            + FREQUENCY + " INTEGER DEFAULT 1,"
            + FOLLOWING + " TEXT NOT NULL DEFAULT ''" +
            ")";

    static final String DROP_USER_DICTIONARY_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
