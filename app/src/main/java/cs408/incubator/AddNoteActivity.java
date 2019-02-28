package cs408.incubator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import firestore_library.FirestoreLibraryKt;

public class AddNoteActivity extends AppCompatActivity {
    String idea_id = "";
    //    String USERNAME = FirestoreLibraryKt.getUSERNAME();
    String USERNAME = "User1";
    Boolean new_note = true;
    EditText tv_title = null;
    EditText tv_desc = null;
    String old_title = "";
    String old_desc = "";
    List<String> title_list = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.addNoteToolbar);
        setSupportActionBar(toolbar);

        tv_title = findViewById(R.id.et_add_note_title);
        tv_desc = findViewById(R.id.et_add_note_desc);

        Intent i = getIntent();
        idea_id = i.getStringExtra("ideaID");
        new_note = i.getBooleanExtra("new_note", true);

        if (!new_note) {
            // TODO set edit text values
            String tit = i.getStringExtra("title");
            String des = i.getStringExtra("desc");
            tv_title.setText(tit);
            tv_desc.setText(des);
            old_title = tit;
            old_desc = des;
        }

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Notes").document(idea_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, String> map_user_notes = (Map<String, String>) document.get(USERNAME);
                        title_list = new ArrayList<String>(map_user_notes.keySet());
                    }
                }
            }
        });

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
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference docRef = db.collection("Notes").document(idea_id);

                final String title = tv_title.getText().toString();
                String desc = tv_desc.getText().toString();

                if (title.length() == 0) {
                    Toast.makeText(this, "Cannot have an empty title.", Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }

                if (desc.length() == 0) {
                    Toast.makeText(this, "Cannot create an empty note.", Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }

                // check if title is unique
                if(title_list != null && title_list.contains(title)) {
                    Toast.makeText(this, "Must have a unique title.", Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }

                // check if content changed
                if ((!new_note) && title.equals(old_title) && desc.equals(old_desc)) {
                    // title and desc are the same
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                } else {
                    // delete old
                    Map<String, Object> data = new HashMap<>();
                    Map<String, Object> mitem = new HashMap<>();
                    mitem.put(old_title, FieldValue.delete());
                    data.put(USERNAME, mitem);
                    docRef.set(data, SetOptions.merge());
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
