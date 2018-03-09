/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.externalApi;

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
public class OmdbApi {

    public static void main(String[] args) {
        try {
            UserAgent userAgent = new UserAgent();
            userAgent.sendGET("http://www.omdbapi.com/?s=" + URLEncoder.encode("blade runner", "UTF-8") + "&apikey=BanMePlz",
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            System.out.println(userAgent.json);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OmdbApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResponseException ex) {
            Logger.getLogger(OmdbApi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<OmdbSearch> omdbSearchMovie(String keywords, String type) {
        List<OmdbSearch> omdbResult = new ArrayList<>();
        try {

            UserAgent userAgent = new UserAgent();
            userAgent.sendGET("http://www.omdbapi.com/?s=" + URLEncoder.encode(keywords, "UTF-8") + "&type=" + type + "&apikey=26544caf",
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");

            JNode listResult = userAgent.json.get("Search");
            for (JNode result : listResult) {
                OmdbSearch os = new OmdbSearch();
                os.setTitle(result.get("Title").toString());
                os.setImdb(result.get("imdbID").toString());
                os.setPoster(result.get("Poster").toString());
                os.setType(result.get("Type").toString());
                os.setYear(result.get("Year").toString());
                omdbResult.add(os);
            }

        } catch (ResponseException | NotFound | UnsupportedEncodingException ex) {
            //Logger.getLogger(OmdbApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return omdbResult;
    }

    public JNode getDetailMovie(String imdb) {
        JNode ret = null;
        UserAgent userAgent = new UserAgent();
        try {
            userAgent.sendGET("http://www.omdbapi.com/?i=" + imdb + "&apikey=26544caf",
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36",
                    "Accept: application/json");
            ret = userAgent.json;
        } catch (Exception ex) {
            ret = null;
        }
        return ret;
    }
}
