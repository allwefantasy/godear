package wgs0120.controller.http;

import com.google.inject.Inject;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.ApplicationController;
import org.quartz.CronExpression;
import wgs0120.model.BookmarkFeed;
import wgs0120.model.Bookmarker;
import wgs0120.model.Feed;
import wgs0120.service.FeedFetchService;

import java.util.List;

import static net.csdn.filter.FilterHelper.BeforeFilter.except;
import static net.csdn.filter.FilterHelper.BeforeFilter.only;
import static net.csdn.modules.http.RestRequest.Method.GET;
import static net.csdn.modules.http.RestRequest.Method.POST;

/**
 * 1/11/14 WilliamZhu(allwefantasy@gmail.com)
 */
public class FeedsController extends ApplicationController {

    static {
        beforeFilter("paginate", WowCollections.map(only, WowCollections.list("feeds", "myFeed")));
        beforeFilter("login", WowCollections.map(except, WowCollections.list("feeds")));
        beforeFilter("findUser", WowCollections.map(except, WowCollections.list()));
    }

    @At(path = "/feeds", types = GET)
    public void feeds() {
        paginate.totalItems(paramAsInt("totalItems", (int) Feed.count()));
        List<Feed> feedList = Feed
                .order("rank desc,createdAt desc")
                .offset((Integer) paginate.pageCalc()._1())
                .limit(paramAsInt("pageSize", 15))
                .fetch();
        renderHtmlWithMaster(200, "master.vm", map("feeds", feedList));
    }

    Bookmarker bookmarker;

    @At(path = "/feeds/{id}/subscribe", types = GET)
    public void subscribeFeed() {
        Feed feed = Feed.findById(paramAsInt("id"));
        BookmarkFeed bookmarkFeed = bookmarker.bookmarkFeeds().where(map("id", feed.id())).singleFetch();
        if (isNull(bookmarkFeed)) {
            bookmarkFeed = BookmarkFeed.create(map("createdAt", System.currentTimeMillis()));
            bookmarkFeed.bookmarker().set(bookmarker);
            bookmarkFeed.feed().set(feed);
            bookmarkFeed.save();
        }
        redirectTo("/feeds", map());
    }

    @At(path = "/feeds/my", types = GET)
    public void myFeed() {
        paginate.totalItems(paramAsInt("totalItems", (int) bookmarker.bookmarkFeeds().count("count(bookmarkfeed)")));
        List<BookmarkFeed> bookmarkFeeds = bookmarker.bookmarkFeeds().order("createdAt desc")
                .offset((Integer) paginate.pageCalc()._1())
                .limit(paramAsInt("pageSize", 15))
                .fetch();
        List<Feed> feedList = list();
        for (BookmarkFeed bookmarkFeed : bookmarkFeeds) {
            feedList.addAll(bookmarkFeed.feed().fetch());
        }
        renderHtmlWithMaster(200, "master.vm", map("feeds", feedList, "template", "/feeds/feeds.vm"));
    }

    @At(path = "/feeds/{id}/cancel", types = GET)
    public void cancelFeed() {
        BookmarkFeed bookmarkFeed = bookmarker.bookmarkFeeds().where(map("id", paramAsInt("id"))).singleFetch();
        if (!isNull(bookmarkFeed)) {
            bookmarkFeed.delete();
        }
        redirectTo("/feeds", map());
    }

    @At(path = "/feeds/create", types = POST)
    public void saveFeed() {
        if (!bookmarker.isAdmin()) redirectTo("/feeds", map());
        Feed feed = Feed.create(params());
        feed.save();
        redirectTo("/feeds", map());
    }

    @At(path = "/feeds/create", types = GET)
    public void createFeed() {
        if (!bookmarker.isAdmin()) redirectTo("/feeds", map());
        renderHtmlWithMaster(200, "admin_master.vm", map());
    }

    @At(path = "/fetch/rss", types = GET)
    public void fetchRss() {
        if (!bookmarker.isAdmin()) redirectTo("/feeds", map());
        renderHtmlWithMaster(200, "admin_master.vm", map("msg", feedFetchService.isFetch() ? "正在执行[<a href=\"/fetch/rss/cancel\">取消</a>]" : "还没有执行"));
    }

    @At(path = "/fetch/rss", types = POST)
    public void submitFetchRss() {
        if (!bookmarker.isAdmin()) redirectTo("/feeds", map());
        if (!CronExpression.isValidExpression(param("time"))) {
            renderHtmlWithMaster(200, "admin_master.vm", map("msg", "时间格式错误",
                    "template", "/feeds/fetch_rss.vm"
            ));
        }
        feedFetchService.time(param("time"));
        renderHtmlWithMaster(200, "admin_master.vm", map("msg",
                feedFetchService.isFetch() ? "正在执行[<a href=\"/fetch/rss/cancel\">取消</a>]" : "还没有执行",
                "template", "/feeds/fetch_rss.vm"
        ));
    }

    @At(path = "/fetch/rss/cancel", types = GET)
    public void cancelFetchRss() {
        if (!bookmarker.isAdmin()) redirectTo("/feeds", map());
        feedFetchService.cancel();
        redirectTo("/fetch/rss", map());
    }


    private void paginate() {
        this.paginate = new Paginate(paramAsInt("page", 1), paramAsInt("pageSize", 15));
    }

    private void findUser() {
        String email = cookie(Bookmarker.LOGINKEY);
        if (isNull(session(Bookmarker.LOGINKEY))) {
            session(Bookmarker.LOGINKEY, email);
        }
        bookmarker = Bookmarker.findByEmail((String) session(Bookmarker.LOGINKEY));
    }

    private void login() {
        if (isNull(cookie(Bookmarker.LOGINKEY)) && isNull(session(Bookmarker.LOGINKEY))) {
            redirectTo("/users/login", map());
        }
    }

    private Paginate paginate;
    @Inject
    private FeedFetchService feedFetchService;
}
