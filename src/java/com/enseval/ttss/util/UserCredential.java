package com.enseval.ttss.util;

import java.io.*;
import com.enseval.ttss.model.*;

public class UserCredential implements Serializable
{
    private static final long serialVersionUID = 1L;
    String account;
    String name;
    User user;
    String roles;
    
    public UserCredential(String account, String name, User user) {
        this.roles = "anon";
        this.name = name;
        this.account = account;
        this.user = user;
    }
    
    public UserCredential() {
        this.roles = "anon";
        this.account = "anonymous";
        this.roles = "anon";
    }
    
    public boolean isAnonymous() {
        return this.getRoles().equals("anon") || "anonymous".equals(this.account);
    }
    
    public String getAccount() {
        return this.account;
    }
    
    public void setAccount(final String account) {
        this.account = account;
    }
    
    public String getRoles() {
        return this.roles;
    }
    
    public void setRoles(final String roles) {
        this.roles = roles;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public void setUser(final User user) {
        this.user = user;
    }
}
