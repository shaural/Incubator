package cs408.incubator;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.type.Date;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import firestore_library.FirestoreLibraryKt;

public class image_activity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText FileName;
    private ImageView mImageView;
    private static String clickUrl;
    private ProgressBar mProgressBar;
    private static final String TAG = "image_activity";
    static Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = findViewById(R.id.imageToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_images);
        FileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

        //creating instance that would stored the images in "uploads" folders in firebase
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef  = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(image_activity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            openImagesActivity();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);
            Log.d(TAG,"image uri at onactivityresult" + mImageUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile() {
        if (mImageUri != null) {
            Log.d(TAG,"image uri at uploadfile" + mImageUri);
            //creating the name randomly from uploads/time.extension to the uploads folder
            //child is continuously use the uploads folder
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //mProgressBar.setProgress(0);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(image_activity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();

                            Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString() + "Name : " + FileName.getText().toString().trim());
                            uploadImage upload = new uploadImage(FileName.getText().toString().trim(),downloadUrl.toString());
                            IdeaDetailsActivity idea = new IdeaDetailsActivity();
                            //String uploadId = databaseReference.push().getKey();
                            //databaseReference.child(uploadId).setValue(upload);
                            Log.d(TAG,"Idead id in image_activity: " + idea.tag);

                            //uploadImage upload = new uploadImage(FileName.getText().toString().trim(),
                                  //  taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            //Toast.makeText(image_activity.this, "1: " + FileName.getText().toString().trim(), Toast.LENGTH_LONG).show();
                            //Toast.makeText(image_activity.this,"2:"+ taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(), Toast.LENGTH_LONG).show();
                            //create new db entries that contain metadata from the uploads so we don't overwrite the old stuff
                            //String uploadId = mDatabaseRef.push().getKey();
                            Toast.makeText(image_activity.this,"Successfully upload the image ", Toast.LENGTH_LONG).show();

                            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                            final DocumentReference docRef2 = db1.collection("Ideas").document(idea.tag);
                            docRef2.update("Log", FieldValue.arrayUnion(LogKt.genLogStr(FirestoreLibraryKt.getUSERNAME(), "upload", "image", FileName.getText().toString().trim())));
                            //mDatabaseRef.child(uploadId).setValue(upload);

                            // Add a new document with a generated id.
                            Map<String, Object> data = new HashMap<>();
                            String name = FileName.getText().toString().trim();

                                data.put("mName", upload.mName);
                                data.put("mImageUrl", upload.mImageUrl);
                                data.put("ideaId",idea.tag);

                          // Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("images").document(String.valueOf(System.currentTimeMillis()))
                                    .set(data)
                                    /*.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                           /// documentReference = System.currentTimeMillis();
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                        }
                                    })*/
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                            Log.d(TAG, "Document ID: " + String.valueOf(System.currentTimeMillis()));

                            openImagesActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(image_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //getting the progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, image_list.class);
        intent.putExtra("ideaID",getIntent().getStringExtra("ideaID"));
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Intent i = new Intent(this, image_list.class);
                i.putExtra("ideaID",getIntent().getStringExtra("ideaID"));
                startActivity(i);
                finish();
                break;
        }
        return true;
    }
}