package net.studymongolian.todochimee;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
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

import static android.content.ContentValues.TAG;

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
        new GetWordsFollowing(this).execute(word);
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
            DatabaseManager db = new DatabaseManager(serviceReference.get());
            return db.queryWordsStartingWith(prefix, MAX_CANDIDATES);
        }

        @Override
        protected void onPostExecute(List<String> result) {
            MyInputMethodService service = serviceReference.get();
            if (service == null) return;

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
            DatabaseManager db = new DatabaseManager(context);
            db.insertOrUpdateWord(word, previousWord);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            MyInputMethodService service = serviceReference.get();
            if (service == null) return;

        }
    }

    private static class GetWordsFollowing extends AsyncTask<String, Integer, Word> {

        private WeakReference<MyInputMethodService> serviceReference;

        GetWordsFollowing(MyInputMethodService context) {
            serviceReference = new WeakReference<>(context);
        }

        @Override
        protected Word doInBackground(String... params) {
            String word = params[0];
            Context context = serviceReference.get();
            DatabaseManager db = new DatabaseManager(context);
            return db.queryWord(word);
        }

        @Override
        protected void onPostExecute(Word result) {
            MyInputMethodService service = serviceReference.get();
            if (service == null) return;
            List<String> words = getFollowingWords(result);
            service.imeContainer.setCandidates(words);
        }

        private List<String> getFollowingWords(Word word) {
            String following = word.getFollowing();
            String[] followingSplit = following.split(",");
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