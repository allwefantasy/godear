package wgs0120;

import net.csdn.ServiceFramwork;
import net.csdn.bootstrap.Application;
import org.apache.velocity.app.Velocity;

/**
 * 1/11/14 WilliamZhu(allwefantasy@gmail.com)
 */
public class GodEar {
    public static void main(String[] args) {
        ServiceFramwork.scanService.setLoader(GodEar.class);
        Velocity.addProperty("eventhandler.include.class", "org.apache.velocity.app.event.implement.IncludeRelativePath");
        Velocity.addProperty("velocimacro.library.autoreload", true);
        Application.main(args);
    }
}
