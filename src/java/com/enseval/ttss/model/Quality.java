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
import javax.persistence.Transient;

/**
 *
 * @author asus
 */
@Entity
public class Quality {

    @Id
    @GeneratedValue
    private Long qualityId;
    String quality;
    int rank;
    @OneToMany(mappedBy = "quality")
    private List<StreamLink> streamLinks;
    @OneToMany(mappedBy = "quality")
    private List<LinkProcessor> linkProcessors;


    public Long getQualityId() {
        return qualityId;
    }

    public void setQualityId(Long qualityId) {
        this.qualityId = qualityId;
    }

    public List<StreamLink> getStreamLinks() {
        return streamLinks;
    }

    public void setStreamLinks(List<StreamLink> streamLinks) {
        this.streamLinks = streamLinks;
    }


    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Quality(String quality) {
        this.quality = quality;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<LinkProcessor> getLinkProcessors() {
        return linkProcessors;
    }

    public void setLinkProcessors(List<LinkProcessor> linkProcessors) {
        this.linkProcessors = linkProcessors;
    }

    
}
