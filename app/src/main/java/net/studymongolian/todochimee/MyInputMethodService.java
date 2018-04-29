package net.studymongolian.todochimee;

import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import net.studymongolian.mongollibrary.ImeContainer;

public class MyInputMethodService extends InputMethodService implements ImeContainer.OnSystemImeListener {

    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();
        ImeContainer jianpan = (ImeContainer) inflater.inflate(R.layout.jianpan_yangshi, null, false);
        jianpan.showSystemKeyboardsOption("ᠰᡇᡊᡎᡇᡍᡇ"); // 长按键盘键可以切换到别的系统输入法
        jianpan.setOnSystemImeListener(this);
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
}