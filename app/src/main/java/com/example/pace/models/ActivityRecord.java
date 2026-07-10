package com.example.pace.models;

import java.io.Serializable;
import java.util.List;

public class ActivityRecord implements Serializable {
    private String id;
    private String title;
    private String dateTime;
    private String location;
    private double distance; // km
    private String duration; // HH:mm:ss or mm:ss
    private String avgPace;  // mm:ss
    private int calories;
    private int steps;
    private int elevationGain;
    private int maxElevation;

    // Chart data
    private float[] paceData;
    private float[] cadenceData;
    private float[] elevationData;
    private String[] xLabels;

    // Splits
    private List<Split> splits;

    public ActivityRecord(String id, String title, String dateTime, String location, double distance, 
                          String duration, String avgPace, int calories, int steps, 
                          int elevationGain, int maxElevation) {
        this.id = id;
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.distance = distance;
        this.duration = duration;
        this.avgPace = avgPace;
        this.calories = calories;
        this.steps = steps;
        this.elevationGain = elevationGain;
        this.maxElevation = maxElevation;
    }

    // Inner class for Splits
    public static class Split implements Serializable {
        public int km;
        public String pace;
        public String elev;
        public int progress; // for bar chart

        public Split(int km, String pace, String elev, int progress) {
            this.km = km;
            this.pace = pace;
            this.elev = elev;
            this.progress = progress;
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDateTime() { return dateTime; }
    public String getLocation() { return location; }
    public double getDistance() { return distance; }
    public String getDuration() { return duration; }
    public String getAvgPace() { return avgPace; }
    public int getCalories() { return calories; }
    public int getSteps() { return steps; }
    public int getElevationGain() { return elevationGain; }
    public int getMaxElevation() { return maxElevation; }
    public float[] getPaceData() { return paceData; }
    public void setPaceData(float[] paceData) { this.paceData = paceData; }
    public float[] getCadenceData() { return cadenceData; }
    public void setCadenceData(float[] cadenceData) { this.cadenceData = cadenceData; }
    public float[] getElevationData() { return elevationData; }
    public void setElevationData(float[] elevationData) { this.elevationData = elevationData; }
    public String[] getXLabels() { return xLabels; }
    public void setXLabels(String[] xLabels) { this.xLabels = xLabels; }
    public List<Split> getSplits() { return splits; }
    public void setSplits(List<Split> splits) { this.splits = splits; }
}
