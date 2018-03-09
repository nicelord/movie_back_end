/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.vm;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.model.LinkProcessor;
import com.enseval.ttss.model.Movie2;
import com.enseval.ttss.model.Quality;
import com.enseval.ttss.model.Resolution;
import com.enseval.ttss.model.StreamLink;
import com.enseval.ttss.model.StreamSource;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import static org.zkoss.zk.ui.select.Selectors.wireComponents;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

/**
 *
 * @author asus
 */
public class AddToMovieVM {

    @Wire("#addToMovie")
    private Window winAddMovie;
    @Wire("#cmbSource")
    private Combobox cmbSource;
    @Wire("#cmbQuality")
    private Combobox cmbQuality;
    @Wire("#cmbResolution")
    private Combobox cmbResolution;

    LinkProcessor linkProcessor;
    String task;
    List<Movie2> listMovie = new ArrayList<>();
    Movie2 selectedMovie;
    String filterTitle = "";
    String linkResult = "";
    List<Quality> listQuality = new ArrayList<>();
    List<Resolution> listResolution = new ArrayList<>();
    List<StreamSource> listSource = new ArrayList<>();
    boolean isIframe;
    
    

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view,
            @ExecutionArgParam("lp") LinkProcessor lp,
            @ExecutionArgParam("task") String task) {

        this.linkProcessor = lp;
        
        this.task = task;
        this.listMovie = Ebean.find(Movie2.class).order("movieId desc").findList();
        if(lp.getMovieId()!=null){
            this.selectedMovie = Ebean.find(Movie2.class, this.linkProcessor.getMovieId());
        }
        
        this.listQuality = Ebean.find(Quality.class).orderBy("qualityId desc").findList();
        this.listResolution = Ebean.find(Resolution.class).orderBy("resolutionId desc").findList();
        this.listSource = Ebean.find(StreamSource.class).orderBy("streamSourceId desc").findList();
        
        if (this.task.equals("streamango")) {
            this.linkResult = this.linkProcessor.getStreamangoTask().getTaskResult();
            this.isIframe = true;
        } else if (this.task.equals("openload")) {
            this.linkResult = this.linkProcessor.getOpenloadTask().getTaskResult();
            this.isIframe = true;
        } else if (this.task.equals("gphotos")) {
            this.linkResult = this.linkProcessor.getGphotosTask().getTaskResult();
            this.isIframe = false;
        }
        
        

        Selectors.wireComponents(view, (Object) this, false);

    }

    @Command
    @NotifyChange({"listMovie"})
    public void filterTitle() {
        this.listMovie = Ebean.find(Movie2.class).where().like("title", "%" + this.filterTitle + "%").orderBy("movieId desc").findList();
    }
    
    @Command
    public void saveToMovie(){
        StreamLink streamLink = new StreamLink(this.cmbSource.getSelectedItem().getValue(), 
                this.cmbQuality.getSelectedItem().getValue(), 
                this.cmbResolution.getSelectedItem().getValue(), 
                this.linkResult, this.isIframe);
        streamLink.setScrapedUrl(linkProcessor.getScrapedUrl());
        System.out.println("-------------->"+linkProcessor.getScrapedUrl());
        Ebean.save(streamLink);
        this.selectedMovie.getStreamLinks().add(streamLink);
        Ebean.save(selectedMovie);
        this.winAddMovie.detach();
    }

    public Window getWinAddMovie() {
        return winAddMovie;
    }

    public void setWinAddMovie(Window winAddMovie) {
        this.winAddMovie = winAddMovie;
    }

    public LinkProcessor getLinkProcessor() {
        return linkProcessor;
    }

    public void setLinkProcessor(LinkProcessor linkProcessor) {
        this.linkProcessor = linkProcessor;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public List<Movie2> getListMovie() {
        return listMovie;
    }

    public void setListMovie(List<Movie2> listMovie) {
        this.listMovie = listMovie;
    }

    public Movie2 getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(Movie2 selectedMovie) {
        this.selectedMovie = selectedMovie;
    }

    public String getFilterTitle() {
        return filterTitle;
    }

    public void setFilterTitle(String filterTitle) {
        this.filterTitle = filterTitle;
    }

    public String getLinkResult() {
        return linkResult;
    }

    public void setLinkResult(String linkResult) {
        this.linkResult = linkResult;
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

    public List<StreamSource> getListSource() {
        return listSource;
    }

    public void setListSource(List<StreamSource> listSource) {
        this.listSource = listSource;
    }

    public Combobox getCmbSource() {
        return cmbSource;
    }

    public void setCmbSource(Combobox cmbSource) {
        this.cmbSource = cmbSource;
    }

    public Combobox getCmbQuality() {
        return cmbQuality;
    }

    public void setCmbQuality(Combobox cmbQuality) {
        this.cmbQuality = cmbQuality;
    }

    public Combobox getCmbResolution() {
        return cmbResolution;
    }

    public void setCmbResolution(Combobox cmbResolution) {
        this.cmbResolution = cmbResolution;
    }

    public boolean isIsIframe() {
        return isIframe;
    }

    public void setIsIframe(boolean isIframe) {
        this.isIframe = isIframe;
    }

}
