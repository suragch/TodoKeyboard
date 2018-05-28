package net.studymongolian.todochimee;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import net.studymongolian.mongollibrary.MongolTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HelpActivity extends AppCompatActivity {

    private static final String FILE = "help.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        MongolTextView textView = findViewById(R.id.help_content);

        try {
            String fileText = readFromAssets(this, FILE);
            textView.setText(fileText);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            if (sb.length() > 0)
                sb.append('\n');
            sb.append(mLine);
            mLine = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }

    public void onContactButtonClick(View view) {
        String contactUrl = getString(R.string.contact_url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contactUrl));
        startActivity(browserIntent);
    }
}