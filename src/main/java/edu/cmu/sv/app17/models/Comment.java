package edu.cmu.sv.app17.models;


public class Comment {
     String id = null;
     String mediaType;
     String mediaName;
     String content;
     String userId;

    public Comment(String mediaType, String mediaName,
                   String content, String userId) {
        this.mediaType = mediaType;
        this.mediaName = mediaName;
        this.content = content;
        this.userId = userId;
    }
    public void setId(String id) {
        this.id = id;
    }
}
