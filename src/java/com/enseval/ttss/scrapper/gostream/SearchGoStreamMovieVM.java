/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.scrapper.gostream;

import com.enseval.ttss.externalApi.TMDBApi;
import com.enseval.ttss.scrapper.ScrapperVM;
import com.enseval.ttss.scrapper.ScrapperVM;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

/**
 *
 * @author asus
 */
public class SearchGoStreamMovieVM {

    @Wire("#searchGoStreamMovie")
    Window searchGoStreamMovie;

    List<String> moviePageLink = new ArrayList<>();
    List<String> listSelectedMoviePage = new ArrayList<>();
    String selectedMoviePage;
    String keywords;
    ScrapperVM scrapperVM;
    String episode = "";
    boolean fixedEpisodeTitle = false;

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view,
            @ExecutionArgParam("parent") ScrapperVM scrapperVM) {
        this.scrapperVM = scrapperVM;
        Selectors.wireComponents(view, this, false);

    }

    @Command
    @NotifyChange({"moviePageLink"})
    public void search() {
        GoStreamScrapper gs = new GoStreamScrapper();
        this.moviePageLink = gs.search(this.keywords);
    }

    @Command
    public void addSelectedItemToScrapper(@BindingParam("url") String url) {
        StringBuilder sb = new StringBuilder();
        GoStreamScrapper gs = new GoStreamScrapper();
        for (String s : gs.getStreamPage(url, this.episode,this.fixedEpisodeTitle)) {
            sb.append(s).append("\n");
        }
        this.scrapperVM.setUrl(sb.toString());
        BindUtils.postGlobalCommand(null, null, "refresh", null);
        this.searchGoStreamMovie.detach();

    }

    public List<String> getMoviePageLink() {
        return moviePageLink;
    }

    public void setMoviePageLink(List<String> moviePageLink) {
        this.moviePageLink = moviePageLink;
    }

    public List<String> getListSelectedMoviePage() {
        return listSelectedMoviePage;
    }

    public void setListSelectedMoviePage(List<String> listSelectedMoviePage) {
        this.listSelectedMoviePage = listSelectedMoviePage;
    }

    public String getSelectedMoviePage() {
        return selectedMoviePage;
    }

    public void setSelectedMoviePage(String selectedMoviePage) {
        this.selectedMoviePage = selectedMoviePage;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Window getSearchGoStreamMovie() {
        return searchGoStreamMovie;
    }

    public void setSearchGoStreamMovie(Window searchGoStreamMovie) {
        this.searchGoStreamMovie = searchGoStreamMovie;
    }

    public ScrapperVM getScrapperVM() {
        return scrapperVM;
    }

    public void setScrapperVM(ScrapperVM scrapperVM) {
        this.scrapperVM = scrapperVM;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public boolean isFixedEpisodeTitle() {
        return fixedEpisodeTitle;
    }

    public void setFixedEpisodeTitle(boolean fixedEpisodeTitle) {
        this.fixedEpisodeTitle = fixedEpisodeTitle;
    }

}
