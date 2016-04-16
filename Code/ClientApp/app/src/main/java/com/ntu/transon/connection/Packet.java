package com.ntu.transon.connection;

import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Type;

import com.google.gson.Gson;

public class Packet {
    private String command;
    private HashMap<String,String> items;
    public Packet(String _command){
        command = _command;
        items = new HashMap<String,String>();
    }
    public void addItems(String name,Object item){
        Gson gson = new Gson();
        String itemString = gson.toJson(item);
        items.put(name,itemString);
    }
    public Object getItem(String key, Type type){
        Gson gson = new Gson();
        Object obj = gson.fromJson(items.get(key),type);
        return obj;
    }
    public <T> T getItem(String key, Class<T> klass){
        Gson gson = new Gson();
        return (T) gson.fromJson(items.get(key), klass);
    }
    public Iterator<String> getIterator(){
        return items.keySet().iterator();
    }
    public String getCommand(){
        return command;
    }
}
