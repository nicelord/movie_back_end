/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.vm;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.model.Actor;
import com.enseval.ttss.model.Country;
import com.enseval.ttss.model.Genre;
import com.enseval.ttss.model.LinkProcessor;
import com.enseval.ttss.model.Movie2;
import com.enseval.ttss.model.Quality;
import com.enseval.ttss.model.Resolution;
import com.enseval.ttss.model.StreamSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;

/**
 *
 * @author asus
 */
public class LinkProcessorVM {

    List<LinkProcessor> links = new ArrayList<>();
    List<LinkProcessor> selectedLinks = new ArrayList<>();
    List<String> listResult = new ArrayList<>();
    List<String> selectedResult = new ArrayList<>();
    LinkProcessor selectedLink = new LinkProcessor();

    List<Quality> listQuality = new ArrayList<>();
    List<Resolution> listResolution = new ArrayList<>();

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view) {
        this.links = Ebean.find(LinkProcessor.class).findList();
        this.listQuality = Ebean.find(Quality.class).orderBy("qualityId desc").findList();
        this.listResolution = Ebean.find(Resolution.class).orderBy("resolutionId desc").findList();

        Selectors.wireComponents(view, (Object) this, false);

    }

    @Command
    @NotifyChange({"*"})
    public void deleteSelectedLinks() {
        for (LinkProcessor lp : selectedLinks) {
            Ebean.delete(lp);
            if (lp.getGphotosTask() != null) {
                Ebean.delete(lp.getGphotosTask());
            }
            if (lp.getStreamangoTask() != null) {
                Ebean.delete(lp.getStreamangoTask());
            }
            if (lp.getOpenloadTask() != null) {
                Ebean.delete(lp.getOpenloadTask());
            }

        }
        this.links = Ebean.find(LinkProcessor.class).findList();
    }

    @Command
    @NotifyChange({"*"})
    public void saveLinkData(@BindingParam("lp") LinkProcessor lp) {
        Ebean.save(lp);
    }

    @Command
    @NotifyChange({"*"})
    public void deleteLink(@BindingParam("lp") LinkProcessor lp) {
        Ebean.delete(lp);
        if (lp.getGphotosTask() != null) {
            Ebean.delete(lp.getGphotosTask());
        }
        if (lp.getStreamangoTask() != null) {
            Ebean.delete(lp.getStreamangoTask());
        }
        if (lp.getOpenloadTask() != null) {
            Ebean.delete(lp.getOpenloadTask());
        }

        refresh();
    }

    @Command
    public void addToMovie(@BindingParam("lp") LinkProcessor lp, @BindingParam("task") String task) {
        Map m = new HashMap();
        m.put("lp", lp);
        m.put("task", task);
        Executions.createComponents("addToMovie.zul", null, m);

    }

    @Command
    public void addNewLink() {
        Executions.createComponents("addNewLinkProcessor.zul", null, null);

    }

    @GlobalCommand
    @NotifyChange({"*"})
    public void refresh() {
        this.links = Ebean.find(LinkProcessor.class).findList();
    }

    public List<LinkProcessor> getLinks() {
        return links;
    }

    public void setLinks(List<LinkProcessor> links) {
        this.links = links;
    }

    public List<LinkProcessor> getSelectedLinks() {
        return selectedLinks;
    }

    public void setSelectedLinks(List<LinkProcessor> selectedLinks) {
        this.selectedLinks = selectedLinks;
    }

    public List<String> getListResult() {
        return listResult;
    }

    public void setListResult(List<String> listResult) {
        this.listResult = listResult;
    }

    public List<String> getSelectedResult() {
        return selectedResult;
    }

    public void setSelectedResult(List<String> selectedResult) {
        this.selectedResult = selectedResult;
    }

    public LinkProcessor getSelectedLink() {
        return selectedLink;
    }

    public void setSelectedLink(LinkProcessor selectedLink) {
        this.selectedLink = selectedLink;
    }

    public List<Quality> getListQuality() {
        return listQuality;
    }

    public void setListQuality(List<Quality> listQuality) {
        this.listQuality = listQuality;
    }

    public List<Resolution> getListResolution() {
        return listResolution;
    }

    public void setListResolution(List<Resolution> listResolution) {
        this.listResolution = listResolution;
    }

}
