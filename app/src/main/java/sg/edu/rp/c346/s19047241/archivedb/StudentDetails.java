package sg.edu.rp.c346.s19047241.archivedb;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentDetails extends AppCompatActivity {

    private TextView txtName, txtSchool, txtClub, txtRole, txtDescription, txtAttack, txtArmor, txtAttackTitle, txtArmorTitle;
    private TextView txtExSkill, txtNormalSkill, txtPassiveSkill, txtSubSkill;
    private TextView txtSchoolTitle, txtClubTitle, txtDescriptionTitle, txtRoleTitle;
    private TextView txtExName, txtExCost, txtNormName, txtPassiveName, txtSubName;
    private TextView txtNormalTitle, txtPassiveTitle, txtSubTitle;
    private ImageView fullBodyImageView, envIconCity, envIconOutdoor, envIconIndoor, expandedCharaImage;
    private ImageView ivCharaType;

    private DrawerLayout drawerLayout;

    private CardView cardAttack, cardArmor;
    private Button btnSkillView;
    private LinearLayout expandSkillView, parentView;

    private LinearLayout fullView;

    private SwitchCompat switchLang;

    private RatingBar rarityBar;

    private TextView txtHobbyTitle, txtHobby;
    private TextView txtAgeTitle, txtAge;

    private TextView txtPositionTitle, txtPosition;
    private TextView txtWeaponTitle, txtWeapon;


    // Set up expanded image
    private Animator currentAnimator;
    private int shortAnimationDuration;
    private String imageUri;

    // Set up sharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCHLANG = "switchLang";

    private boolean switchLangOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        initViews();

        // Retrieve intent from Character Grid View
        // to check for which character was selected
        Intent intent = getIntent();
        String studentName = intent.getStringExtra("name");

        // Set up database reference
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("students");

        // Checks for language switcher state in Character Grid View,
        // and sets it to the same state
        switchLangOnOff = intent.getBooleanExtra("language", false);
        loadLangSwitch();
        updateLangSwitch();

        // Set some font color to white to ensure visibility
        txtAttackTitle.setTextColor(Color.WHITE);
        txtArmorTitle.setTextColor(Color.WHITE);
        txtAttack.setTextColor(Color.WHITE);
        txtArmor.setTextColor(Color.WHITE);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snap : snapshot.getChildren()){
                    Character chara = snap.getValue(Character.class);

                    if(chara.getEn_name().equals(studentName)){
                        if(!switchLangOnOff){
                            setEnValues(chara);
                        }else{
                            setJpValues(chara);
                        }

                        setSharedValues(chara);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set up image enlarger
        final View fullBodyView = findViewById(R.id.fullBody);
        fullBodyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(fullBodyView, imageUri);

                // Set transparent background
                fullView.setBackgroundColor(Color.parseColor("#80000000"));

            }
        });

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        // Set button to expand character skills when clicked
        btnSkillView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(expandSkillView.getVisibility()==View.GONE){
                    TransitionManager.beginDelayedTransition(parentView, new AutoTransition());
                    expandSkillView.setVisibility(View.VISIBLE);
                }else{
                    TransitionManager.beginDelayedTransition(parentView, new AutoTransition());
                    expandSkillView.setVisibility(View.GONE);
                }

                languageSwap();
            }
        });

        switchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean checked = switchLang.isChecked();

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot snap : snapshot.getChildren()){
                            Character chara = snap.getValue(Character.class);

                            if(chara.getEn_name().equals(studentName)){
                                if(!checked){
                                    switchLangOnOff = false;

                                    setEnValues(chara);
                                }else{
                                    switchLangOnOff = true;

                                    setJpValues(chara);


                                }
                                languageSwap();
                                setSharedValues(chara);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                saveLangSwitch();

            }
        });


    }


    /* ----------------- CODE FOR ENLARGING CHARACTER IMAGE UPON CLICK - START ------------------- */

    private void zoomImageFromThumb(final View thumbView, String imageUri) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expandedImage);
        //expandedImageView.setImageResource(imageResId);
        Glide.with(StudentDetails.this).load(imageUri).into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.parentView)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                set.start();
                currentAnimator = set;
                fullView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        /* ----------------- CODE FOR ENLARGING CHARACTER IMAGE UPON CLICK - END ------------------------------ */

    }


    /* ----------------- INITALIZING EACH ITEM - START ------------------- */

    public void initViews(){
        // Language switcher
        switchLang = findViewById(R.id.switchLang);

        // View for zoomed-in image
        fullView = findViewById(R.id.fullView);

        envIconCity = findViewById(R.id.envIcon1_comp);
        envIconOutdoor = findViewById(R.id.envIcon2_comp);
        envIconIndoor = findViewById(R.id.envIcon3_comp);
        fullBodyImageView = findViewById(R.id.fullBody);

        // expandedCharaImage is a PhotoView, imported from an external library
        // to allow zooming in of images with tap gestures
        expandedCharaImage = findViewById(R.id.expandedImage);

        txtName = findViewById(R.id.studentName);
        txtSchool = findViewById(R.id.txtSchool);
        txtClub = findViewById(R.id.txtClub);
        txtRole = findViewById(R.id.txtRole);
        txtDescription = findViewById(R.id.txtDesc);

        txtAttackTitle = findViewById(R.id.txtAttackTitle);
        txtAttack = findViewById(R.id.txtAttack);
        txtArmorTitle = findViewById(R.id.txtArmorTitle);
        txtArmor = findViewById(R.id.txtArmor);

        drawerLayout = findViewById(R.id.drawerLayout);

        cardArmor = findViewById(R.id.cardArmor);
        cardAttack = findViewById(R.id.cardAttack);

        txtExSkill = findViewById(R.id.txtExSkill);
        txtNormalSkill = findViewById(R.id.txtNormalSkill);
        txtPassiveSkill = findViewById(R.id.txtPassiveSkill);
        txtSubSkill = findViewById(R.id.txtSubSkill);

        txtExName = findViewById(R.id.txtExName);
        txtExCost = findViewById(R.id.txtExCost);
        txtNormName = findViewById(R.id.txtNormName);
        txtPassiveName = findViewById(R.id.txtPassiveName);
        txtSubName = findViewById(R.id.txtSubName);

        txtNormalTitle = findViewById(R.id.txtNormalTitle);
        txtPassiveTitle = findViewById(R.id.txtPassiveTitle);
        txtSubTitle = findViewById(R.id.txtSubTitle);

        btnSkillView = findViewById(R.id.btnSkillView);
        expandSkillView = findViewById(R.id.expandSkillView);
        parentView = findViewById(R.id.parentView);

        txtSchoolTitle = findViewById(R.id.txtSchoolTitle);
        txtClubTitle = findViewById(R.id.textTitleClub);
        txtDescriptionTitle = findViewById(R.id.txtDescriptionTitle);
        txtRoleTitle = findViewById(R.id.txtRoleTitle);

        rarityBar = findViewById(R.id.rarityBar);
        ivCharaType = findViewById(R.id.ivCharaType);


        txtHobby = findViewById(R.id.txtHobby);
        txtHobbyTitle = findViewById(R.id.txtHobbyTitle);

        txtAge = findViewById(R.id.txtAge);
        txtAgeTitle = findViewById(R.id.txtAgeTitle);

        txtPosition = findViewById(R.id.txtPosition);
        txtPositionTitle = findViewById(R.id.txtPositionTitle);

        txtWeapon = findViewById(R.id.txtWeapon);
        txtWeaponTitle = findViewById(R.id.txtWeaponTitle);
    }

    /* ----------------- INITIALIZING EACH ITEM - END -------------------------------------- */



    /* ------------------------------------ METHODS LANGUAGE SHARED PREF - START ------------------------------------ */

    public void saveLangSwitch(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCHLANG, switchLang.isChecked());

        editor.apply();

        //Toast.makeText(this, "Data saved.", Toast.LENGTH_SHORT).show();
    }

    public void loadLangSwitch(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switchLangOnOff = sharedPreferences.getBoolean(SWITCHLANG, false);
    }

    public void updateLangSwitch(){
        switchLang.setChecked(switchLangOnOff);

    }

    /* ------------------------------------ METHODS LANGUAGE SHARED PREF - END ------------------------------------ */




    /* --------- NAV DRAWER, REDIRECTING TO DIFFERENT PAGES - START --------- */

    public void ClickMenu(View view){
        // Open drawer
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        // Close drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickStudents(View view){
        // Redirect Activity to Students
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickGacha(View view){
        // Redirect Activity to Gacha
        redirectActivity(StudentDetails.this, GachaScreenActivity.class);
    }

    public void ClickInventory(View view){
        // Redirect Activity to Inventory
        redirectActivity(StudentDetails.this, InventoryActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Close drawer
        MainActivity.closeDrawer(drawerLayout);
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


    // -> Ensure that when back button is pressed,
    // passes over new data of language switcher state
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        // -> super.onBackPressed() returns a RESULT_CANCELLED,
        // which is not what is needed
        Intent intent = new Intent();
        intent.putExtra("lang", switchLangOnOff);

        setResult(RESULT_OK, intent);

        finish();
    }


    /* ****************************************************** LANGUAGE SWITCHER ****************************************************** */


    /* ---------------------- METHOD FOR UPDATING EN CHARACTER VALUES - START ---------------------- */

    public void setEnValues(Character chara) {
        txtName.setText(chara.getEn_name());
        txtSchool.setText(chara.getEn_school());
        txtClub.setText(chara.getEn_club());
        txtRole.setText(chara.getEn_role());
        txtDescription.setText(chara.getEn_description());
        txtAttack.setText(chara.getEn_attackType());
        txtArmor.setText(chara.getEn_armorType());

        txtHobby.setText(chara.getEn_hobby());

        /* DISPLAY EX SKILL NAME & DETAILS ---------------------- */
        String exFullDetail = chara.getEn_skillEx();
        String[] exSplitDetails = exFullDetail.split("//");

        // Display ex skill name
        String exName = exSplitDetails[0];
        txtExName.setText(exName);

        // Display ex skill cost
        String exCost = exSplitDetails[1];
        txtExCost.setText("Cost: " + exCost);

        // Display ex skill details
        String exDesc = exSplitDetails[2];
        txtExSkill.setText((String) exDesc);

        /* DISPLAY NORMAL SKILL NAME & DETAILS ---------------------- */

        String normFullDetail = chara.getEn_skillNorm();
        String[] normSplitDetails = normFullDetail.split("//");

        // Display norm skill name
        String normName = normSplitDetails[0];
        txtNormName.setText(normName);

        // Display normal skill details
        String normDesc = normSplitDetails[1];
        txtNormalSkill.setText(normDesc);

        /* DISPLAY PASSIVE SKILL NAME & DETAILS ---------------------- */
        String passiveFullDetail = chara.getEn_skillPassive();
        String[] passiveSplitDetails = passiveFullDetail.split("//");

        // Display passive skill name
        String passiveName = passiveSplitDetails[0];
        txtPassiveName.setText(passiveName);

        // Display passive skill details
        String passiveDesc = passiveSplitDetails[1];
        txtPassiveSkill.setText(passiveDesc);

        /* DISPLAY SUB SKILL NAME & DETAILS ---------------------- */
        String subFullDetail = chara.getEn_skillSub();
        String[] subSplitDetails = subFullDetail.split("//");

        // Display sub skill name
        String subName = subSplitDetails[0];
        txtSubName.setText(subName);

        // Display sub skill details
        String subDesc = subSplitDetails[1];
        txtSubSkill.setText(subDesc);

        languageSwap();
    }

    /* ---------------------- METHOD FOR UPDATING EN CHARACTER VALUES - END ------------------------------------------------------- */



    /* ---------------------- METHOD FOR UPDATING JP CHARACTER VALUES - START ---------------------- */

    public void setJpValues(Character chara) {
        txtName.setText(chara.getJp_name());
        txtSchool.setText(chara.getJp_school());
        txtClub.setText(chara.getJp_club());
        txtRole.setText(chara.getJp_role());
        txtDescription.setText(chara.getJp_description());
        txtAttack.setText(chara.getJp_attackType());
        txtArmor.setText(chara.getJp_armorType());

        txtHobby.setText(chara.getJp_hobby());


        /* DISPLAY EX SKILL NAME & DETAILS ---------------------- */
        String exFullDetail = chara.getJp_skillEx();
        String[] exSplitDetails = exFullDetail.split("//");

        // Display ex skill name
        String exName = exSplitDetails[0];
        txtExName.setText(exName);

        // Display ex skill cost
        String exCost = exSplitDetails[1];
        txtExCost.setText("コスト: " + exCost);

        // Display ex skill details
        String exDesc = exSplitDetails[2];
        txtExSkill.setText((String) exDesc);

        /* DISPLAY NORMAL SKILL NAME & DETAILS ---------------------- */

        String normFullDetail = chara.getJp_skillNorm();
        String[] normSplitDetails = normFullDetail.split("//");

        // Display norm skill name
        String normName = normSplitDetails[0];
        txtNormName.setText(normName);

        // Display normal skill details
        String normDesc = normSplitDetails[1];
        txtNormalSkill.setText(normDesc);

        /* DISPLAY PASSIVE SKILL NAME & DETAILS ---------------------- */
        String passiveFullDetail = chara.getJp_skillPassive();
        String[] passiveSplitDetails = passiveFullDetail.split("//");

        // Display passive skill name
        String passiveName = passiveSplitDetails[0];
        txtPassiveName.setText(passiveName);

        // Display passive skill details
        String passiveDesc = passiveSplitDetails[1];
        txtPassiveSkill.setText(passiveDesc);

        /* DISPLAY SUB SKILL NAME & DETAILS ---------------------- */
        String subFullDetail = chara.getJp_skillSub();
        String[] subSplitDetails = subFullDetail.split("//");

        // Display sub skill name
        String subName = subSplitDetails[0];
        txtSubName.setText(subName);

        // Display sub skill details
        String subDesc = subSplitDetails[1];
        txtSubSkill.setText(subDesc);

        languageSwap();
    }

    /* ---------------------- METHOD FOR UPDATING JP CHARACTER VALUES - END ------------------------------------------------------- */

    /* ---------------------- METHOD FOR SETTING SHARED VALUES - START ---------------------- */

    public void setSharedValues(Character chara){
        if(chara.getShared_type().equalsIgnoreCase("striker")){
            ivCharaType.setImageResource(R.drawable.sort_striker_on);
        }else{
            ivCharaType.setImageResource(R.drawable.sort_special_on);
        }


        // -> Some characters have their age hidden,
        // so I have to find which one that is and display it
        // based on the current language
        String age = chara.getShared_age();
        String[] hiddenAge = age.split("//");

        if(hiddenAge.length > 1 && switchLangOnOff){
            txtAge.setText(hiddenAge[1]);
        }else{
            txtAge.setText(hiddenAge[0]);


        }

        txtPosition.setText(chara.getShared_position());


        txtWeapon.setText(chara.getShared_weapon());


        rarityBar.setNumStars(chara.getShared_rarity());

        imageUri = chara.getShared_imageFull();
        Glide.with(StudentDetails.this).load(imageUri).into(fullBodyImageView);

        // Setting image for city compatibility
        if(chara.getShared_typeCity().equals("GOOD")){
            envIconCity.setImageResource(R.drawable.env_good);
        }else if(chara.getShared_typeCity().equals("MID")){
            envIconCity.setImageResource(R.drawable.env_mid);
        }else{
            envIconCity.setImageResource(R.drawable.env_bad);
        }

        // Setting image for indoor compatibility
        if(chara.getShared_typeIndoor().equals("GOOD")){
            envIconIndoor.setImageResource(R.drawable.env_good);
        }else if(chara.getShared_typeCity().equals("MID")){
            envIconIndoor.setImageResource(R.drawable.env_mid);
        }else{
            envIconIndoor.setImageResource(R.drawable.env_bad);
        }

        // Setting image for outdoor compatibility
        if(chara.getShared_typeOutdoor().equals("GOOD")){
            envIconOutdoor.setImageResource(R.drawable.env_good);
        }else if(chara.getShared_typeCity().equals("MID")){
            envIconOutdoor.setImageResource(R.drawable.env_mid);
        }else{
            envIconOutdoor.setImageResource(R.drawable.env_bad);
        }


        // Attack/Armor type colors
        // Explosive/Light = Red
        // Penetration/Heavy = Orange
        // Mystic/Special = Blue
        int red = Color.parseColor("#930008");
        int orange = Color.parseColor("#bf8901");
        int blue = Color.parseColor("#226f9c");

        // Set armor card color based
        if(chara.getEn_attackType().equals("Explosive")){
            cardAttack.setCardBackgroundColor(red);
        }else if(chara.getEn_attackType().equals("Penetration")){
            cardAttack.setCardBackgroundColor(orange);
        }else{
            cardAttack.setCardBackgroundColor(blue);
        }

        // Set armor card color based
        if(chara.getEn_armorType().equals("Light")){
            cardArmor.setCardBackgroundColor(red);
        }else if(chara.getEn_armorType().equals("Heavy")){
            cardArmor.setCardBackgroundColor(orange);
        }else{
            cardArmor.setCardBackgroundColor(blue);
        }
    }

    /* ---------------------- METHOD FOR SETTING SHARED VALUES - END ------------------------------------------------------- */




    /* ------------- METHOD FOR SWAPPING LANGUAGES ON FOR STATIC VALUES - START ------------------------------------------------------------ */
    @SuppressLint("SetTextI18n")
    public void languageSwap(){
        if(switchLangOnOff){
            txtAttackTitle.setText("攻撃タイプ");
            txtArmorTitle.setText("防御タイプ");

            txtSchoolTitle.setText("学園");
            txtClubTitle.setText("部活");
            txtDescriptionTitle.setText("フレーバーテキスト");
            txtRoleTitle.setText("クラス");

            txtNormalTitle.setText("ノーマル");
            txtPassiveTitle.setText("パシッブ");
            txtSubTitle.setText("サブ");

            txtPositionTitle.setText("ポジション");
            txtAgeTitle.setText("年齢");
            txtHobbyTitle.setText("趣味");
            txtWeaponTitle.setText("武器");

            if(expandSkillView.getVisibility() == View.VISIBLE){
                btnSkillView.setText("スキルを隠す");
            }else{
                btnSkillView.setText("スキルを示す");
            }

        }else{
            txtAttackTitle.setText("Attack");
            txtArmorTitle.setText("Armor");

            txtSchoolTitle.setText("School");
            txtClubTitle.setText("Club");
            txtDescriptionTitle.setText("Description");
            txtRoleTitle.setText("Role");

            txtNormalTitle.setText("Normal");
            txtPassiveTitle.setText("Passive");
            txtSubTitle.setText("Sub");

            txtPositionTitle.setText("Position");
            txtAgeTitle.setText("Age");
            txtHobbyTitle.setText("Hobby");
            txtWeaponTitle.setText("Weapon");

            if(expandSkillView.getVisibility() == View.VISIBLE){
                btnSkillView.setText("Hide Skills");
            }else{
                btnSkillView.setText("Show Skills");
            }

        }
    }

    /* ------------- METHOD FOR SWAPPING LANGUAGES ON FOR STATIC VALUES - END ----------------------------------- */

}