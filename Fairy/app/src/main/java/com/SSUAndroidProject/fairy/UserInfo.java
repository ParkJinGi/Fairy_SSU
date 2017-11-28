package com.SSUAndroidProject.fairy;

/**
 * Created by 박PC on 2017-11-08.
 */

public class UserInfo {
    private String Email;
    private String Name;
    private String Password;
    private String Profile;

    public String getEmail() {
        return Email;
    }

    public UserInfo(String email, String name,String password) {
        Email = email;
        Name = name;
        Password=password;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public UserInfo() {
    }
}
