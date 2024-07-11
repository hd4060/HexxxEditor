package com.dio5656.hexxxeditor;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.dio5656.hexxxeditor.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
  static   Uri myUri = null;
   static TableLayout.LayoutParams params = new TableLayout.LayoutParams(android.widget.TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

   static int column_size =5;
    static int columnSetCount=0;
   static long edittextcount;
 static    long textviewcount;
   static HashMap<String, String> savehashMap = new HashMap<>();
    ReadFromFile readFromFile;
    static int skip=0; //total file read
    static   int count=0; // read
    static int readsize=150; // to read part by part
    static boolean readdone = false;
    static ArrayList<CustomTextView> customTextViewArrayList = new ArrayList<>();
    static ArrayList<CustomEditText> customEditTextArrayList = new ArrayList<>();
    static int nextchar=1;
    static String result=  "";
    static String texttofind ="";
    static boolean customColumns=false;
    int customColumnSize;
     TableLayout tableLayout;
    int screenRatio;
  static   boolean firsttime = true;

static  String Encoding ="Utf8";
    private NavController navController;
   static FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Encoding =getFromSharedPreferences("Encoding","Utf8");
        System.out.println("Encoding="+ Encoding);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // Get screen dimensions and density

        int density = (int) displayMetrics.density;
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int scaledDensity = (int)displayMetrics.scaledDensity;
        int scaledFontSize = 16*(screenWidth/ scaledDensity );
         screenRatio =  screenWidth*10/screenHeight;
        System.out.println("scaledDensity="+scaledDensity);
        System.out.println("Screen ratio="+screenRatio);
        //column_size = scaledFontSize/60/22*5/11*3+1;
        column_size = screenRatio ;
// column size depends on screen width and density
        // column_size = scaledFontSize/60/22*5/11*3+2;
        System.out.println("column_size="+column_size);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
       if (!checkPermissions()) {
            requestPermissions();
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main3);
         navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController);
       // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
       NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //open file button
        View openFileButton = findViewById(R.id.openfilebutton2);
        openFileButton.setOnClickListener(press-> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            readdone=false;
            startActivityForResult(intent,2);

        });

        //Set classes to use
        readFromFile = new ReadFromFile(this,MainActivity.this);
        Find find= new Find(this, MainActivity.this);
        FindNext findNext = new FindNext(this,MainActivity.this);
        Edit edit = new Edit(this,MainActivity.this);
      SaveFile saveFile = new SaveFile(this,MainActivity.this);


        //find button pressed
        Button findButton = findViewById(R.id.findbutton);
        findButton.setOnClickListener(view -> find.start(view));

        //read button
        Button readmbutton = findViewById(R.id.readmbutton);
        readmbutton.setOnClickListener(view -> readFromFile.start());

        //find next button
        Button findnextbutton = findViewById(R.id.findnextbutton);
        findnextbutton.setOnClickListener(view -> findNext.start(view));

        //edit as string button
        Button editButton = findViewById(R.id.editbutton);
        editButton.setOnClickListener(view -> edit.start(view));

        //Save to File
        Button savebutton =findViewById(R.id.savebutton);
        savebutton.setOnClickListener(view -> saveFile.start(view));


        //Save theme option
        SharedPreferences prefs = getSharedPreferences("theme", MODE_PRIVATE);
        String theme= prefs.getString("theme","auto");
        System.out.println("theme="+theme);
        if (theme.equals("white"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (theme.equals("dark"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);



        //check theme button presses
        ImageButton autotheme =findViewById(R.id.autotheme);
        autotheme.setOnClickListener(v ->
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                    editor.putString("theme", "auto");
                    editor.apply();
                }
        );
        ImageButton darktheme =findViewById(R.id.darktheme);
             darktheme.setOnClickListener(v -> {
                 AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                 SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                 editor.putString("theme", "dark");
                 editor.apply();
             });
        ImageButton whitetheme =findViewById(R.id.whitetheme);
        whitetheme.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
            editor.putString("theme", "white");
            editor.apply();
        });


        /// check update
         fragmentManager = getSupportFragmentManager();
        UpdateChecker updateChecker=new UpdateChecker(this,MainActivity.this);
        updateChecker.execute();
        /*try {

       //  long versionCode = packageInfo.getLongVersionCode();

       //     String versionInfo = "Version Name: " + versionName + "\nVersion Code: " + versionCode;
          //  System.out.println("vvvvvv"+versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/

    }
    public boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }

    Cursor c;
    /// get filename method
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
         c = getContentResolver().query(uri, null, null, null, null);
        c.moveToFirst();

        return c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
    }



    //get uri and read file when open file button is pressed
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
       MaterialToolbar toolbar = findViewById(R.id.toolbar);
         tableLayout = findViewById(R.id.mytable);
        ScrollView scrollView = findViewById(R.id.scrollviewfortable);
        super.onActivityResult( requestCode,  resultCode, resultData);
        if (resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                savehashMap.clear();
                myUri = resultData.getData();
                skip=0;
               toolbar.setTitle( getFileName(myUri));
               c.close();
               Button findnextbutton = findViewById(R.id.findnextbutton);
               findnextbutton.setEnabled(false);
               nextchar=1;
               edittextcount=0;
               textviewcount=0;
                column_size=screenRatio;
                 firsttime = true;
               ///hide or set visible the buttons
                Button openFileButton = findViewById(R.id.openfilebutton2);
                openFileButton.setVisibility(View.INVISIBLE);
                Button findButton = findViewById(R.id.findbutton);
                findButton.setVisibility(View.VISIBLE);
              //  Button findnext = findViewById(R.id.findnextbutton);
                findnextbutton.setVisibility(View.VISIBLE);
               // Button readmbutton = findViewById(R.id.readmbutton);
                /* readmbutton.setVisibility(View.VISIBLE);*/
                Button editButton = findViewById(R.id.editbutton);
                editButton.setVisibility(View.VISIBLE);
                Button saveButton = findViewById(R.id.savebutton);
                saveButton.setVisibility(View.VISIBLE);

                scrollView.setScrollbarFadingEnabled(false);
                scrollView.setVisibility(View.VISIBLE);
                View themecontainer = findViewById(R.id.themecontainer);
                themecontainer.setVisibility(View.INVISIBLE);
                tableLayout.removeAllViews();
                customEditTextArrayList.clear();
                customTextViewArrayList.clear();
                columnSetCount=0;

                ExecutorService executorService = Executors.newSingleThreadExecutor();

                executorService.execute(() -> {
                    runOnUiThread(() -> {


                        params.setMargins(20, 0, 20, 20);
                        TableRow header_row = new TableRow(this);
                        header_row.setPadding(10, 0, 20, 0);
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int screenWidth = displayMetrics.widthPixels;
                        int scaledDensity = (int)displayMetrics.scaledDensity;
                        int scaledFontSize = 16*(screenWidth/ scaledDensity );
                        //column_size = scaledFontSize/60/22*5/11*3+2;
                        tableLayout.addView(header_row, params);
                        tableLayout.setScrollContainer(true);
                        tableLayout.setVerticalScrollBarEnabled(true);

                       // ScrollView scrollview = findViewById(R.id.scrollviewfortable);



                        //create header
                  /* for (int i = 1; i <= column_size; i++) {
                       TextView textView_header1 = new TextView(MainActivity.this);
                       textView_header1.setTextSize(20);
                       textView_header1.setTypeface(null, Typeface.BOLD);
                       textView_header1.setText("0" + i);
                       header_row.addView(textView_header1);
                   }*/



                        ///read hex and string values
                        readFromFile.start();


                        //read more from file when  bottom of scroll view is reached
                        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                            if (!scrollView.canScrollVertically(1)) {

                                readFromFile.start();

                            }
                        });


                    });
                });
                executorService.shutdown();
            }

        }




    }


    public static String hexToString(String hex) {
        StringBuilder output = new StringBuilder();
        output.append((char) Integer.parseInt(hex, 16));
        System.out.println("output="+output );
        return output.toString();
    }
    public static String stringToHex(String input) {
        StringBuilder hexString = new StringBuilder();
        for (char ch : input.toCharArray()) {
            String hex = Integer.toHexString(ch);
            hexString.append(hex);
        }

        return hexString.toString();
    }
    public static byte[] hexStringToByteArray(String hexString) throws IllegalArgumentException {
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even length");
        }

        int len = hexString.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }

        return data;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuInflater inflater = getMenuInflater();
      //  inflater.inflate(R.menu.main_menu, menu);

        // Find the Spinner
        MenuItem item = menu.findItem(R.id.showcolumnpopup);
        Spinner spinner = (Spinner) item.getActionView();

      /*  // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Columns, android.R.layout.simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);*/
//if( getCurrentFocus() == findViewById(R.id.mainmenu))


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.openanewfile) {
             Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
                  intent.setType("*/*");
           readdone=false;
            //column_size = scaledFontSize/60/22*5/11*3+2;
            startActivityForResult(intent,2);
            return true;
        }
        if (id == R.id.settings) {
            //
       //     NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main2);
      // NavController navController = navHostFragment.getNavController();
          //  Intent intent = new Intent(MainActivity.this, );
         //   startActivity(intent);
             // navController.navigate(R.id.secondFragment);
            setContentView(R.layout.fragment_second);
            Spinner spinner =findViewById(R.id.spinner);
            if (Encoding.equals("Ansi"))
                spinner.setSelection(1);
            else
                spinner.setSelection(0);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   saveToSharedPreferences("Encoding",spinner.getSelectedItem().toString());
                    Encoding =getFromSharedPreferences("Encoding","Utf8");
                    System.out.println("hh"+spinner.getSelectedItem().toString());
                    }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            System.out.println("spinner="+spinner);

        }
        if (id == R.id.showcolumnpopup)
        {        //  View view = MainActivity.this.findViewById(id);
           // view.setVisibility(View.INVISIBLE);


        showSpinnerPopup(findViewById(R.id.findbutton));
           // return  true;
        }

return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem layoutsettings = menu.findItem(R.id.showcolumnpopup);
        if (myUri!=null)
            layoutsettings.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main3);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
   public void onBackPressed() {
/*
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main2);
//        NavController navController = navHostFragment.getNavController();
      if (navController.getCurrentDestination().getId() == R.id.secondFragment) {
            System.out.println("ssssssdddddssssssssssss");
           // setContentView(R.layout.activity_main);
       //     navController.navigate(R.id.action_secondFragment_to_firstFragment);
        } else {
            super.onBackPressed();
        }*/
  //  if (this.getconte)

        View rootView = findViewById(android.R.id.content);
        if (rootView.findViewById(R.id.findbutton)==null)
        setContentView(binding.getRoot());
    //    setSupportActionBar(binding.toolbar);
       else
       {
             super.onBackPressed();
        }
      //  NavigationUI.setupActionBarWithNavController(this, navController);
       // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
       // setContentView(R.layout.activity_main);
    }

    private void saveToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply(); // or editor.commit() to save immediately
    }
    private String getFromSharedPreferences(String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,defaultValue);
    }
    private void showSpinnerPopup(View anchor) {
         PopupWindow popupWindow;
        // Inflate the spinner layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.column_spinner_popup, findViewById(R.id.nav_host_fragment_content_main3),false);

        // Create the popup window
        popupWindow = new PopupWindow(popupView,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                true);
        View dimBackground = findViewById(R.id.dim_background);
        dimBackground.setVisibility(View.VISIBLE);

        // Initialize the spinner
        Spinner spinner = popupView.findViewById(R.id.column_spinner);
        spinner.setSelection(column_size-3);
      /*  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Columns, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource*/

        // Set the listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // String selectedItem = parent.getItemAtPosition(position).toString();
                String selectedItem =  spinner.getSelectedItem().toString();
          //      Toast.makeText(MainActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();

           customColumnSize= Integer.parseInt(selectedItem);
           if (column_size != customColumnSize)
           {
               customColumns=true;
               column_size = Integer.parseInt(selectedItem);
               resetValuesAndReadFromStart();
           }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no item is selected
            }
        });
        Button closebutton = popupView.findViewById(R.id.closebutton);
        closebutton.setOnClickListener(v -> {popupWindow.dismiss();});

        // Show the popup window below the menu item
        popupWindow.showAsDropDown(anchor, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dimBackground.setVisibility(View.GONE);
            }
        });

    }


    //method for reading values again after layout update
       public   void  resetValuesAndReadFromStart() {
        readdone=false;
        count=0;
        nextchar=1;
        skip=0;
        edittextcount=0;
        textviewcount=0;
        customEditTextArrayList.clear();
        customTextViewArrayList.clear();
        // savehashMap.clear();
        //TableLayout tableLayout=findViewById(R.id.mytable);
        tableLayout.removeAllViews();
      //  ReadFromFile readFromFile1 = new ReadFromFile()
        readFromFile.start();
        int lastaddress=0;
        // get the last address that was modified
        for (String address: savehashMap.keySet())
        {
            if (Integer.parseInt(address)>lastaddress)
             lastaddress= Integer.parseInt(address);

        }
        // read the file till the last adress and update changes to the file
        while (customEditTextArrayList.size()<lastaddress)
            readFromFile.start();
        for (String address : savehashMap.keySet()) {
            EditText editText=customEditTextArrayList.get(Integer.parseInt(address));
            editText.setText(savehashMap.get(address));
        }

    }
}