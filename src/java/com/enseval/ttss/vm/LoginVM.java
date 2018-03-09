package com.enseval.ttss.vm;

import com.enseval.ttss.util.*;
import org.zkoss.zk.ui.select.*;
import org.zkoss.zk.ui.*;
import org.zkoss.bind.annotation.*;

public class LoginVM
{
    AuthenticationService authService;
    String username = "admin";
    String password = "admin";
    String msg;
    String browser;
    
    public LoginVM() {
        this.authService = new AuthenticationServiceImpl();
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getMsg() {
        return this.msg;
    }
    
    public void setMsg(final String msg) {
        this.msg = msg;
    }
    
    public String getBrowser() {
        return this.browser;
    }
    
    public void setBrowser(final String browser) {
        this.browser = browser;
    }
    
    @AfterCompose
    @NotifyChange({ "browser" })
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view) {
        Selectors.wireComponents(view, (Object)this, false);
//        this.setBrowser(Executions.getCurrent().getBrowser());
//        if (!this.browser.equals("webkit")) {
//            Executions.sendRedirect("chromeDownload.zul");
//        }
    }
    
    @Command
    @NotifyChange({ "username", "password", "msg" })
    public void doLogin() {
        if (!this.authService.login(this.username, this.password)) {
            this.msg = "Username atau password salah.";
            return;
        }
        Executions.sendRedirect("/home.zul");
    }
}
