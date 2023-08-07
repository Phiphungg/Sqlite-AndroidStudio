package com.pipo.sqlite;

import android.media.Image;

public class Contact {
    int id;
    String Name;
    String Phone;
    String Email;
    String image;

    public Contact() {
        super();
    }
    public  Contact(int id, String name, String phone, String email, String image){
        this.id =id;
        Name = name;
        Phone = phone;
        Email = email;
        this.image = image;
    }
    public Contact(String name, String phone, String email, String image){
        Name = name;
        Phone = phone;
        Email = email;
        this.image = image;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return Name;
    }

    public void setName(String name){
        Name = name;
    }

    public String getPhone(){
        return Phone;
    }

    public void setPhone(String phone){
        Phone = phone;
    }

    public String getEmail(){
        return Email;
    }

    public void setEmail(String email){
        Email = email;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

}
