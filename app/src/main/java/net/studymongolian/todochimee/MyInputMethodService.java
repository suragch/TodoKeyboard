package net.studymongolian.todochimee;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import net.studymongolian.mongollibrary.ImeContainer;

import java.util.List;

import static android.content.ContentValues.TAG;

public class MyInputMethodService extends InputMethodService
        implements ImeContainer.OnSystemImeListener, ImeContainer.DataSource {

    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();
        ImeContainer jianpan = (ImeContainer) inflater.inflate(R.layout.jianpan_yangshi, null, false);
        jianpan.showSystemKeyboardsOption(getString(R.string.show_system_keyboards_option)); // 长按键盘键可以切换到别的系统输入法
        jianpan.setOnSystemImeListener(this);
        jianpan.setDataSource(this);
        return jianpan;
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
        Log.i(TAG, "onRequestWordsStartingWith: ");
    }

    @Override
    public void onRequestWordsFollowing(String word) {
        Log.i(TAG, "onRequestWordsFollowing: ");
    }

    @Override
    public void onCandidateLongClick(int position, String text) {
        Log.i(TAG, "onCandidateLongClick: ");
    }
}