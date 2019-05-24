package uk.ac.imperial.cisbio.cell_metric.output;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;

import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import uk.ac.imperial.cisbio.cell_metric.om.Cell_Nucleus;
import uk.ac.imperial.cisbio.cell_metric.om.Intra_Cell_Distance;
import uk.ac.imperial.cisbio.cell_metric.om.Nucleus_Collection;
import uk.ac.imperial.cisbio.cell_metric.utilities.ExcelWriter;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.NucleusTool;
/************************************************************/
/* Nucleus_Spreadsheet										*/
/* Creates spreadsheet about cell nucleus					*/
/************************************************************/

public class Nucleus_SpreadSheet {

		/************************************************************/
		/* INSTANCE VARIABLES										*/
		/************************************************************/
		protected String fileName;
		protected ExcelWriter excelWriter;
		protected NucleusTool nucleusTool;
		
		private static final double CELL_DEFAULT_HEIGHT = 17;
		private static final double CELL_DEFAULT_WIDTH = 64;
		
		/************************************************************/
		/* CLASS CONSTRUCTOR										*/
		/************************************************************/
		public Nucleus_SpreadSheet(String fileName, NucleusTool tool){
			this.fileName=fileName;
			this.excelWriter=new ExcelWriter(fileName);	
			this.nucleusTool=tool;
		}
		
		
		/************************************************************/
		/* CREATE SPREADSHEET METHOD								*/
		/************************************************************/
		public void makeNucleusSheet(){
			try{
				
				WritableSheet sheet=this.excelWriter.getNewSheet("Nucleus Image");	
				this.excelWriter.addHeader(sheet, 0, 0, nucleusTool.getImageDirectory(),true,ExcelWriter.NO_CORNER);
				
				BufferedImage oim=nucleusTool.getOriginalImage();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();   
				ImageIO.write(oim, "PNG", baos); 
				sheet.addImage(new WritableImage(0,10,oim.getWidth() / CELL_DEFAULT_WIDTH, oim.getHeight() / CELL_DEFAULT_HEIGHT,baos.toByteArray()));
				
				
				BufferedImage im=nucleusTool.getNucleusCollection().getNucleusImage(true, null);
				baos = new ByteArrayOutputStream();   
				ImageIO.write(im, "PNG", baos); 
				sheet.addImage(new WritableImage((oim.getWidth() / CELL_DEFAULT_WIDTH)+2,10,im.getWidth() / CELL_DEFAULT_WIDTH, im.getHeight() / CELL_DEFAULT_HEIGHT,baos.toByteArray()));
				
				//Nucleus_Collection nc = this.nucleusTool.getNucleusCollection();
				Nucleus_Collection nc = this.nucleusTool.getClickedOnObjects();
				Iterator<Cell_Nucleus> itr = nc.iterator();
			    while(itr.hasNext()) {
			    	Cell_Nucleus el = (Cell_Nucleus)itr.next();
			    	sheet=this.excelWriter.getNewSheet("# "+el.getNumber());
			    	
			    	this.excelWriter.addHeader(sheet, 0, 0, "Cell Number",false,ExcelWriter.NO_CORNER);
			    	this.excelWriter.addHeader(sheet, 1, 0, ""+el.getNumber(),false,ExcelWriter.NO_CORNER);
			    	this.excelWriter.addHeader(sheet, 0, 1, "Nucleus Area",false,ExcelWriter.NO_CORNER);
			    	this.excelWriter.addHeader(sheet, 1, 1, ""+el.getArea(),false,ExcelWriter.NO_CORNER);
			    	this.excelWriter.addHeader(sheet, 0, 2, "Cell Area",false,ExcelWriter.NO_CORNER);
			    	int cellArea=0;
			    	if(el.getMyMembrane()!=null)cellArea=el.getMyMembrane().getArea();
			    	this.excelWriter.addHeader(sheet, 1, 2, ""+cellArea,false,ExcelWriter.NO_CORNER);
			    	this.excelWriter.addHeader(sheet, 0, 3, "Cell Circumference",false,ExcelWriter.NO_CORNER);
			    	int cellCirc=0;
			    	if(el.getMyMembrane()!=null)cellCirc=el.getMyMembrane().getCircumference();
			    	this.excelWriter.addHeader(sheet, 1, 3, ""+cellCirc,false,ExcelWriter.NO_CORNER);
		 	    	
			    	int r=5;
			    	
			    	this.excelWriter.addHeader(sheet, 0, r, "From Cell",false,ExcelWriter.BOTTOM);
			    	this.excelWriter.addHeader(sheet, 1, r, "(x,y)",false,ExcelWriter.BOTTOM);
			    	this.excelWriter.addHeader(sheet, 2, r, "To Cell",false,ExcelWriter.BOTTOM);
			    	this.excelWriter.addHeader(sheet, 3, r, "(x,y)",false,ExcelWriter.BOTTOM);
			    	this.excelWriter.addHeader(sheet, 4, r, "Distance",false,ExcelWriter.BOTTOM);
			    	
			    	el.calculateIntraCellDistances();
			    	r++;
			    	int c=1;
			    	Iterator<Intra_Cell_Distance> cditr = el.getIntraCellularDistances().iterator();
			 	    while(cditr.hasNext()) {
			 	    	Intra_Cell_Distance d=(Intra_Cell_Distance)cditr.next();
			 	    	this.excelWriter.addNumber(sheet, 0, r,  d.getFromCell().getNumber(),0);
			 	    	Point p=d.getFromCell().getGeometricMean();
			 	    	this.excelWriter.addLabel(sheet, 1, r,  "("+(int)p.getX()+","+(int)p.getY()+")",0);
			 	    	this.excelWriter.addNumber(sheet, 2, r, d.getToCell().getNumber(),0);
			 	    	p=d.getToCell().getGeometricMean();
			 	    	this.excelWriter.addLabel(sheet, 3, r,  "("+(int)p.getX()+","+(int)p.getY()+")",0);
			 	    	this.excelWriter.addNumber(sheet, 4, r, d.getDistance(),0);
			 	    	r++;c++;
			 	    	if(c>this.nucleusTool.getNumIntraCells()){
			 	    		oim=el.getDistanceImage(this.nucleusTool);
							baos = new ByteArrayOutputStream();   
							ImageIO.write(oim, "PNG", baos); 
							sheet.addImage(new WritableImage(6,0,oim.getWidth() / CELL_DEFAULT_WIDTH, oim.getHeight() / CELL_DEFAULT_HEIGHT,baos.toByteArray()));
			 	    		
							BufferedImage image=el.getCombinedImage(true, null);
							baos = new ByteArrayOutputStream();   
							ImageIO.write(image, "PNG", baos); 
							sheet.addImage(new WritableImage((oim.getWidth() / CELL_DEFAULT_WIDTH)+8,0,im.getWidth() / CELL_DEFAULT_WIDTH, im.getHeight() / CELL_DEFAULT_HEIGHT,baos.toByteArray()));
							
			 	    		break;
			 	    	}
			 	    }
			    }
				
				this.excelWriter.writeFile();
				this.nucleusTool.getTool().setTextArea("Written Excel Processed Results File to "+this.fileName+"\n");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

}
