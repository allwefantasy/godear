package wgs0120.model;

import net.csdn.common.exception.AutoGeneration;
import net.csdn.jpa.association.Association;
import net.csdn.jpa.model.Model;

import javax.persistence.OneToMany;
import java.util.List;

import static net.csdn.common.collections.WowCollections.list;

/**
 * 1/11/14 WilliamZhu(allwefantasy@gmail.com)
 */
public class Feed extends Model {
    @OneToMany
    private List<BookmarkFeed> bookmarkFeeds = list();
    @OneToMany
    private List<Content> contents = list();

    public Association bookmarkFeeds() {
        throw new AutoGeneration();
    }

    public Association contents() {
        throw new AutoGeneration();
    }
}
