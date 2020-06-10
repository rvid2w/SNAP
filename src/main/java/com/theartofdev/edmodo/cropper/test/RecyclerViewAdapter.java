package com.theartofdev.edmodo.cropper.test;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
     private Context context;
    private List<Data> dataList;
    public RecyclerViewAdapter(List<Data> v, Context context) {
        this.context = context;
        dataList = v;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_list_row, parent, false);
        ViewHolder vh = new ViewHolder(v, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Data data = dataList.get(position);
        holder.tag.setText("Tag: " + (data.getTag()) + "\nID: " + data.getID());
        holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(data.getImage(),0,data.getImage().length));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tag;
        public ImageView imageView;
        public ViewHolder(View data, Context ctx) {
            super(data);
            context = ctx;
            tag = data.findViewById(R.id.data_list_row_tag);
            imageView = data.findViewById(R.id.data_list_row_image_view);
        }


    }
}
