package com.taluttasgiran.pickermodule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReactContext;

public class RNSpinner extends AlertDialog {
    private AlertDialog dialog;

    RNSpinner(Context context, ReactContext reactContext, ReadableArray labels, String selectedValue, String title, Boolean showDeleteButton, ReadableArray selectedColor, Callback callback, final Callback onCancelCallback) {
        super(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout linearLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.spinner_view, null);
        RecyclerView recyclerView = linearLayout.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        TextView tvPlaceholder = linearLayout.findViewById(R.id.placeholder);
        if (title != null) {
            tvPlaceholder.setText(title);
        } else {
            tvPlaceholder.setVisibility(View.GONE);
        }
        RNSpinnerAdapter mAdapter = new RNSpinnerAdapter(reactContext, labels, this, showDeleteButton, callback, selectedValue, selectedColor);
        recyclerView.setAdapter(mAdapter);
        if (linearLayout.getParent() != null) {
            ((ViewGroup) linearLayout.getParent()).removeView(linearLayout); // <- fix
        }
        builder.setView(linearLayout);
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onCancelCallback.invoke();
            }
        });
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.dismiss();
    }
}
