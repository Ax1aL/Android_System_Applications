package com.example.ProjectADMD;

import android.app.Activity;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListView extends ArrayAdapter<String> {

    private final Activity context;
    private final  ArrayList<String> maintitle;
    private final  ArrayList<Drawable> imgid;

    public MyListView(Activity context, ArrayList<String> maintitle, ArrayList<Drawable> imgid) {
        super(context, R.layout.mylist, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;
        this.imgid=imgid;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        titleText.setText(maintitle.get(position));
        imageView.setImageDrawable(imgid.get(position));

        return rowView;

    };
}
