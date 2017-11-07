package edu.cmu.sv.app17.models;

import edu.cmu.sv.app17.helpers.APPCrypt;

public class Session {

    String token = null;
    String userId = null;
    String firstName = null;

    public Session(User user) throws Exception{
        this.userId = user.id;
        this.token = APPCrypt.encrypt(user.id);
        this.firstName = user.username;
    }
}
