package com.taluttasgiran.pickermodule;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RNSpinnerAdapter extends RecyclerView.Adapter<RNSpinnerAdapter.MyViewHolder> {
    ReactContext reactContext;
    private String[] mDataset;
    RNSpinner rnSpinner;
    Callback callback;
    Boolean showDeleteButton;
    int selectedItemPosition;
    private String[] imageList;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        MyViewHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
        }
    }

    RNSpinnerAdapter(ReactContext reactContext, String[] myDataset, RNSpinner androidSpinner, Boolean showDeleteButton,
            Callback spinnerCallback, int selectedItem, String[] images) {
        this.reactContext = reactContext;
        mDataset = myDataset;
        rnSpinner = androidSpinner;
        callback = spinnerCallback;
        this.showDeleteButton = showDeleteButton;
        selectedItemPosition = selectedItem;
        imageList = images;
    }

    @Override
    public RNSpinnerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spinner_item, parent, false);
        return new MyViewHolder(linearLayout);
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("RNPickerModule", "Error getting bitmap", e);
        }
        return bm;
    }

    private void sendEvent(ReactContext reactContext, String eventName, WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Button button = holder.linearLayout.findViewById(R.id.button);
        ImageButton deleteButton = holder.linearLayout.findViewById(R.id.deleteButton);
        ImageView imageView = holder.linearLayout.findViewById(R.id.item_image);
        if (this.imageList.length > 0) {
            button.setPadding(15, 15, 15, 15);
            imageView.setImageBitmap(getImageBitmap(this.imageList[position]));
        } else {
            imageView.setVisibility(View.GONE);
        }
        button.setText(mDataset[position]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rnSpinner.hide();
                callback.invoke(mDataset[position], position);
            }
        });
        if (this.showDeleteButton == true) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WritableMap params = Arguments.createMap();
                    params.putString("item", mDataset[position]);
                    params.putInt("position", position);
                    sendEvent(RNSpinnerAdapter.this.reactContext, "ItemDeleted", params);
                }
            });
        } else {
            deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
