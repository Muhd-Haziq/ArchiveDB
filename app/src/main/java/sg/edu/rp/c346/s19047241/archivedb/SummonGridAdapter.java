package sg.edu.rp.c346.s19047241.archivedb;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class SummonGridAdapter extends BaseAdapter {

    private ArrayList<Character> characters;
    private Context context;
    private LayoutInflater inflater;
    private Boolean langBool;

    public SummonGridAdapter(Context context, ArrayList<Character> characters, Boolean langBool){
        this.context = context;
        this.characters = characters;
        this.langBool = langBool;
    }

    @Override
    public int getCount() {
        return characters.size();
    }

    @Override
    public Object getItem(int position) {
        return characters.get(position).getEn_name();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View gridView = convertView;

        if(convertView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.custom_character_grid_summon_row, null);

        }

        Character currentChar = characters.get(position);


        RelativeLayout characterLayout = gridView.findViewById(R.id.characterLayout);
        LinearLayout rarityBgLayout = gridView.findViewById(R.id.rarityBgLayout);
        ImageView imgViewIcon = gridView.findViewById(R.id.charThumbnail);
        TextView txtName = gridView.findViewById(R.id.charName);
        RatingBar rarityBar = gridView.findViewById(R.id.rarityBar);

        // Display character icon in a circular imageView
        Glide.with(gridView).load(currentChar.getShared_imageThumbnail()).apply(RequestOptions.circleCropTransform()).into(imgViewIcon);


        // Set rarity of each character in a rating bar
        rarityBar.setNumStars(currentChar.getShared_rarity());

        // Check if langBool is either true or false
        // langBool represents language switcher state
        // If false, set to English name. If true, set to JP name
        txtName.setTextColor(Color.WHITE);

        characterLayout.setVisibility(View.GONE);



        if(currentChar.getShared_rarity() == 1){
            rarityBgLayout.setBackgroundResource(R.drawable.one_star_bg);
        }else if(currentChar.getShared_rarity() == 2){
            rarityBgLayout.setBackgroundResource(R.drawable.two_star_bg);
        }else{
            rarityBgLayout.setBackgroundResource(R.drawable.three_star_bg);
        }

        setDelayPerCharacterDisplay(characterLayout, position, 400);

        String firstName;
        if(!langBool){
            firstName = currentChar.getEnFirstName();
        }else{
            firstName = currentChar.getJpFirstName();
        }

        txtName.setText(firstName);

        return gridView;
    }


    /* --------------------------------------------- SET A DELAY IN DISPLAYING THE SUMMONED CHARACTER ICON --------------------------------------------- */
    // Each character will be displayed one by one

    private void setDelayPerCharacterDisplay(RelativeLayout characterLayout, int id, int timer){
        Handler handler = new Handler(Looper.getMainLooper());

        if(id == 0){
            timer *= 1;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }else if(id == 1){
            timer *= 2;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }else if(id == 2){
            timer *= 3;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }else if(id == 3){
            timer *= 4;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }else if(id == 4){
            timer *= 5;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }else if(id == 5){
            timer *= 6;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }else if(id == 6){
            timer *= 7;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }else if(id == 7){
            timer *= 8;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }else if(id == 8){
            timer *= 9;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }else if(id == 9){
            timer *= 10;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    characterLayout.setVisibility(View.VISIBLE);

                }
            }, timer);
        }


    }


    public Boolean getLangBool() {
        return langBool;
    }

    public void setLangBool(Boolean langBool) {
        this.langBool = langBool;
    }



}
