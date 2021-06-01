package sg.edu.rp.c346.s19047241.archivedb;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InventoryActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Button btnDeleteAll;
    private ListView lvInventory;
    private TextView tvEmptyMessage;

    private ArrayList<LocalCharacter> localCharacterArrayList;
    private CharaListLocalAdapter charaListLocalAdapter;

    private SwitchCompat switchLang;
    private boolean switchLangOnOff;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SHARED_SWITCHLANG = "switchLang";

    // Database Helper
    private final DBHelper dbHelper = new DBHelper(this);


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        drawerLayout = findViewById(R.id.drawerLayout);
        switchLang = findViewById(R.id.switchLang);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);

        lvInventory = findViewById(R.id.lvInventory);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        loadSharedPrefs();
        updateLangSwitch();

        localCharacterArrayList = new ArrayList<LocalCharacter>();
        localCharacterArrayList.addAll(dbHelper.getAllCharacters());

        Collections.sort(localCharacterArrayList, Comparator.comparing(LocalCharacter::getRarity)
                .thenComparing(LocalCharacter::getEnName));

        charaListLocalAdapter = new CharaListLocalAdapter(this, localCharacterArrayList, switchLangOnOff);
        lvInventory.setAdapter(charaListLocalAdapter);

        setEmptyMessage();


        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!localCharacterArrayList.isEmpty()){
                    dbHelper.deleteAll();
                    localCharacterArrayList.clear();
                    charaListLocalAdapter.notifyDataSetChanged();
                }

                setEmptyMessage();

            }
        });


        /* --------------------- SET LANGUAGE ON TOGGLE SWITCH - START ------------------------------------------ */

        switchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean checked = switchLang.isChecked();

                switchLangOnOff = checked;
                charaListLocalAdapter.setLangBool(switchLangOnOff);

                charaListLocalAdapter.notifyDataSetChanged();

                languageSwap();
                saveSharedPrefs();
            }
        });

        /* --------------------- SET LANGUAGE ON TOGGLE SWITCH - END -------------------------- */
    }

    private void setEmptyMessage() {
        if(localCharacterArrayList.isEmpty()){
            tvEmptyMessage.setVisibility(View.VISIBLE);
            lvInventory.setVisibility(View.GONE);
        }else{
            tvEmptyMessage.setVisibility(View.GONE);
            lvInventory.setVisibility(View.VISIBLE);
        }

    }

    /* ------------------------------------ METHODS LANGUAGE SHARED PREF - START ------------------------------------ */

    public void saveSharedPrefs(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHARED_SWITCHLANG, switchLang.isChecked());

        editor.apply();

        //Toast.makeText(this, "Data saved.", Toast.LENGTH_SHORT).show();
    }

    public void loadSharedPrefs(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switchLangOnOff = sharedPreferences.getBoolean(SHARED_SWITCHLANG, false);
        languageSwap();

    }

    public void updateLangSwitch(){
        switchLang.setChecked(switchLangOnOff);

    }

    public void updateLangSwitchNoAnimation(){
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
        MainActivity.redirectActivity(this, GachaScreenActivity.class);
    }

    public void ClickInventory(View view) {
        // Redirect Activity to Inventory
        MainActivity.closeDrawer(drawerLayout);
    }


    /* ------------------ NAV DRAWER, REDIRECTING TO DIFFERENT PAGES - END ------------------ */

    @Override
    protected void onPause() {
        super.onPause();
        // Close drawer
        MainActivity.closeDrawer(drawerLayout);

    }

    /* ------------------------------------ METHODS FOR NAV DRAWER - END ------------------------------------ */

    public void languageSwap(){
        if (switchLangOnOff) {                    // Set to JP Lang
            btnDeleteAll.setText("全てを消す");
            tvEmptyMessage.setText("インベントリは何もない");

        } else {                                  // Set to EN Lang
            btnDeleteAll.setText("Delete All");
            tvEmptyMessage.setText("There is nothing in your inventory");
        }
    }

}