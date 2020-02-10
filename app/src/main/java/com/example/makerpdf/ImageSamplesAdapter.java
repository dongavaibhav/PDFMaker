package com.example.makerpdf;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class ImageSamplesAdapter extends Adapter<ImageSamplesAdapter.ImageSampleViewHolder> {

    private Context context;
    public ArrayList<String> mSelectedImages;
    OnImageEdit onImageEdit;

    public ImageSamplesAdapter(Context context2, ArrayList<String> imageuri, OnImageEdit onImageEdit2) {
        context = context2;
        mSelectedImages = imageuri;
        onImageEdit = onImageEdit2;
    }

    class ImageSampleViewHolder extends ViewHolder {
        ImageView image_crosss;
        ImageView ivProfilePic;

        public ImageSampleViewHolder(View view) {
            super(view);
            ivProfilePic = (ImageView) view.findViewById(R.id.ivGallery);
            image_crosss = (ImageView) view.findViewById(R.id.image_cross);
            image_crosss.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    mSelectedImages.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                    notifyItemRangeChanged(adapterPosition, getItemCount());
                }
            });
        }
    }

    public interface OnImageEdit {
        void onImageEdit(int i);
    }

    public ImageSampleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(context).inflate(R.layout.selected_image_layout, viewGroup, false);
        return new ImageSampleViewHolder(layoutView);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ImageSampleViewHolder holder, int position) {
        try {
            loadImage(context, (String) mSelectedImages.get(position), holder.ivProfilePic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getItemCount() {
        return mSelectedImages.size();
    }

    private void loadImage(Context context2, String str, ImageView imageView) {
        Glide.with(context2).load(new File(str)).into(imageView);
    }

    public ArrayList<String> getmSelectedImages() {
        return mSelectedImages;
    }
}
