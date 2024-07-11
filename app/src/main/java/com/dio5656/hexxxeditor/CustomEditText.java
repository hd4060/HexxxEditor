package com.dio5656.hexxxeditor;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;

public class CustomEditText  extends androidx.appcompat.widget.AppCompatEditText  {
    long address;

    public CustomEditText(Context context) {
        super(context);
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public long getAddress() {
        return address;
    }

}
