package hogent.intents;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.inputTextView)
    TextView inputTextView;

    private String userInputString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            inputTextView.setText(savedInstanceState.getString("inputText"));
        }
    }


    @OnClick(R.id.speechButton)
    public void onBtnCommandClick() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.NoSpeech),
                    Toast.LENGTH_SHORT).show();
        }

    }


    @OnClick(R.id.websiteButton)
    public void onBtnSiteClick() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View dialogView = li.inflate(R.layout.dialog_main, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView
                .findViewById(R.id.inputEditText);
        TextView dialogTextView = (TextView) dialogView.findViewById(R.id.dialogTextView);
        dialogTextView.setText("Website: ");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                MainActivity.this.userInputString = userInput.getText().toString();

                                if (!MainActivity.this.userInputString.contains("http://")) {
                                    MainActivity.this.userInputString = "http://" + MainActivity.this.userInputString;
                                }

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.this.userInputString));
                                startActivity(browserIntent);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    //    Button but = (Button) findViewById(R.id.googleButton);
//    but.setOnClickListener(new OnClickListener());
    @OnClick(R.id.googleButton)
    public void onBtnGoogleClick() {

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View dialogView = li.inflate(R.layout.dialog_main, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView
                .findViewById(R.id.inputEditText);
        TextView dialogTextView = (TextView) dialogView.findViewById(R.id.dialogTextView);
        dialogTextView.setText("Google: ");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.userInputString = userInput.getText().toString();

                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                intent.putExtra(SearchManager.QUERY, userInputString);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @OnClick(R.id.contactsButton)
    public void onBtnContactsClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.dialerButton)
    public void onBtnDialerClick() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        startActivity(intent);
    }


    private void openBol() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bol.com"));
        startActivity(browserIntent);
    }

    private void googleSomething(String searchQuery) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, searchQuery);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    inputTextView.setText(result.get(0));

                    switch (result.get(0).toLowerCase()) {
                        case "contacten":
                            onBtnContactsClick();
                            break;
                        case "telefoon":
                            onBtnDialerClick();
                            break;
                        case "google":
                            onBtnGoogleClick();
                            break;
                        case "website":
                            onBtnSiteClick(); // vraagt nog welke site
                            break;
                        case "bol.com":
                            openBol(); // direct naar bol.com
                            break;
                        default:
                            break;
                    }

                    if (result.get(0).toLowerCase().startsWith("google")) {
                        googleSomething(result.get(0).toLowerCase().substring(6));
                    }
                }
                break;
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("inputText", inputTextView.getText().toString());
    }


}
