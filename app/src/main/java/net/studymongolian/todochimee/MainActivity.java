package net.studymongolian.todochimee;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolLabel;
import net.studymongolian.mongollibrary.MongolToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String PREFS_NAME = "TodoPrefsFile";
    private static final String DRAFT_KEY = "draft";
    private static final String CURSOR_POSITION_KEY = "cursor_position";

    MongolEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (keyboardIsEnabled()) {
            MongolLabel activateButton = findViewById(R.id.btnActivate);
            activateButton.setVisibility(View.GONE);
        }

        editText = findViewById(R.id.editText);
        editText.requestFocus();
        setSavedDraft();
    }

    private void setSavedDraft() {
        if (editText.getText().length() == 0) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String savedText = settings.getString(DRAFT_KEY, "");
            editText.setText(savedText);
            int cursorPosition = settings.getInt(CURSOR_POSITION_KEY, 0);
            if (cursorPosition == 0)
                cursorPosition = savedText.length();
            editText.setSelection(cursorPosition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // in case user accidentally closes app
        saveInputWindowDraftToSharedPreferences();
    }

    private void saveInputWindowDraftToSharedPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String text = editText.getText().toString();
        int cursorPosition = editText.getSelectionStart();
        editor.putString(DRAFT_KEY, text);
        editor.putInt(CURSOR_POSITION_KEY, cursorPosition);
        editor.apply();
    }

    private boolean keyboardIsEnabled() {
        String packageLocal = getPackageName();
        boolean isInputDeviceEnabled = false;
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return false;
        List<InputMethodInfo> list = inputMethodManager.getEnabledInputMethodList();

        // check if our keyboard is enabled as input method
        for (InputMethodInfo inputMethod : list) {
            String packageName = inputMethod.getPackageName();
            if (packageName.equals(packageLocal)) {
                isInputDeviceEnabled = true;
            }
        }

        return isInputDeviceEnabled;
    }

    public void onActivateButtonClick(View view) {
        Intent inputSettings = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        startActivityForResult(inputSettings, 0);
    }

    public void onChooseButtonClick(View view) {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (im == null) return;
        im.showInputMethodPicker();
    }

    public void onCopyButtonClick(View view) {
        String text = editText.getText().toString();
        if (TextUtils.isEmpty(text)) return;
        int startSelection = editText.getSelectionStart();
        int endSelection = editText.getSelectionEnd();
        String selectedText = editText.getText().toString().substring(startSelection, endSelection);
        if (!TextUtils.isEmpty(selectedText)) {
            text = selectedText;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("todo", text);
        if (clipboard == null) return;
        clipboard.setPrimaryClip(clip);

        String message = getString(R.string.text_copied);
        MongolToast.makeText(this, message, MongolToast.LENGTH_SHORT).show();
    }

    public void onPasteButtonClick(View view) {
        String textToInsert = readFromClipboard();
        if (TextUtils.isEmpty(textToInsert)) return;

        int start = Math.max(editText.getSelectionStart(), 0);
        int end = Math.max(editText.getSelectionEnd(), 0);
        editText.getText().replace(Math.min(start, end), Math.max(start, end),
                textToInsert, 0, textToInsert.length());
    }

    private String readFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) return "";
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        return "";
    }

    public void onShareButtonClick(android.view.View view) {

        Context context = getApplicationContext();


        editText.setCursorVisible(false);

        Bitmap bitmap = Bitmap.createBitmap(editText.getWidth(), editText.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        editText.draw(canvas);

        // save bitmap to cache directory
        try {

            File cachePath = new File(context.getCacheDir(), "images");
            //noinspection ResultOfMethodCallIgnored
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(context.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(context, "net.studymongolian.todochimee.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, null));
        }

        editText.setCursorVisible(true);
    }
}
