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
import com.enseval.ttss.model.Movie2;
import com.enseval.ttss.model.Quality;
import com.enseval.ttss.model.Resolution;
import com.enseval.ttss.model.StreamLink;
import com.enseval.ttss.model.StreamSource;
import com.enseval.ttss.model.Tag;
import com.enseval.ttss.model.User;
import com.enseval.ttss.util.AuthenticationServiceImpl;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.bind.BindUtils;
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
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author asus
 */
public class AddNewMovieVM {
    @Wire("#addNewMovie")
    private Window winAddNewMovie;
    @Wire("#bdcast")
    private Bandbox bbcast;
    @Wire("#bdgenre")
    private Bandbox bdgenre;
    @Wire("#bdcountry")
    private Bandbox bdcountry;
    @Wire("#tag")
    private Textbox tag;
    
    List<Genre> listGenre = new ArrayList<>();
    List<Quality> listQuality = new ArrayList<>();
    List<Resolution> listResolution = new ArrayList<>();
    List<StreamSource> listSource = new ArrayList<>();
    String newQuality, newResolution, newServer;
    List<Actor> listActor = new ArrayList<>();
    List<Country> listCountry = new ArrayList<>();
    
    Movie2 movie;
    
    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view) {
        this.movie = new Movie2();
        this.movie.setPostDate(new Timestamp(new Date().getTime()));
        this.movie.setLastUpdate(new Timestamp(new Date().getTime()));
        StreamSource ss = new StreamSource("", "direct");
        StreamLink sl = new StreamLink(ss, new Quality(""), new Resolution(""), "", false);
        this.movie.getStreamLinks().add(sl);
        
        this.listGenre = Ebean.find(Genre.class).orderBy("genreId desc").findList();
        this.listQuality = Ebean.find(Quality.class).orderBy("qualityId desc").findList();
        this.listResolution = Ebean.find(Resolution.class).orderBy("resolutionId desc").findList();
        this.listSource = Ebean.find(StreamSource.class).orderBy("streamSourceId desc").findList();
        this.listActor = Ebean.find(Actor.class).orderBy("actorId desc").findList();
        this.listCountry = Ebean.find(Country.class).orderBy("countryName asc").findList();
        Selectors.wireComponents(view, (Object) this, false);
    }
    
    @Command
    @NotifyChange({"*"})
    public void saveMovie() {

        List<StreamLink> temp = new ArrayList<>();
        for (StreamLink sl : this.movie.getStreamLinks()) {
            try {
                boolean empty = sl.getQuality().getQuality() == null;
                System.out.println(empty);
            } catch (Exception e) {
                if (!sl.getNewQuality().isEmpty()) {
                    Quality q = new Quality(sl.getNewQuality());
                    Ebean.save(q);
                    sl.setQuality(q);
                } else {
                    Clients.alert("quality empty!");
                    return;
                }
            }

            try {
                boolean empty = sl.getResolution().getResolution() == null;
                System.out.println(empty);
            } catch (Exception e) {
                if (!sl.getNewResolution().isEmpty()) {
                    Resolution r = new Resolution(sl.getNewResolution());
                    Ebean.save(r);
                    sl.setResolution(r);
                } else {
                    Clients.alert("resolution empty!");
                    return;
                }
            }

            try {
                boolean empty = sl.getServerSource().getServerName() == null;
                System.out.println(empty);
            } catch (Exception e) {
                if (!sl.getNewServerSource().isEmpty()) {
                    StreamSource s = new StreamSource(sl.getNewServerSource(), "direct");
                    Ebean.save(s);
                    sl.setServerSource(s);
                } else {
                    Clients.alert("server empty!");
                    return;
                }
            }

            if (!(sl.getQuality().getQuality().isEmpty() || sl.getResolution().getResolution().isEmpty() || sl.getServerSource().getServerName().isEmpty())) {
                temp.add(sl);
            } else {
                Clients.alert("new link contain missing property!");
                return;
            }
        }

        List<Actor> tempActor = new ArrayList<>();
        for (String s : this.bbcast.getValue().split(",")) {
            if (!s.isEmpty()) {
                Actor actor = Ebean.find(Actor.class).where().eq("actorName", s).findUnique();
                if (actor == null) {
                    Ebean.save(new Actor(s));
                    tempActor.add(Ebean.find(Actor.class).where().eq("actorName", s).findUnique());
                } else {
                    tempActor.add(actor);
                }
            }

        }

        List<Genre> tempGenre = new ArrayList<>();
        for (String s : this.bdgenre.getValue().split(",")) {
            if (!s.isEmpty()) {
                Genre genre = Ebean.find(Genre.class).where().eq("name", s).findUnique();
                if (genre == null) {
                    Ebean.save(new Genre(s));
                    tempGenre.add(Ebean.find(Genre.class).where().eq("name", s).findUnique());
                } else {
                    tempGenre.add(genre);
                }
            }
        }

        List<Country> tempCountry = new ArrayList<>();
        for (String s : this.bdcountry.getValue().split(",")) {
            if (!s.isEmpty()) {
//                Country country = Ebean.find(Country.class).where().eq("countryName", s).findUnique();
//                if (country == null) {
//                    Ebean.save(new Country("", s));
//                    tempCountry.add(Ebean.find(Country.class).where().eq("countryName", s).findUnique());
//                } else {
//                    tempCountry.add(country);
//                }
                
                Country c = null;
                try {
                    c = Ebean.find(Country.class).where().eq("countryName", s).findList().get(0);
                } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    c = new Country(s, s);
                }
                Ebean.save(c);
                tempCountry.add(c);
            }
        }

        List<Tag> tempTag = new ArrayList<>();
        for (String s : this.tag.getValue().split(",")) {
            if (!s.isEmpty()) {
                Tag t = Ebean.find(Tag.class).where().eq("tagName", s).findUnique();
                if (t == null) {
                    Ebean.save(new Tag(s));
                    tempTag.add(Ebean.find(Tag.class).where().eq("tagName", s).findUnique());
                } else {
                    tempTag.add(t);
                }
            }
        }

        this.movie.setTags(tempTag);
        this.movie.setCountries(tempCountry);
        this.movie.setGenres(tempGenre);
        this.movie.setCast(tempActor);
        this.movie.setStreamLinks(temp);
        this.movie.setUser(new AuthenticationServiceImpl().getUserCredential().getUser());
        Ebean.save(this.movie.getStreamLinks());
        Ebean.save(this.movie);
        BindUtils.postGlobalCommand(null, null, "refresh", null);
        this.winAddNewMovie.detach();

    }

    @Command
    public String genreListToComma(List<Genre> o) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Genre g : o) {
                sb.append(g.getName());
                sb.append(",");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Command
    public String actorListToComma(List<Actor> o) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Actor g : o) {
                sb.append(g.getActorName());
                sb.append(",");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Command
    public String countryListToComma(List<Country> o) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Country g : o) {
                sb.append(g.getCountryName());
                sb.append(",");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Command
    public String tagListToComma(List<Tag> t) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Tag tag : t) {
                sb.append(tag.getTagName());
                sb.append(",");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    @Command
    @NotifyChange({"movie"})
    public void addLink() {
        StreamSource ss = new StreamSource("", "direct");
        StreamLink sl = new StreamLink(ss, new Quality(""), new Resolution(""), "", false);
        this.movie.getStreamLinks().add(sl);
    }
    
    @Command
    @NotifyChange({"movie"})
    public void removeLink(@BindingParam("streamLink") StreamLink sl) {
        this.movie.getStreamLinks().remove(sl);
    }
    
    @Command
    public void scrapeLink() {
        Map m = new HashMap();
        m.put("movie", this.movie);
        Window window = (Window) Executions.createComponents(
                "scrapelink.zul", null, m);
        window.setClosable(true);
        window.doModal();
    }
    
    @Command
    public void searchMovieOnline(){
        Map m = new HashMap();
        m.put("parent", this);
        Executions.createComponents("searchNewMovie.zul", null, m);
    }
    
    @Command
    public void searchMovieInfoGoStream(){
        Map m = new HashMap();
        m.put("parent", this);
        Executions.createComponents("searchMovieInfoGoStream.zul", null, m);
    }
    
    @Command
    public void searchMovieInfoImdb(){
        Map m = new HashMap();
        m.put("parent", this);
        Executions.createComponents("searchMovieInfoImdb.zul", null, m);
    }
    
    @GlobalCommand
    @NotifyChange({"*"})
    public void refresh(){
        
    }
    
    public Window getWinAddNewMovie() {
        return winAddNewMovie;
    }

    public void setWinAddNewMovie(Window winAddNewMovie) {
        this.winAddNewMovie = winAddNewMovie;
    }

    public Movie2 getMovie() {
        return movie;
    }

    public void setMovie(Movie2 movie) {
        this.movie = movie;
    }

    public Bandbox getBbcast() {
        return bbcast;
    }

    public void setBbcast(Bandbox bbcast) {
        this.bbcast = bbcast;
    }

    public Bandbox getBdgenre() {
        return bdgenre;
    }

    public void setBdgenre(Bandbox bdgenre) {
        this.bdgenre = bdgenre;
    }

    public Bandbox getBdcountry() {
        return bdcountry;
    }

    public void setBdcountry(Bandbox bdcountry) {
        this.bdcountry = bdcountry;
    }

    public Textbox getTag() {
        return tag;
    }

    public void setTag(Textbox tag) {
        this.tag = tag;
    }

    public List<Genre> getListGenre() {
        return listGenre;
    }

    public void setListGenre(List<Genre> listGenre) {
        this.listGenre = listGenre;
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

    public String getNewQuality() {
        return newQuality;
    }

    public void setNewQuality(String newQuality) {
        this.newQuality = newQuality;
    }

    public String getNewResolution() {
        return newResolution;
    }

    public void setNewResolution(String newResolution) {
        this.newResolution = newResolution;
    }

    public String getNewServer() {
        return newServer;
    }

    public void setNewServer(String newServer) {
        this.newServer = newServer;
    }

    public List<Actor> getListActor() {
        return listActor;
    }

    public void setListActor(List<Actor> listActor) {
        this.listActor = listActor;
    }

    public List<Country> getListCountry() {
        return listCountry;
    }

    public void setListCountry(List<Country> listCountry) {
        this.listCountry = listCountry;
    }
    
    
}
