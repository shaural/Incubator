package cs408.incubator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import firestore_library.FirestoreLibraryKt;

public class UploadFileActivity extends AppCompatActivity implements View.OnClickListener {

    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;
    String USERNAME = FirestoreLibraryKt.getUSERNAME();
    //these are the views
    TextView textViewStatus;
    EditText editTextFilename;
    ProgressBar progressBar;

    //the firebase objects for storage and database
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mStorageReference;

    private FirebaseFirestore db;
    private Map<String, Object> docs = new HashMap<>();

    /* Var */
    String idea_id, docs_id;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        Toolbar toolbar = findViewById(R.id.upload_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);


        Intent i = getIntent();
        idea_id = i.getStringExtra("ideaID");

        //getting firebase objects
        mStorageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();

        //getting the views
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        editTextFilename = (EditText) findViewById(R.id.editTextFileName);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);


        //attaching listeners to views
        findViewById(R.id.buttonUploadFile).setOnClickListener(this);
        findViewById(R.id.textViewUploads).setOnClickListener(this);
    }

    //this function will get the pdf from the storage
    private void getPDF() {
        /* if the permission is not available user will go to the screen to allow storage permission */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PDF_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                Uri uri = data.getData();

                Cursor cursor = getContentResolver().query(uri,null,null,null,null);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                if(editTextFilename.getText().toString().isEmpty())
                    editTextFilename.setText(cursor.getString(nameIndex));

                uploadFile(uri);

            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /* this method is uploading the file */
    private void uploadFile(Uri data) {
        progressBar.setVisibility(View.VISIBLE);

        final StorageReference ref = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + "/" +
                idea_id + "/" + System.currentTimeMillis() + ".pdf");
        final DocumentReference docRef = db.collection("Ideas").document(idea_id).collection("Documents").document();
        docs_id = docRef.getId();

        UploadTask uploadTask = ref.putFile(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                textViewStatus.setText("Uploading...");
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    progressBar.setVisibility(View.GONE);
                    textViewStatus.setText("File Uploaded Successfully :)");
                    if(!editTextFilename.getText().toString().isEmpty())
                        docs.put("name", editTextFilename.getText().toString());
                    else{
                        Date d = new Date();
                        String format = "MMM d, YYY";
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                        String ts = sdf.format(d);
                        String n = "Document (" + ts +")";
                        docs.put("name",n);
                    }
                    docs.put("url", downloadUri.toString());
                    docs.put("docsID", docs_id);
                    docRef.set(docs, SetOptions.merge());
                    db.collection("Ideas").document(idea_id)
                            .update("Log", FieldValue.arrayUnion(LogKt.genLogStr(USERNAME, "uploaded", "document", docs.get("name").toString())));
                    findViewById(R.id.textViewUploads).performClick();
                } else {
                    // Handle failures
                    // ...
                    progressBar.setVisibility(View.GONE);
                    textViewStatus.setText("Fail File Upload :(");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonUploadFile:
                getPDF();
                break;
            case R.id.textViewUploads:
                Intent i = new Intent(this, ViewUploadsActivity.class);
                i.putExtra("ideaID",idea_id);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent i = new Intent(this, ViewUploadsActivity.class);
                i.putExtra("ideaID",idea_id);
                startActivity(i);
                finish();
        }
        return true;
    }

}
