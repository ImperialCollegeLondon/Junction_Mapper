package uk.ac.imperial.cisbio.cell_metric.output;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import uk.ac.imperial.cisbio.cell_metric.om.Cell_Membrane;
import uk.ac.imperial.cisbio.cell_metric.om.Membrane_Collection;
import uk.ac.imperial.cisbio.cell_metric.utilities.ImageUtilities;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MembraneTool;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MultiChannelImage;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

/************************************************************/
/* PDFWriter												*/
/* Class to make PDF Document								*/
/************************************************************/
public class PDFWriter {


	/************************************************************/
	/* STATIC METHODS - PUBLIC									*/
	/************************************************************/
	
	public static void writeDocument(MembraneTool tool){
		Document document	 = new Document(PageSize.A4.rotate());
		try {
			String filePath = tool.getMultiChannelImage().getCellMetricTool().getOutputDirectory();
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy_MM_dd_HHmm");
			java.util.Date date = new java.util.Date();
			String time=formatter.format(date);
			String name=tool.getMultiChannelImage().getName();
			
			tool.getMultiChannelImage().getCellMetricTool().setTextArea("Writing PDF file and montage to "+filePath);
			PdfWriter writer = PdfWriter.getInstance(document, (new FileOutputStream(filePath+"/"+name+"_"+time+".pdf")));
			document.open();	
			
			Paragraph p1 = new Paragraph(
					"Braga Laboratory         Experiment Description :: "+tool.getMultiChannelImage().getExperimentDescription(), FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, Color.BLACK));
					
			
		
			
            Image tiff1;
			try {
				BufferedImage image1=tool.getOriginalImage();
				File ifile = new File(filePath+"/original.jpg");		
				image1=resize(image1,600);
		        ImageIO.write(image1, "jpg", ifile);
		        tiff1 = Image.getInstance(filePath+"/original.jpg");
		        document.add(p1);
	            document.add(tiff1);
		        
		        Membrane_Collection mc = tool.getMembraneCollection();
		        
		        image1=mc.getMembraneImage(true, tool);
				ifile = new File(filePath+"/cells.jpg");		
				image1=resize(image1,600);
		        ImageIO.write(image1, "jpg", ifile);
		        tiff1 = Image.getInstance(filePath+"/cells.jpg");
		        document.add(tiff1);
		        
		     
				
			    for(int i=mc.size()-1;i>=0;i--){
			    	
			    	Cell_Membrane el = (Cell_Membrane)mc.get(i);
			    	if(el.getCorners().size()>0){
			    		 image1=el.getCornerImage(tool, 1);
						 ifile = new File(filePath+"/cell_actin"+el.getNumber()+".jpg");		
						 image1=resize(image1,600);
					     ImageIO.write(image1, "jpg", ifile);
					     tiff1 = Image.getInstance(filePath+"/cell_actin"+el.getNumber()+".jpg");
					     document.add(tiff1);
					     
					     image1=el.getCornerImage(tool, 0);
						 ifile = new File(filePath+"/cell_measurement"+el.getNumber()+".jpg");		
						 image1=resize(image1,600);
					     ImageIO.write(image1, "jpg", ifile);
					     tiff1 = Image.getInstance(filePath+"/cell_measurement"+el.getNumber()+".jpg");
					     document.add(tiff1);
			    		
			    	}
			    	
			    }
		        
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
            
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		finally{
			document.close();
			
		}
		
	}
	
	/************************************************************/
	/* STATIC METHODS - PRIVATE									*/
	/************************************************************/
	
	private static BufferedImage resize(BufferedImage image1,int width){
		
		while(image1.getWidth()>width)
			image1=ImageUtilities.reSizeImage(image1, (int)(image1.getWidth()*0.9), (int)(image1.getHeight()*0.9));
		
		return image1;
	}
}




