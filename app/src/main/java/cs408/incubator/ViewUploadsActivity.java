package cs408.incubator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import firestore_library.FirestoreLibraryKt;

public class ViewUploadsActivity extends AppCompatActivity {

    //the listview
    ListView listView;
    ProgressBar progressbar;
    FloatingActionButton fab;

    //database reference to get uploads data
    private FirebaseFirestore db;

    //list to store uploads data
    List<Upload> uploadList;
    private List<String> namesList = new ArrayList<>();

    String idea_id;
    String docs_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_upload);

        Toolbar toolbar = findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);


        fab = (FloatingActionButton) findViewById(R.id.fab_item);
        listView = (ListView) findViewById(R.id.listView);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        uploadList = new ArrayList<>();

        Intent i = getIntent();
        idea_id = i.getStringExtra("ideaID");

        progressbar.setVisibility(View.VISIBLE);

        /* Get Firestore instance and data */
        db = FirebaseFirestore.getInstance();
        /* File storage path */
        final CollectionReference docRef = db.collection("Documents");
        /* Make documents list */
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                namesList.clear();
                progressbar.setVisibility(View.GONE);
                for (DocumentSnapshot snapshot : documentSnapshots){
                    String docsID = snapshot.getString("docsID");
                    String name = snapshot.getString("name");
                    String url = snapshot.getString("url");
                    namesList.add(name);
                    Upload doc = new Upload(docsID, name, url);
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

        /* Remove Documents */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Upload upload = uploadList.get(i);
                docs_id = upload.getID();
                final DocumentReference docRef = db.collection("Ideas").document(idea_id).collection("Documents").document(docs_id);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                docRef.delete();
                                final DocumentReference docRef2 = db.collection("Ideas").document(idea_id);
                                docRef2.update("Log", FieldValue.arrayUnion(LogKt.genLogStr(FirestoreLibraryKt.getUSERNAME(), "delete", "document", docs_id)));
                                finish();
                                startActivity(getIntent());
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewUploadsActivity.this);
                builder.setMessage("Delete this Document?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            }
        });

        /* Intent to Upload Docs */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewUploadsActivity.this, UploadFileActivity.class);
                intent.putExtra("ideaID",idea_id);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent =  new Intent(this,IdeaDetailsActivity.class);
                intent.putExtra("ideaTag",idea_id);
                startActivity(intent);
                finish();
        }
        return true;
    }


}