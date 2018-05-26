package net.studymongolian.todochimee;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.studymongolian.mongollibrary.ImeContainer;
import net.studymongolian.mongollibrary.MongolToast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyInputMethodService extends InputMethodService
        implements ImeContainer.OnSystemImeListener, ImeContainer.DataSource {

    private static final int MAX_CANDIDATES = 10;

    ImeContainer imeContainer;

    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();
        imeContainer = (ImeContainer) inflater.inflate(R.layout.jianpan_yangshi, null, false);
        imeContainer.showSystemKeyboardsOption(getString(R.string.show_system_keyboards_option)); // 长按键盘键可以切换到别的系统输入法
        imeContainer.setOnSystemImeListener(this);
        imeContainer.setDataSource(this);
        return imeContainer;
    }

    // ImeContainer.OnSystemImeListener的方法

    @Override
    public InputConnection getInputConnection() {
        return getCurrentInputConnection();
    }

    @Override
    public void onChooseNewSystemKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (im == null) return;
        im.showInputMethodPicker();
    }

    // ImeContainer.DataSource的方法

    @Override
    public void onRequestWordsStartingWith(String text) {
        new GetWordsStartingWith(this).execute(text);
    }

    @Override
    public void onWordFinished(String word, String previousWord) {
        new AddOrUpdateDictionaryWordsTask(this).execute(word, previousWord);
    }

    @Override
    public void onCandidateClick(int position, String word) {
        new RespondToCandidateClick(this).execute(word);
    }

    @Override
    public void onCandidateLongClick(int position, String text) {
        new DeleteWord(this).execute(text);
        MongolToast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private static class GetWordsStartingWith extends AsyncTask<String, Integer, List<String>> {

        private WeakReference<MyInputMethodService> serviceReference;

        GetWordsStartingWith(MyInputMethodService context) {
            serviceReference = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String prefix = params[0];

            Context context = serviceReference.get();

            List<String> words = new ArrayList<>();
            Cursor cursor = UserDictionary.Words.queryPrefix(context, prefix);
            if (cursor == null) return words;
            int indexWord = cursor.getColumnIndex(UserDictionary.Words.WORD);
            while (cursor.moveToNext()) {
                words.add(cursor.getString(indexWord));
            }
            cursor.close();
            return words;

//            // If so then update then send results to UI and update LV
//            String following = "";
//            if (cursor.moveToNext()) {
//                following = cursor.getString(cursor
//                        .getColumnIndex(ChimeeUserDictionary.Words.FOLLOWING));
//            }
//            cursor.close();
//
//
//
//            DatabaseManager db = new DatabaseManager(serviceReference.get());
//            return db.queryWordsStartingWith(prefix, MAX_CANDIDATES);
        }

        @Override
        protected void onPostExecute(List<String> result) {
            MyInputMethodService service = serviceReference.get();
            if (service == null) return;

            if (result.size() > 0)
                service.imeContainer.setCandidates(result);
        }
    }

    private static class AddOrUpdateDictionaryWordsTask extends AsyncTask<String, Integer, Void> {

        private WeakReference<MyInputMethodService> serviceReference;

        AddOrUpdateDictionaryWordsTask(MyInputMethodService context) {
            serviceReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... params) {
            String word = params[0];
            String previousWord = params[1];
            Context context = serviceReference.get();
            insertUpdateWord(context, word);

            updateFollowingOfPreviousWord(context, word, previousWord);
            return null;
        }

    }

    private static void insertUpdateWord(Context context, String word) {
        if (context == null) return;

        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[]{BaseColumns._ID,
                UserDictionary.Words.FREQUENCY};
        String selection = UserDictionary.Words.WORD + "=?";
        String[] selectionArgs = {word};

        Cursor cursor = null;
        try {

            cursor = resolver.query(UserDictionary.Words.CONTENT_URI, projection,
                    selection, selectionArgs, null);

            // if exists then increment frequency,
            if (cursor != null && cursor.moveToNext()) {
                incrementFrequency(context, cursor);
            } else { // add word
                UserDictionary.Words.addWord(context, word,
                        UserDictionary.Words.DEFAULT_FREQUENCY, null);
            }

        } catch (Exception e) {
            Log.e("AddOrUpdateDictionary", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static void updateWordFrequency(Context context, String word) {
        if (context == null) return;

        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[]{BaseColumns._ID,
                UserDictionary.Words.FREQUENCY};
        String selection = UserDictionary.Words.WORD + "=?";
        String[] selectionArgs = {word};

        Cursor cursor = null;
        try {
            cursor = resolver.query(UserDictionary.Words.CONTENT_URI, projection,
                    selection, selectionArgs, null);
            incrementFrequency(context, cursor);
        } catch (Exception e) {
            Log.e("AddOrUpdateDictionary", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static void incrementFrequency(Context context, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) return;

        // Get word id from cursor
        long id = cursor.getLong(cursor.getColumnIndex(UserDictionary.Words._ID));
        int frequency = cursor.getInt(cursor.getColumnIndex(UserDictionary.Words.FREQUENCY));
        frequency++;

        // Update word
        UserDictionary.Words.updateWord(context, id, frequency, null);
    }

    private static void updateFollowingOfPreviousWord(Context context, String word, String previousWord) {
        if (TextUtils.isEmpty(previousWord)) return;
        if (context == null) return;

        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[]{BaseColumns._ID,
                UserDictionary.Words.WORD,
                UserDictionary.Words.FOLLOWING};
        String selection = UserDictionary.Words.WORD + "=?";
        String[] selectionArgs = {previousWord};

        // get previous word
        Cursor cursor = null;
        try {
            cursor = resolver.query(UserDictionary.Words.CONTENT_URI, projection,
                    selection, selectionArgs, null);
            updateWordFollowing(context, cursor, word);
        } catch (Exception e) {
            Log.e("AddOrUpdateDictionary", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static void updateWordFollowing(Context context, Cursor cursor, String word) {
        if (cursor == null || !cursor.moveToFirst()) return;

        // Get word id from cursor
        long id = cursor.getLong(cursor
                .getColumnIndex(UserDictionary.Words._ID));
        String following = cursor.getString(cursor
                .getColumnIndex(UserDictionary.Words.FOLLOWING));

        // stick thisWord into following
        following = reorderFollowing(word, following);

        // Update word
        UserDictionary.Words.updateWord(context, id,
                UserDictionary.Words.UNDEFINED_FREQUENCY, following);
    }

    private static String reorderFollowing(String wordToAdd, String following) {

        if (TextUtils.isEmpty(following)) {
            return wordToAdd;
        } else {
            String[] followingSplit = following.split(",");
            StringBuilder builder = new StringBuilder();
            builder.append(wordToAdd);
            int counter = 0;
            for (String item : followingSplit) {
                if (!item.equals(wordToAdd)) {
                    builder.append(",").append(item);
                }
                counter++;
                if (counter >= UserDictionary.MAX_FOLLOWING_WORDS)
                    break;
            }
            return builder.toString();
        }
    }

    private static class RespondToCandidateClick extends AsyncTask<String, Integer, List<String>> {

        private WeakReference<MyInputMethodService> serviceReference;

        RespondToCandidateClick(MyInputMethodService context) {
            serviceReference = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String word = params[0];
            String previousWord = params[1];
            Context context = serviceReference.get();

            updateWordFrequency(context, word);
            updateFollowingOfPreviousWord(context, word, previousWord);
            return getFollowing(word);
        }



        private List<String> getFollowing(String word) {
            return null;
        }

        @Override
        protected void onPostExecute(List<String> followingWords) {
            MyInputMethodService service = serviceReference.get();
            if (service == null) return;
            if (followingWords.size() == 0) {
                // TODO service.imeContainer.clearCandidates();
            } else {
                service.imeContainer.setCandidates(followingWords);
            }
        }

        private List<String> getFollowingWords(String followingString) {
            String[] followingSplit = followingString.split(",");
            return new ArrayList<>(Arrays.asList(followingSplit));
        }

    }

    private static class DeleteWord extends AsyncTask<String, Integer, String> {

        private WeakReference<MyInputMethodService> serviceReference;

        DeleteWord(MyInputMethodService context) {
            serviceReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            String word = params[0];
            Context context = serviceReference.get();
            DatabaseManager db = new DatabaseManager(context);
            int numberOfWordsDeleted = db.deleteWord(word);
            if (numberOfWordsDeleted < 1)
                return null;
            return word;
        }

        @Override
        protected void onPostExecute(String deletedWord) {
            if (deletedWord == null) return;
            MyInputMethodService service = serviceReference.get();
            if (service == null) return;

            // TODO service.imeContainer.removeCandidate(deletedWord);
        }
    }
}