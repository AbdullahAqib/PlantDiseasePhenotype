package com.example.plantdiseasephenotype;

import java.io.Serializable;

public class User implements Serializable {

    public String name, email, phone;

    public User(){}

    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
