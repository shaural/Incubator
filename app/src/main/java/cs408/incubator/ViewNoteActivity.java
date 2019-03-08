package cs408.incubator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ViewNoteActivity extends AppCompatActivity {
    String idea_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tv_title = (TextView)findViewById(R.id.tv_view_note_title);
        TextView tv_desc = (TextView)findViewById(R.id.tv_view_note_desc);
        Intent i = getIntent();
        idea_id = i.getStringExtra("ideaID");
        String tit = i.getStringExtra("title");
        String des = i.getStringExtra("desc");
        tv_title.setText(tit);
        tv_desc.setText(des);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent =  new Intent(this, SharedNotesActivity.class);
                intent.putExtra("ideaID",idea_id);
                startActivity(intent);
                finish();
        }
        return true;
    }

}
