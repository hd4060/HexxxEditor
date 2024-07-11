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

    private Paint paint;
    public void init() {
        paint = new Paint();
        paint.setTextSize(getTextSize());

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adjustWidth();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        adjustWidth();
    }

    private void adjustWidth() {
        String text = getText().toString();
        float textWidth = paint.measureText(text) + getPaddingLeft() + getPaddingRight();
        setWidth((int) textWidth);
    }
}
