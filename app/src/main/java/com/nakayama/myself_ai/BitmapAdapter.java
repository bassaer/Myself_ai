package com.nakayama.myself_ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by nakayama on 2016/02/09.
 */
public class BitmapAdapter extends ArrayAdapter<Bitmap> {

    private LayoutInflater mInflater;

    public BitmapAdapter(Context context, List<Bitmap> objects) {
        super(context, R.layout.grid_item, objects);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item, null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.photo_item);
        imageView.setImageBitmap(getItem(position));
        return convertView;
    }
}
