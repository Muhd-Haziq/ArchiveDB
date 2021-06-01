package sg.edu.rp.c346.s19047241.archivedb;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GachaScreenActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    private ArrayList<Character> oneStarArrayList;
    private ArrayList<Character> twoStarArrayList;
    private ArrayList<Character> threeStarArrayList;

    private AppCompatButton btnSinglePull, btnMultiPull;

    private DatabaseReference charRef;

    private Character singleSummonedCharacter;
    private ImageView ivSummonedChara;
    private LinearLayout singleRarityBgLayout;
    private RelativeLayout characterLayout;
    private RatingBar rarityBar;
    private TextView charName;
    private TextView txtGachaTitle, txtSinglePull, txtMultiPull;

    private GridView gvMultiSummonedChara;

    private ArrayList<LocalCharacter> allLocalCharacters;
    private ArrayList<LocalCharacter> retrievedLocalCharacters;
    private ArrayList<Character> alMultiSummonedCharacters;
    private SummonGridAdapter charaGridAdapter;

    private SwitchCompat switchLang;
    private boolean switchLangOnOff;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SHARED_SWITCHLANG = "switchLang";


    // Database Helper
    private final DBHelper dbHelper = new DBHelper(this);


    // Initialize rates dialog
    private Dialog ratesDialog;
    private ScrollView ratesScrollView;
    private Button btnRatesOk, btnRates;
    private TextView tvRatesText;

    private RatesGridView gvThreeStar, gvTwoStar, gvOneStar;
    private CharaRatesGridAdapter threeCharaRatesGridAdapter, twoCharaRatesGridAdapter, oneCharaRatesGridAdapter;

    private ImageButton btnThreeGridViewDisplay, btnTwoGridViewDisplay, btnOneGridViewDisplay;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gacha_screen);

        initViewsArrayLists();


        charRef = FirebaseDatabase.getInstance().getReference().child("students");

        // -> Retrieving data from Realtime Database and placing
        // them in the array lists based on their rarity
        charRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oneStarArrayList.clear();
                twoStarArrayList.clear();
                threeStarArrayList.clear();

                retrievedLocalCharacters.clear();
                allLocalCharacters.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Character chara = snap.getValue(Character.class);

                    // -> Insert each character into their respective array list
                    // based on their rarity
                    assert chara != null;
                    if (chara.getShared_rarity() == 1) {
                        oneStarArrayList.add(chara);
                    } else if (chara.getShared_rarity() == 2) {
                        twoStarArrayList.add(chara);
                    } else {
                        threeStarArrayList.add(chara);
                    }


                    /* -------------------------- REPLACE CHARACTER IN SQLITE - START ---------------------------------------------------- */

                    // -> This part is for addition to the inventory
                    // Add all characters into a LocalCharacter array list
                    // (LocalCharacter is the object used to store the character into the local database SQLite)
                    allLocalCharacters.add(new LocalCharacter(0, chara.getEnFirstName(), chara.getJpFirstName(), chara.getShared_rarity(), chara.getShared_imageThumbnail(), 0));

                    // -> Retrieve all LocalCharacter from the db
                    retrievedLocalCharacters = dbHelper.getAllCharacters();

                    // If exists,
                    if (retrievedLocalCharacters != null) {
                        // search for each item in the retrievedArrayList,
                        for (LocalCharacter retrievedLc : retrievedLocalCharacters) {
                            // and check it against all values in the allLocalCharacters array list
                            for (LocalCharacter allLc : allLocalCharacters) {
                                // to ensure that the counter is set correctly for that character
                                // Counter pertains to how many times the user pulled that character
                                if (retrievedLc.getEnName().equalsIgnoreCase(allLc.getEnName())) {
                                    allLc.setCount(retrievedLc.getCount());
                                }

                            }
                        }
                    }

                    /* -------------------------- REPLACE CHARACTER IN SQLITE DB - END -------------------------- */


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* --------------------- SINGLE SUMMON BUTTON - START ------------------------------------------ */

        btnSinglePull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display SinglePull View
                singleRarityBgLayout.setVisibility(View.VISIBLE);
                characterLayout.setVisibility(View.GONE);

                // -> Don't Display MultiPull GridList
                gvMultiSummonedChara.setVisibility(View.GONE);

                singleSummonedCharacter = summonOne();

                summonLangSwap(singleSummonedCharacter);


            }
        });

        /* --------------------- SINGLE SUMMON BUTTON - END -------------------------- */



        /* --------------------- MULTI SUMMON BUTTON - START ------------------------------------------ */

        btnMultiPull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alMultiSummonedCharacters.clear();
                // -> Don't Display SinglePull View
                singleRarityBgLayout.setVisibility(View.GONE);
                characterLayout.setVisibility(View.GONE);

                // -> Display MultiPull GridList
                gvMultiSummonedChara.setVisibility(View.VISIBLE);

                alMultiSummonedCharacters = summonMulti();
                Log.e("MULTI COUNT", alMultiSummonedCharacters.size() + "");

            }
        });

        /* --------------------- MULTI SUMMON BUTTON - END -------------------------- */


        /* --------------------- SET LANGUAGE ON TOGGLE SWITCH - START ------------------------------------------ */

        switchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean checked = switchLang.isChecked();

                switchLangOnOff = checked;
                charaGridAdapter.setLangBool(switchLangOnOff);
                threeCharaRatesGridAdapter.setLangBool(switchLangOnOff);
                twoCharaRatesGridAdapter.setLangBool(switchLangOnOff);
                oneCharaRatesGridAdapter.setLangBool(switchLangOnOff);

                charaGridAdapter.notifyDataSetChanged();
                threeCharaRatesGridAdapter.notifyDataSetChanged();
                twoCharaRatesGridAdapter.notifyDataSetChanged();
                oneCharaRatesGridAdapter.notifyDataSetChanged();


                if (singleSummonedCharacter != null) {
                    summonLangSwap(singleSummonedCharacter);
                }

                languageSwap();
                saveSharedPrefs();
            }
        });

        /* --------------------- SET LANGUAGE ON TOGGLE SWITCH - END -------------------------- */

        /* --------------------- SET RATES DIALOG BUTTON - START ---------------------------------------------------- */


        btnRates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ratesDialog.show();
            }
        });

        btnRatesOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratesDialog.dismiss();
            }
        });

        /* --------------------- SET RATES DIALOG BUTTON - END -------------------------- */

    }


    /* ------------------------------------ INITIALIZE VIEWS & ARRAY LISTS - START ------------------------------------ */

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViewsArrayLists() {
        txtGachaTitle = findViewById(R.id.txtGachaTitle);
        txtSinglePull = findViewById(R.id.txtSinglePull);
        txtMultiPull = findViewById(R.id.txtMultiPull);

        drawerLayout = findViewById(R.id.drawerLayout);

        btnRates = findViewById(R.id.btnRates);

        btnSinglePull = findViewById(R.id.btnSinglePull);
        btnMultiPull = findViewById(R.id.btnMultiPull);

        // -> Multi Gacha Layout
        gvMultiSummonedChara = findViewById(R.id.multiSummonedChara);
        gvMultiSummonedChara.setVisibility(View.GONE);


        // -> Single Gacha Layout
        ivSummonedChara = findViewById(R.id.ivSummonedChara);

        singleRarityBgLayout = findViewById(R.id.singleRarityBgLayout);
        characterLayout = findViewById(R.id.characterLayout);
        singleRarityBgLayout.setVisibility(View.GONE);
        characterLayout.setVisibility(View.GONE);

        rarityBar = findViewById(R.id.rarityBar);
        charName = findViewById(R.id.charName);


        // -> Toggle switch for switching languages
        switchLang = findViewById(R.id.switchLang);

        initRatesDialog();

        loadSharedPrefs();
        updateLangSwitch();

        initArrayLists();
    }

    /* ------------------------------------ INITIALIZE VIEWS & ARRAY LISTS - END ------------------------------------ */


    /* -------------------------- INIT RATES POPUP DIALOG - START ----------------------------------------------------*/

    public void initRatesDialog() {
        ratesDialog = new Dialog(this);
        ratesDialog.setContentView(R.layout.rates_popup_layout);
        ratesDialog.setCanceledOnTouchOutside(false);

        ratesScrollView = ratesDialog.findViewById(R.id.ratesScrollView);
        btnRatesOk = ratesDialog.findViewById(R.id.btnRatesOk);
        tvRatesText = ratesDialog.findViewById(R.id.tvRatesText);

        gvThreeStar = ratesDialog.findViewById(R.id.threeStarGridView);
        gvTwoStar = ratesDialog.findViewById(R.id.twoStarGridView);
        gvOneStar = ratesDialog.findViewById(R.id.oneStarGridView);

        //-> Initialize buttons to display the respective grid views above
        // as their default visibility state is set to gone
        btnThreeGridViewDisplay = ratesDialog.findViewById(R.id.threeStarButtonDisplay);
        btnTwoGridViewDisplay = ratesDialog.findViewById(R.id.twoStarButtonDisplay);
        btnOneGridViewDisplay = ratesDialog.findViewById(R.id.oneStarButtonDisplay);

        btnThreeGridViewDisplay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(gvThreeStar.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(ratesScrollView, new AutoTransition());
                    gvThreeStar.setVisibility(View.VISIBLE);
                    btnThreeGridViewDisplay.setImageResource(R.drawable.ic_arrow_up);

                }else{
                    TransitionManager.beginDelayedTransition(ratesScrollView, new AutoTransition());
                    gvThreeStar.setVisibility(View.GONE);
                    btnThreeGridViewDisplay.setImageResource(R.drawable.ic_arrow_down);

                }
            }
        });

        btnTwoGridViewDisplay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(gvTwoStar.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(ratesScrollView, new AutoTransition());
                    gvTwoStar.setVisibility(View.VISIBLE);
                    btnTwoGridViewDisplay.setImageResource(R.drawable.ic_arrow_up);
                }else{
                    TransitionManager.beginDelayedTransition(ratesScrollView, new AutoTransition());
                    gvTwoStar.setVisibility(View.GONE);
                    btnTwoGridViewDisplay.setImageResource(R.drawable.ic_arrow_down);

                }
            }
        });

        btnOneGridViewDisplay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(gvOneStar.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(ratesScrollView, new AutoTransition());
                    gvOneStar.setVisibility(View.VISIBLE);
                    btnOneGridViewDisplay.setImageResource(R.drawable.ic_arrow_up);

                }else{
                    TransitionManager.beginDelayedTransition(ratesScrollView, new AutoTransition());
                    gvOneStar.setVisibility(View.GONE);
                    btnOneGridViewDisplay.setImageResource(R.drawable.ic_arrow_down);

                }
            }
        });

        // -> End of setting visibilities

    }

    /* -------------------------- INIT RATES POPUP DIALOG - END --------------------------*/

    /* -------------------------- INIT ARRAY LISTS - START ----------------------------------------------------*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initArrayLists(){
        oneStarArrayList = new ArrayList<Character>();
        twoStarArrayList = new ArrayList<Character>();
        threeStarArrayList = new ArrayList<Character>();

        allLocalCharacters = new ArrayList<LocalCharacter>();
        retrievedLocalCharacters = new ArrayList<LocalCharacter>();
        alMultiSummonedCharacters = new ArrayList<Character>();

        charaGridAdapter = new SummonGridAdapter(GachaScreenActivity.this, alMultiSummonedCharacters, switchLangOnOff);
        gvMultiSummonedChara.setAdapter(charaGridAdapter);

        // -> Initialize adapters for rates grid views
        gvThreeStar = ratesDialog.findViewById(R.id.threeStarGridView);
        // Sort the array list based on school and name
        Collections.sort(threeStarArrayList, Comparator.comparing(Character::getEn_school)
                .thenComparing(Character::getEnFirstName));

        threeCharaRatesGridAdapter = new CharaRatesGridAdapter(GachaScreenActivity.this, threeStarArrayList, switchLangOnOff);
        gvThreeStar.setAdapter(threeCharaRatesGridAdapter);

        Collections.sort(twoStarArrayList, Comparator.comparing(Character::getEn_school)
                .thenComparing(Character::getEnFirstName));
        twoCharaRatesGridAdapter = new CharaRatesGridAdapter(GachaScreenActivity.this, twoStarArrayList, switchLangOnOff);
        gvTwoStar.setAdapter(twoCharaRatesGridAdapter);

        Collections.sort(oneStarArrayList, Comparator.comparing(Character::getEn_school)
                .thenComparing(Character::getEnFirstName));
        oneCharaRatesGridAdapter = new CharaRatesGridAdapter(GachaScreenActivity.this, oneStarArrayList, switchLangOnOff);
        gvOneStar.setAdapter(oneCharaRatesGridAdapter);
    }

    /* -------------------------- INIT ARRAY LISTS - END --------------------------*/


    /* ------------------------------------ METHODS LANGUAGE SHARED PREF - START ------------------------------------ */

    public void saveSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHARED_SWITCHLANG, switchLang.isChecked());

        editor.apply();

        //Toast.makeText(this, "Data saved.", Toast.LENGTH_SHORT).show();
    }

    public void loadSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switchLangOnOff = sharedPreferences.getBoolean(SHARED_SWITCHLANG, false);
        languageSwap();

    }

    public void updateLangSwitch() {
        switchLang.setChecked(switchLangOnOff);

    }

    public void updateLangSwitchNoAnimation() {
        switchLang.setChecked(switchLangOnOff);
        switchLang.jumpDrawablesToCurrentState();
    }

    /* ------------------------------------ METHODS LANGUAGE SHARED PREF - END ------------------------------------ */

    /* ------------------------------------ METHODS FOR NAV DRAWER - START ------------------------------------ */

    public void ClickMenu(View view) {
        // Open drawer
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        // Close drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    /* --------- NAV DRAWER, REDIRECTING TO DIFFERENT PAGES - START --------- */

    public void ClickStudents(View view) {
        // Redirect Activity to Students
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickGacha(View view) {
        // Redirect Activity to Gacha
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickInventory(View view) {
        // Redirect Activity to Inventory

        MainActivity.redirectActivity(this, InventoryActivity.class);

    }

    /* ------------------ NAV DRAWER, REDIRECTING TO DIFFERENT PAGES - END ------------------ */

    @Override
    protected void onPause() {
        super.onPause();
        // Close drawer
        MainActivity.closeDrawer(drawerLayout);

    }

    /* ------------------------------------ METHODS FOR NAV DRAWER - END ------------------------------------ */

    /* ---------------------------- METHOD TO SUMMON ONE RANDOM CHARACTER AND RETURN IT - START -------------------------------------------------------- */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Character summonOne() {
        Random random = new Random();

        ArrayList<Character> selectedRarityArrayList = new ArrayList<Character>();
        // -> Setting a random number from 0.01 to 100
        // to act as percentage rates
        Double rangeMin = 0.01;
        Double rangeMax = 100.0;
        Double rates = rangeMin + (rangeMax - rangeMin) * random.nextDouble();

        // -> Depending on the range of numbers retrieved,
        // A different rarity array list will be selected

        if (rates < 2.5) {                                    // 2.5%, 3 star
//            Log.e("PULL", "THREE STAR");
            selectedRarityArrayList = threeStarArrayList;
        } else if (rates < 21) {                              // 18.5%, 2 star
//            Log.e("PULL", "TWO STAR");
            selectedRarityArrayList = twoStarArrayList;
        } else {                                              // 79%, 1 star
//            Log.e("PULL", "ONE STAR");
            selectedRarityArrayList = oneStarArrayList;
        }

        // -> Once a rarity array list has been selected,
        // picks a random character from characters in that array
        // This means that all characters of a particular rarity group will have the same rates
        int selectedCharacterIndex = random.nextInt(selectedRarityArrayList.size() - 1);
        Character summonedCharacter = selectedRarityArrayList.get(selectedCharacterIndex);

//        Log.e("DOUBLE RATES", String.valueOf(rates));
//        Log.e("DOUBLE INDEX", String.valueOf(selectedCharacterIndex));


        // INSERT INTO SQLITE
        insertIntoLocalDb(summonedCharacter);

        return summonedCharacter;
    }

    /* ---------------------------- METHOD TO SUMMON ONE RANDOM CHARACTER AND RETURN IT - END ---------------------------- */

    /* ---------------------------- METHOD TO INSERT SUMMONED CHARACTER INTO SQLITE DB - START -------------------------------------------------------- */

    public void insertIntoLocalDb(Character summonedCharacter) {
        retrievedLocalCharacters.clear();
        retrievedLocalCharacters = dbHelper.getAllCharacters();

        String enName = summonedCharacter.getEnFirstName();
        String jpName = summonedCharacter.getJpFirstName();
        int rarity = summonedCharacter.getShared_rarity();
        String icon = summonedCharacter.getShared_imageThumbnail();


        // Check characters that have already been saved
        // against all local characters retrieved before with the count at 0
        for (LocalCharacter chara : allLocalCharacters) {
            if (enName.equalsIgnoreCase(chara.getEnName())) {
                // if summoned character exists, set the saved character's count
                // to the previous array list with 0 count
                int newCount = chara.getCount() + 1;

                chara.setCount(newCount);

                // Afterwards, delete the old record from the db, and add the new one
                // with the updated count
                try {
                    for (LocalCharacter localCharacter : retrievedLocalCharacters) {
                        if (chara.getEnName().equalsIgnoreCase(localCharacter.getEnName())) {
                            dbHelper.deleteCharacter(localCharacter.get_id());
                        }
                    }
                    long inserted_id = dbHelper.insertCharacter(enName, jpName, rarity, icon, newCount);
                    dbHelper.close();

//                    if (inserted_id != -1) {
//                        Log.e("CHARACTER ADD TO SQLITE", "SUCCESS");
//                    } else {
//                        Log.e("CHARACTER ADD TO SQLITE", "FAILED");
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /* ---------------------------- METHOD TO INSERT SUMMONED CHARACTER INTO SQLITE DB - END ---------------------------- */

    /* ---------------------------- METHOD TO SUMMON MULTI RANDOM CHARACTER AND RETURN IT - START -------------------------------------------------------- */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<Character> summonMulti() {
        alMultiSummonedCharacters.clear();
        charaGridAdapter.notifyDataSetChanged();
        Character singleChara;

        for (int i = 0; i < 10; i++) {
            singleChara = summonOne();
            alMultiSummonedCharacters.add(singleChara);
        }


        charaGridAdapter.notifyDataSetChanged();
        return alMultiSummonedCharacters;
    }

    /* ---------------------------- METHOD TO SUMMON MULTI RANDOM CHARACTER AND RETURN IT - END ---------------------------- */

    /* ---------------------------- METHOD TO DISPLAY CHARA DETAILS ON SINGLE SUMMON - START -------------------------------------------------------- */

    private void summonLangSwap(Character chara) {
        final Handler handler = new Handler(Looper.getMainLooper());

        if (chara.getShared_rarity() == 1) {
            singleRarityBgLayout.setBackgroundResource(R.drawable.one_star_bg);
        } else if (chara.getShared_rarity() == 2) {
            singleRarityBgLayout.setBackgroundResource(R.drawable.two_star_bg);
        } else {
            singleRarityBgLayout.setBackgroundResource(R.drawable.three_star_bg);
        }

        rarityBar.setNumStars(chara.getShared_rarity());
        Glide.with(GachaScreenActivity.this).load(singleSummonedCharacter.getShared_imageThumbnail()).apply(RequestOptions.circleCropTransform()).into(ivSummonedChara);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                characterLayout.setVisibility(View.VISIBLE);

            }
        }, 400);


        if (switchLangOnOff) {
            charName.setText(chara.getJpFirstName());
        } else {
            charName.setText(chara.getEnFirstName());
        }
    }

    /* ---------------------------- METHOD TO DISPLAY CHARA DETAILS ON SINGLE SUMMON - END ---------------------------- */


    /* ---------------------------- METHOD TO SWITCH LANGUAGE FOR GENERAL FUNCTIONS - START -------------------------------------------------------- */

    public void languageSwap() {
        if (switchLangOnOff) {                    // Set to JP Lang
            txtGachaTitle.setText("ガチャ");

            txtSinglePull.setText("１回募集");
            txtMultiPull.setText("１０回募集");

            btnRates.setText("出現確率");
            tvRatesText.setText("出現確率");
        } else {                                  // Set to EN Lang
            txtGachaTitle.setText("Gacha");

            txtSinglePull.setText("Single Pull");
            txtMultiPull.setText("Multi Pull");

            btnRates.setText("Rates");
            tvRatesText.setText("Rates");

        }
    }

    /* ---------------------------- METHOD TO SWITCH LANGUAGE FOR GENERAL FUNCTIONS - END ---------------------------- */





}