package com.example.habitapp;

public class TypeItem {

    private String typeName;
    private int typeIcon;

    public TypeItem(String typeName, int typeIcon) {
        this.typeName = typeName;
        this.typeIcon = typeIcon;
    }

    public String getTypeName() { return typeName; }

    public int getTypeIcon() { return typeIcon; }
}
