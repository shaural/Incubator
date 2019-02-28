package cs408.incubator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

import cs408.incubator.R;
import firestore_library.FirestoreLibraryKt;

public class NotesActivity extends AppCompatActivity {
    String idea_id = "idea1";
//    String USERNAME = FirestoreLibraryKt.getUSERNAME();
    String USERNAME = "User1";
    List<String> title_list = null;
    List<String> val_list = null;
    FirebaseFirestore db = null;
    DocumentReference docRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Intent i = getIntent();
//        idea_id = i.getStringExtra("ideaID");

        final ListView notes_list = findViewById(R.id.lv_notes);
        final TextView empty = findViewById(R.id.tv_notes_empty);
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("Notes").document(idea_id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, String> map_user_notes = (Map<String, String>)document.get(USERNAME);
                        if(map_user_notes.isEmpty()) {
                            // no notes
                            empty.setVisibility(View.VISIBLE);
                            notes_list.setVisibility(View.INVISIBLE);
                        } else {
                            empty.setVisibility(View.INVISIBLE);
                            notes_list.setVisibility(View.VISIBLE);

                            // display titles in list view
                            title_list = new ArrayList<String>(map_user_notes.keySet());
                            val_list = new ArrayList<>(map_user_notes.values());
                            ArrayAdapter<String> itemsAdapter =
                                    new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, title_list);
                            notes_list.setAdapter(itemsAdapter);
                            registerForContextMenu(notes_list);
                            // register onClickListener to handle click events on each item
                            notes_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                // argument position gives the index of item which is clicked
                                public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                                    final String selected_title = title_list.get(position);
                                    final String selected_desc = val_list.get(position);
                                    Intent i = new Intent(getApplicationContext(), AddNoteActivity.class);
                                    i.putExtra("ideaID", idea_id);
                                    i.putExtra("new_note", false);
                                    i.putExtra("title", selected_title);
                                    i.putExtra("desc", selected_desc);
                                    startActivity(i);
                                }
                            });
                        }
                    } else {
                        empty.setVisibility(View.VISIBLE);
                        notes_list.setVisibility(View.INVISIBLE);
                    }
                } else {
                    empty.setVisibility(View.VISIBLE);
                    notes_list.setVisibility(View.INVISIBLE);
                }
            }
        });
                    FloatingActionButton fab = findViewById(R.id.fabAddNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddNoteActivity.class);
                i.putExtra("ideaID", idea_id);
                i.putExtra("new_note", true);
                startActivity(i);
            }
        });

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lv_notes) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(title_list.get(info.position));
            String[] menuItems = {"Edit", "Share", "Delete"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final String selected_title = title_list.get(info.position);
        final String selected_desc = val_list.get(info.position);
        if(menuItemIndex == 0) {
            // edit
            Intent i = new Intent(getApplicationContext(), AddNoteActivity.class);
            i.putExtra("ideaID", idea_id);
            i.putExtra("new_note", false);
            i.putExtra("title", selected_title);
            i.putExtra("desc", selected_desc);
            startActivity(i);
        } else if(menuItemIndex == 1) {
            // Share
        } else if(menuItemIndex == 2) {
            // Delete
            Map<String, Object> data = new HashMap<>();
            Map<String, Object> mitem = new HashMap<>();
            mitem.put(selected_title, FieldValue.delete());
            data.put(USERNAME, mitem);
            docRef.set(data, SetOptions.merge());
            finish();
            startActivity(getIntent());
        }


        return true;
    }

}
