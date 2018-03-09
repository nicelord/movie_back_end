/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.externalApi;

import com.avaje.ebean.Ebean;
import static com.enseval.ttss.externalApi.PopcornSearch.SEARCH_ENDPOINT;
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
import com.enseval.ttss.util.AuthenticationService;
import com.enseval.ttss.util.AuthenticationServiceImpl;
import com.enseval.ttss.util.UserCredential;
import com.jaunt.JNode;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asus
 */
public class TMDBApi {

    private static final String END_POINT = "http://api.themoviedb.org/3/";
    private static final String API_KEY = "a98d35cc0997d9396c13e03b036ff1b7";

    public static void main(String[] args) {
        TMDBApi t = new TMDBApi();
        List<TmdbSearchResult> tmdbResult = t.search("dark tower", "movie");
    }

    public List<TmdbSearchResult> search(String keyword, String type) {
        List<TmdbSearchResult> tmdbResult = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(END_POINT);
            sb.append("search/").append(type);
            sb.append("?api_key=").append(API_KEY);
            sb.append("&query=").append(URLEncoder.encode(keyword, "UTF-8"));

            UserAgent userAgent = new UserAgent();
            userAgent.sendGET(sb.toString(),
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            JNode results = userAgent.json.get("results");
            if (type.equals("movie")) {

                for (JNode result : results) {
                    TmdbSearchResult tsr = new TmdbSearchResult();
                    tsr.setTmdbId(result.get("id").toString());
                    tsr.setTitle(result.get("title").toString());
                    tsr.setYear(result.get("release_date").toString().split("-")[0]);
                    tsr.setPoster("http://image.tmdb.org/t/p/w185" + result.get("poster_path").toString());
                    tsr.setSynopsis(result.get("overview").toString());
                    tsr.setType("movie");
                    tmdbResult.add(tsr);
                }
            } else if (type.equals("tv")) {

                for (JNode result : results) {
                    TmdbSearchResult tsr = new TmdbSearchResult();
                    tsr.setTmdbId(result.get("id").toString());
                    tsr.setTitle(result.get("name").toString());
                    tsr.setYear(result.get("first_air_date").toString().split("-")[0]);
                    tsr.setPoster("http://image.tmdb.org/t/p/w185" + result.get("poster_path").toString());
                    tsr.setSynopsis(result.get("overview").toString());
                    tsr.setType("tv");
                    tmdbResult.add(tsr);
                }
            }
        } catch (Exception e) {

        }
        return tmdbResult;
    }

    public Movie2 getTmdbMovieDetail(String tmdbId) {
        Movie2 m = new Movie2();
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(END_POINT);
            sb.append("movie/").append(tmdbId);
            sb.append("?api_key=").append(API_KEY);
            sb.append("&append_to_response=").append(URLEncoder.encode("videos,keywords", "UTF-8"));
            UserAgent userAgent = new UserAgent();

            userAgent.sendGET(sb.toString(),
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            JNode result = userAgent.json;
            m.setTitle(result.get("title").toString());
            m.setSynopsis(result.get("overview").toString());
            JNode videos = result.get("videos").get("results");
            for (JNode video : videos) {
                if (video.get("site").toString().equals("YouTube")) {
                    m.setTrailer(video.get("key").toString());
                    break;
                }
            }
            
            if(!result.get("poster_path").toString().equals("null")){
                m.setPosterLink("http://image.tmdb.org/t/p/w185" + result.get("poster_path").toString().replace("\\", ""));
            }
            
            if(!result.get("backdrop_path").toString().equals("null")){
                m.setBigPosterLink("http://image.tmdb.org/t/p/w600" + result.get("backdrop_path").toString().replace("\\", ""));
            }
            
            m.setDuration(result.get("runtime").toString());
            m.setType("cinema");
            m.setPostDate(new Timestamp(new Date().getTime()));
            m.setLastUpdate(new Timestamp(new Date().getTime()));
            m.setPublish(false);
            m.setReleaseYear(Integer.parseInt(result.get("release_date").toString().split("-")[0]));
            m.setImdbId(result.get("imdb_id").toString());
            m.setSeason(0);
            m.setEpisode(0);
            m.setUser(new AuthenticationServiceImpl().getUserCredential().getUser());

            for (JNode genre : result.get("genres")) {
                String genreName = genre.get("name").toString();
                Genre g = Ebean.find(Genre.class).where().eq("name", genreName).findUnique();
                if (g == null) {
                    g = new Genre(genreName);
                    Ebean.save(g);
                }
                m.getGenres().add(g);
            }
            StreamSource ss = new StreamSource("", "direct");
            StreamLink sl = new StreamLink(ss, new Quality(""), new Resolution(""), "", false);
            m.getStreamLinks().add(sl);

            for (JNode country : result.get("production_countries")) {
                String countryName = country.get("name").toString();
                Country c = Ebean.find(Country.class).where().eq("countryName", countryName).findUnique();
                if (c == null) {
                    c = new Country(countryName, countryName);
                    Ebean.save(c);
                }
                m.getCountries().add(c);
            }

            for (JNode tag : result.get("keywords").get("keywords")) {
                String tagName = tag.get("name").toString();
                Tag t = Ebean.find(Tag.class).where().eq("tagName", tagName).findUnique();
                if (t == null) {
                    t = new Tag(tagName);
                    Ebean.save(t);
                }
                m.getTags().add(t);
            }

            OmdbApi omdb = new OmdbApi();
            JNode omdbResult = omdb.getDetailMovie(m.getImdbId());
            m.setRating(omdbResult.get("imdbRating").toString());

            for (String actor : omdbResult.get("Actors").toString().split(",")) {
                Actor cast = Ebean.find(Actor.class).where().eq("actorName", actor.trim()).findUnique();
                if (cast == null) {
                    cast = new Actor(actor.trim());
                    Ebean.save(cast);
                }
                m.getCast().add(cast);
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }
        return m;
    }

    public Movie2 getTmdbTvDetail(String tmdbId, String season, String episode) {
        Movie2 m = new Movie2();

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(END_POINT);
            sb.append("tv/").append(tmdbId);
            sb.append("?api_key=").append(API_KEY);
            sb.append("&append_to_response=").append(URLEncoder.encode("videos,keywords", "UTF-8"));
            UserAgent userAgent = new UserAgent();

            userAgent.sendGET(sb.toString(),
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            JNode resultTv = userAgent.json;
            userAgent.close();

            StringBuilder sb2 = new StringBuilder();
            sb2.append(END_POINT);
            sb2.append("tv/").append(tmdbId);
            sb2.append("/season/").append(season);
            sb2.append("?api_key=").append(API_KEY);

            sb2.append("&append_to_response=").append(URLEncoder.encode("videos,keywords", "UTF-8"));
            UserAgent userAgent2 = new UserAgent();

            userAgent2.sendGET(sb2.toString(),
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            JNode resultSeason = userAgent2.json;
            userAgent2.close();

            StringBuilder sb3 = new StringBuilder();
            sb3.append(END_POINT);
            sb3.append("tv/").append(tmdbId);
            sb3.append("/season/").append(season);
            sb3.append("/episode/").append(episode);
            sb3.append("?api_key=").append(API_KEY);
            sb3.append("&append_to_response=").append(URLEncoder.encode("videos,keywords", "UTF-8"));
            UserAgent userAgent3 = new UserAgent();

            userAgent3.sendGET(sb3.toString(),
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            JNode resultEpisode = userAgent3.json;
            userAgent3.close();

            //---------movie object--------//
            m.setTitle(resultTv.get("name").toString() + ": " + resultEpisode.get("name"));
            StringBuilder sbSynopsis = new StringBuilder();
            if (!resultTv.get("overview").toString().isEmpty()) {
                sbSynopsis.append("<p>");
                sbSynopsis.append("<strong>Series overview ( ").append(resultTv.get("name").toString()).append(" ) : </strong><br/>");
                sbSynopsis.append(resultTv.get("overview").toString());
                sbSynopsis.append("</p>");
            }

            if (!resultSeason.get("overview").toString().isEmpty()) {
                sbSynopsis.append("<p>");
                sbSynopsis.append("<strong>Season overview ( Season ").append(season).append(" ) : </strong><br/>");
                sbSynopsis.append(resultSeason.get("overview").toString());
                sbSynopsis.append("</p>");
            }

            if (!resultEpisode.get("overview").toString().isEmpty()) {
                sbSynopsis.append("<p>");
                sbSynopsis.append("<strong>Episode overview ( Episode ").append(episode).append(" ) : </strong><br/>");
                sbSynopsis.append(resultEpisode.get("overview").toString());
                sbSynopsis.append("</p>");
            }

            m.setSynopsis(sbSynopsis.toString());

            if (resultEpisode.get("videos").get("results").size() != 0) {
                for (JNode video : resultEpisode.get("videos").get("results")) {
                    if (video.get("site").toString().equals("YouTube")) {
                        m.setTrailer(video.get("key").toString());
                        break;
                    }
                }
            } else if (resultSeason.get("videos").get("results").size() != 0) {
                for (JNode video : resultSeason.get("videos").get("results")) {
                    if (video.get("site").toString().equals("YouTube")) {
                        m.setTrailer(video.get("key").toString());
                        break;
                    }
                }
            } else if (resultTv.get("videos").get("results").size() != 0) {
                for (JNode video : resultTv.get("videos").get("results")) {
                    if (video.get("site").toString().equals("YouTube")) {
                        m.setTrailer(video.get("key").toString());
                        break;
                    }
                }
            }

            if (!resultSeason.get("poster_path").toString().equals("null")) {
                m.setPosterLink("http://image.tmdb.org/t/p/w185" + resultSeason.get("poster_path").toString().replace("\\", ""));
            } else if (!resultTv.get("poster_path").toString().equals("null")) {
                m.setPosterLink("http://image.tmdb.org/t/p/w185" + resultTv.get("poster_path").toString().replace("\\", ""));
            }

            if (!resultEpisode.get("still_path").toString().equals("null")) {
                m.setBigPosterLink("http://image.tmdb.org/t/p/w600" + resultEpisode.get("still_path").toString().replace("\\", ""));
            }else if(!resultTv.get("backdrop_path").toString().equals("null")){
                m.setBigPosterLink("http://image.tmdb.org/t/p/w600" + resultTv.get("backdrop_path").toString().replace("\\", ""));
            }else{
                m.getPosterLink();
            }

            m.setType("tv");
            m.setPostDate(new Timestamp(new Date().getTime()));
            m.setLastUpdate(new Timestamp(new Date().getTime()));
            m.setPublish(false);
            m.setReleaseYear(Integer.parseInt(resultEpisode.get("air_date").toString().split("-")[0]));
            m.setViews(0);
            m.setSeason(Integer.parseInt(season));
            m.setEpisode(Integer.parseInt(episode));
            m.setUser(new AuthenticationServiceImpl().getUserCredential().getUser());
            StreamSource ss = new StreamSource("", "direct");
            StreamLink sl = new StreamLink(ss, new Quality(""), new Resolution(""), "", false);
            m.getStreamLinks().add(sl);
            for (JNode country : resultTv.get("origin_country")) {
                Country c = Ebean.find(Country.class).where().eq("countryCode", country.toString()).findUnique();
                if (c == null) {
                    c = new Country(country.toString(), country.toString());
                    Ebean.save(c);
                }
                m.getCountries().add(c);
            }

            for (JNode tag : resultTv.get("keywords").get("results")) {
                String tagName = tag.get("name").toString();
                Tag t = Ebean.find(Tag.class).where().eq("tagName", tagName).findUnique();
                if (t == null) {
                    t = new Tag(tagName);
                    Ebean.save(t);
                }
                m.getTags().add(t);
            }

            StringBuilder sb4 = new StringBuilder();
            sb4.append(END_POINT);
            sb4.append("tv/").append(tmdbId);
            sb4.append("/season/").append(season);
            sb4.append("/episode/").append(episode);
            sb4.append("/external_ids");
            sb4.append("?api_key=").append(API_KEY);
            UserAgent userAgent4 = new UserAgent();

            userAgent4.sendGET(sb4.toString(),
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            JNode resultImdb = userAgent4.json;
            userAgent4.close();

            String imdb = resultImdb.get("imdb_id").toString();
            if (!imdb.equals("null")) {
                m.setImdbId(imdb);
                OmdbApi omdb = new OmdbApi();
                JNode omdbResult = omdb.getDetailMovie(m.getImdbId());
                m.setRating(omdbResult.get("imdbRating").toString());
                m.setDuration(omdbResult.get("Runtime").toString().replace(" min", ""));

                for (String actor : omdbResult.get("Actors").toString().split(",")) {
                    Actor cast = Ebean.find(Actor.class).where().eq("actorName", actor.trim()).findUnique();
                    if (cast == null) {
                        cast = new Actor(actor.trim());
                        Ebean.save(cast);
                    }
                    m.getCast().add(cast);
                }

                for (String genre : omdbResult.get("Genre").toString().split(",")) {
                    Genre g = Ebean.find(Genre.class).where().eq("name", genre.trim()).findUnique();
                    if (g == null) {
                        g = new Genre(genre.trim());
                        Ebean.save(g);
                    }
                    m.getGenres().add(g);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

}
