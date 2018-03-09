/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enseval.ttss.scrapper.gostream;

import com.enseval.ttss.scrapper.LongOperation;
import com.enseval.ttss.scrapper.ScrappedResult;
import com.enseval.ttss.scrapper.ScrapperVM;
import com.enseval.ttss.scrapper.gostream.Result.Source;
import com.enseval.ttss.scrapper.gostream.Result.Track;
import com.enseval.ttss.util.ScrapeUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.jaunt.Document;
import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JNode;
import com.jaunt.JauntException;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import com.jaunt.util.HandlerForText;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import static java.awt.Desktop.getDesktop;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.bind.BindUtils;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

/**
 *
 * @author asus
 */
public class GoStreamScrapper extends LongOperation {

    UserAgent userAgent;
    WebDriver driver;
    FirefoxOptions ffoptions;
    String url;
    ScrapperVM scrapperVM;

    public GoStreamScrapper(String url, ScrapperVM scrapperVM) {
        this.scrapperVM = scrapperVM;

        this.url = url;

    }

    public GoStreamScrapper() {

    }

    public static void main(String[] args) {
        GoStreamScrapper gs = new GoStreamScrapper();
        gs.search("the dark tower");
    }

    public List<String> search(String keywords) {
        List<String> searchResult = new ArrayList<>();
        userAgent = new UserAgent();
        try {
            userAgent.settings.defaultRequestHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
            userAgent.visit("https://123movieshub.to/movie/search/" + URLEncoder.encode(keywords, "UTF-8"));                        //visit a url  
            Element container = userAgent.doc.findFirst("<div class=\"movies-list movies-list-full\">");
            Elements items = container.findEvery("<div class=\"ml-item\">");
            for (Element item : items) {
                String url = item.findFirst("<a href>").getAt("href");
                searchResult.add(url);
            }
            userAgent.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    public List<String> getStreamPage(String url, String episode, boolean fixedEpTitle) {
        List<String> streamPageResult = new ArrayList<>();

        String mid = url.split("/")[4].split("-")[url.split("/")[4].split("-").length - 1];
        userAgent = new UserAgent();
        try {
            userAgent.visit("https://123movieshub.to/ajax/movie_episodes/" + mid);
            JNode j = userAgent.openJSON(userAgent.doc.innerHTML());
            String html = j.get("html").toString().replace("\\\"", "\"").replace("\\n", "").replace("\\", "").replace("https://123movieshub.to/ajax/movie_episodes/", "");

            Document d = userAgent.openContent(html);
            Elements vip = d.findEvery("<div class='le-server server-item vip'>").findEvery("<a>");
            String ep = "";
            if (!episode.isEmpty()) {
                if (fixedEpTitle) {
                    ep = episode;
                } else {
                    ep = "Episode " + (episode.length() <= 1 ? "0" + episode : episode);
                }
            }

            for (Element el : vip) {
                if (!episode.isEmpty()) {
                    if (el.getAt("title").startsWith(ep)) {
                        streamPageResult.add(url + "/watching.html?ep=" + el.getAt("data-id"));
                    }
                } else {
                    streamPageResult.add(url + "/watching.html?ep=" + el.getAt("data-id"));
                }

            }

            Elements embed = d.findEvery("<div class='le-server server-item embed'>").findEvery("<a>");
            if (!episode.isEmpty()) {
                if (fixedEpTitle) {
                    ep = episode;
                } else {
                    ep = "Episode " + (episode.length() <= 1 ? "0" + episode : episode);
                }
            }

            for (Element el : embed) {
                if (!episode.isEmpty()) {
                    if (el.getAt("title").startsWith(ep)) {
                        streamPageResult.add(url + "/watching.html?ep=" + el.getAt("data-id"));
                    }
                } else {
                    streamPageResult.add(url + "/watching.html?ep=" + el.getAt("data-id"));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return streamPageResult;
    }

    public void getServerDetail() throws InterruptedException {

        init();

        userAgent = new UserAgent();
        driver = new FirefoxDriver(ffoptions);

        driver.get(url);
        waitForAjax(driver);

        WebElement path = driver.findElement(By.tagName("body"));
        String innerhtml = path.getAttribute("innerHTML");

        try {
            userAgent.openContent(innerhtml);

            waitForAjax(driver);

            if (driver instanceof JavascriptExecutor) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                try {
                    String playlist = js.executeScript("return JSON.stringify(playlist)").toString();
                    playlist = playlist.replace("sources\":{", "sources\":[{").replace("},\"tracks", "}],\"tracks");

                    Gson gson = new Gson();
                    List<Result> list = gson.fromJson(playlist, new TypeToken<List<Result>>() {
                    }.getType());
                    for (Result result : list) {

                        for (Source source : result.getSources()) {
                            ScrappedResult sr = new ScrappedResult();
                            sr.setScrapedLink(url);
                            if (source.getFile().startsWith("https://storage.cloud.google.com")) {
                                source.setFile(source.getFile().replace("https://storage.cloud.google.com", "https://storage.googleapis.com"));
                            }
                            sr.setUrl(source.getFile().contains("apidata.googleusercontent.com") ? ScrapeUtil.convertGoStreamToDirectLink(source.getFile()) : source.getFile());
                            sr.setIframe(false);
                            activate();
                            this.scrapperVM.getScrapedResult().add(sr);
                            BindUtils.postNotifyChange(null, null, scrapperVM, "*");
                            deactivate();
                        }

                    }
                } catch (Exception e) {
                    //e.printStackTrace();

                }

                try {

                    userAgent.visit("http://123movieshub.to/ajax/movie_embed/" + url.substring(url.indexOf("=") + 1));
                    JsonObject o = new JsonParser().parse(userAgent.doc.innerText()).getAsJsonObject();

                    String src = o.get("src").getAsString();
                    if (!src.isEmpty()) {
                        ScrappedResult sre = new ScrappedResult();
                        sre.setScrapedLink(url);
                        sre.setUrl(src);
                        sre.setIframe(true);

                        activate();
                        this.scrapperVM.getScrapedResult().add(sre);
                        BindUtils.postNotifyChange(null, null, scrapperVM, "*");
                        deactivate();
                    }

                } catch (Exception e) {
                    //e.printStackTrace();

                }

            }

        } catch (Exception ex) {

        }

        driver.quit();
    }

    public static void waitForAjax(WebDriver driver) {
        new WebDriverWait(driver, 2000).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                return (Boolean) js.executeScript("return jQuery.active == 0");
            }
        });
    }

    @Override
    protected void execute() throws InterruptedException {
        getMovieInfoByToken();
//        getServerDetail();
    }

    @Override
    protected void onFinish() {
        Clients.showNotification("Task completed");
    }

    public void init() {
        System.setProperty("webdriver.gecko.driver", "D:\\geckodriver.exe");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("firefoxdriver");

        ffoptions = new FirefoxOptions();
        ffoptions.setProfile(myprofile);
        ffoptions.addArguments("-headless");
        ffoptions.addPreference("--log", "error");
    }

    public void getMovieInfoByToken() {

        String mid = this.url.split("/")[4].split("-")[this.url.split("/")[4].split("-").length - 1];
        String eid = this.url.substring(this.url.lastIndexOf("=") + 1);
        try {
            userAgent = new UserAgent();
            HandlerForText handler = new HandlerForText();
            userAgent.setHandler("application/javascript", handler);
            userAgent.sendGET("http://123movieshub.to/ajax/movie_token?eid=" + eid + "&mid=" + mid + "&_=" + System.currentTimeMillis());
            String x = handler.getContent().split(",")[0].split("'")[1];
            String y = handler.getContent().split(",")[1].split("'")[1];

            handler = new HandlerForText();
            userAgent.setHandler("text/html", handler);
            userAgent.sendGET("http://123movieshub.to/ajax/movie_sources/" + eid + "?x=" + x + "&y=" + y);
            String playlist = handler.getContent().replace("sources\":{", "sources\":[{").replace("},\"tracks", "}],\"tracks").replace("\\", "");
            Gson gson = new Gson();
            List<Result> list = gson.fromJson(userAgent.openJSON(playlist).get("playlist").toString(), new TypeToken<List<Result>>() {
            }.getType());
            for (Result result : list) {

                for (Source source : result.getSources()) {
                    ScrappedResult sr = new ScrappedResult();
                    sr.setScrapedLink(url);
                    if (source.getFile().startsWith("https://storage.cloud.google.com")) {
                        source.setFile(source.getFile().replace("https://storage.cloud.google.com", "https://storage.googleapis.com"));
                    }
                    sr.setUrl(source.getFile().contains("apidata.googleusercontent.com") ? ScrapeUtil.convertGoStreamToDirectLink(source.getFile()) : source.getFile());
                    sr.setIframe(false);
                    activate();
                    this.scrapperVM.getScrapedResult().add(sr);
                    BindUtils.postNotifyChange(null, null, scrapperVM, "*");
                    deactivate();
                }

            }

        } catch (Exception ex) {
            // Logger.getLogger(GoStreamScrapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            userAgent = new UserAgent();
            userAgent.visit("http://123movieshub.to/ajax/movie_embed/" + url.substring(url.indexOf("=") + 1));
            JsonObject o = new JsonParser().parse(userAgent.doc.innerText()).getAsJsonObject();

            String src = o.get("src").getAsString();
            if (!src.isEmpty()) {
                ScrappedResult sre = new ScrappedResult();
                sre.setScrapedLink(url);
                sre.setUrl(src);
                sre.setIframe(true);

                activate();
                this.scrapperVM.getScrapedResult().add(sre);
                BindUtils.postNotifyChange(null, null, scrapperVM, "*");
                deactivate();
            }

        } catch (Exception e) {
            // e.printStackTrace();

        }
    }

}
