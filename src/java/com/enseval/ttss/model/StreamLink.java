/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author asus
 */
@Entity
public class StreamLink {

    @Id
    @GeneratedValue
    private Long streamLinkId;
    
    @ManyToOne
    StreamSource serverSource;
    
    @ManyToOne
    Quality quality;
    
    @Transient
    String newQuality;
    
    @Transient
    String newResolution;
    
    @Transient
    String newServerSource;
    
    @ManyToOne
    Resolution resolution;
    
    String link;
    boolean isIframe;
    
    String scrapedUrl;
    
    @ManyToMany(mappedBy = "streamLinks")
    private List<Movie2> movie2s;

    public Long getStreamLinkId() {
        return streamLinkId;
    }

    public void setStreamLinkId(Long streamLinkId) {
        this.streamLinkId = streamLinkId;
    }

    public List<Movie2> getMovie2s() {
        return movie2s;
    }

    public void setMovie2s(List<Movie2> movie2s) {
        this.movie2s = movie2s;
    }

    public StreamSource getServerSource() {
        return serverSource;
    }

    public void setServerSource(StreamSource serverSource) {
        this.serverSource = serverSource;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isIsIframe() {
        return isIframe;
    }

    public void setIsIframe(boolean isIframe) {
        this.isIframe = isIframe;
    }

   

    public String getNewQuality() {
        return newQuality;
    }

    public void setNewQuality(String newQuality) {
        this.newQuality = newQuality;
    }

    public String getNewResolution() {
        return newResolution;
    }

    public void setNewResolution(String newResolution) {
        this.newResolution = newResolution;
    }

    public String getNewServerSource() {
        return newServerSource;
    }

    public void setNewServerSource(String newServerSource) {
        this.newServerSource = newServerSource;
    }


    public StreamLink(StreamSource serverSource, Quality quality, Resolution resolution, String link, boolean isIframe) {
        this.serverSource = serverSource;
        this.quality = quality;
        this.resolution = resolution;
        this.link = link;
        this.isIframe = isIframe;
    }

    public String getScrapedUrl() {
        return scrapedUrl;
    }

    public void setScrapedUrl(String scrapedUrl) {
        this.scrapedUrl = scrapedUrl;
    }
    
    
}
