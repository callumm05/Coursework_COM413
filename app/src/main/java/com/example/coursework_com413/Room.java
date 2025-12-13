package com.example.coursework_com413;

public class Room {

    private String id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private int desks;
    private int pcs;

    public Room() {}

    public Room(String id, String name, String description, double latitude, double longitude, int desks, int pcs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.desks = desks;
        this.pcs = pcs;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getDesks() { return desks; }
    public int getPcs() { return pcs; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setDesks(int desks) { this.desks = desks; }
    public void setPcs(int pcs) { this.pcs = pcs; }
}
