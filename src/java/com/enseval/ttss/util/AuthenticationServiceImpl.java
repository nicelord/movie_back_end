package com.enseval.ttss.util;

import java.io.*;
import org.zkoss.zk.ui.*;
import com.enseval.ttss.model.*;
import com.avaje.ebean.*;

public class AuthenticationServiceImpl implements AuthenticationService, Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public UserCredential getUserCredential() {
        Session sess = Sessions.getCurrent();
        UserCredential cre = (UserCredential)sess.getAttribute("userCredential");
        if (cre == null) {
            cre = new UserCredential();
            sess.setAttribute("userCredential", (Object)cre);
        }
        return cre;
    }
    
    @Override
    public boolean login(String username, String password) {
        User userlogin = (User)Ebean.find((Class)User.class).where("username = '" + username + "' and password = '" + password +"'").findUnique();
        if (userlogin == null) {
            return false;
        }
        Session sess = Sessions.getCurrent();
        UserCredential cre = new UserCredential(userlogin.getUsername(), userlogin.getName(), userlogin);
        cre.setRoles(userlogin.getUsername());
        sess.setAttribute("userCredential", cre);
        return true;
    }
    
    @Override
    public void logout() {
        Session sess = Sessions.getCurrent();
        sess.removeAttribute("userCredential");
    }
}
