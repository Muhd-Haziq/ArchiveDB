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

public class CharaListLocalAdapter extends BaseAdapter {
    private ArrayList<LocalCharacter> localCharacters;
    private Context context;
    private LayoutInflater inflater;
    private boolean langBool;

    public CharaListLocalAdapter(Context context, ArrayList<LocalCharacter> localCharacters, boolean langBool) {
        this.context = context;
        this.localCharacters = localCharacters;
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
        return localCharacters.size();
    }

    @Override
    public Object getItem(int position) {
        return localCharacters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return localCharacters.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;

        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listView = inflater.inflate(R.layout.custom_local_character_list_row, null);

        }

        LocalCharacter currentChar = localCharacters.get(position);

        ImageView imgViewIcon = listView.findViewById(R.id.ivLocalCharacterIcon);
        TextView txtName = listView.findViewById(R.id.tvLocalCharacterName);
        TextView txtCount = listView.findViewById(R.id.tvLocalCharacterCount);
        RatingBar rarityBar = listView.findViewById(R.id.localCharacterRarityBar);
        txtName.setTextColor(Color.WHITE);
        txtCount.setTextColor(Color.WHITE);


        // Display character icon in a circular imageView
        Glide.with(listView).load(currentChar.getIcon()).apply(RequestOptions.circleCropTransform()).into(imgViewIcon);

        // Set rarity of each character in a rating bar
        rarityBar.setNumStars(currentChar.getRarity());

        // Set counter for characters
        txtCount.setText(String.valueOf(currentChar.getCount()));


        // Check if langBool is either true or false
        // langBool represents language switcher state
        // If false, set to English name. If true, set to JP name

        String name = "";
        if (!langBool) {
            name = currentChar.getEnName();

        } else {
            name = currentChar.getJpName();

        }

        txtName.setText(name);


        return listView;
    }
}
