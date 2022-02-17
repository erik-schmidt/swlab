package de.hhn.aib.swlab.wise1920.group05.exercise1;


public class Alarm {

    private int id;
    private String name;
    private int hour;
    private int minute;
    private int active;     // 0 = false/deaktiviert 1 = true/aktiv

    public Alarm(int id, String name, int hour, int minute, int active) {
        this.id = id;
        this.name = name;
        this.hour = hour;
        this.minute = minute;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getActive() {
        return active;
    }

    private String getTimeString(int time){
        if (time <= 9 && time >= 0){
            return "0"+time;
        }
        return ""+time;
    }

    public String getTime(){
        return getTimeString(getHour()) + ":" + getTimeString(getMinute());
    }
}
