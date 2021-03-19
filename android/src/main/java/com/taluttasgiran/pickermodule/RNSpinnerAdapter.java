package com.taluttasgiran.pickermodule;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RNSpinnerAdapter extends RecyclerView.Adapter<RNSpinnerAdapter.MyViewHolder> {
    ReactContext reactContext;
    private ReadableArray mDataset;
    RNSpinner rnSpinner;
    Callback callback;
    Boolean showDeleteButton;
    String selectedValue;
    ReadableArray selectedColor;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        MyViewHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
        }
    }

    RNSpinnerAdapter(ReactContext reactContext, ReadableArray myDataset, RNSpinner androidSpinner, Boolean showDeleteButton, Callback spinnerCallback, String mSelectedValue, ReadableArray mSelectedColor) {
        this.reactContext = reactContext;
        mDataset = myDataset;
        rnSpinner = androidSpinner;
        callback = spinnerCallback;
        this.showDeleteButton = showDeleteButton;
        selectedValue = mSelectedValue;
        selectedColor = mSelectedColor;
    }

    @Override
    public RNSpinnerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spinner_item, parent, false);
        return new MyViewHolder(linearLayout);
    }

    private void sendEvent(ReactContext reactContext, String eventName, WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String value = null;
        String text = null;
        Button button = holder.linearLayout.findViewById(R.id.button);
        ImageButton deleteButton = holder.linearLayout.findViewById(R.id.deleteButton);
        if (mDataset.getType(position) == ReadableType.Map) {
            if (mDataset.getMap(position).getType("value") == ReadableType.String) {
                value = mDataset.getMap(position).getString("value");
            } else {
                double number = mDataset.getMap(position).getDouble("value");
                if (number == Math.rint(number)) {
                    value = String.valueOf((int) number);
                } else {
                    value = String.valueOf(number);
                }
            }
            if (mDataset.getMap(position).getType("label") == ReadableType.String) {
                text = mDataset.getMap(position).getString("label");
            } else {
                double number = mDataset.getMap(position).getDouble("label");
                if (number == Math.rint(number)) {
                    text = String.valueOf((int) number);
                } else {
                    text = String.valueOf(number);
                }
            }
        } else if (mDataset.getType(position) == ReadableType.String) {
            text = mDataset.getString(position);
            value = mDataset.getString(position);
        } else if (mDataset.getType(position) == ReadableType.Number) {
            double number = mDataset.getDouble(position);
            if (number == Math.rint(number)) {
                text = String.valueOf((int) number);
                value = String.valueOf((int) number);
            } else {
                text = String.valueOf(number);
                value = String.valueOf(number);
            }
        }
        button.setText(text);
        final String finalValue = value;
        if (selectedValue != null) {
            if (selectedValue.equals(value)) {
                button.setEnabled(false);
                if (selectedColor != null) {
                    button.setTextColor(Color.rgb(selectedColor.getInt(0), selectedColor.getInt(1), selectedColor.getInt(2)));
                }
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rnSpinner.hide();
                callback.invoke(finalValue);
            }
        });
        if (this.showDeleteButton == true) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WritableMap params = Arguments.createMap();
                    params.putString("value", finalValue);
                    sendEvent(RNSpinnerAdapter.this.reactContext, "ItemDeleted", params);
                }
            });
        } else {
            deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


