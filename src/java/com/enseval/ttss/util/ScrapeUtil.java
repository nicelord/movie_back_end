/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asus
 */
public class ScrapeUtil {

    public static String[] QUALITIES = {"CAM",
        "HDTV",
        "HD-TV",
        "HDRip",
        "HD-Rip",
        "TS",
        "WEB-Rip",
        "WEBRip",
        "DVD-Scr",
        "DVDScr",
        "DVDRip",
        "DVD-Rip",
        "BD-Rip",
        "BDRip",
        "WEBDL",
        "WEB-DL",
        "Bluray",
        "Blu-ray"};

    public static String[] RESOLUTIONS = {"720p", "720", "1080p", "1080"};

    public static String checkQualityByLink(String url) {
        for (String s : QUALITIES) {
            if (url.toLowerCase().contains(s.toLowerCase())) {
                return s;
            }
        }
        return "unknown";
    }

    public static String checkResolutionyByLink(String url) {
        for (String s : RESOLUTIONS) {
            if (url.toLowerCase().contains(s.toLowerCase())) {
                return s.replace("p", "");
            }
        }
        return "unknown";

    }

    public static String convertGoStreamToDirectLink(String url) {
        String ret = "convert failed";
        try {
            String s = URLDecoder.decode(url, "UTF-8");
            for(String ss : s.split("/")){
                if(ss.endsWith("appspot.com")){
                    ret = "https://storage.googleapis.com/" + s.substring(s.indexOf(ss), s.indexOf("?")).replace("appspot.com/o/", "appspot.com/");
                }
            }
            //ret = "https://storage.googleapis.com/" + s.substring(s.indexOf("staging"), s.indexOf("?")).replace("appspot.com/o/", "appspot.com/");
        } catch (UnsupportedEncodingException ex) {
            return "convert failed";
        }
        return ret;
    }

}
