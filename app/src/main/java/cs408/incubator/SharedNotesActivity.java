package cs408.incubator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import firestore_library.FirestoreLibraryKt;

public class SharedNotesActivity extends AppCompatActivity {
    String idea_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView lv_shared = (ListView)findViewById(R.id.lv_shared_notes);
        final TextView tv_empty = (TextView)findViewById(R.id.tv_shared_notes_empty);
        Intent i = getIntent();
        idea_id = i.getStringExtra("ideaID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Notes").document(idea_id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, String> map_shared_notes = null;
                        try {
                            map_shared_notes = ((Map<String, String>) document.getData().get("Shared"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(map_shared_notes != null && !map_shared_notes.isEmpty()) {

                            tv_empty.setVisibility(View.INVISIBLE);
                            lv_shared.setVisibility(View.VISIBLE);

                            // display titles in list view
                            ArrayList<String> title_list = new ArrayList<String>(map_shared_notes.keySet());
                            final ArrayList<String> val_list = new ArrayList<String>(map_shared_notes.values());

                            // Start

                            ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();;

                            for(int i =0; i < title_list.size(); i++) {
                                Map<String,Object> listItemMap = new HashMap<String,Object>();
                                listItemMap.put("title", title_list.get(i).substring(0, title_list.get(i).indexOf("~")));
                                listItemMap.put("uname", title_list.get(i).substring(title_list.get(i).indexOf("~") + 1, title_list.get(i).length()));
                                itemDataList.add(listItemMap);
                            }

                            SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),itemDataList,android.R.layout.simple_list_item_2,
                                    new String[]{"title","uname"},new int[]{android.R.id.text1,android.R.id.text2});

//                            ListView listView = (ListView)findViewById(R.id.listViewExample);
                            lv_shared.setAdapter(simpleAdapter);

                            lv_shared.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                                    Object clickItemObj = adapterView.getAdapter().getItem(index);
                                    Map<String, Object> lim = (Map<String, Object>)clickItemObj;
                                    Intent i = new Intent(getApplicationContext(), ViewNoteActivity.class);
                                    i.putExtra("ideaID", idea_id);
                                    i.putExtra("title", lim.get("title").toString());
                                    i.putExtra("desc", val_list.get(index));
                                    startActivity(i);
                                    finish();
                                }
                            });

                            //END


                        } else {
                            // no notes
                            tv_empty.setVisibility(View.VISIBLE);
                            lv_shared.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        tv_empty.setVisibility(View.VISIBLE);
                        lv_shared.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tv_empty.setVisibility(View.VISIBLE);
                    lv_shared.setVisibility(View.INVISIBLE);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent =  new Intent(this, IdeaDetailsActivity.class);
                intent.putExtra("ideaTag",idea_id);
                startActivity(intent);
                finish();
        }
        return true;
    }

}
