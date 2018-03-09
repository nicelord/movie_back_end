
import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.text.PathProperties;
import com.enseval.ttss.model.Genre;
import com.enseval.ttss.model.Movie2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.avaje.agentloader.AgentLoader;
import org.zkoss.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author asus
 */
public class test {

    public static void main(String[] args) {

        test t = new test();
        t.search("un", 1080);

        

//        AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1");
//        PagedList<Movie2> paged = Ebean.find(Movie2.class)
//                .where().eq("genres.name", "action")
//                .where().eq("publish", true)
//                .order().desc("lastUpdate")
//                .findPagedList(1 - 1, 21);
//        System.out.println(paged.getTotalPageCount());
//        AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1");
//
//        PagedList<Movie2> paged = Ebean.find(Movie2.class)
//                .where().eq("publish", true)
//                .where().eq("type", "cinema")
//                .order().desc("lastUpdate")
//                .findPagedList(2, 5);
//        List<Movie2> movies = paged.getList();
//        PathProperties pathProperties = PathProperties.parse("("
//                + "movieId,"
//                + "title,"
//                + "releaseYear,"
//                + "trailer,"
//                + "posterLink,"
//                + "rating,"
//                + "lastUpdate,"
//                + "views,"
//                + "type,"
//                + "season,"
//                + "episode,"
//                + "countries((*)countryCode,countryName),"
//                + "genres((*)name)"
//                + "streamLinks((*)serverSource(serverName),quality(quality),resolution(resolution),isIframe)"
//                + ")");
//
//        JSONObject json = new JSONObject();
//        json.put("totalPage", paged.getTotalPageCount());
//        System.out.println(movies.size());
//        Genre g = Ebean.find(Genre.class).where().eq("name", "asdasd").findUnique();
//        System.out.println(g.getName());
//        int delay = 0;   // delay for 5 sec.
//        int period = 5000;  // repeat every sec.
//        Timer timer = new Timer();
//
//        timer.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                System.out.println("run..");
//            }
//        }, delay, period);
    }

    public static void search(String s, int i) {
        try {
            URL url = new URL("https://jsonmock.hackerrank.com/api/countries/search?name=" + s);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();

        } catch (ProtocolException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
