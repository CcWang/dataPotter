package edu.cmu.sv.app17.models;

import edu.cmu.sv.app17.helpers.APPCrypt;

public class ContributorSession {

    String token = null;
    String contributorId = null;
    String name = null;

    public ContributorSession(Contributor contributor) throws Exception{
        this.token = APPCrypt.encrypt(contributor.id);
        this.contributorId = contributor.id;
        this.name = contributor.name;
    }
}
