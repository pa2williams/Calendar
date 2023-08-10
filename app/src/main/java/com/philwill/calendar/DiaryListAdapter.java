package com.philwill.calendar;

 
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by USER on 13/02/2016.
 */
public class DiaryListAdapter extends BaseAdapter{

    MyClasses MC;
    private static ArrayList<DiarySearchResults> searchArrayList;

    private LayoutInflater mInflater;

    public DiaryListAdapter(Context context, ArrayList<DiarySearchResults> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        Log.i("Phil", "getCount " + searchArrayList.size());
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        Log.i("Phil", "getItem ");

        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        Log.i("Phil", "getItemId");

        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Phil", "getView ");
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.diary_row_view, null);
            holder = new ViewHolder();
            holder.tvw_details = (TextView) convertView.findViewById(R.id.tvw_details);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvw_details.setText(searchArrayList.get(position).getDetails());

        return convertView;
    }

    static class ViewHolder {

        TextView tvw_details;

    }

}
