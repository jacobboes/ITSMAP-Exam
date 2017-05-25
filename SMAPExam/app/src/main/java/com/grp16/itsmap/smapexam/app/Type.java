package com.grp16.itsmap.smapexam.app;

import android.widget.CheckBox;

public class Type {
    private final CheckBox checkBox;
    private final String type;

    public Type(CheckBox checkBox, String type) {
        this.checkBox = checkBox;
        this.type = type;
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public boolean hasType(String type) {
        return this.type.equals(type);
    }

    public void setChecked(boolean value) {
        checkBox.setChecked(value);
    }

    public String getValue() {
        return type;
    }
}
