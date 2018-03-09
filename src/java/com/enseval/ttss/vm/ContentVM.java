package com.enseval.ttss.vm;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.model.User;
import com.enseval.ttss.util.AuthenticationServiceImpl;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.*;
import org.zkoss.bind.annotation.*;

public class ContentVM
{
    String thePage;
    
    public ContentVM() {
//        if(new AuthenticationServiceImpl().getUserCredential().getRoles().equals("OPERATOR")){
//            this.thePage = "/menuInputCashOpname.zul";
//        }else if(new AuthenticationServiceImpl().getUserCredential().getRoles().equals("KASIR_KECIL")){
//            this.thePage = "/menuKasKecil.zul";
//        }else{
//            this.thePage = "/menuSetoran.zul";
//        }
        
        this.thePage = "/movies.zul";
        
    }
    
    @AfterCompose
    @NotifyChange({ "thePage" })
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view) {
        
        Selectors.wireComponents(view, (Object)this, false);
        
    }
    
    @GlobalCommand
    @NotifyChange({ "thePage" })
    public void doChangePage(@BindingParam("page") final String page) {
        this.setThePage(page);
    }
    
    public String getThePage() {
        return this.thePage;
    }
    
    public void setThePage(final String thePage) {
        this.thePage = thePage;
    }
}
