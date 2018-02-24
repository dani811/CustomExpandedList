package com.coffeebreakcodes.customexpandedlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kagan Kartal on 24.02.2018.
 */

public class Item {
    private String identifier;
    private String title;
    private List<Item> item = new ArrayList<>();
    private boolean isExpanded = false;             //Is this item expanded?
    private boolean isSelected = false;             //Is selected item?
    private int hierarchy = 0;                      //Used for deciding indent by rank of item

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Item> getItemList() {
        return item;
    }

    public void setItemList(List<Item> itemList) {
        this.item = itemList;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
