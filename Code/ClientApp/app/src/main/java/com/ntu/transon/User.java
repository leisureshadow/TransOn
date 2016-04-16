package com.ntu.transon;

/**
 * Created by Mars on 2014/12/28.
 */
public class User {
    private String name;
    private String id;
    private String googleAccount;

    public User(String id, String name, String googleAccount) {
        this.id = id;
        this.name = name;
        this.googleAccount = googleAccount;
    }

    @Override
    public String toString() {
        return String.format("User {id:%s, name:%s, googleAccount:%s}", this.id, this.name, this.googleAccount);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
