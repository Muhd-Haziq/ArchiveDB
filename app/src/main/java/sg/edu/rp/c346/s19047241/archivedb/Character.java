package sg.edu.rp.c346.s19047241.archivedb;

public class Character {

    private String en_name;
    private String en_school;
    private String en_club;
    private String en_description;
    private String en_hobby;
    private String en_role;
    private String en_attackType;
    private String en_armorType;
    private String en_skillEx;
    private String en_skillNorm;
    private String en_skillPassive;
    private String en_skillSub;

    private String jp_name;
    private String jp_school;
    private String jp_club;
    private String jp_description;
    private String jp_hobby;
    private String jp_role;
    private String jp_attackType;
    private String jp_armorType;
    private String jp_skillEx;
    private String jp_skillNorm;
    private String jp_skillPassive;
    private String jp_skillSub;

    private int shared_rarity;
    private String shared_age;
    private String shared_type;
    private String shared_position;
    private String shared_typeCity;
    private String shared_typeOutdoor;
    private String shared_typeIndoor;
    private String shared_weapon;
    private String shared_imageThumbnail;
    private String shared_imageFull;


    public Character(){

    }

    public Character(String en_name, String en_school, String en_club, String en_description, String en_hobby, String en_role, String en_attackType, String en_armorType, String en_skillEx, String en_skillNorm, String en_skillPassive, String en_skillSub, String jp_name, String jp_school, String jp_club, String jp_description, String jp_hobby, String jp_role, String jp_attackType, String jp_armorType, String jp_skillEx, String jp_skillNorm, String jp_skillPassive, String jp_skillSub, int shared_rarity, String shared_age, String shared_type, String shared_position, String shared_typeCity, String shared_typeOutdoor, String shared_typeIndoor, String shared_weapon, String shared_imageThumbnail, String shared_imageFull) {
        this.en_name = en_name;
        this.en_school = en_school;
        this.en_club = en_club;
        this.en_description = en_description;
        this.en_hobby = en_hobby;
        this.en_role = en_role;
        this.en_attackType = en_attackType;
        this.en_armorType = en_armorType;
        this.en_skillEx = en_skillEx;
        this.en_skillNorm = en_skillNorm;
        this.en_skillPassive = en_skillPassive;
        this.en_skillSub = en_skillSub;
        this.jp_name = jp_name;
        this.jp_school = jp_school;
        this.jp_club = jp_club;
        this.jp_description = jp_description;
        this.jp_hobby = jp_hobby;
        this.jp_role = jp_role;
        this.jp_attackType = jp_attackType;
        this.jp_armorType = jp_armorType;
        this.jp_skillEx = jp_skillEx;
        this.jp_skillNorm = jp_skillNorm;
        this.jp_skillPassive = jp_skillPassive;
        this.jp_skillSub = jp_skillSub;
        this.shared_rarity = shared_rarity;
        this.shared_age = shared_age;
        this.shared_type = shared_type;
        this.shared_position = shared_position;
        this.shared_typeCity = shared_typeCity;
        this.shared_typeOutdoor = shared_typeOutdoor;
        this.shared_typeIndoor = shared_typeIndoor;
        this.shared_weapon = shared_weapon;
        this.shared_imageThumbnail = shared_imageThumbnail;
        this.shared_imageFull = shared_imageFull;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getEn_school() {
        return en_school;
    }

    public void setEn_school(String en_school) {
        this.en_school = en_school;
    }

    public String getEn_club() {
        return en_club;
    }

    public void setEn_club(String en_club) {
        this.en_club = en_club;
    }

    public String getEn_description() {
        return en_description;
    }

    public void setEn_description(String en_description) {
        this.en_description = en_description;
    }

    public String getEn_hobby() {
        return en_hobby;
    }

    public void setEn_hobby(String en_hobby) {
        this.en_hobby = en_hobby;
    }

    public String getEn_role() {
        return en_role;
    }

    public void setEn_role(String en_role) {
        this.en_role = en_role;
    }

    public String getEn_attackType() {
        return en_attackType;
    }

    public void setEn_attackType(String en_attackType) {
        this.en_attackType = en_attackType;
    }

    public String getEn_armorType() {
        return en_armorType;
    }

    public void setEn_armorType(String en_armorType) {
        this.en_armorType = en_armorType;
    }

    public String getEn_skillEx() {
        return en_skillEx;
    }

    public void setEn_skillEx(String en_skillEx) {
        this.en_skillEx = en_skillEx;
    }

    public String getEn_skillNorm() {
        return en_skillNorm;
    }

    public void setEn_skillNorm(String en_skillNorm) {
        this.en_skillNorm = en_skillNorm;
    }

    public String getEn_skillPassive() {
        return en_skillPassive;
    }

    public void setEn_skillPassive(String en_skillPassive) {
        this.en_skillPassive = en_skillPassive;
    }

    public String getEn_skillSub() {
        return en_skillSub;
    }

    public void setEn_skillSub(String en_skillSub) {
        this.en_skillSub = en_skillSub;
    }

    public String getJp_name() {
        return jp_name;
    }

    public void setJp_name(String jp_name) {
        this.jp_name = jp_name;
    }

    public String getJp_school() {
        return jp_school;
    }

    public void setJp_school(String jp_school) {
        this.jp_school = jp_school;
    }

    public String getJp_club() {
        return jp_club;
    }

    public void setJp_club(String jp_club) {
        this.jp_club = jp_club;
    }

    public String getJp_description() {
        return jp_description;
    }

    public void setJp_description(String jp_description) {
        this.jp_description = jp_description;
    }

    public String getJp_hobby() {
        return jp_hobby;
    }

    public void setJp_hobby(String jp_hobby) {
        this.jp_hobby = jp_hobby;
    }

    public String getJp_role() {
        return jp_role;
    }

    public void setJp_role(String jp_role) {
        this.jp_role = jp_role;
    }

    public String getJp_attackType() {
        return jp_attackType;
    }

    public void setJp_attackType(String jp_attackType) {
        this.jp_attackType = jp_attackType;
    }

    public String getJp_armorType() {
        return jp_armorType;
    }

    public void setJp_armorType(String jp_armorType) {
        this.jp_armorType = jp_armorType;
    }

    public String getJp_skillEx() {
        return jp_skillEx;
    }

    public void setJp_skillEx(String jp_skillEx) {
        this.jp_skillEx = jp_skillEx;
    }

    public String getJp_skillNorm() {
        return jp_skillNorm;
    }

    public void setJp_skillNorm(String jp_skillNorm) {
        this.jp_skillNorm = jp_skillNorm;
    }

    public String getJp_skillPassive() {
        return jp_skillPassive;
    }

    public void setJp_skillPassive(String jp_skillPassive) {
        this.jp_skillPassive = jp_skillPassive;
    }

    public String getJp_skillSub() {
        return jp_skillSub;
    }

    public void setJp_skillSub(String jp_skillSub) {
        this.jp_skillSub = jp_skillSub;
    }

    public int getShared_rarity() {
        return shared_rarity;
    }

    public void setShared_rarity(int shared_rarity) {
        this.shared_rarity = shared_rarity;
    }

    public String getShared_age() {
        return shared_age;
    }

    public void setShared_age(String shared_age) {
        this.shared_age = shared_age;
    }

    public String getShared_type() {
        return shared_type;
    }

    public void setShared_type(String shared_type) {
        this.shared_type = shared_type;
    }

    public String getShared_position() {
        return shared_position;
    }

    public void setShared_position(String shared_position) {
        this.shared_position = shared_position;
    }

    public String getShared_typeCity() {
        return shared_typeCity;
    }

    public void setShared_typeCity(String shared_typeCity) {
        this.shared_typeCity = shared_typeCity;
    }

    public String getShared_typeOutdoor() {
        return shared_typeOutdoor;
    }

    public void setShared_typeOutdoor(String shared_typeOutdoor) {
        this.shared_typeOutdoor = shared_typeOutdoor;
    }

    public String getShared_typeIndoor() {
        return shared_typeIndoor;
    }

    public void setShared_typeIndoor(String shared_typeIndoor) {
        this.shared_typeIndoor = shared_typeIndoor;
    }

    public String getShared_weapon() {
        return shared_weapon;
    }

    public void setShared_weapon(String shared_weapon) {
        this.shared_weapon = shared_weapon;
    }

    public String getShared_imageThumbnail() {
        return shared_imageThumbnail;
    }

    public void setShared_imageThumbnail(String shared_imageThumbnail) {
        this.shared_imageThumbnail = shared_imageThumbnail;
    }

    public String getShared_imageFull() {
        return shared_imageFull;
    }

    public void setShared_imageFull(String shared_imageFull) {
        this.shared_imageFull = shared_imageFull;
    }

    public String getEnFirstName(){

        // Display character name by their first name by splitting their full name

        String[] fullName = getEn_name().split(" ");
        String charaName = fullName[1];

        return charaName;
    }

    public String getJpFirstName(){
        String jpFullName = getJp_name();

        // -> The firstnames of the characters are always in katakana
        // Since database only has fullname in JP, which includes kanji + katakana,
        // there is a need to detect any katakana that is in that fullname
        // and append to a new string called jpFirstName

        StringBuilder jpFirstName = new StringBuilder();
        for(int i = 0; i < jpFullName.length(); i++){
            // -> Converting each character in the fullname to a String
            String letter = String.valueOf(jpFullName.charAt(i));

            // -> Matching each letter with the katakana unicode regex
            // If true, meaning it detects a katakana character, append to jpFirstName

            // [\\p{Katakana}] -> older method

            if(letter.matches("[ァ-ヾ]*+")){
                jpFirstName.append(letter);
            }

        }

        return jpFirstName.toString();
    }


}
