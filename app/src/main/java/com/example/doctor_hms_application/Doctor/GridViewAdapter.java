package com.example.doctor_hms_application.Doctor;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bitmap> imagelist;
    public GridViewAdapter(Context context, ArrayList<Bitmap> imagelist){
        this.context = context;
        this.imagelist = imagelist;
    }

    public ArrayList<Bitmap> getImagelist() {
        return imagelist;
    }

    public void setImagelist(ArrayList<Bitmap> imagelist) {
        this.imagelist = imagelist;
    }

    @Override
    public int getCount() {
        return this.imagelist.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(500, 500));
        } else {
            imageView = (ImageView) view;
        }
        imageView.setImageBitmap(imagelist.get(i));
        return imageView;
    }
}
