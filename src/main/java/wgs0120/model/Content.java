package wgs0120.model;

import net.csdn.common.exception.AutoGeneration;
import net.csdn.jpa.association.Association;
import net.csdn.jpa.model.Model;

import javax.persistence.ManyToOne;

/**
 * 1/13/14 WilliamZhu(allwefantasy@gmail.com)
 */
public class Content extends Model {

    @ManyToOne
    private Feed feed;

    public Association feed() {
        throw new AutoGeneration();
    }
}
