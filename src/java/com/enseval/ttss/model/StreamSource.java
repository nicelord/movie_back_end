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
public class StreamSource {

    @Id
    @GeneratedValue
    private Long streamSourceId;
    
    String serverName;
    String type;
    @OneToMany(mappedBy = "serverSource")
    private List<StreamLink> streamLinks;

    public Long getStreamSourceId() {
        return streamSourceId;
    }

    public void setStreamSourceId(Long streamSourceId) {
        this.streamSourceId = streamSourceId;
    }

    public List<StreamLink> getStreamLinks() {
        return streamLinks;
    }

    public void setStreamLinks(List<StreamLink> streamLinks) {
        this.streamLinks = streamLinks;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StreamSource(String serverName, String type) {
        this.serverName = serverName;
        this.type = type;
    }
    
    
    
}
