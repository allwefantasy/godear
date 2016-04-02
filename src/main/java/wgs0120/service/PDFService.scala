package wgs0120.service

import com.google.inject.{Inject, Singleton}
import net.csdn.common.env.Environment
import com.itextpdf.text.{FontFactoryImp, Paragraph, Font, Document}
import com.itextpdf.text.pdf.PdfWriter
import java.io.{ByteArrayInputStream, InputStream, FileOutputStream}
import wgs0120.model.Content
import scala.collection.JavaConversions._
import com.itextpdf.tool.xml.{XMLWorkerHelper, XMLWorkerFontProvider}


/**
 * 1/14/14 WilliamZhu(allwefantasy@gmail.com)
 */
@Singleton
class PDFService @Inject()(env: Environment, encryption: Encryption) {

  def pdfHome = env.dataFile().getPath + "/" + "pdf/"

  def toPDF(contents: java.util.List[Content]): Unit = {
    contents.foreach(con => toPDF(
      con.attr("title", classOf[String]),
      con.attr("body", classOf[String]),
      con.attr("link", classOf[String])))
  }

  def fromHTMLToPDF(contents: java.util.List[Content]): Unit = {
    contents.foreach(con => fromHTMLToPDF(
      con.attr("title", classOf[String]),
      con.attr("body", classOf[String]),
      con.attr("link", classOf[String])))
  }

  def fromHTMLToPDF(title: String, body: String, link: String) = {
    val pdfFile = pdfHome + encryption.encrypt(link) + ".pdf"
    val doc = new Document()
    doc.open()
    val writer = PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));
    val is: InputStream = new ByteArrayInputStream(toHTML(title, body, link).getBytes());
    XMLWorkerHelper.getInstance().parseXHtml(writer, doc, is);
    doc.close()
  }

  def toPDF(title: String, body: String, link: String): Unit = {
    val pdfFile = pdfHome + encryption.encrypt(link) + ".pdf"

    val doc = new Document()
    val writer = PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));
    doc.open()

    doc.add(new Paragraph(title, getFont("宋体")))
    doc.add(new Paragraph("", getFont("宋体")))
    doc.add(new Paragraph(body, getFont("宋体")))
    doc.close()
    writer.close()

  }

  def getFont(fontName: String): Font = {
    val fp: FontFactoryImp = new XMLWorkerFontProvider()
    fp.registerDirectories()
    fp.getFont(fontName);
  }


  def toHTML(title: String, body: String, link: String) = {
    val content =
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
          <title>
            {title}
          </title>
        </head>
        <body>
          {body}
        </body>
      </html>

    """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">""" + content.text
  }
}
