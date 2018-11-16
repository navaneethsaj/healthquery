package com.blazingapps.asus.healthquery;

class AnswerObj {
    String reply;
    String docid;

    public AnswerObj(String reply, String docid) {
        this.reply = reply;
        this.docid = docid;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getReply() {

        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
