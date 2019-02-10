package cs408.incubator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tags extends AppCompatActivity {
String idea_id = "irWRcr2YIjDxi2kgEn93";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Ideas").document(idea_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                final List<String> tag_list = (List<String>)document.get("Tags");
                ListView lv_tags = (ListView)findViewById(R.id.lv_tags);
                ArrayAdapter<String> aa = new ArrayAdapter<String>(getApplicationContext(), R.layout.layout_list_tag, tag_list);
                lv_tags.setAdapter( aa );
                // register onClickListener to handle click events on each item
                lv_tags.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    // argument position gives the index of item which is clicked
                    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
                    {
                        final String selected_tag = tag_list.get(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Tags.this);
                        builder.setTitle("Update Tag");

                        final EditText input = new EditText(Tags.this);
                        input.setText(selected_tag);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String m_Text = input.getText().toString();

                                docRef.update("Tags", FieldValue.arrayRemove(selected_tag));
                                docRef.update("Tags", FieldValue.arrayUnion(m_Text));
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });
                lv_tags.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                   int pos, long id) {
                        final String selected_tag = tag_list.get(pos);
                        final DocumentReference docRef = db.collection("Ideas").document(idea_id);
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int choice) {
                                switch (choice) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        docRef.update("Tags", FieldValue.arrayRemove(selected_tag));
                                        finish();
                                        startActivity(getIntent());
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(Tags.this);
                        builder.setMessage("Delete this Tag?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                        return true;
                    }
                });
            }
        });

    }


    public void onClickbtnAddTag(View v)
    {

        EditText et_tag = (EditText)findViewById(R.id.et_tag);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Ideas").document(idea_id);
        // Atomically add a new region to the "regions" array field.
        docRef.update("Tags", FieldValue.arrayUnion(et_tag.getText().toString()));
        Toast.makeText(this, "Tag has been added to Idea.", Toast.LENGTH_LONG).show();
        finish();
        startActivity(getIntent());
    }

    public void onClickbtnBack(View v)
    {
        this.finish();
    }
}
