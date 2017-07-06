package com.hit.pretstreet.pretstreet.navigation.models;

/**
 * Created by User on 6/27/2017.
 */

public class NavDrawerItem {
    public String id;
    public String name;

    public NavDrawerItem(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
