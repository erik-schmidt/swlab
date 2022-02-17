package de.hhn.aib.swlab.wise1920.group05.exercise2.model;

public class OtherUser {

    private String name;
    private String description;
    private MyLocation currentLocation;

    public OtherUser(String name, String description, MyLocation location){
        this.name = name;
        this.description = description;
        this.currentLocation = location;
    }

    public OtherUser(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MyLocation getLocation() {
        return currentLocation;
    }

    public void setLocation(MyLocation location) {
        this.currentLocation = location;
    }
}
