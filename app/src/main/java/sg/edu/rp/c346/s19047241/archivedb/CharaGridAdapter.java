package sg.edu.rp.c346.s19047241.archivedb;

import android.content.Context;
import android.graphics.Color;
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

public class CharaGridAdapter extends BaseAdapter {
    private ArrayList<Character> characters;
    private Context context;
    private LayoutInflater inflater;
    private Boolean langBool;

    public CharaGridAdapter(Context context, ArrayList<Character> characters, Boolean langBool){
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
            gridView = inflater.inflate(R.layout.custom_character_grid_row, null);

        }

        Character currentChar = characters.get(position);

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

        String firstName;
        if(!langBool){
            firstName = currentChar.getEnFirstName();
        }else{
            firstName = currentChar.getJpFirstName();
        }

        txtName.setText(firstName);

        return gridView;
    }

    public Boolean getLangBool() {
        return langBool;
    }

    public void setLangBool(Boolean langBool) {
        this.langBool = langBool;
    }
}
