package wgs0120.model;

import net.csdn.common.exception.AutoGeneration;
import net.csdn.jpa.association.Association;
import net.csdn.jpa.model.Model;
import wgs0120.service.Encryption;

import javax.persistence.OneToMany;
import java.util.List;

import static net.csdn.common.collections.WowCollections.list;
import static net.csdn.common.collections.WowCollections.map;
import static net.csdn.validate.ValidateHelper.presence;
import static net.csdn.validate.ValidateHelper.uniqueness;

/**
 * 1/11/14 WilliamZhu(allwefantasy@gmail.com)
 */
public class Bookmarker extends Model {

    static {
        validate("email", map(
                presence, map("message", "邮箱字段不能为空"),
                uniqueness, map("message", "邮箱已经存在"))
        );

        validate("password", map(presence, map("message", "邮箱字段不能为空")));
    }

    @OneToMany
    private List<BookmarkFeed> bookmarkFeeds = list();

    public Association bookmarkFeeds() {
        throw new AutoGeneration();
    }

    public static Bookmarker findByEmail(String email) {
        return where(map("email", email)).singleFetch();
    }

    public boolean isAdmin() {
        return attr("isAdmin", Integer.class) == 1;
    }

    public Bookmarker encryptPassword() {
        Encryption encryption = findService(Encryption.class);
        String password = new String(encryption.encrypt(attr("password", String.class)));
        attr("password", password);
        return this;
    }

    public final static String LOGINKEY = "wgs0120";
}
