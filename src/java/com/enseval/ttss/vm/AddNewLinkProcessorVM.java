/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.vm;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.model.GphotosTask;
import com.enseval.ttss.model.LinkProcessor;
import com.enseval.ttss.model.Movie2;
import com.enseval.ttss.model.OpenloadTask;
import com.enseval.ttss.model.Quality;
import com.enseval.ttss.model.Resolution;
import com.enseval.ttss.model.StreamSource;
import com.enseval.ttss.model.StreamangoTask;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

/**
 *
 * @author asus
 */
public class AddNewLinkProcessorVM {

    @Wire("#addToMovie")
    private Window winAddNewLink;
    @Wire("#cmbQuality")
    private Combobox cmbQuality;
    @Wire("#cmbResolution")
    private Combobox cmbResolution;

    LinkProcessor linkProcessor;
    String task;
    List<Movie2> listMovie = new ArrayList<>();
    Movie2 selectedMovie;
    String filterTitle = "";
    List<Quality> listQuality = new ArrayList<>();
    List<Resolution> listResolution = new ArrayList<>();
    List<StreamSource> listSource = new ArrayList<>();
    boolean openload = false, streamango = false, gphotos = false;

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view) {

        this.linkProcessor = new LinkProcessor();
        this.listMovie = Ebean.find(Movie2.class).order("movieId desc").findList();

        this.listQuality = Ebean.find(Quality.class).orderBy("qualityId desc").findList();
        this.listResolution = Ebean.find(Resolution.class).orderBy("resolutionId desc").findList();
        this.listSource = Ebean.find(StreamSource.class).orderBy("streamSourceId desc").findList();

        Selectors.wireComponents(view, (Object) this, false);

    }

    @Command
    @NotifyChange({"listMovie"})
    public void filterTitle() {
        this.listMovie = Ebean.find(Movie2.class).where().like("title", "%" + this.filterTitle + "%").orderBy("movieId desc").findList();
    }

    @Command
    public void saveNewLink() {
        this.linkProcessor.setMovieId(this.selectedMovie.getMovieId());
        this.linkProcessor.setAddedDate(new Timestamp(new Date().getTime()));
        this.linkProcessor.setQuality(this.cmbQuality.getSelectedItem().getValue());
        this.linkProcessor.setResolution(this.cmbResolution.getSelectedItem().getValue());
        if(this.streamango){
            StreamangoTask st = new StreamangoTask(true);
            Ebean.save(st);
            this.linkProcessor.setStreamangoTask(st);
        }
        if(this.openload){
            OpenloadTask ot = new OpenloadTask(true);
            Ebean.save(ot);
            this.linkProcessor.setOpenloadTask(ot);
        }
        if(this.gphotos){
            GphotosTask gt = new GphotosTask(true);
            Ebean.save(gt);
            this.linkProcessor.setGphotosTask(gt);
        }
        Ebean.save(this.linkProcessor);
        BindUtils.postGlobalCommand(null, null, "refresh", null);
        this.winAddNewLink.detach();
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

    public boolean isOpenload() {
        return openload;
    }

    public void setOpenload(boolean openload) {
        this.openload = openload;
    }

    public boolean isStreamango() {
        return streamango;
    }

    public void setStreamango(boolean streamango) {
        this.streamango = streamango;
    }

    public boolean isGphotos() {
        return gphotos;
    }

    public void setGphotos(boolean gphotos) {
        this.gphotos = gphotos;
    }

    public Window getWinAddNewLink() {
        return winAddNewLink;
    }

    public void setWinAddNewLink(Window winAddNewLink) {
        this.winAddNewLink = winAddNewLink;
    }

}
