/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.externalApi;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.model.Country;
import com.enseval.ttss.model.Genre;
import com.enseval.ttss.model.Movie2;
import com.enseval.ttss.model.Tag;
import com.jaunt.JNode;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asus
 */
public class PopcornSearch {

    final static String SEARCH_ENDPOINT = "http://api.ukfrnlge.xyz/list?sort=seeds&cb=&quality=720p,1080p&page=1&";
    final static String MOVIE_DETAIL_ENDPOINT = "http://tinfo.ukfrnlge.xyz/3/movie/";
    final static String API_KEY = "6b6effafe7c0b6fa17191d0430f546f8";

    public static void main(String[] args) {
        String s = "Chris Evans, Robert Downey Jr., Scarlett Johansson";
        for (String ss : s.split(",")) {
            System.out.println(ss.trim());
        }
    }

    public List<SearchResult> doSearch(String keywords) {
        List<SearchResult> searchResult = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(SEARCH_ENDPOINT).append("keywords=");
            sb.append(URLEncoder.encode(keywords, "UTF-8"));

            UserAgent userAgent = new UserAgent();
            userAgent.sendGET(sb.toString(),
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            JNode movieList = userAgent.json.get("MovieList");
            for (JNode movie : movieList) {
                SearchResult sr = new SearchResult();
                sr.setImdb(movie.get("imdb").toString());
                sr.setTitle(movie.get("title").toString());
                sr.setYear(movie.get("year").toString());
                sr.setSynopsis(movie.get("description").toString());
                sr.setPoster(movie.get("poster_med").toString());
                sr.setTrailer(movie.get("trailer").toString());
                sr.setRating(movie.get("rating").toString());
                sr.setActors(movie.get("actors").toString());
                searchResult.add(sr);
            }

        } catch (ResponseException | UnsupportedEncodingException | NotFound ex) {
            Logger.getLogger(PopcornSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return searchResult;
    }

    public Movie2 getMovieDetail(String imdb) {
        Movie2 m = new Movie2();

        StringBuilder sb = new StringBuilder();
        sb.append(MOVIE_DETAIL_ENDPOINT).append(imdb);
        sb.append("?api_key=").append(API_KEY);

        UserAgent userAgent = new UserAgent();
        try {
            userAgent.sendGET(sb.toString(),
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            JNode movie = userAgent.json;
            m.setTitle(movie.get("title").toString());
            m.setSynopsis(movie.get("overview").toString());
            m.setBigPosterLink("http://image.tmdb.org/t/p/w780" + movie.get("backdrop_path").toString());
            m.setDuration(movie.get("runtime").toString());
            for (JNode genre : movie.get("genres")) {
                String genreName = genre.get("name").toString();
                Genre g = Ebean.find(Genre.class).where().eq("name", genreName).findUnique();
                if (g == null) {
                    g = new Genre(genreName);
                    Ebean.save(g);
                }
                m.getGenres().add(g);
            }

            for (JNode country : movie.get("production_countries")) {
                String countryName = country.get("name").toString();
                Country c = Ebean.find(Country.class).where().eq("countryName", countryName).findUnique();
                if (c == null) {
                    c = new Country(countryName, countryName);
                    Ebean.save(c);
                }
                m.getCountries().add(c);
            }

        } catch (ResponseException | NotFound ex) {
            Logger.getLogger(PopcornSearch.class.getName()).log(Level.SEVERE, null, ex);
        }

        return m;
    }

    
    public List<Tag> getTags(){
        List<Tag> tags = new ArrayList<>();
        
        
        
        return tags;
    }
}
