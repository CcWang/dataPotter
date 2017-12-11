package edu.cmu.sv.app17.models;


public class Share {
    String id = null;
    String userId;
    String shoren_link;
    Number count;
    String media;
    String type;

    public Share(String userId,
                 String shoren_link, Number count, String media, String type) {
        this.userId = userId;
        this.shoren_link = shoren_link;
        this.count = count;
        this.media = media;
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }
}
