package com.ntu.transon.meeting;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* Created by 佳芷 on 2014/12/28.
*/
class HashMapAdapter extends BaseAdapter {
    private final ArrayList mData;
    private Activity activity;

    public HashMapAdapter(HashMap<String, String> map,Activity activity) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String,String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView result;
        Map.Entry<String,String> item = getItem(position);
        result = new TextView(activity);
        result.setHeight(100);
        result.setPadding(15, 15, 15, 15);
        result.setTextSize(20);
        result.setText(item.getValue());
        return result;
    }

}
