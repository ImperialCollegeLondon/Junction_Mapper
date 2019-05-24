package uk.ac.imperial.cisbio.cell_metric.utilities;



import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.print.*;
import javax.media.jai.*;
import com.sun.media.jai.codec.*;


/************************************************************/
/* ImagePrinter												*/
/* Print image on a PDF document 							*/
/* Code taken from the web									*/
/************************************************************/

public class ImagePrinter implements Printable {
	
	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
   protected RenderedImage renderedImage;
   protected int imageWidth, imageHeight;
   protected Point printLoc = new Point(0,0);
   
   	/************************************************************/
	/* UTILITY METHODS											*/
	/************************************************************/
   public void setPrintLocation(Point d) {
       printLoc = d;
   }

   public Point getPrintLocation() {
      return printLoc;
   }

   public void loadAndPrint(String filename){
      RenderedOp renderedOp = JAI.create("fileload", filename);
      renderedImage = renderedOp.createInstance();
      //Before printing, scale the image appropriately.
      imageWidth = renderedImage.getWidth();
      imageHeight = renderedImage.getHeight();
      //If the image is bigger than the page, you may to scale 
      //it to fit the paper.
      //Include your code here to scale the image.
 
      //Finally, print it
      print();
   }


   protected void print() {
      PrinterJob pj = PrinterJob.getPrinterJob();
      pj.setPrintable(this);
      //Include this statement if you need the print dialog
      //pj.printDialog();
      try{
          pj.print();
      }catch(Exception e){System.out.println(e);}
   }


   public int print(Graphics g, PageFormat f, int pageIndex){
       if(pageIndex >= 1) return Printable.NO_SUCH_PAGE;
       Graphics2D g2d = (Graphics2D) g;
       g2d.translate(f.getImageableX(), f.getImageableY());
       if(renderedImage != null){
          printImage(g2d, renderedImage);
          return Printable.PAGE_EXISTS;
       } else  return Printable.NO_SUCH_PAGE;
    }

    public void printImage(Graphics2D g2d, RenderedImage image){
       if((image == null)|| (g2d == null)) return;
       int x = printLoc.x;
       int y = printLoc.y;
       AffineTransform at = new AffineTransform();
       at.translate(x,y);
       g2d.drawRenderedImage(image,at);
   }
}


