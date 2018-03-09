/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.scrapper.gostream;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.externalApi.TMDBApi;
import com.enseval.ttss.model.Actor;
import com.enseval.ttss.model.Country;
import com.enseval.ttss.model.Genre;
import com.enseval.ttss.model.Movie2;
import com.enseval.ttss.scrapper.ScrapperVM;
import com.enseval.ttss.scrapper.ScrapperVM;
import com.enseval.ttss.vm.AddNewMovieVM;
import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.OptimisticLockException;
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
public class SearchMovieInfoGoStreamVM {

    @Wire("#searchMovieInfoGoStream")
    Window searchMovieInfoGoStream;

    List<String> moviePageLink = new ArrayList<>();
    List<String> listSelectedMoviePage = new ArrayList<>();
    String selectedMoviePage;
    String keywords;
    AddNewMovieVM addNewMovieVM;
    String episode = "";
    boolean fixedEpisodeTitle = false;
    boolean isFixMode = false;

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view,
            @ExecutionArgParam("parent") AddNewMovieVM addNewMovieVM) {
        this.addNewMovieVM = addNewMovieVM;
        Selectors.wireComponents(view, this, false);

    }

    @Command
    @NotifyChange({"moviePageLink"})
    public void search() {
        GoStreamScrapper gs = new GoStreamScrapper();
        this.moviePageLink = gs.search(this.keywords);
    }

    @Command
    public void addToNewMovie(@BindingParam("url") String url) {
        try {
            UserAgent userAgent = new UserAgent();
            userAgent.visit(url);
            Document d = userAgent.doc;

            if (isFixMode) {
                if (this.addNewMovieVM.getMovie().getTitle().isEmpty()) {
                    this.addNewMovieVM.getMovie().setTitle(d.findFirst("<div class=\"mvic-desc\">").findFirst("<h3>").getText());
                }
                if (this.addNewMovieVM.getMovie().getSynopsis().isEmpty()) {
                    this.addNewMovieVM.getMovie().setSynopsis(d.findFirst("<div class=\"mvic-desc\">").findFirst("<div class=\"desc\">").getText().trim());
                }
                if (this.addNewMovieVM.getMovie().getTrailer().isEmpty()) {
                    try {
                        this.addNewMovieVM.getMovie().setTrailer(d.innerHTML().split("https://www.youtube.com/embed/")[1].split("\"")[0]);
                    } catch (Exception e) {
                    }
                }
                if (this.addNewMovieVM.getMovie().getPosterLink().isEmpty()) {
                    this.addNewMovieVM.getMovie().setPosterLink(d.findFirst("<div class=\"thumb mvic-thumb\">").getAt("style").split("url=")[1].replace(");", ""));
                }
                if (this.addNewMovieVM.getMovie().getBigPosterLink().isEmpty()) {
                    this.addNewMovieVM.getMovie().setBigPosterLink(d.findFirst("<a class=\"thumb mvi-cover\">").getAt("style").split("url=")[1].replace(")", ""));

                }
                if (this.addNewMovieVM.getMovie().getImdbId().isEmpty()) {
                    try {
                        this.addNewMovieVM.getMovie().setRating(d.innerHTML().split("<p><strong>IMDb:</strong>")[1].split("</p>")[0].trim());
                    } catch (Exception e) {
                    }
                }
                if (this.addNewMovieVM.getMovie().getDuration().isEmpty()) {
                    try {
                        this.addNewMovieVM.getMovie().setDuration(d.innerHTML().split("<p><strong>Duration:</strong>")[1].split("min</p>")[0].trim());
                    } catch (Exception e) {
                    }
                }
                if (this.addNewMovieVM.getMovie().getReleaseYear() == 0) {
                    try {
                        this.addNewMovieVM.getMovie().setReleaseYear(Integer.parseInt(d.innerHTML().split("<p><strong>Release:</strong>")[1].split("</p>")[0].trim()));
                    } catch (Exception e) {
                    }
                }
                if (this.addNewMovieVM.getMovie().getCast().isEmpty()) {
                    try {
                        for (Element e : d.findFirst("<strong>Actor: </strong>").getParent().findEvery("<a href>")) {
                            Actor a = Ebean.find(Actor.class).where().eq("actorName", e.innerText()).findUnique();
                            if (a == null) {
                                a = new Actor(e.innerText());
                                Ebean.save(a);
                            }
                            this.addNewMovieVM.getMovie().getCast().add(a);
                        }
                    } catch (Exception e) {
                    }
                }

                if (this.addNewMovieVM.getMovie().getGenres().isEmpty()) {
                    try {
                        for (Element e : d.findFirst("<strong>Genre: </strong>").getParent().findEvery("<a href>")) {
                            Genre g = Ebean.find(Genre.class).where().eq("name", e.innerText()).findUnique();
                            if (g == null) {
                                g = new Genre(e.innerText());
                                Ebean.save(g);
                            }
                            this.addNewMovieVM.getMovie().getGenres().add(g);
                        }
                    } catch (Exception e) {
                    }
                }

                if (this.addNewMovieVM.getMovie().getCountries().isEmpty()) {
                    try {
                        for (Element e : d.findFirst("<strong>Country: </strong>").getParent().findEvery("<a href>")) {
                            Country c = Ebean.find(Country.class).where().eq("countryName", e.innerText()).findUnique();
                            if (c == null) {
                                c = new Country(e.innerText(), e.innerText());
                                Ebean.save(c);
                            }
                            this.addNewMovieVM.getMovie().getCountries().add(c);
                        }
                    } catch (Exception e) {
                    }
                }
            } else {
                this.addNewMovieVM.getMovie().setTitle(d.findFirst("<div class=\"mvic-desc\">").findFirst("<h3>").getText());
                this.addNewMovieVM.getMovie().setSynopsis(d.findFirst("<div class=\"mvic-desc\">").findFirst("<div class=\"desc\">").getText().trim());
                try {
                    this.addNewMovieVM.getMovie().setTrailer(d.innerHTML().split("https://www.youtube.com/embed/")[1].split("\"")[0]);
                } catch (Exception e) {
                }
                this.addNewMovieVM.getMovie().setPosterLink(d.findFirst("<div class=\"thumb mvic-thumb\">").getAt("style").split("url=")[1].replace(");", ""));
                this.addNewMovieVM.getMovie().setBigPosterLink(d.findFirst("<a class=\"thumb mvi-cover\">").getAt("style").split("url=")[1].replace(")", ""));
                try {
                    this.addNewMovieVM.getMovie().setRating(d.innerHTML().split("<p><strong>IMDb:</strong>")[1].split("</p>")[0].trim());
                } catch (Exception e) {
                }
                try {
                    this.addNewMovieVM.getMovie().setDuration(d.innerHTML().split("<p><strong>Duration:</strong>")[1].split("min</p>")[0].trim());
                } catch (Exception e) {
                }
                try {
                    this.addNewMovieVM.getMovie().setReleaseYear(Integer.parseInt(d.innerHTML().split("<p><strong>Release:</strong>")[1].split("</p>")[0].trim()));
                } catch (Exception e) {
                }
                try {
                    List<Actor> listActor = new ArrayList<>();
                    for (Element e : d.findFirst("<strong>Actor: </strong>").getParent().findEvery("<a href>")) {
                        Actor a = Ebean.find(Actor.class).where().eq("actorName", e.innerText()).findUnique();
                        if (a == null) {
                            a = new Actor(e.innerText());
                            Ebean.save(a);
                        }
                        listActor.add(a);
                    }
                    this.addNewMovieVM.getMovie().setCast(listActor);
                } catch (Exception e) {
                }
                try {
                    List<Genre> listGenre = new ArrayList<>();
                    for (Element e : d.findFirst("<strong>Genre: </strong>").getParent().findEvery("<a href>")) {
                        Genre g = Ebean.find(Genre.class).where().eq("name", e.innerText()).findUnique();
                        if (g == null) {
                            g = new Genre(e.innerText());
                            Ebean.save(g);
                        }
                        listGenre.add(g);
                    }
                    this.addNewMovieVM.getMovie().setGenres(listGenre);
                } catch (Exception e) {
                }
                try {
                    List<Country> listCountry = new ArrayList<>();
                    for (Element e : d.findFirst("<strong>Country: </strong>").getParent().findEvery("<a href>")) {
                        Country c = Ebean.find(Country.class).where().eq("countryName", e.innerText()).findUnique();
                        if (c == null) {
                            c = new Country(e.innerText(), e.innerText());
                            Ebean.save(c);
                        }
                        listCountry.add(c);
                    }
                    this.addNewMovieVM.getMovie().setCountries(listCountry);
                } catch (Exception e) {
                }

            }

            BindUtils.postGlobalCommand(null, null, "refresh", null);
            this.searchMovieInfoGoStream.detach();

        } catch (NotFound | ResponseException | NumberFormatException | ArrayIndexOutOfBoundsException | OptimisticLockException e) {
//            Logger.getLogger(SearchGoStreamMovieVM.class.getName()).log(Level.SEVERE, null, e);
        }

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

    public Window getSearchMovieInfoGoStream() {
        return searchMovieInfoGoStream;
    }

    public void setSearchMovieInfoGoStream(Window searchMovieInfoGoStream) {
        this.searchMovieInfoGoStream = searchMovieInfoGoStream;
    }

    public AddNewMovieVM getAddNewMovieVM() {
        return addNewMovieVM;
    }

    public void setAddNewMovieVM(AddNewMovieVM addNewMovieVM) {
        this.addNewMovieVM = addNewMovieVM;
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

    public boolean isIsFixMode() {
        return isFixMode;
    }

    public void setIsFixMode(boolean isFixMode) {
        this.isFixMode = isFixMode;
    }

}
