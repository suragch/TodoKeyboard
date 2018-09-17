package net.studymongolian.todochimee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import net.studymongolian.mongollibrary.ImeContainer;
import net.studymongolian.mongollibrary.MongolCode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MyInputMethodService extends InputMethodService
        implements ImeContainer.OnSystemImeListener, ImeContainer.DataSource {

    ImeContainer imeContainer;

    @SuppressLint("InflateParams") // there is apparently no root view to pass in as the parent?
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
    public void onSystemKeyboardRequest() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (im == null) return;
        im.showInputMethodPicker();
    }

    @Override
    public void onHideKeyboardRequest() {
        requestHideSelf(0);
    }

    // ImeContainer.DataSource的方法

    @Override
    public void onRequestWordsStartingWith(String text) {
        if (text.startsWith(String.valueOf(MongolCode.Uni.NNBS))) {
            new GetSuffixesStartingWith(this).execute(text);
        } else {
            new GetWordsStartingWith(this).execute(text);
        }
    }

    @Override
    public void onWordFinished(String word, String previousWord) {
        new AddOrUpdateDictionaryWordsTask(this).execute(word, previousWord);
    }

    @Override
    public void onCandidateClick(int position, String word, String previousWordInEditor) {
        addSpace();
        new RespondToCandidateClick(this).execute(word, previousWordInEditor);
    }

    private void addSpace() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.commitText(" ", 1);
    }

    @Override
    public void onCandidateLongClick(int position, String text, String previousWordInEditor) {
        new DeleteWord(this, position).execute(text, previousWordInEditor);
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

        }

        @Override
        protected void onPostExecute(List<String> result) {
            MyInputMethodService service = serviceReference.get();
            if (service == null) return;

            if (result.size() > 0)
                service.imeContainer.setCandidates(result);
            else
                service.imeContainer.clearCandidates();
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

            UserDictionary.Words.addFollowing(context, previousWord, word);
            return null;
        }

    }

    private static void insertUpdateWord(Context context, String word) {
        if (context == null) return;

        int id = UserDictionary.Words.incrementFrequency(context, word);
        if (id < 0) {
            UserDictionary.Words.addWord(context, word);
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
            MyInputMethodService service = serviceReference.get();

            int id = UserDictionary.Words.incrementFrequency(service, word);
            if (word.charAt(0) == MongolCode.Uni.NNBS) {
                if (id < 0) {
                    // it should already be in the suffix database, but adding it
                    // to the user dictionary will make it so that there is no error
                    // when incrementing the frequency in the user dictionary later
                    UserDictionary.Words.addWord(service, word);
                }
                incrementSuffixFrequency(service, word);

            }
            UserDictionary.Words.addFollowing(service, previousWord, word);
            return UserDictionary.Words.getFollowing(service, word);
        }

        private void incrementSuffixFrequency(Context context, String suffix) {
            SuffixDatabaseAdapter adapter = new SuffixDatabaseAdapter(context);
            adapter.updateFrequencyForSuffix(suffix);
        }

        @Override
        protected void onPostExecute(List<String> followingWords) {
            MyInputMethodService service = serviceReference.get();
            if (service == null) return;
            if (followingWords.size() == 0) {
                service.imeContainer.clearCandidates();
            } else {
                service.imeContainer.setCandidates(followingWords);
            }
        }
    }

    private static class DeleteWord extends AsyncTask<String, Integer, Void> {

        private WeakReference<MyInputMethodService> serviceReference;
        private int index;

        DeleteWord(MyInputMethodService context, int index) {
            serviceReference = new WeakReference<>(context);
            this.index = index;
        }

        @Override
        protected Void doInBackground(String... params) {
            String word = params[0];
            String previousWord = params[1];
            Context context = serviceReference.get();
            UserDictionary.Words.deleteWord(context, word);
            UserDictionary.Words.deleteFollowingWord(context, previousWord, word);
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            MyInputMethodService service = serviceReference.get();
            if (service == null) return;
            service.imeContainer.removeCandidate(index);
        }
    }

    private static class GetSuffixesStartingWith extends AsyncTask<String, Integer, List<String>> {


        private WeakReference<MyInputMethodService> classReference;

        GetSuffixesStartingWith(MyInputMethodService context) {
            classReference = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String suffixPrefix = params[0];

            List<String> words = new ArrayList<>();

            MyInputMethodService helper = classReference.get();
            if (helper == null) return words;
            ImeContainer imeContainer = helper.imeContainer;
            if (imeContainer == null) return words;

            List<String> previousWords = imeContainer.getPreviousMongolWords(2, false);
            String wordBeforePreviousWord = previousWords.get(1);

            Suffix.WordEnding ending = getEndingOf(wordBeforePreviousWord);
            Suffix.WordGender gender;
            if (ending == Suffix.WordEnding.Nil) {
                gender = getWordGender(suffixPrefix);
            } else {
                gender = getWordGender(wordBeforePreviousWord);
            }

            SuffixDatabaseAdapter adapter = new SuffixDatabaseAdapter(helper.getApplicationContext());
            return adapter.findSuffixesBeginningWith(suffixPrefix, gender, ending);
        }

        private Suffix.WordEnding getEndingOf(String word) {

            Suffix.WordEnding ending = Suffix.WordEnding.Nil;

            if (TextUtils.isEmpty(word)) {
                return ending;
            }

            // determine ending character
            char endingChar = word.charAt(word.length() - 1);
            if (MongolCode.isFVS(endingChar)) {
                if (word.length() > 1) {
                    endingChar = word.charAt(word.length() - 2);
                } else {
                    return ending;
                }
            }

            // determine type
            if (MongolCode.isVowel(endingChar)) {
                ending = Suffix.WordEnding.Vowel;
            } else if (MongolCode.isConsonant(endingChar)) {
                if (endingChar == MongolCode.Uni.NA) {
                    ending = Suffix.WordEnding.N;
                } else if (isBGDRS(endingChar)) {
                    ending = Suffix.WordEnding.BigDress;
                } else {
                    ending = Suffix.WordEnding.OtherConsonant;
                }
            }

            return ending;
        }

        private Suffix.WordGender getWordGender(String word) {
            MongolCode.Gender gender = MongolCode.getWordGender(word);
            if (gender == null)
                return Suffix.WordGender.Neutral;
            switch (gender) {
                case MASCULINE:
                    return Suffix.WordGender.Masculine;
                case FEMININE:
                    return Suffix.WordGender.Feminine;
                default:
                    return Suffix.WordGender.Neutral;
            }
        }

        private boolean isBGDRS(char character) {
            return (character == MongolCode.Uni.BA ||
                    character == MongolCode.Uni.GA ||
                    character == MongolCode.Uni.DA ||
                    character == MongolCode.Uni.RA ||
                    character == MongolCode.Uni.SA);
        }
        @Override
        protected void onPostExecute(List<String> result) {
            MyInputMethodService helper = classReference.get();
            if (helper == null) return;
            ImeContainer imeContainer = helper.imeContainer;
            if (imeContainer == null) return;

            if (result.size() > 0)
                imeContainer.setCandidates(result);
            else
                imeContainer.clearCandidates();
        }

    }
}