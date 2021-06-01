package sg.edu.rp.c346.s19047241.archivedb;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class CharaListAdapter extends BaseAdapter {
    private ArrayList<Character> characters;
    private Context context;
    private LayoutInflater inflater;
    private Boolean langBool;

    public CharaListAdapter(Context context, ArrayList<Character> characters, Boolean langBool){
        this.context = context;
        this.characters = characters;
        this.langBool = langBool;
    }


    public Boolean getLangBool() {
        return langBool;
    }

    public void setLangBool(Boolean langBool) {
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

        View listView = convertView;

        if(convertView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listView = inflater.inflate(R.layout.custom_character_list_row, null);

        }

        Character currentChar = characters.get(position);

        ImageView imgViewIcon = listView.findViewById(R.id.charThumbnailList);
        TextView txtName = listView.findViewById(R.id.charNameList);
        RatingBar rarityBar = listView.findViewById(R.id.rarityBar);


        // Display character icon in a circular imageView
        Glide.with(listView).load(currentChar.getShared_imageThumbnail()).apply(RequestOptions.circleCropTransform()).into(imgViewIcon);

        // Set rarity of each character in a rating bar
        rarityBar.setNumStars(currentChar.getShared_rarity());

        // Display character name by their first name by splitting their full name

        // Check if langBool is either true or false
        // langBool represents language switcher state
        // If false, set to English name. If true, set to JP name
        txtName.setTextColor(Color.WHITE);

        String name;
        if(!langBool){
            String[] fullName = currentChar.getEn_name().split(" ");
            String firstName = fullName[1];
            name = firstName;
        }else{
            String jpFirstName = currentChar.getJpFirstName();
            name= jpFirstName;
        }

        txtName.setText(name);


        return listView;
    }


}
