package cs408.incubator;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import static cs408.incubator.image_activity.mImageUri;
import static cs408.incubator.image_list.ur;

public class FullscreenImageActivity extends AppCompatActivity {
    private Uri uri;
    private static final String TAG = "FullscreenImageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        Toolbar toolbar = findViewById(R.id.fullscreenToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        ImageView fullScreenImageView = (ImageView) findViewById(R.id.fullScreenImageView);

        //Intent callingActivityIntent = getIntent();
        Intent callingActivityIntent = getIntent();
        Log.d(TAG,"image url @fullscreenimageActivity 1 " + ur);
        if(callingActivityIntent != null){
           uri = callingActivityIntent.getData();
            Log.d(TAG,"image url @fullscreenimageActivity 2 " + ur);
            Picasso.with(this).load(ur).into(fullScreenImageView);
           if(mImageUri != null && fullScreenImageView != null){
               Log.d(TAG,"image url @fullscreenimageActivity 3 " + ur);
              // Picasso.with(this).load(ur).into(fullScreenImageView);
           }
        }
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
