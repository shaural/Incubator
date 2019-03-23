package cs408.incubator;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Type;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import firestore_library.FirestoreLibraryKt;


public class image_list extends AppCompatActivity implements Image_list_adapter.OnItemClickListener {
        private RecyclerView mRecyclerView;
        private Image_list_adapter mAdapter;
        private Task mDBListener;
        private FirebaseStorage mStorage;
        private ProgressBar mProgressCircle;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        private DatabaseReference mDatabaseRef;
        static String ur;
        private List<String> mUploads;
        private List<String> keys;
        private static ArrayList<Type> mArrayList = new ArrayList<>();
        private static final String TAG = "image_list";

        private String ideaId = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_image_list);

            Toolbar toolbar = findViewById(R.id.showImagesToolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

            mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mProgressCircle = findViewById(R.id.progress_circle);
            ideaId = getIntent().getStringExtra("ideaID");

            mUploads = new ArrayList<>();
            keys = new ArrayList<>();

            mAdapter = new Image_list_adapter(image_list.this, mUploads);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(image_list.this);
            mStorage = FirebaseStorage.getInstance();
            mDBListener = db.collection("images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                IdeaDetailsActivity idea = new IdeaDetailsActivity();
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    mUploads.clear();
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            //getting the idea id from image's collection doc & compare it w/ current idea id
                            if(document.get("ideaId").equals(idea.tag)) {
                                String url = document.get("mImageUrl").toString();
                                //uploadImage upload = document.toObject(uploadImage.class);
                                //upload.setKey(document.getId());
                                System.out.println("print"+url);
                                //Log.d(TAG,"DOC ID!!! "+ document.getId());
                                mUploads.add(url);
                                keys.add(document.getId());
                                Log.d(TAG, document.getId() + " => " + url);
                                Log.d(TAG, document.getId() + " => idea id " + document.get("ideaId"));
                            }
                        }



                        mAdapter.notifyDataSetChanged();
                        mProgressCircle.setVisibility(View.INVISIBLE);
                    } else{
                        Log.d(TAG,"Error getting documents: ",task.getException());
                        mProgressCircle.setVisibility(View.INVISIBLE);
                    }
                }
            });


        }

        public void manageImages_fullscreen() {
            Intent i = new Intent(this, FullscreenImageActivity.class);
           // i.putExtra("ideaID",tag);
            uploadImage u = new uploadImage();
            String ur = u.mImageUrl;
            Log.d(TAG,"image url @mageimage_fullscreen " + ur);
            startActivity(i);
            finish();
        }
        @Override
        public void onItemClick( int k) {
            Toast.makeText(this, "Click image ", Toast.LENGTH_SHORT).show();


        }

        public void OnClickImage(Uri imageuri){
            Intent fullscreenintent = new Intent(this,FullscreenImageActivity.class);
            fullscreenintent.setData(imageuri);
            startActivity(fullscreenintent);
        }
        public void refresh() {
            Intent intent = getIntent();
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
        }


        /**@Override
        public void onBackPressed()
        {
            super.onBackPressed();
            Intent i = new Intent(getApplicationContext(),IdeaDetailsActivity.class);
            i.putExtra("ideaTag",ideaId);
            startActivity(new Intent(image_list.this, image_activity.class));
            finish();

        }*/

        @Override
        public void onRestart()
        {
            super.onRestart();
            finish();
            startActivity(getIntent());
        }

        @Override
        public void onDeleteClick(final int position) {
            Toast.makeText(this, "Delete click at position: " + position, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alert = new AlertDialog.Builder(image_list.this);
            alert.setTitle("Delete");
            alert.setMessage("Are you sure you want to delete ? ");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String selectedKey = keys.get(position);
                    Log.d(TAG,"selected key: " + selectedKey);
                    StorageReference imageRef = mStorage.getReferenceFromUrl(mUploads.get(position));
                    Log.d(TAG,"imageRef: " + imageRef);
                    //AlertDialog.Builder alert = new AlertDialog.Builder(image_list)
                    imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        //making sure the data got delete from both db and storage
                        public void onSuccess(Void aVoid) {
                            System.out.println("image");
                            db.collection("images").document(selectedKey).delete();
                            final DocumentReference docRef2 = db.collection("Ideas").document(getIntent().getStringExtra("ideaID"));
                            docRef2.update("Log", FieldValue.arrayUnion(LogKt.genLogStr(FirestoreLibraryKt.getUSERNAME(), "delete", "image", "new-image")));

                            Toast.makeText(image_list.this,"Item deleted", Toast.LENGTH_SHORT).show();

                        }
                    });

                    dialogInterface.dismiss();

                }

            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });




            alert.show();

        }

        public void addImage(View v){
            Intent i = new Intent(this,image_activity.class);
            i.putExtra("ideaID",getIntent().getStringExtra("ideaID"));
            startActivity(i);
            finish();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {

                case android.R.id.home:
                    Intent i = new Intent(this, IdeaDetailsActivity.class);
                    i.putExtra("ideaTag",getIntent().getStringExtra("ideaID"));
                    startActivity(i);
                    finish();
                    break;
            }
            return true;
        }
       // recreate();
    }

