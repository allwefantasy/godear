package wgs0120.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.csdn.common.logging.CSLogger;
import net.csdn.common.logging.Loggers;
import net.csdn.common.path.Url;
import net.csdn.modules.http.RestRequest;
import net.csdn.modules.threadpool.ThreadPoolService;
import net.csdn.modules.transport.HttpTransportService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import wgs0120.model.Content;
import wgs0120.model.Feed;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static net.csdn.common.collections.WowCollections.*;

/**
 * 1/13/14 WilliamZhu(allwefantasy@gmail.com)
 */
@Singleton
public class FeedFetchService {


    private String jobName = "RSS源抓取";

    @Inject
    public FeedFetchService(HttpTransportService httpTransportService, ThreadPoolService threadPoolService) {
        this.httpTransportService = httpTransportService;
        this.threadPoolService = threadPoolService;
        this.httpTransportService.header("http.useragent", "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");

    }

    public boolean isFetch() {
        Trigger.TriggerState triggerState = QuartzManager.jobStat(jobName);
        if (triggerState == Trigger.TriggerState.NONE || triggerState == Trigger.TriggerState.COMPLETE) {
            return false;
        }
        return true;
    }

    public void cancel() {
        if (isFetch()) {
            QuartzManager.removeJob(jobName);
        }
    }

    public void time(String crontime) {
        if (isFetch()) return;
        QuartzManager.addJob(jobName, RSSFetchJob.class, crontime);

    }


    public static class RSSFetchJob implements Job {
        private CSLogger logger = Loggers.getLogger(FeedFetchService.class);
        private Map header = map("http.useragent", "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");

        public void rss(Feed feed) {
            try {
                URL feedUrl = new URL(feed.attr("rss", String.class));
                HttpTransportService.SResponse response = httpTransportService.http(new Url(feedUrl.toURI()), "", header, RestRequest.Method.GET);
                if (response == null || response.getStatus() != 200) return;
                logger.info("抓取rss:" + feedUrl.toString());
                SyndFeedInput input = new SyndFeedInput();
                InputStream stream = new ByteArrayInputStream(response.getContent().getBytes("UTF-8"));
                SyndFeed syndFeed = input.build(new XmlReader(stream));

                for (SyndEntry entry : (List<SyndEntry>) syndFeed.getEntries()) {

                    String body = join(projectByMethod(entry.getContents(), "getValue"), ",");
                    if (isEmpty(body)) body = entry.getDescription().getValue();
                    Content.nativeSqlClient().execute("insert into content(feed_id,title,guid,link,body,category,created_at) values(?,?,?,?,?,?,?)",
                            feed.id(),
                            entry.getTitle(),
                            entry.getUri(),
                            join(projectByMethod(entry.getLinks(), "getHref"), ","),
                            body,
                            join(projectByMethod(entry.getCategories(), "getName"), ","),
                            System.currentTimeMillis());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

            List<Feed> feeds = Feed.findAll();
            for (final Feed feed : feeds) {
                threadPoolService.executor(ThreadPoolService.Names.CACHED).execute(new Runnable() {
                    @Override
                    public void run() {
                        rss(feed);
                    }
                });

            }
        }
    }


    private static HttpTransportService httpTransportService;
    private static ThreadPoolService threadPoolService;
}
