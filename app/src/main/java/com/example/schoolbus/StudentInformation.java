package com.example.schoolbus;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class StudentInformation {
    private String Name;
    private String Age;
    private String Gender;
    private String Stage;
    private String Latitude;
    private String Longitude;
    private String ImageUrl;
    private String key;

    public StudentInformation() {
        // empty constructor needed
    }

    public StudentInformation(String name, String age, String gender, String stage, String latitude, String longitude, String imageUrl) {
        Name = name;
        Age = age;
        Gender = gender;
        Stage = stage;
        Latitude = latitude;
        Longitude = longitude;
        ImageUrl = imageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getStage() {
        return Stage;
    }

    public void setStage(String stage) {
        Stage = stage;
    }


    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
