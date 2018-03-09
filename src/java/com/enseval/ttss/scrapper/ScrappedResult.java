/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.scrapper;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.model.Quality;
import com.enseval.ttss.model.Resolution;
import com.enseval.ttss.util.ScrapeUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asus
 */
public class ScrappedResult {
    String url;
    Quality quality;
    Resolution Resolution;
    boolean iframe;
    String server;
    String scrapedLink;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.setQuality(Ebean.find(Quality.class).where().eq("quality", ScrapeUtil.checkQualityByLink(url)).findUnique());
        this.setResolution(Ebean.find(Resolution.class).where().eq("resolution", ScrapeUtil.checkResolutionyByLink(url)).findUnique());
        try {
            URL aURL = new URL(url);
            this.setServer(aURL.getHost());
        } catch (MalformedURLException ex) {
        }
        
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public Resolution getResolution() {
        return Resolution;
    }

    public void setResolution(Resolution Resolution) {
        this.Resolution = Resolution;
    }

  

    public boolean isIframe() {
        return iframe;
    }

    public void setIframe(boolean iframe) {
        this.iframe = iframe;
    }

 

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }


    public String getScrapedLink() {
        return scrapedLink;
    }

    public void setScrapedLink(String scrapedLink) {
        this.scrapedLink = scrapedLink;
    }
    
    
    
}
