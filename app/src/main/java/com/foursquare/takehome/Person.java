package com.foursquare.takehome;

public final class Person {
    private int id;
    private String name;
    private long arriveTime;
    private long leaveTime;

    //Create Constructor for Empty visitor
    public Person(String name , long arrival , long departure){
        this.name = name;
        this.arriveTime = arrival;
        this.leaveTime = departure;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getArriveTime() {
        return arriveTime;
    }

    public long getLeaveTime() {
        return leaveTime;
    }
}
