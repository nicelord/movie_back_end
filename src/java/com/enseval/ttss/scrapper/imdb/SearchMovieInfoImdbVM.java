/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.scrapper.imdb;

import com.enseval.ttss.scrapper.gostream.*;
import com.avaje.ebean.Ebean;
import com.enseval.ttss.externalApi.TMDBApi;
import com.enseval.ttss.model.Actor;
import com.enseval.ttss.model.Country;
import com.enseval.ttss.model.Genre;
import com.enseval.ttss.model.Movie2;
import com.enseval.ttss.model.Tag;
import com.enseval.ttss.scrapper.ScrapperVM;
import com.enseval.ttss.scrapper.ScrapperVM;
import com.enseval.ttss.vm.AddNewMovieVM;
import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
public class SearchMovieInfoImdbVM {

    @Wire("#searchMovieInfoImdb")
    Window searchMovieInfoImdb;

    List<String> moviePageLink = new ArrayList<>();
    List<String> listSelectedMoviePage = new ArrayList<>();
    String selectedMoviePage;
    String keywords;
    AddNewMovieVM addNewMovieVM;
    boolean isFixMode = false;
    String searchType = "all";

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view,
            @ExecutionArgParam("parent") AddNewMovieVM addNewMovieVM) {
        this.addNewMovieVM = addNewMovieVM;
        Selectors.wireComponents(view, this, false);

    }

    @Command
    @NotifyChange({"moviePageLink"})
    public void search() {
        try {
            List<String> s = new ArrayList<>();
            UserAgent userAgent = new UserAgent();
            userAgent.settings.defaultRequestHeaders.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
            if (this.searchType.equals("all")) {
                userAgent.visit("http://www.imdb.com/find?q=" + URLEncoder.encode(keywords, "UTF-8") + "&s=tt&ref_=fn_ft");
            } else {
                userAgent.visit("http://www.imdb.com/find?q=" + URLEncoder.encode(keywords, "UTF-8") + "&s=tt&ttype=" + this.searchType + "&ref_=fn_ft");
            }

            for (Element element : userAgent.doc.findFirst("<table class=\"findList\">").findEvery("<td class=\"result_text\">")) {
                s.add(element.findFirst("<a>").getAt("href").split("/")[4] + "-" + element.findFirst("<a>").getText() + " " + element.getText().trim());
            }
            this.moviePageLink = s;
        } catch (ResponseException | NotFound | UnsupportedEncodingException ex) {
            Logger.getLogger(SearchMovieInfoImdbVM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Command
    public void addToNewMovie(@BindingParam("url") String s) {
        try {
            UserAgent userAgent = new UserAgent();
            userAgent.visit("http://www.imdb.com/title/" + s.split("-")[0]);
            Document d = userAgent.doc;

            this.addNewMovieVM.getMovie().setImdbId(s.split("-")[0]);
            if (isFixMode) {
                if (this.searchType.equals("ep")) {
                    this.addNewMovieVM.getMovie().setType("tv");
                    if (this.addNewMovieVM.getMovie().getTitle().isEmpty()) {
                        try {
                            StringBuilder t = new StringBuilder();
                            t.append(d.findFirst("<div class=\"titleParent\">").findFirst("<a>").getAt("title"));
                            t.append(": ").append(d.findFirst("<div class=\"title_wrapper\">").findFirst("<h1>").getText().replace("&nbsp;", "").trim());
                            this.addNewMovieVM.getMovie().setTitle(t.toString());
                            this.addNewMovieVM.getMovie().setSeason(Integer.parseInt(d.findFirst("<div class=\"bp_heading\">Season").getText().split(" ")[1]));
                            this.addNewMovieVM.getMovie().setEpisode(Integer.parseInt(d.findFirst("<div class=\"bp_heading\">Season").getText().split(" ")[4]));
                        } catch (Exception e) {
                        }
                    }
                } else {
                    this.addNewMovieVM.getMovie().setType("cinema");
                    if (this.addNewMovieVM.getMovie().getTitle().isEmpty()) {
                        try {
                            this.addNewMovieVM.getMovie().setTitle(d.findFirst("<div class=\"title_wrapper\">").findFirst("<h1>").getText().replace("&nbsp;", "").trim());
                        } catch (Exception exception) {
                        }
                    }
                }

                if (this.addNewMovieVM.getMovie().getSynopsis().isEmpty()) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append(d.findFirst("<div class=\"summary_text\" itemprop=\"description\">").getText().trim());
                        sb.append("<br/>").append(d.findFirst("<div class=\"inline canwrap\" itemprop=\"description\">").findEvery("<p>").innerText().trim());
                        this.addNewMovieVM.getMovie().setSynopsis(sb.toString());
                    } catch (Exception exception) {
                    }
                }

                if (this.addNewMovieVM.getMovie().getPosterLink().isEmpty()) {
                    try {
                        this.addNewMovieVM.getMovie().setPosterLink(d.findFirst("<div class=\"poster\">").findFirst("<img>").getAt("src"));
                    } catch (Exception exception) {
                    }
                }
                if (this.addNewMovieVM.getMovie().getRating().isEmpty()) {
                    try {
                        this.addNewMovieVM.getMovie().setRating(d.findFirst("<div class=\"ratingValue\">").findFirst("<span itemprop=\"ratingValue\">").getText());
                    } catch (Exception exception) {
                    }
                }

                if (this.addNewMovieVM.getMovie().getDuration().isEmpty()) {
                    try {
                        this.addNewMovieVM.getMovie().setDuration(d.findFirst("<time itemprop=\"duration\">").getAt("datetime").replace("PT", "").replace("M", ""));
                    } catch (Exception exception) {
                    }
                }

                if (this.addNewMovieVM.getMovie().getReleaseYear() == 0) {
                    try {
                        String release = d.findFirst("<h4 class=\"inline\">Release Date:</h4>").getParent().getText().trim();
                        this.addNewMovieVM.getMovie().setReleaseYear(Integer.parseInt(release.substring(release.indexOf("(") - 5, release.indexOf("(")).trim()));
                    } catch (Exception exception) {
                    }
                }

                if (this.addNewMovieVM.getMovie().getGenres().isEmpty()) {
                    try {
                        for (Element e : userAgent.doc.findFirst("<h4 class=\"inline\">Genres:</h4>").getParent().findEvery("<a>")) {
                            Genre g = Ebean.find(Genre.class).where().eq("name", e.getText().trim()).findUnique();
                            if (g == null) {
                                g = new Genre(e.getText().trim());
                                Ebean.save(g);
                            }
                            this.addNewMovieVM.getMovie().getGenres().add(g);
                        }
                    } catch (Exception exception) {
                    }
                }

                if (this.addNewMovieVM.getMovie().getCountries().isEmpty()) {
                    try {
                        for (Element e : userAgent.doc.findFirst("<h4 class=\"inline\">Country:</h4>").getParent().findEvery("<a>")) {
                            Country c = Ebean.find(Country.class).where().eq("countryName", e.getText().trim()).findUnique();
                            if (c == null) {
                                c = new Country(e.innerText(), e.getText().trim());
                                Ebean.save(c);
                            }
                            this.addNewMovieVM.getMovie().getCountries().add(c);
                        }
                    } catch (Exception exception) {
                    }
                }

                if (this.addNewMovieVM.getMovie().getCast().isEmpty()) {
                    try {
                        for (Element e : userAgent.doc.findFirst("<h4 class=\"inline\">Stars:</h4>").getParent().findEvery("<span class=\"itemprop\" itemprop=\"name\">")) {
                            Actor a = Ebean.find(Actor.class).where().eq("actorName", e.innerText().trim()).findUnique();
                            if (a == null) {
                                a = new Actor(e.innerText().trim());
                                Ebean.save(a);
                            }
                            this.addNewMovieVM.getMovie().getCast().add(a);
                        }
                    } catch (Exception exception) {
                    }
                }

                if (this.addNewMovieVM.getMovie().getTags().isEmpty()) {
                    try {
                        for (Element e : userAgent.doc.findFirst("<h4 class=\"inline\">Plot Keywords:</h4>").getParent().findEvery("<span class=\"itemprop\" itemprop=\"keywords\">")) {
                            Tag t = Ebean.find(Tag.class).where().eq("tagName", e.innerText().trim()).findUnique();
                            if (t == null) {
                                t = new Tag(e.innerText().trim());
                                Ebean.save(t);
                            }
                            this.addNewMovieVM.getMovie().getTags().add(t);
                        }
                    } catch (Exception e) {
                    }
                }

            } else {
                if (this.searchType.equals("ep")) {
                    this.addNewMovieVM.getMovie().setType("tv");
                    try {
                        StringBuilder t = new StringBuilder();
                        t.append(d.findFirst("<div class=\"titleParent\">").findFirst("<a>").getAt("title"));
                        t.append(": ").append(d.findFirst("<div class=\"title_wrapper\">").findFirst("<h1>").getText().replace("&nbsp;", "").trim());
                        this.addNewMovieVM.getMovie().setTitle(t.toString());
                        this.addNewMovieVM.getMovie().setSeason(Integer.parseInt(d.findFirst("<div class=\"bp_heading\">Season").getText().split(" ")[1]));
                        this.addNewMovieVM.getMovie().setEpisode(Integer.parseInt(d.findFirst("<div class=\"bp_heading\">Season").getText().split(" ")[4]));
                    } catch (Exception exception) {
                    }
                } else {
                    this.addNewMovieVM.getMovie().setType("cinema");
                    try {
                        this.addNewMovieVM.getMovie().setTitle(d.findFirst("<div class=\"title_wrapper\">").findFirst("<h1>").getText().replace("&nbsp;", "").trim());
                    } catch (Exception exception) {
                    }

                }

                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append(d.findFirst("<div class=\"summary_text\" itemprop=\"description\">").getText().trim());
                    sb.append("<br/>").append(d.findFirst("<div class=\"inline canwrap\" itemprop=\"description\">").findEvery("<p>").innerText().trim());
                    this.addNewMovieVM.getMovie().setSynopsis(sb.toString());
                } catch (Exception exception) {
                }
                try {
                    this.addNewMovieVM.getMovie().setPosterLink(d.findFirst("<div class=\"poster\">").findFirst("<img>").getAt("src"));
                } catch (Exception exception) {
                }
                try {
                    this.addNewMovieVM.getMovie().setRating(d.findFirst("<div class=\"ratingValue\">").findFirst("<span itemprop=\"ratingValue\">").getText());
                } catch (Exception exception) {
                }
                try {
                    this.addNewMovieVM.getMovie().setDuration(d.findFirst("<time itemprop=\"duration\">").getAt("datetime").replace("PT", "").replace("M", ""));
                } catch (Exception exception) {
                }
                try {
                    String release = d.findFirst("<h4 class=\"inline\">Release Date:</h4>").getParent().getText().trim();
                    this.addNewMovieVM.getMovie().setReleaseYear(Integer.parseInt(release.substring(release.indexOf("(") - 5, release.indexOf("(")).trim()));
                } catch (Exception exception) {
                }
                try {
                    List<Genre> listGenre = new ArrayList<>();
                    for (Element e : userAgent.doc.findFirst("<h4 class=\"inline\">Genres:</h4>").getParent().findEvery("<a>")) {
                        Genre g = Ebean.find(Genre.class).where().eq("name", e.getText().trim()).findUnique();
                        if (g == null) {
                            g = new Genre(e.getText().trim());
                            Ebean.save(g);
                        }
                        listGenre.add(g);
                    }
                    this.addNewMovieVM.getMovie().setGenres(listGenre);
                } catch (Exception exception) {
                }
                try {
                    List<Country> listCountry = new ArrayList<>();
                    for (Element e : userAgent.doc.findFirst("<h4 class=\"inline\">Country:</h4>").getParent().findEvery("<a>")) {
                        Country c = Ebean.find(Country.class).where().eq("countryName", e.getText().trim()).findUnique();
                        if (c == null) {
                            c = new Country(e.innerText(), e.getText().trim());
                            Ebean.save(c);
                        }
                        listCountry.add(c);
                    }
                    this.addNewMovieVM.getMovie().setCountries(listCountry);
                } catch (Exception exception) {
                }
                try {
                    List<Actor> listActor = new ArrayList<>();
                    for (Element e : userAgent.doc.findFirst("<h4 class=\"inline\">Stars:</h4>").getParent().findEvery("<span class=\"itemprop\" itemprop=\"name\">")) {
                        Actor a = Ebean.find(Actor.class).where().eq("actorName", e.innerText().trim()).findUnique();
                        if (a == null) {
                            a = new Actor(e.innerText().trim());
                            Ebean.save(a);
                        }
                        listActor.add(a);
                        
                    }
                    this.addNewMovieVM.getMovie().setCast(listActor);
                } catch (Exception exception) {
                }
                try {
                    List<Tag> listTag = new ArrayList<>();
                    for (Element e : userAgent.doc.findFirst("<h4 class=\"inline\">Plot Keywords:</h4>").getParent().findEvery("<span class=\"itemprop\" itemprop=\"keywords\">")) {
                        Tag t = Ebean.find(Tag.class).where().eq("tagName", e.innerText().trim()).findUnique();
                        if (t == null) {
                            t = new Tag(e.innerText().trim());
                            Ebean.save(t);
                        }
                        listTag.add(t);
                        
                    }
                    this.addNewMovieVM.getMovie().setTags(listTag);
                } catch (Exception e) {
                }
            }

            BindUtils.postGlobalCommand(null, null, "refresh", null);
            this.searchMovieInfoImdb.detach();

        } catch (ResponseException | NumberFormatException | ArrayIndexOutOfBoundsException | OptimisticLockException e) {
            Logger.getLogger(SearchGoStreamMovieVM.class.getName()).log(Level.SEVERE, null, e);
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

    public Window getSearchMovieInfoImdb() {
        return searchMovieInfoImdb;
    }

    public void setSearchMovieInfoImdb(Window searchMovieInfoImdb) {
        this.searchMovieInfoImdb = searchMovieInfoImdb;
    }

    public AddNewMovieVM getAddNewMovieVM() {
        return addNewMovieVM;
    }

    public void setAddNewMovieVM(AddNewMovieVM addNewMovieVM) {
        this.addNewMovieVM = addNewMovieVM;
    }

    public boolean isIsFixMode() {
        return isFixMode;
    }

    public void setIsFixMode(boolean isFixMode) {
        this.isFixMode = isFixMode;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

}
