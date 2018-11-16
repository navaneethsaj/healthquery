package com.blazingapps.asus.healthquery;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class QuestionObject implements Comparable {

    String uid;
    String qid;
    String question;
    ArrayList<AnswerObj> answerlist;
    double timestamp;

    public QuestionObject(String uid, String qid, String question, double timestamp, ArrayList<AnswerObj> answerlist) {
        this.uid = uid;
        this.qid = qid;
        this.question = question;
        this.timestamp = timestamp;
        this.answerlist = answerlist;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<AnswerObj> getAnswerlist() {
        return answerlist;
    }

    public void setAnswerlist(ArrayList<AnswerObj> answerlist) {
        this.answerlist = answerlist;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        double compareage=((QuestionObject)o).getTimestamp();
        /* For Ascending order*/
        return (int) (this.timestamp-compareage);

    }
}
