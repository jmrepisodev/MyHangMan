package com.repiso.myhangman;

public class Word {
    private int id;
    private String name;
    private String category;

    public Word(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public Word() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
