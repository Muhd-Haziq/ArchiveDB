 package sg.edu.rp.c346.s19047241.archivedb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

 public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_DETAIL = 1; // -> Request to CharacterDetails screen

    private GridView charaGridView;
    private EditText searchText;
    private ToggleButton btnListSwitch;
    private SwitchCompat switchLang;
    private ListView charaListView;

    private TextView tvSortText;
    private Button btnSort, btnSortSave, btnSortCancel;
    private RadioGroup radioGroupCharaType;
    private RadioButton radioSortAll, radioSortStriker, radioSortSpecial;
    private Dialog sortDialog;

    private DatabaseReference charRef;

    private DrawerLayout drawerLayout;

    private ArrayList<Character> charactersArrayList = new ArrayList<Character>();
    private CharaGridAdapter charaGridAdapter;
    private CharaListAdapter charaListAdapter;

    private boolean switchLangOnOff;

    private String userSearch = "";

    private boolean SORT_ALL = true;
    private boolean SORT_STRIKER = false;
    private boolean SORT_SPECIAL = false;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SHARED_SWITCHLANG = "switchLang";
    public static final String SHARED_SORT_ALL = "sortAll";
    public static final String SHARED_SORT_STRIKER = "sortStriker";
    public static final String SHARED_SORT_SPECIAL = "sortSpecial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // NO DARK MODE
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);

        charaGridView = findViewById(R.id.charGridView);
        charaListView = findViewById(R.id.charaListView);

        searchText = findViewById(R.id.txtSearch);

        initSortDialog();

        btnSort = findViewById(R.id.btnSort);

        btnListSwitch = findViewById(R.id.btnGridSwap);
        switchLang = findViewById(R.id.switchLang);

        loadSharedPrefs();
        updateLangSwitch();
        languageSwap();

        charaGridAdapter = new CharaGridAdapter(MainActivity.this, charactersArrayList, switchLangOnOff);
        charaGridView.setAdapter(charaGridAdapter);

        charaListAdapter = new CharaListAdapter(MainActivity.this, charactersArrayList, switchLangOnOff);
        charaListView.setAdapter(charaListAdapter);

        charRef = FirebaseDatabase.getInstance().getReference().child("students");


        // -> Retrieving data from Realtime Database and placing them in the charactersArrayList
        charRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                charactersArrayList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Character chara = snap.getValue(Character.class);

                   addCharaBasedOnCharaTypeSort(chara);
                }

                charaListAdapter.notifyDataSetChanged();
                charaGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // -> Updating GridView with characters based on user searching their names
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userSearch = s.toString();

                updateCharaListBasedOnSearchText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        Character chara = new Character();

//        chara = new Character();
//
//        chara.setEn_name("Kuromi Serika");
//        chara.setEn_school("Abydos High School");
//        chara.setEn_club("Countermeasure Committee");
//        chara.setEn_description("A grouchy secretary on the task force at of the Countermeasure Committee at Abydos High. One with many complaints, Serika's the type hesitant to show her emotions. Despite her daily habit of saying, \"It's fine if this school would just go out of business!\", she has such a deep love for her school that she secretly works part-time at Shibaseki to pay off the school's debts.");
//        chara.setEn_hobby("Saving money, working part-time");
//        chara.setEn_role("Attacker");







        // -> Setting up the GridView to move to Character Details
        // screen with appropriate information when clicked
        charaGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intentToCharaDetails(position);
            }
        });

        charaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intentToCharaDetails(position);
            }
        });

        // -> Saving state of language switch
        switchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean checked = switchLang.isChecked();

                switchLangOnOff = checked;
                charaGridAdapter.setLangBool(switchLangOnOff);
                charaListAdapter.setLangBool(switchLangOnOff);

                charaGridAdapter.notifyDataSetChanged();
                charaListAdapter.notifyDataSetChanged();

                languageSwap();
                saveSharedPrefs();

                userSearch = searchText.getText().toString().trim();

                updateCharaListBasedOnSearchText();
            }
        });

        /* ------------------ SWITCHES BETWEEN GRID AND LIST VIEWS - START ------------------ */

        btnListSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnListSwitch.isChecked()){
                    charaGridView.setVisibility(View.GONE);
                    charaListView.setVisibility(View.VISIBLE);
                }else{
                    charaGridView.setVisibility(View.VISIBLE);
                    charaListView.setVisibility(View.GONE);
                }

            }
        });

        /* ------------------ SWITCHES BETWEEN GRID AND LIST VIEWS - END ------------------------------------ */


        /* -----------------------  OPEN UP SORT ACTIVITY - START ----------------------- */

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchLangOnOff){
                    btnSortCancel.setText("キャンセル");
                }else{
                    btnSortCancel.setText("Cancel");
                }

                // Method to change sort selected
                btnSortOnOffSwitch();

                if(SORT_ALL){
                    radioSortAll.setChecked(true);
                }else if(SORT_STRIKER){
                    radioSortStriker.setChecked(true);
                }else if(SORT_SPECIAL){
                    radioSortSpecial.setChecked(true);
                }

                btnSortCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        sortDialog.dismiss();
                    }
                });

                btnSortSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(radioSortAll.isChecked()){
                            SORT_ALL = true;
                            SORT_STRIKER = false;
                            SORT_SPECIAL = false;
                        }else if(radioSortStriker.isChecked()){
                            SORT_STRIKER = true;
                            SORT_SPECIAL = false;
                            SORT_ALL = false;
                        }else if(radioSortSpecial.isChecked()){
                            SORT_SPECIAL = true;
                            SORT_STRIKER = false;
                            SORT_ALL = false;
                        }

                        updateCharaListBasedOnSearchText();
                        charaListAdapter.notifyDataSetChanged();
                        charaGridAdapter.notifyDataSetChanged();
                        saveSharedPrefs();
                        sortDialog.dismiss();

                    }
                });

                sortDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                sortDialog.show();
            }
        });

    }

     /* -----------------------  OPEN UP SORT ACTIVITY - END ---------------------------------------------- */



    /* ------------------------------ METHOD TO CONVERT HIRAGANA INPUT TO KATAKANA - START ---------------------- */
    // -> Ensures both inputs can be read and matches each other

    private String hiraganaTokatakana(String hiraganastring) {

        StringBuilder katakanaVal = new StringBuilder();

        for(int i = 0; i < hiraganastring.length(); i++){

            char hiraganachar = java.lang.Character.valueOf(hiraganastring.charAt(i));
            // convert char to unicode value
            String hiraganahex = Integer.toHexString(hiraganachar & 0xFFFF);
            // convert unicode to decimal
            int hiraganadecimalNumber = Integer.parseInt(hiraganahex, 16);
            // convert hiragana decimal to katakana decimal
            int katakanadecimalNumber = Integer.valueOf(hiraganadecimalNumber) + 96;
            // covert decimal to unicode value
            String katakanahex = Integer.toString(katakanadecimalNumber, 16);
            // convert unicode to char
            char katakanaChar = (char) Integer.parseInt(String.valueOf(katakanahex), 16);

            katakanaVal.append(katakanaChar);
        }

        return katakanaVal.toString();
    }

    /* ------------------------------ METHOD TO CONVERT HIRAGANA INPUT TO KATAKANA - END --------------------------------------------------- */




    /* ------------------------------------ METHODS LANGUAGE SHARED PREF - START ------------------------------------ */

    public void saveSharedPrefs(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHARED_SWITCHLANG, switchLang.isChecked());
        editor.putBoolean(SHARED_SORT_ALL, SORT_ALL);
        editor.putBoolean(SHARED_SORT_STRIKER, SORT_STRIKER);
        editor.putBoolean(SHARED_SORT_SPECIAL, SORT_SPECIAL);

        editor.apply();

        //Toast.makeText(this, "Data saved.", Toast.LENGTH_SHORT).show();
    }

    public void loadSharedPrefs(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switchLangOnOff = sharedPreferences.getBoolean(SHARED_SWITCHLANG, false);
        SORT_ALL = sharedPreferences.getBoolean(SHARED_SORT_ALL, false);
        SORT_STRIKER = sharedPreferences.getBoolean(SHARED_SORT_STRIKER, false);
        SORT_SPECIAL = sharedPreferences.getBoolean(SHARED_SORT_SPECIAL, false);
    }

    public void updateLangSwitch(){
        switchLang.setChecked(switchLangOnOff);

    }

    public void updateLangSwitchNoAnimation(){
        switchLang.setChecked(switchLangOnOff);
        switchLang.jumpDrawablesToCurrentState();
    }


    /* ------------------------------------ METHODS LANGUAGE SHARED PREF - END ------------------------------------ */





    /* ------------------------------------ METHOD TO CHANGE GRID VIEW ITEMS BASED ON SEARCH BAR - START ------------------------------------ */

    public void updateCharaListBasedOnSearchText(){
        charRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // -> Clear array list to ensure no duplicate data
                charactersArrayList.clear();

                // -> First, retrieve all character data from Realtime Database
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Character chara = snap.getValue(Character.class);

                    // -> Second, check whether userSearch EditText is empty
                    // If empty, display all characters
                    if (userSearch.equals("")) {
                        addCharaBasedOnCharaTypeSort(chara);
                    }else{
                        // -> Third, if userSearch is not empty, check for the
                        // first name of each character in DB

                        // (In the database, character full names are listed)

                        // -> If set to EN
                        if(!switchLangOnOff){
                            String enFirstName = chara.getEnFirstName();

                            // -> Fourth, set firstName and userSearch
                            // to lowercase to ensure proper match
                            if (enFirstName.toLowerCase().contains(userSearch.toLowerCase())) {
                                addCharaBasedOnCharaTypeSort(chara);
                            }

                        }else{
                            // Else is JP
                            String jpFirstName = chara.getJpFirstName();

                            String nameConvertKatakana = hiraganaTokatakana(userSearch);
                            //Log.e("SEARCH TEXT", jpKatakana);

                            // -> Checks for both exact katakana input
                            // as well as hiragana input
                            if (jpFirstName.contains(String.valueOf(nameConvertKatakana)) || jpFirstName.contains(userSearch)) {
                                addCharaBasedOnCharaTypeSort(chara);
                            }

                        }
                    }
                }
                charaListAdapter.notifyDataSetChanged();
                charaGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /* ------------------------------------ METHOD TO CHANGE GRID VIEW ITEMS BASED ON SEARCH BAR - END ------------------------------------ */




    /* ------------------------------------ METHODS FOR NAV DRAWER - START ------------------------------------ */

    public void ClickMenu(View view){
        // Open drawer
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {

        // Open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view){
        // Close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        // Close drawer layout
        // Check condition
        if(drawerLayout.isDrawerOpen((GravityCompat.START))){
            // When drawer is open, close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }

    }

    /* --------- NAV DRAWER, REDIRECTING TO DIFFERENT PAGES - START --------- */

    public void ClickStudents(View view){
        // close drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickGacha(View view){
        // Redirect activity to Gacha
        redirectActivity(this, GachaScreenActivity.class);
    }

    public void ClickInventory(View view){
        // Redirect activity to Inventory
        redirectActivity(this, InventoryActivity.class);
    }


    // redirect activity class (swapping screens)

    public static void redirectActivity(Activity activity, Class aClass) {
        // Initialize intent
        Intent intent = new Intent(activity, aClass);

        // set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //Start activity
        activity.startActivity(intent);

    }

    /* ------------------ NAV DRAWER, REDIRECTING TO DIFFERENT PAGES - END ------------------ */

    @Override
    protected void onPause() {
        super.onPause();

        // Close drawer
        closeDrawer(drawerLayout);
    }

    /* ------------------------------------ METHODS FOR NAV DRAWER - END ------------------------------------ */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_DETAIL){

            // -> Check if Intent data received from StudentDetails is null
            // If not null, execute command to retrieve new language switcher value
            // and passes it over to current switcher

            if(data != null){
                if(resultCode == RESULT_OK){
                    //Log.e("SWITCH RESULT CODE", "OK");
                    // -> Retrieve "lang" variable from StudentDetails activity on back button pressed
                    switchLangOnOff = data.getBooleanExtra("lang", false);

                    updateLangSwitchNoAnimation();
                    charaGridAdapter.setLangBool(switchLangOnOff);
                    charaGridAdapter.notifyDataSetChanged();

                    charaListAdapter.setLangBool(switchLangOnOff);
                    charaListAdapter.notifyDataSetChanged();

                    languageSwap();
                }
            }
        }
    }


     /* ---------------------------- CREATING INTENT TO CHARACTER DETAILS SCREEN - START --------------------------------------------------------------  */

     public void intentToCharaDetails(int position){
        Intent intent = new Intent(MainActivity.this, StudentDetails.class);
        intent.putExtra("name", charactersArrayList.get(position).getEn_name());
        intent.putExtra("language", switchLangOnOff);

        startActivityForResult(intent, REQUEST_DETAIL);
    }

     /* ---------------------------- CREATING INTENT TO CHARACTER DETAILS SCREEN - END ----------------------------  */

     /* ---------------------------- SWITCHING LANGUAGES OF GENERAL FUNCTION - START --------------------------------------------------------------  */

     public void languageSwap(){
        if(switchLangOnOff){
            // Set Search TextField and btnSort text to JP
            searchText.setHint("アーカイブ内検索");
            btnSort.setText("並び替え");
            tvSortText.setText("並び替え");

            // Set btnListSwitch on/off/current text to JP
            btnListSwitch.setTextOff("リストへ");
            btnListSwitch.setTextOn("グリッドへ");

            if(btnListSwitch.isChecked()){
                btnListSwitch.setText("グリッドへ");
            }else{
                btnListSwitch.setText("リストへ");
            }

        }else{
            // Set Search TextField and btnSort text to EN
            searchText.setHint("Search...");
            btnSort.setText("Filter");
            tvSortText.setText("Filter");

            // Set btnListSwitch on/off/current text to EN
            btnListSwitch.setTextOff("List View");
            btnListSwitch.setTextOn("Grid View");

            if(btnListSwitch.isChecked()){
                btnListSwitch.setText("Grid View");
            }else{
                btnListSwitch.setText("List View");
            }
        }
    }

     /* ---------------------------- SWITCHING LANGUAGES OF GENERAL FUNCTION - END ----------------------------  */


     public void btnSortOnOffSwitch(){

        radioGroupCharaType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == radioSortAll.getId()){ // All is selected

                    radioSortAll.setBackgroundResource(R.drawable.sort_all_on);

                    radioSortSpecial.setBackgroundResource(R.drawable.sort_special_off);
                    radioSortStriker.setBackgroundResource(R.drawable.sort_striker_off);
                }else if(checkedId == radioSortStriker.getId()){  // Striker is selected

                    radioSortStriker.setBackgroundResource(R.drawable.sort_striker_on);

                    radioSortAll.setBackgroundResource(R.drawable.sort_all_off);
                    radioSortSpecial.setBackgroundResource(R.drawable.sort_special_off);
                }else if(checkedId == radioSortSpecial.getId()){ // Special is selected

                    radioSortSpecial.setBackgroundResource(R.drawable.sort_special_on);

                    radioSortAll.setBackgroundResource(R.drawable.sort_all_off);
                    radioSortStriker.setBackgroundResource(R.drawable.sort_striker_off);
                }
                //Log.e("RADIO CHECKED ID", "" + checkedId);
            }
        });

    }

     /* ---------------------------- SWITCHING LANGUAGES OF GENERAL FUNCTION - END ----------------------------  */


     /* ----------------- ADD CHARACTERS TO ARRAY LIST BASED ON CHARACTER TYPE - START ---------------------------------- */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addCharaBasedOnCharaTypeSort(Character chara){
        if(SORT_ALL){
            charactersArrayList.add(chara);
        }else if(SORT_STRIKER){

            if(chara.getShared_type().equals("STRIKER")){
                charactersArrayList.add(chara);
            }

        }else{

            if(chara.getShared_type().equals("SPECIAL")){
                charactersArrayList.add(chara);
            }
        }

        sortCharacters();
    }

    /* ----------------- ADD CHARACTERS TO ARRAY LIST BASED ON CHARACTER TYPE - END ----------------- */


    /* ---------------------- INITIALIZE DIALOG FOR SORTING ARRAY LIST VALUES - START ------------------- */

    public void initSortDialog(){
        sortDialog = new Dialog(this);
        sortDialog.setContentView(R.layout.sort_popup_layout);
        sortDialog.setCanceledOnTouchOutside(false);

        tvSortText = sortDialog.findViewById(R.id.tvSortText);

        btnSortCancel = sortDialog.findViewById(R.id.btnSortCancel);
        btnSortSave = sortDialog.findViewById(R.id.btnSortSave);

        radioGroupCharaType = sortDialog.findViewById(R.id.rgCharaType);

        radioSortAll = sortDialog.findViewById(R.id.radioSortAll);
        radioSortStriker = sortDialog.findViewById(R.id.radioSortStriker);
        radioSortSpecial = sortDialog.findViewById(R.id.radioSortSpecial);

    }

    /* ---------------------- INITIALIZE DIALOG FOR SORTING ARRAY LIST VALUES - END ------------------------------------- */


     /* ----------------- SORT CHARACTER ARRAY LIST - START ------------------------ */

     @RequiresApi(api = Build.VERSION_CODES.N)
     public void sortCharacters(){
         Comparator comparator;

         if(switchLangOnOff){
             comparator = Comparator.comparing(Character::getEnFirstName);
         }else{
             comparator = Comparator.comparing(Character::getJpFirstName);
         }

         Collections.sort(charactersArrayList, comparator);
     }

     /* ----------------- SORT CHARACTER ARRAY LIST - END -------------------------------------- */


}