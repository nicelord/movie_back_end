/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.vm;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.externalApi.OmdbApi;
import com.enseval.ttss.externalApi.OmdbSearch;
import com.enseval.ttss.model.Actor;
import com.enseval.ttss.model.GphotosTask;
import com.enseval.ttss.model.LinkProcessor;
import com.enseval.ttss.model.Movie2;
import com.enseval.ttss.model.OpenloadTask;
import com.enseval.ttss.model.Quality;
import com.enseval.ttss.model.Resolution;
import com.enseval.ttss.model.StreamSource;
import com.enseval.ttss.model.StreamangoTask;
import com.enseval.ttss.externalApi.PopcornSearch;
import com.enseval.ttss.externalApi.SearchResult;
import com.enseval.ttss.externalApi.TMDBApi;
import com.enseval.ttss.externalApi.TmdbSearchResult;
import com.enseval.ttss.model.Country;
import com.enseval.ttss.model.Genre;
import com.enseval.ttss.util.AuthenticationServiceImpl;
import com.jaunt.Element;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 *
 * @author asus
 */
public class SearchNewMovieVM {

    @Wire("#searchNewMovie")
    private Window winSearchMovie;
    @Wire("#cmbQuality")
    private Combobox cmbQuality;
    @Wire("#cmbResolution")
    private Combobox cmbResolution;

    String keywords = "";

    OmdbApi omdbApi;

    UserAgent userAgent;
    AddNewMovieVM addnewMovieVM;

    String season = "";
    String episode = "";
    String searchType;

    TMDBApi tmdb;
    List<TmdbSearchResult> listSearch = new ArrayList<>();
    boolean isFixMode = false;

    @AfterCompose
    public void initSetup(@ContextParam(ContextType.VIEW) final Component view,
            @ExecutionArgParam("parent") AddNewMovieVM addnewMovieVM) {
        this.addnewMovieVM = addnewMovieVM;
        tmdb = new TMDBApi();
        this.searchType = "movie";

        Selectors.wireComponents(view, this, false);

    }

    @Command
    @NotifyChange({"listSearch"})
    public void search() {
        this.listSearch = this.tmdb.search(keywords, searchType);
    }

    @Command
    public void selectMovie(@BindingParam("tmdbId") String tmdbId) {
        if (searchType.equals("movie")) {
            Movie2 m = this.tmdb.getTmdbMovieDetail(tmdbId);
            if (isFixMode) {
                if (this.addnewMovieVM.getMovie().getTitle().isEmpty()) {
                    this.addnewMovieVM.getMovie().setTitle(m.getTitle());
                }
                if (this.addnewMovieVM.getMovie().getSynopsis().isEmpty()) {
                    this.addnewMovieVM.getMovie().setSynopsis(m.getSynopsis());
                }
                if (this.addnewMovieVM.getMovie().getTrailer().isEmpty()) {
                    this.addnewMovieVM.getMovie().setTrailer(m.getTrailer());
                }
                if (this.addnewMovieVM.getMovie().getPosterLink().isEmpty()) {
                    this.addnewMovieVM.getMovie().setPosterLink(m.getPosterLink());
                }
                if (this.addnewMovieVM.getMovie().getBigPosterLink().isEmpty()) {
                    this.addnewMovieVM.getMovie().setBigPosterLink(m.getBigPosterLink());
                }
                if (this.addnewMovieVM.getMovie().getImdbId().isEmpty()) {
                    this.addnewMovieVM.getMovie().setImdbId(m.getImdbId());
                }
                if (this.addnewMovieVM.getMovie().getDuration().isEmpty()) {
                    this.addnewMovieVM.getMovie().setDuration(m.getDuration());
                }
                if (this.addnewMovieVM.getMovie().getReleaseYear() == 0) {
                    this.addnewMovieVM.getMovie().setReleaseYear(m.getReleaseYear());
                }
                if (this.addnewMovieVM.getMovie().getCast().isEmpty()) {
                    this.addnewMovieVM.getMovie().setCast(m.getCast());
                }

                if (this.addnewMovieVM.getMovie().getGenres().isEmpty()) {
                    this.addnewMovieVM.getMovie().setGenres(m.getGenres());
                }

                if (this.addnewMovieVM.getMovie().getCountries().isEmpty()) {
                    this.addnewMovieVM.getMovie().setCountries(m.getCountries());
                }
                if (this.addnewMovieVM.getMovie().getTags().isEmpty()) {
                    this.addnewMovieVM.getMovie().setTags(m.getTags());
                }
            } else {
                this.addnewMovieVM.getMovie().setTitle(m.getTitle());
                this.addnewMovieVM.getMovie().setSynopsis(m.getSynopsis());
                this.addnewMovieVM.getMovie().setTrailer(m.getTrailer());
                this.addnewMovieVM.getMovie().setPosterLink(m.getPosterLink());
                this.addnewMovieVM.getMovie().setBigPosterLink(m.getBigPosterLink());
                this.addnewMovieVM.getMovie().setRating(m.getRating());
                this.addnewMovieVM.getMovie().setDuration(m.getDuration());
                this.addnewMovieVM.getMovie().setType(m.getType());
                this.addnewMovieVM.getMovie().setPostDate(m.getPostDate());
                this.addnewMovieVM.getMovie().setLastUpdate(m.getLastUpdate());
                this.addnewMovieVM.getMovie().setReleaseYear(m.getReleaseYear());
                this.addnewMovieVM.getMovie().setImdbId(m.getImdbId());
                this.addnewMovieVM.getMovie().setSeason(m.getSeason());
                this.addnewMovieVM.getMovie().setEpisode(m.getEpisode());
                this.addnewMovieVM.getMovie().setGenres(m.getGenres());
                this.addnewMovieVM.getMovie().setCountries(m.getCountries());
                this.addnewMovieVM.getMovie().setCast(m.getCast());
                this.addnewMovieVM.getMovie().setTags(m.getTags());
            }

            BindUtils.postGlobalCommand(null, null, "refresh", null);
            this.winSearchMovie.detach();
        } else if (searchType.equals("tv")) {
            if (this.season.isEmpty() || this.episode.isEmpty()) {
                Messagebox.show("Season/episode not specified!", "ERROR", Messagebox.OK, Messagebox.ERROR);
                return;
            }
            Movie2 m = this.tmdb.getTmdbTvDetail(tmdbId, this.season, this.episode);
            if (isFixMode) {
                if (this.addnewMovieVM.getMovie().getTitle().isEmpty()) {
                    this.addnewMovieVM.getMovie().setTitle(m.getTitle());
                }
                if (this.addnewMovieVM.getMovie().getSynopsis().isEmpty()) {
                    this.addnewMovieVM.getMovie().setSynopsis(m.getSynopsis());
                }
                if (this.addnewMovieVM.getMovie().getTrailer().isEmpty()) {
                    this.addnewMovieVM.getMovie().setTrailer(m.getTrailer());
                }
                if (this.addnewMovieVM.getMovie().getPosterLink().isEmpty()) {
                    this.addnewMovieVM.getMovie().setPosterLink(m.getPosterLink());
                }
                if (this.addnewMovieVM.getMovie().getBigPosterLink().isEmpty()) {
                    this.addnewMovieVM.getMovie().setBigPosterLink(m.getBigPosterLink());
                }
                if (this.addnewMovieVM.getMovie().getImdbId().isEmpty()) {
                    this.addnewMovieVM.getMovie().setImdbId(m.getImdbId());
                }
                if (this.addnewMovieVM.getMovie().getDuration().isEmpty()) {
                    this.addnewMovieVM.getMovie().setDuration(m.getDuration());
                }
                if (this.addnewMovieVM.getMovie().getReleaseYear() == 0) {
                    this.addnewMovieVM.getMovie().setReleaseYear(m.getReleaseYear());
                }
                if (this.addnewMovieVM.getMovie().getCast().isEmpty()) {
                    this.addnewMovieVM.getMovie().setCast(m.getCast());
                }

                if (this.addnewMovieVM.getMovie().getGenres().isEmpty()) {
                    this.addnewMovieVM.getMovie().setGenres(m.getGenres());
                }

                if (this.addnewMovieVM.getMovie().getCountries().isEmpty()) {
                    this.addnewMovieVM.getMovie().setCountries(m.getCountries());
                }
                if (this.addnewMovieVM.getMovie().getTags().isEmpty()) {
                    this.addnewMovieVM.getMovie().setTags(m.getTags());
                }
            } else {
                this.addnewMovieVM.getMovie().setTitle(m.getTitle());
                this.addnewMovieVM.getMovie().setSynopsis(m.getSynopsis());
                this.addnewMovieVM.getMovie().setTrailer(m.getTrailer());
                this.addnewMovieVM.getMovie().setPosterLink(m.getPosterLink());
                this.addnewMovieVM.getMovie().setBigPosterLink(m.getBigPosterLink());
                this.addnewMovieVM.getMovie().setRating(m.getRating());
                this.addnewMovieVM.getMovie().setDuration(m.getDuration());
                this.addnewMovieVM.getMovie().setType(m.getType());
                this.addnewMovieVM.getMovie().setPostDate(m.getPostDate());
                this.addnewMovieVM.getMovie().setLastUpdate(m.getLastUpdate());
                this.addnewMovieVM.getMovie().setReleaseYear(m.getReleaseYear());
                this.addnewMovieVM.getMovie().setImdbId(m.getImdbId());
                this.addnewMovieVM.getMovie().setSeason(m.getSeason());
                this.addnewMovieVM.getMovie().setEpisode(m.getEpisode());
                this.addnewMovieVM.getMovie().setGenres(m.getGenres());
                this.addnewMovieVM.getMovie().setCountries(m.getCountries());
                this.addnewMovieVM.getMovie().setCast(m.getCast());
                this.addnewMovieVM.getMovie().setTags(m.getTags());
            }
            BindUtils.postGlobalCommand(null, null, "refresh", null);
            this.winSearchMovie.detach();
        }

    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public Window getWinSearchMovie() {
        return winSearchMovie;
    }

    public void setWinSearchMovie(Window winSearchMovie) {
        this.winSearchMovie = winSearchMovie;
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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public OmdbApi getOmdbApi() {
        return omdbApi;
    }

    public void setOmdbApi(OmdbApi omdbApi) {
        this.omdbApi = omdbApi;
    }

    public AddNewMovieVM getAddnewMovieVM() {
        return addnewMovieVM;
    }

    public void setAddnewMovieVM(AddNewMovieVM addnewMovieVM) {
        this.addnewMovieVM = addnewMovieVM;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public TMDBApi getTmdb() {
        return tmdb;
    }

    public void setTmdb(TMDBApi tmdb) {
        this.tmdb = tmdb;
    }

    public List<TmdbSearchResult> getListSearch() {
        return listSearch;
    }

    public void setListSearch(List<TmdbSearchResult> listSearch) {
        this.listSearch = listSearch;
    }

    public boolean isIsFixMode() {
        return isFixMode;
    }

    public void setIsFixMode(boolean isFixMode) {
        this.isFixMode = isFixMode;
    }

}
