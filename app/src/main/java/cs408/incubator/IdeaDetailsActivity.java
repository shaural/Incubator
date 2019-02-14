package cs408.incubator;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class IdeaDetailsActivity extends AppCompatActivity {

    EditText descTV;
    String tag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_details);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //descTV = findViewById(R.id.descriptionText1);
        //stDesc = getIntent().getExtras().getString("Desc");
        //descTV.setText("test");

        Intent i = getIntent();
        String ideaTag = i.getStringExtra("ideaTag");
        tag = ideaTag;

        DocumentReference docRef = db.collection("Ideas").document(ideaTag);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        EditText desc = findViewById(R.id.descriptionText1);
                        EditText tags = findViewById(R.id.tagText);
                        EditText collab = findViewById(R.id.collaboratorText);

                        desc.setText(document.getString("Description"));
                        tags.setText(document.get("Tags").toString());
                        collab.setText(document.get("Collaborators").toString());
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
        Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT).show();
        Button btn = findViewById(R.id.button);

        EditText et_desc = (EditText)findViewById(R.id.descriptionText1);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Ideas").document(tag);


        if (findViewById(R.id.descriptionText1).isEnabled()) {
            docRef
                    .update("Description", et_desc.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
            findViewById(R.id.descriptionText1).setEnabled(false);
            btn.setText("Edit");
        } else if (!findViewById(R.id.descriptionText1).isEnabled()) {
            findViewById(R.id.descriptionText1).setEnabled(true);
            btn.setText("Save");
        }

    }

    public void btnBackOnClick(View v) {
        Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
        Button btn = findViewById(R.id.backbutton);
        Intent i = new Intent(this, MainIdeasActivity.class);
        startActivity(i);
        finish();
    }
}
