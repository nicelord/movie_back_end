/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author asus
 */
@Entity
public class Resolution {

    @Id
    @GeneratedValue
    private Long resolutionId;
    String resolution;
    @OneToMany(mappedBy = "resolution")
    private List<StreamLink> streamLinks;
    @OneToMany(mappedBy = "resolution")
    private List<LinkProcessor> linkProcessors;

    public Long getResolutionId() {
        return resolutionId;
    }

    public void setResolutionId(Long resolutionId) {
        this.resolutionId = resolutionId;
    }

    public List<StreamLink> getStreamLinks() {
        return streamLinks;
    }

    public void setStreamLinks(List<StreamLink> streamLinks) {
        this.streamLinks = streamLinks;
    }
    
    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Resolution(String resolution) {
        this.resolution = resolution;
    }
    
    
}
