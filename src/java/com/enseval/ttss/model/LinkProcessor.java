/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.model;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author asus
 */
@Entity
public class LinkProcessor {

    @Id
    @GeneratedValue
    private Long id;
    
    String url;
    Long movieId;
    
    boolean downloaded = false;
    
    
    @ManyToOne
    Quality quality;
   
    @ManyToOne
    Resolution resolution;

    @OneToOne
    StreamangoTask streamangoTask;
    
    @OneToOne
    OpenloadTask openloadTask;
    
    @OneToOne
    GphotosTask gphotosTask;
    
    
    String currentState = "never";
    @Temporal(TemporalType.TIMESTAMP)
    Timestamp lastCheck;
    @Temporal(TemporalType.TIMESTAMP)
    Timestamp addedDate;
    
    String scrapedUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }


    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public Timestamp getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(Timestamp lastCheck) {
        this.lastCheck = lastCheck;
    }

    public Timestamp getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Timestamp addedDate) {
        this.addedDate = addedDate;
    }

    public StreamangoTask getStreamangoTask() {
        return streamangoTask;
    }

    public void setStreamangoTask(StreamangoTask streamangoTask) {
        this.streamangoTask = streamangoTask;
    }

    public OpenloadTask getOpenloadTask() {
        return openloadTask;
    }

    public void setOpenloadTask(OpenloadTask openloadTask) {
        this.openloadTask = openloadTask;
    }

    public GphotosTask getGphotosTask() {
        return gphotosTask;
    }

    public void setGphotosTask(GphotosTask gphotosTask) {
        this.gphotosTask = gphotosTask;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
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

    public String getScrapedUrl() {
        return scrapedUrl;
    }

    public void setScrapedUrl(String scrapedUrl) {
        this.scrapedUrl = scrapedUrl;
    }
    
    
}
