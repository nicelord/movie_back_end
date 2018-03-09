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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 *
 * @author asus
 */

@Entity
public class Tag {

    @Id
    @GeneratedValue
    private Long tagId;
    String tagName;
    @ManyToMany(mappedBy = "tags")
    private List<Movie2> movie2s;  

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public List<Movie2> getMovie2s() {
        return movie2s;
    }

    public void setMovie2s(List<Movie2> movie2s) {
        this.movie2s = movie2s;
    }
    
    

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Tag(String tagName) {
        this.tagName = tagName;
    }
    
    
}
