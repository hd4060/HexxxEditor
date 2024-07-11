package com.dio5656.hexxxeditor;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputEditText;
import static com.dio5656.hexxxeditor.MainActivity.customEditTextArrayList;
import static com.dio5656.hexxxeditor.MainActivity.Encoding;
import static com.dio5656.hexxxeditor.MainActivity.stringToHex;
import static com.dio5656.hexxxeditor.ReadFromFile.utf8ToHex;

import java.nio.charset.StandardCharsets;


public class Edit {
    Context context;
    Activity activity;
    public Edit (Context context, Activity activity) {
        this.context=context;
        this.activity=activity;
    }
  //for ansi and utf8
    public  void start (View anchorView)
    {
        // Inflate the popup_layout.xml
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.edit_popup, activity.findViewById(R.id.nav_host_fragment_content_main3),false);

        // Create the PopupWindow
        int width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // flag for taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        View dimBackground = activity.findViewById(R.id.dim_background);
        dimBackground.setVisibility(View.VISIBLE);
        // Show the PopupWindow
        popupWindow.showAsDropDown(anchorView);
        Button saveasstringbutton = popupView.findViewById(R.id.saveasstringbutton);
        System.out.println("saveasstringbutton="+saveasstringbutton);
        saveasstringbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TextInputEditText saveAsStringText = popupView.findViewById(R.id.saveasstring);
                System.out.println("savvvvvvvvvvvvvvvvvvv"+saveAsStringText);
                //get focused edittext
                View focusedView = activity.getCurrentFocus();
                if (focusedView instanceof CustomEditText) {
                    CustomEditText custom = (CustomEditText) focusedView;
                    String texttosave= saveAsStringText.getText().toString();
                    // loop to edit multiple hex values
                    for (int i=0;i<texttosave.length();i++) {
                        //break if text is longer than file
                        if (custom.getAddress()+i>customEditTextArrayList.size()-1)
                            break;
                        CustomEditText customEditText = customEditTextArrayList.get((int)custom.getAddress()+i);
                        System.out.println("customEditText="+customEditText);
                        if (customEditText!=null) {
                            if (Encoding.equals("ansi")) {
                                customEditText.setText(stringToHex(String.valueOf(texttosave.charAt(i))));
                            }else {//utf8
                                byte[] aa = String.valueOf(texttosave.charAt(i)).getBytes();
                                customEditText.setText(utf8ToHex(new String(aa, StandardCharsets.UTF_8)));
                            }
                        }
                    }

                }
                View dimBackground = activity.findViewById(R.id.dim_background);
                dimBackground.setVisibility(View.INVISIBLE);
                popupWindow.dismiss();

            }
        });
        // Dismiss the dim background when the popup is dismissed
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dimBackground.setVisibility(View.GONE);
            }
        });
    }
}
