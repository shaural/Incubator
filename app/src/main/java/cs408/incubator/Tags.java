package cs408.incubator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Tags extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

    }

    public void onClickbtnAddTag(View v)
    {
        EditText et_tag = (EditText)findViewById(R.id.et_tag);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Ideas").document("irWRcr2YIjDxi2kgEn93");
        // Atomically add a new region to the "regions" array field.
        docRef.update("Tags", FieldValue.arrayUnion(et_tag.getText().toString()));
        Toast.makeText(this, "Tag has been added to Idea.", Toast.LENGTH_LONG).show();
    }
}
