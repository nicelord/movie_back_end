package com.enseval.ttss.util;

import org.zkoss.zk.ui.util.*;
import java.util.*;
import org.zkoss.zk.ui.*;

public class AuthenticationInit implements Initiator {

    AuthenticationService authService;

    public AuthenticationInit() {
        this.authService = new AuthenticationServiceImpl();
    }

    @Override
    public void doInit(Page page, Map<String, Object> map) throws Exception {
        UserCredential cre = this.authService.getUserCredential();
        if (cre == null || cre.isAnonymous()) {
            if (!page.getRequestPath().equals("/info_giro.zul")) {
                Executions.sendRedirect("/login.zul");
            }
        }
    }
}
