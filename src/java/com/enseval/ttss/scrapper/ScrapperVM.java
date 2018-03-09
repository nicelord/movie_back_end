package com.enseval.ttss.scrapper;

import com.avaje.ebean.Ebean;
import com.enseval.ttss.model.Movie2;
import com.enseval.ttss.model.Quality;
import com.enseval.ttss.model.Resolution;
import com.enseval.ttss.model.StreamLink;
import com.enseval.ttss.model.StreamSource;
import com.enseval.ttss.scrapper.gostream.GoStreamScrapper;
import java.awt.Desktop;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class ScrapperVM {

    String url;
    List<ScrappedResult> scrapedResult = new ArrayList<>();
    Movie2 movie;
    List<ScrappedResult> selectedResults = new ArrayList<>();
    List<Quality> listQuality = new ArrayList<>();
    List<Resolution> listResolution = new ArrayList<>();

    @AfterCompose
    public void initSetup(@ExecutionArgParam("movie") Movie2 movie) {
        this.movie = movie;
        this.listQuality = Ebean.find(Quality.class).orderBy("qualityId desc").findList();
        this.listResolution = Ebean.find(Resolution.class).orderBy("resolutionId desc").findList();
    }

    @Command
    @NotifyChange({"scrapedResult"})
    public void scrape() {

        for (String s : url.split("\n")) {
            GoStreamScrapper gs = new GoStreamScrapper(s, this);
            gs.start();
        }
    }

    @Command
    public void addSelectedItemsTask() {
        Map m = new HashMap();
        m.put("urls", this.selectedResults);
        m.put("scraper", this);
        Executions.createComponents("addToLinkProcessor.zul", (Component) null, m);
    }

    @Command
    public void addSelectedItemTask(@BindingParam("url") ScrappedResult url) {
        List<ScrappedResult> urlx = new ArrayList<>();
        urlx.add(url);
        Map m = new HashMap();
        m.put("urls", urlx);
        m.put("scraper", this);
        Executions.createComponents("addToLinkProcessor.zul", null, m);
    }

    @Command
    @NotifyChange({"movie", "scrapedResult"})
    public void addSelectedItemToMovie(@BindingParam("url") ScrappedResult url) {
        List<ScrappedResult> urlx = new ArrayList<>();
        urlx.add(url);
        for (ScrappedResult scrappedResult : urlx) {
            StreamLink sl = new StreamLink(
                    null,
                    scrappedResult.getQuality(),
                    scrappedResult.getResolution(),
                    scrappedResult.getUrl(),
                    scrappedResult.isIframe());
            sl.setScrapedUrl(url.getScrapedLink());
            this.movie.getStreamLinks().add(sl);
            this.scrapedResult.remove(scrappedResult);
        }
        BindUtils.postNotifyChange(null, null, this.movie, "streamLinks");

    }

    @Command
    @NotifyChange({"scrapedResult"})
    public void removeDuplicate() {
        this.scrapedResult = scrapedResult.stream().filter(distinctByKey(p -> p.getUrl())).collect(Collectors.toList());
    }
    
    @Command
    public void searchMoviePage(){
        Map m = new HashMap();
        m.put("parent", this);
        Executions.createComponents("searchGoStreamMovie.zul", null, m);
    }
    
    @GlobalCommand
    @NotifyChange({"*"})
    public void refresh(){
        
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ScrappedResult> getScrapedResult() {
        return scrapedResult;
    }

    public void setScrapedResult(List<ScrappedResult> scrapedResult) {
        this.scrapedResult = scrapedResult;
    }

    public Movie2 getMovie() {
        return movie;
    }

    public void setMovie(Movie2 movie) {
        this.movie = movie;
    }

    public List<ScrappedResult> getSelectedResults() {
        return selectedResults;
    }

    public void setSelectedResults(List<ScrappedResult> selectedResults) {
        this.selectedResults = selectedResults;
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

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
