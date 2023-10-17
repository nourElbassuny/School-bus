package com.example.schoolbus;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ParentInformation {
    private String Email, FirstName, LastName, Password;

    public ParentInformation() {
    }

    public ParentInformation(String email, String firstName, String lastName, String password) {
        Email = email;
        FirstName = firstName;
        LastName = lastName;
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}