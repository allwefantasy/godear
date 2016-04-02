package wgs0120.controller.http;

import com.google.inject.Inject;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.ApplicationController;
import wgs0120.model.Bookmarker;
import wgs0120.service.Encryption;

import static net.csdn.modules.http.RestRequest.Method.GET;
import static net.csdn.modules.http.RestRequest.Method.POST;

/**
 * 1/12/14 WilliamZhu(allwefantasy@gmail.com)
 */
public class UsersController extends ApplicationController {

    @At(path = "/users/create", types = GET)
    public void createUser() {
        renderHtmlWithMaster(200, FORMASTER, map("msg", param("msg")));
    }


    @At(path = "/users/create", types = POST)
    public void submitCreateUser() {
        Bookmarker bookmarker = Bookmarker.create(params());
        if (!bookmarker.valid()) {
            redirectTo("/users/create", map(
                    "msg", join(projectByMethod(bookmarker.validateResults, "getMessage"), "<br>")));
        }
        bookmarker.encryptPassword();
        bookmarker.save();
        session(Bookmarker.LOGINKEY, param("email"));
        writeLoginCookie();
        redirectTo("/feeds", map());
    }


    @At(path = "/users/login", types = GET)
    public void loginUser() {
        renderHtmlWithMaster(200, FORMASTER, map("msg", param("msg")));
    }

    @At(path = "/users/login", types = POST)
    public void submitLoginUser() {

        Bookmarker bookmarker = Bookmarker.where(map("email", param("email"))).singleFetch();
        if (isNull(bookmarker) ||
                bookmarker.attr("password", String.class).equals(encryption.encrypt(param("password")))) {
            redirectTo("/users/login", map("msg", "账号或者密码不正确"));
        }
        session(Bookmarker.LOGINKEY, param("email"));
        writeLoginCookie();
        redirectTo("/feeds", map());
    }

    private void writeLoginCookie() {
        cookie(map("name", Bookmarker.LOGINKEY,
                "value", param("email"),
                "max_age", 7 * 24 * 60 * 60,
                "path", "/"
        ));
    }

    private static String FORMASTER = "form_master.vm";

    @Inject
    private Encryption encryption;
}
