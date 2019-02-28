package cs408.incubator;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import firestore_library.FirestoreLibraryKt;

import static firestore_library.FirestoreLibraryKt.getDB;


public class IdeaDetailsActivity extends AppCompatActivity {
    String USERNAME = FirestoreLibraryKt.getUSERNAME();

    EditText descTV;
    String tag;
    String idea_owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_details);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.ideaDetailToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        //descTV = findViewById(R.id.descriptionText1);
        //stDesc = getIntent().getExtras().getString("Desc");
        //descTV.setText("test");

        Intent i = getIntent();
        final String ideaTag = i.getStringExtra("ideaTag");
        tag = ideaTag;

        DocumentReference docRef = db.collection("Ideas").document(ideaTag);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        TextView title = findViewById(R.id.ideaName);
                        TextView desc = findViewById(R.id.detailDesc);
                        EditText editableDesc = findViewById(R.id.descriptionText1);
                        TextView tags = findViewById(R.id.tagText);
                        TextView collab = findViewById(R.id.collaboratorText);

                        title.setText(document.getString("Name"));
                        desc.setText(document.getString("Description"));
                        editableDesc.setText(document.getString("Description"));
                        idea_owner = document.getString("Owner");

                        if(document.get("Tags") != null) {
                            ArrayList<String> ideaTags = (ArrayList<String>) document.get("Tags");
                            StringBuilder t = new StringBuilder();
                            for(int i=0; i<ideaTags.size();i++) {
                                if(i==ideaTags.size()-1)
                                    t.append(ideaTags.get(i));
                                else
                                    t.append(ideaTags.get(i)).append("\n");
                            }

                            tags.setText(t.toString());
                        }
                        else
                            tags.setText("No tags yet!");

                        if(document.get("Collaborators") != null) {
                            ArrayList<String> ideaCollabs = (ArrayList<String>) document.get("Collaborators");
                            StringBuilder t = new StringBuilder();
                            for(int i=0; i<ideaCollabs.size();i++) {
                                if(i == ideaCollabs.size()-1)
                                    t.append(ideaCollabs.get(i));
                                else
                                    t.append(ideaCollabs.get(i)).append("\n");
                            }

                            collab.setText(t.toString());
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "No document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Task unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_idea_detail, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    public void btnEditOnClick(View v) {
        ImageButton btn = findViewById(R.id.button);
        EditText et_desc = findViewById(R.id.descriptionText1);
        TextView desc = findViewById(R.id.detailDesc);

        EditText idea_title = findViewById(R.id.editIdeaName);
        TextView prevTitle = findViewById(R.id.ideaName);

        Toast.makeText(getApplicationContext(),btn.getTag().toString(),Toast.LENGTH_SHORT).show();
        if(btn.getTag().toString().equals("edit")){
            idea_title.setText(prevTitle.getText());
            desc.setVisibility(View.GONE);
            prevTitle.setVisibility(View.GONE);
            idea_title.setVisibility(View.VISIBLE);
            et_desc.setVisibility(View.VISIBLE);
            btn.setImageResource(R.drawable.ic_done);
            btn.setTag("save");
            Toast.makeText(getApplicationContext(),"Editing",Toast.LENGTH_SHORT).show();

        }
        else if(btn.getTag().toString().equals("save")){
            btn.setImageResource(R.drawable.ic_baseline_create_24px);
            btn.setTag("edit");
            final String oldDesc = desc.getText().toString();
            final String oldTitle = prevTitle.getText().toString();

            desc.setVisibility(View.VISIBLE);
            desc.setText(et_desc.getText().toString());
            et_desc.setVisibility(View.GONE);

            prevTitle.setVisibility(View.VISIBLE);
            prevTitle.setText(idea_title.getText().toString());
            idea_title.setVisibility(View.GONE);


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("Ideas").document(tag);

            final String update_desc = et_desc.getText().toString();
            final String update_title = idea_title.getText().toString();
            docRef.update("Description", update_desc,"Name",update_title)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(!oldDesc.equals(update_desc))
                                    docRef.update("Log", FieldValue.arrayUnion(LogKt.genLogStr(USERNAME, "update", "description", update_desc)));

                                if(!oldTitle.equals(update_title))
                                    docRef.update("Log", FieldValue.arrayUnion(LogKt.genLogStr(USERNAME, "update", "Name", update_title)));
                            }
                        })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

            Toast.makeText(getApplicationContext(),"Saved Changes",Toast.LENGTH_SHORT).show();
        }
    }

    public void manageTags(View v) {
        Intent i = new Intent(this, Tags.class);
        i.putExtra("ideaID",tag);
        startActivity(i);
        finish();
    }

    public void manageTasks(View v) {
        Intent i = new Intent(this, TaskActivity.class);
        i.putExtra("ideaID",tag);
        startActivity(i);
        finish();
    }

    public void manageCollabs(View v){

    }

    public void deleteIdea(){
        getDB().collection("Users").document(USERNAME)
                .update("Ideas_Owned", FieldValue.arrayRemove(tag), "Priority", FieldValue.arrayRemove(tag))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Idea removed from user metadata");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        TextView a = findViewById(R.id.collaboratorText);
        if (a.getText().toString().equals(USERNAME)) {
            getDB().collection("Ideas").document(tag).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Idea Deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        } else {

            getDB().collection("Ideas").document(tag)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                String owner = document.getString("Owner");
                                final ArrayList<String> ideaCollabs = (ArrayList<String>) document.get("Collaborators");
                                if (owner.equals(USERNAME)) {
                                    ideaCollabs.remove(USERNAME);
                                    getDB().collection("Ideas").document(tag)
                                            .update("Collaborators", FieldValue.arrayRemove(USERNAME), "Owner", ideaCollabs.get(0))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    getDB().collection("Ideas").document(tag)
                                                            .update("Log", FieldValue.arrayUnion(LogKt.genLogStr(USERNAME, "delete", "idea", USERNAME)),
                                                                    "Log", FieldValue.arrayUnion(LogKt.genLogStr("Owner", "changed",
                                                                            "to", ideaCollabs.get(0))));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            });
                                } else {
                                    getDB().collection("Ideas").document(tag)
                                            .update("Collaborators", FieldValue.arrayRemove(USERNAME))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    getDB().collection("Ideas").document(tag)
                                                            .update("Log", FieldValue.arrayUnion(LogKt.genLogStr(USERNAME, "delete", "idea", USERNAME)));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            });
                                }

                            }
                        }
                    });

        }

        Intent j = new Intent(getApplicationContext(), MainIdeasActivity.class);
        startActivity(j);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                Intent i = new Intent(this,MainIdeasActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(IdeaDetailsActivity.this);
                builder.setTitle("Delete Idea")
                        .setMessage("Are you sure you want to delete this idea?")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteIdea();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


        }
        return true;
    }
}
