package test.wgs0120;

import net.csdn.junit.BaseServiceTest;
import org.junit.Test;
import wgs0120.model.Content;
import wgs0120.service.PDFService;

/**
 * 1/14/14 WilliamZhu(allwefantasy@gmail.com)
 */
public class PDFTest extends BaseServiceTest {
    @Test
    public void test() {
        PDFService pdfService = findService(PDFService.class);
        pdfService.toPDF(Content.limit(20).fetch());
    }
}
