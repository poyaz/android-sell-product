package com.example.woods.amin.Database;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table "SETTINGS".
 */
@Entity
public class Settings {
    private String key;
    private String value;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    @Generated
    public Settings() {
    }

    @Generated
    public Settings(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
