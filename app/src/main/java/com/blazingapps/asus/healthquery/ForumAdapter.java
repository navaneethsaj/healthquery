package com.blazingapps.asus.healthquery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
public class ForumAdapter extends ArrayAdapter<QuestionObject> {

    private static final String MYPREF = "mypreferences";
    private static final String NAME = "username";
    SharedPreferences sharedPreferences;
    Context context;
    ArrayList<QuestionObject> questionObjectArrayList;
    public ForumAdapter(@NonNull Context context, int resource, @NonNull ArrayList<QuestionObject> objects) {
        super(context, resource, objects);
        this.questionObjectArrayList=objects;
        this.context=context;
        sharedPreferences=context.getSharedPreferences(MYPREF,Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return questionObjectArrayList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView != null)
            return convertView;
        LayoutInflater inflater;
        View v;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v=inflater.inflate(R.layout.forum_item_layout,parent,false);

        TextView questiontv = v.findViewById(R.id.questiontextview);
        final EditText answeret = v.findViewById(R.id.answeredittext);
        Button sendbutton = v.findViewById(R.id.sendbutton);
        LinearLayout answerlayout = v.findViewById(R.id.answerslist);
        LinearLayout expandview = v.findViewById(R.id.expandlayout);

        String question = questionObjectArrayList.get(position).getQuestion();
        final String uid = questionObjectArrayList.get(position).getUid();
        final String qid = questionObjectArrayList.get(position).getQid();
        double time = questionObjectArrayList.get(position).getTimestamp();

        TextView textViewz = new TextView(context);
        textViewz.setText("Answers");
        textViewz.setTextColor(context.getResources().getColor(R.color.textcolor));
        textViewz.setTextSize(16);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textViewz.setTextColor(context.getColor(R.color.colorPrimary));
        }

        ArrayList<AnswerObj> answerObjArrayList = questionObjectArrayList.get(position).getAnswerlist();
        if (answerObjArrayList.size()>0){
            answerlayout.addView(textViewz);
        }
        for (AnswerObj answerObj : answerObjArrayList){
            TextView textView = new TextView(context);
            textView.setText(answerObj.getReply());
            textView.setTextColor(context.getResources().getColor(R.color.textcolor));
            TextView textView1 = new TextView(context);
            textView1.setText("Answered by,\n"+answerObj.getDocid());
            textView1.setGravity(Gravity.RIGHT);
            textView1.setTextColor(context.getResources().getColor(R.color.textcolor));
            answerlayout.addView(textView);
            answerlayout.addView(textView1);
        }
        final boolean[] expanded = {false};
        expandview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expanded[0]){
                    answerlayout.setVisibility(View.VISIBLE);
                    expanded[0] = true;
                }
                else {
                    answerlayout.setVisibility(View.GONE);
                    expanded[0] = false;
                }
            }
        });
        questiontv.setText(question);

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = answeret.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("chat").child(uid).child(qid).child("answer");
                String key = myRef.push().getKey();
                myRef.child(key).child("reply").setValue(answer);
                myRef.child(key).child("docId").setValue(sharedPreferences.getString(NAME,""))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                        answeret.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        return v;
    }
    public int getViewTypeCount() {
        return getCount();
    }

    public int getItemViewType(int position) {
        return position;
    }
}
