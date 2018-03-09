/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.scrapper;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.model.GphotosTask;
import com.enseval.ttss.model.LinkProcessor;
import com.enseval.ttss.model.OpenloadTask;
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
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

/**
 *
 * @author asus
 */
public class AddToLinkProcessorVM {

    @Wire("#addTask")
    private Window winAddTask;
    List<ScrappedResult> urls = new ArrayList<>();
    ScrapperVM scrapperVM;
    boolean streamango = false, openload = false, gphotos = false;
    List<String> taskList = new ArrayList<>();
    List<String> selectedTaskList = new ArrayList<>();

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view,
            @ExecutionArgParam("urls") List<ScrappedResult> urls, @ExecutionArgParam("scraper") ScrapperVM scrapper) {
        this.urls = urls;
        this.scrapperVM = scrapper;
        this.taskList.add("ADD TO MOVIE");
        this.taskList.add("ADD TO STREAMANGO");
        this.taskList.add("ADD TO OPENLOAD");
        this.taskList.add("ADD TO GPHOTOS");

        Selectors.wireComponents(view, this, false);
    }

    @Command
    public void saveLinkProcess() {
        for (ScrappedResult url : urls) {

            if (selectedTaskList.contains("ADD TO STREAMANGO") || selectedTaskList.contains("ADD TO OPENLOAD") || selectedTaskList.contains("ADD TO GPHOTOS")) {
                LinkProcessor lp = new LinkProcessor();
                lp.setUrl(url.getUrl());

                lp.setMovieId(this.scrapperVM.getMovie().getMovieId());

                lp.setQuality(url.getQuality());
                lp.setResolution(url.getResolution());

                if (selectedTaskList.contains("ADD TO STREAMANGO")) {
                    StreamangoTask st = new StreamangoTask(true);
                    Ebean.save(st);
                    lp.setStreamangoTask(st);
                }

                if (selectedTaskList.contains("ADD TO OPENLOAD")) {
                    OpenloadTask ol = new OpenloadTask(true);
                    Ebean.save(ol);
                    lp.setOpenloadTask(ol);
                }

                if (selectedTaskList.contains("ADD TO GPHOTOS")) {
                    GphotosTask gp = new GphotosTask(true);
                    Ebean.save(gp);
                    lp.setGphotosTask(gp);
                }

                lp.setCurrentState("never");
                lp.setAddedDate(new Timestamp(new Date().getTime()));
                lp.setScrapedUrl(url.getScrapedLink());
                Ebean.save(lp);
            }

            if (selectedTaskList.contains("ADD TO MOVIE")) {
                this.scrapperVM.addSelectedItemToMovie(url);
            }

            this.scrapperVM.getScrapedResult().remove(url);

        }

        BindUtils.postNotifyChange(null, null, this.scrapperVM, "scrapedResult");
        this.winAddTask.detach();

    }

    public List<ScrappedResult> getUrls() {
        return urls;
    }

    public void setUrls(List<ScrappedResult> urls) {
        this.urls = urls;
    }

    public ScrapperVM getScrapperVM() {
        return scrapperVM;
    }

    public void setScrapperVM(ScrapperVM scrapperVM) {
        this.scrapperVM = scrapperVM;
    }

    public boolean isStreamango() {
        return streamango;
    }

    public void setStreamango(boolean streamango) {
        this.streamango = streamango;
    }

    public boolean isOpenload() {
        return openload;
    }

    public void setOpenload(boolean openload) {
        this.openload = openload;
    }

    public boolean isGphotos() {
        return gphotos;
    }

    public void setGphotos(boolean gphotos) {
        this.gphotos = gphotos;
    }

    public List<String> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<String> taskList) {
        this.taskList = taskList;
    }

    public Window getWinAddTask() {
        return winAddTask;
    }

    public void setWinAddTask(Window winAddTask) {
        this.winAddTask = winAddTask;
    }

    public List<String> getSelectedTaskList() {
        return selectedTaskList;
    }

    public void setSelectedTaskList(List<String> selectedTaskList) {
        this.selectedTaskList = selectedTaskList;
    }

}
