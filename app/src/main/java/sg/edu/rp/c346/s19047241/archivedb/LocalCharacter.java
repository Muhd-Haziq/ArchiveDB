package sg.edu.rp.c346.s19047241.archivedb;

import java.io.Serializable;

public class LocalCharacter implements Serializable {
    private int _id;
    private String enName;
    private String jpName;
    private int rarity;
    private String icon;
    private int count;

    public LocalCharacter(int _id, String enName, String jpName, int rarity, String icon, int count) {
        this._id = _id;
        this.enName = enName;
        this.jpName = jpName;
        this.rarity = rarity;
        this.icon = icon;
        this.count = count;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getJpName() {
        return jpName;
    }

    public void setJpName(String jpName) {
        this.jpName = jpName;
    }

    public int getRarity() {
        return rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
