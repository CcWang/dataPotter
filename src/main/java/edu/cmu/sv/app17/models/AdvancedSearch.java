package edu.cmu.sv.app17.models;


public class AdvancedSearch {
    String id = null;
    String metaCategory;
    String category;

    public AdvancedSearch(String metaCategory,
                String category) {
        this.metaCategory = metaCategory;
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }
}
