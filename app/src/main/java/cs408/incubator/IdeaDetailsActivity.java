package cs408.incubator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
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


public class IdeaDetailsActivity extends AppCompatActivity {
    String USERNAME = FirestoreLibraryKt.getUSERNAME();

    EditText descTV;
    static String tag;
    private static final String TAG = "IdeaDetailsActivity";
    //private Button addimagebutton;
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
        ImageButton addimagebutton =findViewById(R.id.picButton);
        addimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"IDEA ID :  " + tag);
                manageImages();
            }
        });

        Button showimagebutton = findViewById(R.id.show_image_button);
        showimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"IDEA ID :  " + tag);
                manageImages_list();

            }
        });

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
                       // TextView addimage = findViewById(R.id.)

                        title.setText(document.getString("Name"));
                        desc.setText(document.getString("Description"));
                        editableDesc.setText(document.getString("Description"));

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
                                    t.append(ideaCollabs.get(i)).append("\n");
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


    public void btnEditOnClick(View v) {
        ImageButton btn = findViewById(R.id.button);
        EditText et_desc = findViewById(R.id.descriptionText1);
        TextView desc = findViewById(R.id.detailDesc);

        Toast.makeText(getApplicationContext(),btn.getTag().toString(),Toast.LENGTH_SHORT).show();
        if(btn.getTag().toString().equals("edit")){
            desc.setVisibility(View.GONE);
            et_desc.setVisibility(View.VISIBLE);
            btn.setImageResource(R.drawable.ic_done);
            btn.setTag("save");
            Toast.makeText(getApplicationContext(),"Editing",Toast.LENGTH_SHORT).show();

        }
        else if(btn.getTag().toString().equals("save")){
            btn.setImageResource(R.drawable.ic_baseline_create_24px);
            btn.setTag("edit");
            desc.setVisibility(View.VISIBLE);
            desc.setText(et_desc.getText().toString());
            et_desc.setVisibility(View.GONE);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("Ideas").document(tag);

            final String update_desc = et_desc.getText().toString();
            docRef.update("Description", et_desc.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                docRef.update("Log", FieldValue.arrayUnion(LogKt.genLogStr(USERNAME, "update", "description", update_desc)));
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

    public void manageImages() {
        Intent i = new Intent(this, image_activity.class);
        i.putExtra("ideaID",tag);
        Log.d(TAG,"IDEA ID :  " + tag);
        startActivity(i);
        finish();
    }

    public void manageImages_list() {
        Intent i = new Intent(this, image_list.class);
        i.putExtra("ideaID",tag);
        startActivity(i);
        //finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return true;
    }
}
