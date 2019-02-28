package cs408.incubator;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewUploadsActivity extends AppCompatActivity {

    //the listview
    ListView listView;
    ProgressBar progressbar;

    //database reference to get uploads data
    private FirebaseFirestore db;

    //list to store uploads data
    List<Upload> uploadList;
    private List<String> namesList = new ArrayList<>();

    String idea_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_upload);

        listView = (ListView) findViewById(R.id.listView);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        uploadList = new ArrayList<>();

        Intent i = getIntent();
        idea_id = i.getStringExtra("ideaID");

        progressbar.setVisibility(View.VISIBLE);

        /* Get Firestore instance and data */
        db = FirebaseFirestore.getInstance();
        final CollectionReference docRef = db.collection("Ideas").document(idea_id).collection("Documents");

        /* Make documents list */
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                namesList.clear();
                progressbar.setVisibility(View.GONE);
                for (DocumentSnapshot snapshot : documentSnapshots){
                    String name = snapshot.getString("name");
                    String url = snapshot.getString("url");
                    namesList.add(name);
                    Upload doc = new Upload(name, url);
                    uploadList.add(doc);
                }
                ArrayAdapter<String>adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_selectable_list_item,namesList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
        });

        //adding a clicklistener on listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the upload
                Upload upload = uploadList.get(i);

                //Opening the upload file in browser using the upload url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(upload.getUrl()));
                startActivity(intent);
            }
        });

    }


}