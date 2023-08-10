package com.philwill.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AttachmentsListAdapter extends BaseAdapter {


    MyClasses MC;
    private static ArrayList<AttachmentsSearchResults> searchArrayList;

    private LayoutInflater mInflater;

    public AttachmentsListAdapter(Context context, ArrayList<AttachmentsSearchResults> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        Log.i("Phil", "getCount " + searchArrayList.size());
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        //Log.i("Phil", "getItem ");

        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        //Log.i("Phil", "getItemId");

        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.i("Phil", "getView ");
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.attachments_row_view, null);
            holder = new ViewHolder();
            //holder.uploadDate = (TextView) convertView.findViewById(R.id.TextView_Upload_Date);
            holder.details = (TextView) convertView.findViewById(R.id.tvw_attachment);
            //holder.original_file_name = (TextView) convertView.findViewById(R.id.TextView_Original_File_Name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //holder.uploadDate.setText(searchArrayList.get(position).getUploadDate());
        holder.details.setText(searchArrayList.get(position).getDetails());
        holder.original_file_name.setText(searchArrayList.get(position).getOriginalFileName());

        return convertView;
    }

    static class ViewHolder {

        TextView uploadDate;
        TextView details;
        TextView original_file_name;

    }

}
