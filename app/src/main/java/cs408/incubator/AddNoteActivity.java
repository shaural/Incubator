package cs408.incubator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import firestore_library.FirestoreLibraryKt;

public class AddNoteActivity extends AppCompatActivity {
    String idea_id = "";
//    String USERNAME = FirestoreLibraryKt.getUSERNAME();
    String USERNAME = "User1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.addNoteToolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        idea_id = i.getStringExtra("ideaID");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm_add_note:
                // Save note
                final EditText tv_title = findViewById(R.id.et_add_note_title);
                final EditText tv_desc = findViewById(R.id.et_add_note_desc);
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference docRef = db.collection("Notes").document(idea_id);

                String title = tv_title.getText().toString();
                String desc = tv_desc.getText().toString();

                if (title.length() == 0) {
                    Toast.makeText(this, "Cannot have an empty title.", Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }

                if (desc.length() == 0) {
                    Toast.makeText(this, "Cannot create an empty note.", Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }

                Map<String, Object> data = new HashMap<>();
                Map<String, String> mitem = new HashMap<>();
                mitem.put(title, desc);
                data.put(USERNAME, mitem);
                docRef.set(data, SetOptions.merge());
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
