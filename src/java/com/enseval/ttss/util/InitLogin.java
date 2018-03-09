package com.enseval.ttss.util;

import org.zkoss.zk.ui.util.*;
import java.util.*;
import org.zkoss.zk.ui.*;

public class InitLogin implements Initiator
{
    AuthenticationService authService;
    
    public InitLogin() {
        this.authService = new AuthenticationServiceImpl();
    }
    
    @Override
    public void doInit(Page page, Map<String, Object> map) throws Exception {
        final UserCredential cre = this.authService.getUserCredential();
        if (!cre.isAnonymous()) {
            Executions.sendRedirect("/home.zul");
        }
    }
}
