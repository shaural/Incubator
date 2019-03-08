package cs408.incubator;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Type;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


    public class image_list extends AppCompatActivity implements Image_list_adapter.OnItemClickListener {
        private RecyclerView mRecyclerView;
        private Image_list_adapter mAdapter;
        private Task mDBListener;
        private FirebaseStorage mStorage;
        private ProgressBar mProgressCircle;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        private DatabaseReference mDatabaseRef;
        static String ur;
        private List<uploadImage> mUploads;
        private static ArrayList<Type> mArrayList = new ArrayList<>();
        private static final String TAG = "image_list";

        private String ideaId = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_image_list);

            mRecyclerView = findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mProgressCircle = findViewById(R.id.progress_circle);
            ideaId = getIntent().getStringExtra("ideaID");

            mUploads = new ArrayList<>();

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
                                uploadImage upload = document.toObject(uploadImage.class);
                                upload.setKey(document.getId());
                                //Log.d(TAG,"DOC ID!!! "+ document.getId());
                                mUploads.add(upload);
                                Log.d(TAG, document.getId() + " => " + document.getData());
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

            /*mDatabaseRef = FirebaseDatabase.getInstance().getReference("images");
            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        uploadImage upload = postSnapshot.getValue(uploadImage.class);
                        mUploads.add(upload);
                    }

                   // mAdapter = new Image_list_adapter(image_list.this, mUploads);

                    //mRecyclerView.setAdapter(mAdapter);
                    //mProgressCircle.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(image_list.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }


            });*/
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
            //when image is clicked
            //static String clickUrl = ;
            //manageImages_fullscreen();
            //Intent i = new Intent(this,FullscreenImageActivity.class);
            //image_activity ima = new image_activity();
            //i.setData(image_activity.mImageUri);
            //Toast.makeText(this, "image uri " + image_activity.mImageUri, Toast.LENGTH_SHORT).show();
            //Log.d(TAG,"image uri " + image_activity.mImageUri);
            //startActivity(i);

            Intent i = new Intent(this, FullscreenImageActivity.class);
            // i.putExtra("ideaID",tag);
            uploadImage u = new uploadImage();
            ur = mUploads.get(k).mImageUrl;
            Log.d(TAG,"image url @mageimage_fullscreen " + ur);
            startActivity(i);
            finish();



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
                    uploadImage selectedItem = mUploads.get(position);
                    final String selectedKey = selectedItem.getKey();
                    Log.d(TAG,"selected key: " + selectedKey);
                    StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
                    Log.d(TAG,"imageRef: " + imageRef);
                    //AlertDialog.Builder alert = new AlertDialog.Builder(image_list)
                    imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        //making sure the data got delete from both db and storage
                        public void onSuccess(Void aVoid) {
                            db.collection("images").document(selectedKey).delete();
                            Toast.makeText(image_list.this,"Item deleted", Toast.LENGTH_SHORT);
                        }
                    });
                    onRestart();
                    dialogInterface.dismiss();

                    //onResume();
                    mAdapter.notifyDataSetChanged();
                }

            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });



            /*uploadImage selectedItem = mUploads.get(position);
            final String selectedKey = selectedItem.getKey();
            Log.d(TAG,"selected key: " + selectedKey);
            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
            Log.d(TAG,"imageRef: " + imageRef);
            //AlertDialog.Builder alert = new AlertDialog.Builder(image_list)
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                //making sure the data got delete from both db and storage
                public void onSuccess(Void aVoid) {
                   db.collection("images").document(selectedKey).delete();
                   Toast.makeText(image_list.this,"Item deleted", Toast.LENGTH_SHORT);
                }
            });*/

            alert.show();

        }

       // recreate();
    }

