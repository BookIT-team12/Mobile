package com.example.bookit.model;

import com.example.bookit.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
public class User implements Serializable {
    private String name;
    private String lastName;
    @SerializedName("email")

    private String email;
    private String password;
    private String confirmPassword;
    private String address;
    private String phone;
    private Role role;
    private boolean isReported;


    public User(String name, String lastName, String email, String password, String confirmPassword, String address, String phone, Role role, boolean isReported, boolean isBlocked) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.role = role;
        this.isReported = isReported;
        this.isBlocked = isBlocked;
        this.confirmPassword = confirmPassword;
    }

    public User(){}

    public User(User user) {
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.address = user.getAddress();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.isReported = user.isReported();
        this.isBlocked = user.isBlocked();
        this.confirmPassword = user.confirmPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return email.equals(((User) o).getEmail());  // Assuming email is a unique identifier
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);  // Assuming email is a unique identifier
    }


    public boolean isReported() {
        return isReported;
    }

    public void setReported(boolean reported) {
        isReported = reported;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    private boolean isBlocked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

}