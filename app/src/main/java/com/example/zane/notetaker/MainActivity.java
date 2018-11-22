package com.example.zane.notetaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText notesEditText;
    Button buttonSettings;

    private static final int SETTINGS_INFO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesEditText = (EditText) findViewById(R.id.notesEditText);

        //If savedInstanceState has a value, means there was a config change
        //We must retrieve the values and apply them back to the view
        if(savedInstanceState != null) {

            String notes = savedInstanceState.getString("NOTES");

            notesEditText.setText(notes);
        }

        //Use shared preferences to persist data through the app being closed
        String sPNotes = getPreferences(Context.MODE_PRIVATE).getString("NOTES", "EMPTY");

        //if not empty, means we were able to retrieve something
        if(!sPNotes.equals("EMPTY")){

            notesEditText.setText(sPNotes);

        }


        buttonSettings = (Button) findViewById(R.id.button);

        buttonSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intentPreferences = new Intent(getApplicationContext(),
                        SettingsActivity.class);

                startActivityForResult(intentPreferences, SETTINGS_INFO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SETTINGS_INFO) {
            updateNoteText();
        }
    }

    private void updateNoteText() {

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPreferences.getBoolean("pref_text_bold",false)){
            notesEditText.setTypeface(null, Typeface.BOLD_ITALIC);
        } else {
            notesEditText.setTypeface(null, Typeface.NORMAL);
        }

        String textSizeStr = sharedPreferences.getString("pref_text_size", "16");

        float textSizeFloat = Float.parseFloat(textSizeStr);

        notesEditText.setTextSize(textSizeFloat);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save notes in case of config change
        outState.putString("NOTES",
                notesEditText.getText().toString());

        super.onSaveInstanceState(outState);
    }

    private void saveSettings() {
        /*
        * sets access to private, only accessed by this app
        * saves data if app is force closed by user
        */
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();

        editor.putString("NOTES", notesEditText.getText().toString());

        editor.commit();
    }

    @Override
    protected void onStop() {
        //onStop is called when app is closed, this is when we save notes to sharedPreferences
        saveSettings();

        super.onStop();
    }
}
