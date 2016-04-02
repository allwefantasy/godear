package wgs0120.model;

import net.csdn.common.exception.AutoGeneration;
import net.csdn.jpa.association.Association;
import net.csdn.jpa.model.Model;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * 1/11/14 WilliamZhu(allwefantasy@gmail.com)
 */
public class BookmarkFeed extends Model {
    @ManyToOne
    private Bookmarker bookmarker;

    @ManyToOne
    private Feed feed;

    public Association bookmarker() {
        throw new AutoGeneration();
    }

    public Association feed() {
        throw new AutoGeneration();
    }
}
