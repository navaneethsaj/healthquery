package com.blazingapps.asus.healthquery;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private static final String MYPREF = "mypreferences";
    private static final String NAME = "username";
    ArrayList<QuestionObject> questionObjects;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    ListView listView;
    ForumAdapter forumAdapter;
    ProgressBar progressBar;
    Button syncbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences(MYPREF,MODE_PRIVATE);
        syncbutton=findViewById(R.id.syncbutton);
        editor=sharedPreferences.edit();
        questionObjects=new ArrayList<>();
        progressBar = findViewById(R.id.progress);
        //questionObjects.add(new QuestionObject("11","11","11",13425));
        listView = findViewById(R.id.forumlistview);
        //forumAdapter = new ForumAdapter(getApplicationContext(),R.layout.forum_item_layout,questionObjects);
        //listView.setAdapter(forumAdapter);

        syncbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        FirebaseUser user = mAuth.getCurrentUser();
        if (user==null){
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
        else {
            refresh();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //Toast.makeText(getApplicationContext(),user.getDisplayName(),Toast.LENGTH_LONG).show();
                refresh();

            } else {

                Toast.makeText(getApplicationContext(),"Sign IN failed",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void refresh(){
        FirebaseUser user = mAuth.getCurrentUser();
        Toast.makeText(getApplicationContext(),"Welcome "+user.getDisplayName(),Toast.LENGTH_SHORT).show();
        editor.putString(NAME,user.getDisplayName());
        editor.commit();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("chat");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questionObjects=new ArrayList<>();
                for (DataSnapshot usersnapshot : dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    for (DataSnapshot questionsnapshot : usersnapshot.getChildren()) {
                        ArrayList<AnswerObj> answerlist = new ArrayList<>();
                        DataSnapshot answersnapshot = questionsnapshot.child("answer");
                        for (DataSnapshot answer : answersnapshot.getChildren()){
                            answerlist.add(new AnswerObj(answer.child("reply").getValue().toString(),answer.child("docId").getValue().toString()));
                        }
                        Log.d("TAG",questionsnapshot.getKey());
                        String usrkey=""+usersnapshot.getKey();
                        String qstnkey=""+questionsnapshot.getKey();
                        String qstn=""+questionsnapshot.child("question").getValue().toString();
                        String time=""+questionsnapshot.child("time").getValue().toString();
                        questionObjects.add(new QuestionObject(
                                usrkey,
                                qstnkey,
                                qstn,
                                Double.valueOf(time),
                                answerlist));
                    }
                }
                progressBar.setVisibility(View.GONE);
                //Log.d("TAG",questionObjects.get(0).question);

                Collections.sort(questionObjects);
                Collections.reverse(questionObjects);
                forumAdapter = new ForumAdapter(getApplicationContext(),R.layout.forum_item_layout,questionObjects);
                listView.setAdapter(forumAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
