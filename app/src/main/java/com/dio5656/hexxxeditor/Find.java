package com.dio5656.hexxxeditor;

import static com.dio5656.hexxxeditor.MainActivity.customEditTextArrayList;
import static com.dio5656.hexxxeditor.MainActivity.customTextViewArrayList;
import static com.dio5656.hexxxeditor.MainActivity.edittextcount;
import static com.dio5656.hexxxeditor.MainActivity.nextchar;
import static com.dio5656.hexxxeditor.MainActivity.readdone;
import static com.dio5656.hexxxeditor.MainActivity.textviewcount;
import static com.dio5656.hexxxeditor.MainActivity.result;
import static com.dio5656.hexxxeditor.MainActivity.texttofind;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class Find extends AppCompatActivity {
    Context context;
    Activity activity;
    ReadFromFile readFromFile;
    public Find (Context context, Activity activity) {
        this.context=context;
        this.activity=activity;
        readFromFile = new ReadFromFile(context,activity);
    }
    // searches with String value in textviews
    public void start(View view) {
        {
            // Inflate the popup_layout.xml
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.find_popup, activity.findViewById(R.id.nav_host_fragment_content_main3),false);

            // Create the PopupWindow
            int width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
            View dimBackground =activity.findViewById(R.id.dim_background);
            dimBackground.setVisibility(View.VISIBLE);
            // Show the PopupWindow
            popupWindow.showAsDropDown(view);
            Button findtextsave = popupView.findViewById(R.id.findtextsave);
            findtextsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean found =false;
                    TextInputEditText findText = popupView.findViewById(R.id.findtext);
                    texttofind = findText.getText().toString();

                    if(texttofind.isEmpty())
                        return;
                    // System.out.println("texttofind=" + texttofind);
                    // loop to find
                    int skipcharsread=0;
                    int firsttry=0;
                    // System.out.println("readdone="+readdone);
                    while (firsttry==0 || nextchar!=-1) {
                        if (found)
                            break;
                        firsttry++;
                        // System.out.println("read file while looooooooop starts");
                        // System.out.println("edittextcount="+edittextcount);
                        // System.out.println("textviewcount="+textviewcount);
                        // System.out.println("readdone="+readdone);
                        // System.out.println("nextchar="+nextchar);

                        //   readsize = 500; to read more
                        readFromFile.start();
                        // System.out.println("File is read");
                        // System.out.println("edittextcount="+edittextcount);
                        // System.out.println("textviewcount="+textviewcount);
                        result="";
                        // System.out.println("customTextViewArrayList.size()="+customTextViewArrayList.size());
                        // System.out.println("skipcharsread="+skipcharsread);
                        while (skipcharsread < customTextViewArrayList.size()) {
                            // System.out.println("looooooooop");
                            result+=customTextViewArrayList.get(skipcharsread).getText().toString();
                            skipcharsread++;
                            if (result.contains(texttofind)){
                                found=true;
                                // System.out.println("fooooooooooound");
                                // System.out.println("result.lenght="+result.length());
                                // System.out.println("texttofind.lenght="+texttofind.length());
                                // System.out.println("customTextViewArrayList.size()="+customTextViewArrayList.size());
                                CustomEditText customEditText =customEditTextArrayList.get(skipcharsread-texttofind.length());
                                customEditText.requestFocus();
                                customEditText.setSelection(0,customEditText.getText().toString().length());
                                result="";
                                Button findnext= activity.findViewById(R.id.findnextbutton);
                                findnext.setEnabled(true);
                                Snackbar.make(view, "Found", Snackbar.LENGTH_SHORT).show();
                                break;
                            }


                            // System.out.println("resultttttt="+result);



                        }
                    }
                  //  readsize = 50; change to default
                    View dimBackground = activity.findViewById(R.id.dim_background);
                    dimBackground.setVisibility(View.INVISIBLE);
                    popupWindow.dismiss();
                    if (!found) {
                        Snackbar.make(view, "Not Found", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    dimBackground.setVisibility(View.GONE);
                }
            });
        }
    }
}
