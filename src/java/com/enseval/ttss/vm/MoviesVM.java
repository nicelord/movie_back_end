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
public class MoviesVM {

    List<Movie2> listMovies, selectedMovie = new ArrayList<>();
    Movie2 movie;
    List<Genre> listGenre = new ArrayList<>();
    List<Quality> listQuality = new ArrayList<>();
    List<Resolution> listResolution = new ArrayList<>();
    List<StreamSource> listSource = new ArrayList<>();
    String newQuality, newResolution, newServer;
    List<Actor> listActor = new ArrayList<>();
    List<Country> listCountry = new ArrayList<>();

    @Wire("#bdcast")
    private Bandbox bbcast;
    @Wire("#bdgenre")
    private Bandbox bdgenre;
    @Wire("#bdcountry")
    private Bandbox bdcountry;
    @Wire("#tag")
    private Textbox tag;

    String filterTitle = "";

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view) {
        this.listMovies = Ebean.find(Movie2.class).orderBy("movieId desc").setMaxRows(30).findList();
        this.listGenre = Ebean.find(Genre.class).orderBy("genreId desc").findList();
        this.listQuality = Ebean.find(Quality.class).orderBy("qualityId desc").findList();
        this.listResolution = Ebean.find(Resolution.class).orderBy("resolutionId desc").findList();
        this.listSource = Ebean.find(StreamSource.class).orderBy("streamSourceId desc").findList();
        this.listActor = Ebean.find(Actor.class).orderBy("actorId desc").findList();
        this.listCountry = Ebean.find(Country.class).orderBy("countryName asc").findList();

        Selectors.wireComponents(view, (Object) this, false);

    }

    @Command
    @NotifyChange({"listMovies", "movie"})
    public void showDetail(@BindingParam("movie") final Movie2 movie) {
        this.movie = movie;
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
                Country country = Ebean.find(Country.class).where().eq("countryName", s).findUnique();
                if (country == null) {
                    Ebean.save(new Country("", s));
                    tempCountry.add(Ebean.find(Country.class).where().eq("countryName", s).findUnique());
                } else {
                    tempCountry.add(country);
                }
            }
        }

        List<Tag> tempTag = new ArrayList<>();
        for (String s : this.tag.getValue().split(",")) {
            if (!s.isEmpty()) {
                Tag t = null;
                try {
                    t = Ebean.find(Tag.class).where().eq("tagName", s).findList().get(0);
                } catch (IndexOutOfBoundsException ex) {
                    t = new Tag(s);
                    Ebean.save(t);
                }
                tempTag.add(t);
            }
        }

        this.movie.setTags(tempTag);
        this.movie.setCountries(tempCountry);
        this.movie.setGenres(tempGenre);
        this.movie.setCast(tempActor);
        this.movie.setStreamLinks(temp);
        this.movie.setLastUpdate(new Timestamp(new Date().getTime()));
        Ebean.save(this.movie.getStreamLinks());
        Ebean.save(this.movie);
        refresh();

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
    @NotifyChange({"movie", "listActor"})
    public void deleteCast(@BindingParam("actor") Actor a) {
        List<Movie2> movies = Ebean.find(Movie2.class).where().eq("cast.actorName", a.getActorName()).findList();
        for (Movie2 m : movies) {
            m.getCast().remove(a);
            Ebean.save(m);
        }
        Ebean.delete(a);
        this.movie.getCast().remove(a);
        this.listActor = Ebean.find(Actor.class).orderBy("actorId desc").findList();
    }

    @Command
    @NotifyChange({"movie", "listGenre"})
    public void deleteGenre(@BindingParam("genre") Genre g) {
        List<Movie2> movies = Ebean.find(Movie2.class).where().eq("genres.name", g.getName()).findList();
        for (Movie2 m : movies) {
            m.getGenres().remove(g);
            Ebean.save(m);
        }
        Ebean.delete(g);
        this.movie.getGenres().remove(g);
        this.listGenre = Ebean.find(Genre.class).orderBy("genreId desc").findList();
    }

    @Command
    @NotifyChange({"movie", "listGenre"})
    public void deleteCountry(@BindingParam("country") Country c) {
        List<Movie2> movies = Ebean.find(Movie2.class).where().eq("countries.countryName", c.getCountryName()).findList();
        for (Movie2 m : movies) {
            m.getCountries().remove(c);
            Ebean.save(m);
        }
        Ebean.delete(c);
        this.movie.getCountries().remove(c);
        this.listCountry = Ebean.find(Country.class).orderBy("countryName asc").findList();
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
    @NotifyChange({"listMovies"})
    public void filterTitle() {
        this.listMovies = Ebean.find(Movie2.class).where().like("title", "%" + this.filterTitle + "%").setMaxRows(30).orderBy("movieId desc").findList();
    }

    @Command
    public void addNewMovie() {
        Executions.createComponents("addNewMovie.zul", null, null);
    }

    @GlobalCommand
    @NotifyChange({"*"})
    public void refresh() {
        this.listMovies = Ebean.find(Movie2.class).orderBy("movieId desc").setMaxRows(30).findList();
        this.listGenre = Ebean.find(Genre.class).orderBy("genreId desc").findList();
        this.listQuality = Ebean.find(Quality.class).orderBy("qualityId desc").findList();
        this.listResolution = Ebean.find(Resolution.class).orderBy("resolutionId desc").findList();
        this.listActor = Ebean.find(Actor.class).orderBy("actorId desc").findList();
        this.listSource = Ebean.find(StreamSource.class).orderBy("streamSourceId desc").findList();
        this.listCountry = Ebean.find(Country.class).orderBy("countryName asc").findList();
    }

    @Command
    @NotifyChange({"listMovies"})
    public void deleteMovie(@BindingParam("movie") Movie2 movie) {
        Ebean.deleteManyToManyAssociations(movie, "cast");
        Ebean.deleteManyToManyAssociations(movie, "countries");
        Ebean.deleteManyToManyAssociations(movie, "genres");
        Ebean.deleteManyToManyAssociations(movie, "streamLinks");
        Ebean.deleteManyToManyAssociations(movie, "tags");
        List<StreamLink> ls = movie.getStreamLinks();
        Ebean.delete(ls);
        List<LinkProcessor> lp = Ebean.find(LinkProcessor.class).where().eq("movieId", movie.getMovieId()).findList();
        for (LinkProcessor linkProcessor : lp) {
            Ebean.delete(lp);
            if (linkProcessor.getGphotosTask() != null) {
                Ebean.delete(linkProcessor.getGphotosTask());
            }
            if (linkProcessor.getStreamangoTask() != null) {
                Ebean.delete(linkProcessor.getStreamangoTask());
            }
            if (linkProcessor.getOpenloadTask() != null) {
                Ebean.delete(linkProcessor.getOpenloadTask());
            }
        }
        Ebean.delete(movie);

        this.listMovies = Ebean.find(Movie2.class).orderBy("movieId desc").setMaxRows(30).findList();
    }

    @Command
    @NotifyChange({"listMovies"})
    public void deleteSelectedMovies() {
        for (Movie2 movie2 : selectedMovie) {
            Ebean.deleteManyToManyAssociations(movie2, "cast");
            Ebean.deleteManyToManyAssociations(movie2, "countries");
            Ebean.deleteManyToManyAssociations(movie2, "genres");
            Ebean.deleteManyToManyAssociations(movie2, "streamLinks");
            Ebean.deleteManyToManyAssociations(movie2, "tags");
            List<StreamLink> ls = movie2.getStreamLinks();
            Ebean.delete(ls);
            List<LinkProcessor> lp = Ebean.find(LinkProcessor.class).where().eq("movieId", movie2.getMovieId()).findList();
            for (LinkProcessor linkProcessor : lp) {
                Ebean.delete(lp);
                if (linkProcessor.getGphotosTask() != null) {
                    Ebean.delete(linkProcessor.getGphotosTask());
                }
                if (linkProcessor.getStreamangoTask() != null) {
                    Ebean.delete(linkProcessor.getStreamangoTask());
                }
                if (linkProcessor.getOpenloadTask() != null) {
                    Ebean.delete(linkProcessor.getOpenloadTask());
                }
            }
            Ebean.delete(movie2);

            this.listMovies = Ebean.find(Movie2.class).orderBy("movieId desc").setMaxRows(30).findList();
        }

    }

    @Command
    public void test() {
        System.out.println("TEST");
    }

    public Movie2 getMovie() {
        return movie;
    }

    public void setMovie(Movie2 movie) {
        this.movie = movie;
    }

    public List<Movie2> getListMovies() {
        return listMovies;
    }

    public void setListMovies(List<Movie2> listMovies) {
        this.listMovies = listMovies;
    }

    public List<Movie2> getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(List<Movie2> selectedMovie) {
        this.selectedMovie = selectedMovie;
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

    public String getFilterTitle() {
        return filterTitle;
    }

    public void setFilterTitle(String filterTitle) {
        this.filterTitle = filterTitle;
    }

}
