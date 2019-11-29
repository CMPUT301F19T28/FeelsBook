package com.cmput.feelsbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

import javax.annotation.Nullable;

public class SearchActivity extends AppCompatActivity {

    private User user;
    private EditText search;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        user = (User) getIntent().getExtras().get("user");

        Client client = new Client("CZHQW4KJVA","394205d7e7f173719c08f3e187b2a77b");
        Index index = client.getIndex("users");


        recyclerView = findViewById(R.id.search_list);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        adapter = new SearchAdapter(user);
        recyclerView.setAdapter(adapter);

        search = findViewById(R.id.search_text);
        Button searchButtton = findViewById(R.id.search_button);
        searchButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clearList();
                index.searchAsync(new Query(search.getText().toString()), new CompletionHandler() {
                    @Override
                    public void requestCompleted(@androidx.annotation.Nullable JSONObject jsonObject, @androidx.annotation.Nullable AlgoliaException e) {
                        if (jsonObject != null) {
                            try {
                                JSONArray arr = jsonObject.getJSONArray("hits");
                                for (int i = 0; i < arr.length(); i++) {
                                    if(arr.getJSONObject(i).getString("username").equals(user.getUserName()))
                                        continue;
                                    FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(arr.getJSONObject(i).getString("username"))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful() && task.getResult() != null) {
                                                        DocumentSnapshot doc = task.getResult();
                                                        if(doc.exists()){
                                                            adapter.addUser(new FollowUser(task.getResult().getId(), (String) doc.getData().get("name"), doc.getString("profilePic")));
                                                        }

                                                    }
                                                }
                                            });
                                }
                            } catch (JSONException ex) {
                                Log.d("Search", "JsonException");
                            }
                        }
                    }
                });
            }
        });

        Button back = findViewById(R.id.search_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
