package com.dio5656.hexxxeditor;

import android.content.Context;


public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
    long address;

    public CustomTextView(Context context) {
        super(context);
    }


    public void setAddress(long address) {
        this.address = address;
    }

    public long getAddress() {
        return address;
    }

}
