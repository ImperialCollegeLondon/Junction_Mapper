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
import uk.ac.imperial.cisbio.cell_metric.om.Cell_Membrane;
import uk.ac.imperial.cisbio.cell_metric.om.Cell_Membrane_Corner;
import uk.ac.imperial.cisbio.cell_metric.om.Cell_Nucleus;
import uk.ac.imperial.cisbio.cell_metric.om.Intra_Cell_Distance;
import uk.ac.imperial.cisbio.cell_metric.om.Membrane_Collection;
import uk.ac.imperial.cisbio.cell_metric.om.Nucleus_Collection;
import uk.ac.imperial.cisbio.cell_metric.utilities.ExcelWriter;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MembraneTool;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MeasurementTool;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.NucleusTool;


public class Membrane_SpreadSheet {

	
		protected String fileName;
		protected ExcelWriter excelWriter;
		protected MembraneTool membraneTool;
		
		private static final double CELL_DEFAULT_HEIGHT = 17;
		private static final double CELL_DEFAULT_WIDTH = 64;
		
		
		public Membrane_SpreadSheet(String fileName, MembraneTool tool){
			this.fileName=fileName;
			this.excelWriter=new ExcelWriter(fileName);	
			this.membraneTool=tool;
		}
		
		
		public void makeMembraneSheet(){
			try{
				
				
				//Membrane_Collection mc = this.membraneTool.getMembraneCollection();
				//Iterator<Cell_Membrane> itr = mc.iterator();
				Cell_Membrane el = this.membraneTool.getCurrentMembrane();
			    //for(int i=mc.size()-1;i>=0;i--){
			    	
			    	
			    	//Cell_Membrane el = (Cell_Membrane)itr.next();
			    	//Cell_Membrane el = (Cell_Membrane)mc.get(i);
			    	if(el.getCorners().size()>0){
					    	WritableSheet sheet=this.excelWriter.getNewSheet("# "+el.getNumber());
					    	
					    	BufferedImage cim=el.getCornerImage(membraneTool,1);
							ByteArrayOutputStream cos = new ByteArrayOutputStream();   
							ImageIO.write(cim, "PNG", cos); 
							sheet.addImage(new WritableImage(20,0,cim.getWidth() / CELL_DEFAULT_WIDTH, cim.getHeight() / CELL_DEFAULT_HEIGHT,cos.toByteArray()));
							
							cim=el.getCornerImage(membraneTool,0);
							cos = new ByteArrayOutputStream();   
							ImageIO.write(cim, "PNG", cos); 
							sheet.addImage(new WritableImage(22 + (cim.getWidth() / CELL_DEFAULT_WIDTH),0,cim.getWidth() / CELL_DEFAULT_WIDTH, cim.getHeight() / CELL_DEFAULT_HEIGHT,cos.toByteArray()));
					    	
							
							/**cim=membraneTool.getECadherinThresholdImage();
							cos = new ByteArrayOutputStream();   
							ImageIO.write(cim, "PNG", cos); 
							sheet.addImage(new WritableImage(20 + (cim.getWidth() / CELL_DEFAULT_WIDTH),0,cim.getWidth() / CELL_DEFAULT_WIDTH, cim.getHeight() / CELL_DEFAULT_HEIGHT,cos.toByteArray()));
					    	
							
							MeasurementTool mt=((MeasurementTool)membraneTool.getMultiChannelImage().getMeasurementTool());
							cim = mt.getMeasurementThresholdImage();
							cos = new ByteArrayOutputStream();   
							ImageIO.write(cim, "PNG", cos); 
							sheet.addImage(new WritableImage(25 + (2 * (cim.getWidth() / CELL_DEFAULT_WIDTH)),0,cim.getWidth() / CELL_DEFAULT_WIDTH, cim.getHeight() / CELL_DEFAULT_HEIGHT,cos.toByteArray()));
					    	**/
							
					    	this.excelWriter.addHeader(sheet, 0, 0, "Membrane #",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addNumber(sheet, 1, 0, el.getNumber(),ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 0, 1, "Area",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addNumber(sheet, 1, 1, el.getArea(),ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 0, 2, "Circumference",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addNumber(sheet, 1, 2, el.getCircumference(),ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 0, 3, "# Corners",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addNumber(sheet, 1, 3, el.getCorners().size(),ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 0, 4, "ECadherin Threshold",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addNumber(sheet, 1, 4, el.getECadherinThresholdAtMeasurement(),ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 0, 5, "Internal ECadherin Area (pixels)",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addNumber(sheet, 1, 5, el.getInternalECadherinArea(),ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 0, 6, "Measurement Channel Threshold",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addNumber(sheet, 1, 6, el.getMeasurementThresholdAtMeasurement(),ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 0, 7, "Internal Measurement Channel Area (pixels)",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addNumber(sheet, 1, 7, el.getInternalActinArea(),ExcelWriter.NO_CORNER);
					    	
					    	int c=11;
					    	this.excelWriter.addHeader(sheet, 0, c-1, "Junction Analysis",true,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 0, c, "Corner #",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 1, c, "Paired With",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 2, c, "Interface contour",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 3, c, "Predicted interface length",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 4, c, "Interface linearity Index",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 5, c, "Fragmented Junction Length",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 6, c, "Coverage index",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 7, c, "Dilation Cycles",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 8, c, "Interface area",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 9, c, "Ecadherin Area :within  Dilated Edge Area (pixels)",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 10, c, " % Interface occupancy",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 11, c, "Summed Ecad intensity",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 12, c, "Ecadherin intensity per interface area",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 13, c, "Cluster density",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 14, c, "Junction contour",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 15, c, "Predicted junction length",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 16, c, "Junction linearity",false,ExcelWriter.NO_CORNER);
					    	
					    	c++;
					    	Iterator<Cell_Membrane_Corner> citr = el.getCorners().iterator();
							while(citr.hasNext()){
								Cell_Membrane_Corner corner=citr.next();
								
								this.excelWriter.addNumber(sheet, 0, c, corner.getNumber(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 1, c, corner.getPairedCorner().getNumber(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 2, c, corner.getDistanceToNeighbour(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 3, c, corner.getEuclidianDistanceToNeighbour(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 4, c, corner.getDistanceRatio(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 5, c, corner.getFragmentedJunctionLength(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 6, c, (corner.getFragmentedJunctionLength()/corner.getDistanceToNeighbour()),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 7, c, corner.getDilationCycles(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 8, c, corner.getDilatedEdgePixelArea(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 9, c, corner.getECadherinDilatedPixelArea(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 10, c,  (((double)corner.getECadherinDilatedPixelArea()/(double)corner.getDilatedEdgePixelArea())*100.0),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 11, c, corner.getTotalEcadherinIntensityWithinDilatedEdge(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 12, c, ((double)corner.getTotalEcadherinIntensityWithinDilatedEdge()/(double)corner.getDilatedEdgePixelArea()),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 13, c, ((double)corner.getTotalEcadherinIntensityWithinDilatedEdge()/(double)corner.getECadherinDilatedPixelArea()),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 14, c, corner.getECadherinDistanceToNeighbour(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 15, c, corner.getECadherinEuclidianDistance(),ExcelWriter.NO_CORNER);
								double dist=0.0;
								if(corner.getECadherinEuclidianDistance()>0)dist=(double)corner.getECadherinDistanceToNeighbour()/(double)corner.getECadherinEuclidianDistance();
								this.excelWriter.addNumber(sheet, 16, c, dist,ExcelWriter.NO_CORNER);
						    	
								c++;
							}
					    	
					    
							c+=5;
							this.excelWriter.addHeader(sheet, 0, c-1, "Label #2 analysis",true,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 0, c, "Corner #",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 1, c, "Paired With",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 2, c, "Interface contour",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 3, c, "Predicted interface length",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 4, c, "Interface linearity Index",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 5, c, "Fragmented Junction Length",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 6, c, "Label 2 Coverage",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 7, c, "Dilation Cycles",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 8, c, "Interface area",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 9, c, "Label 2 Area :within  Dilated Edge Area (pixels)",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 10, c, " % Label 2 Area",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 11, c, "Summed Label 2 intensity",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 12, c, "Label 2 density along junction",false,ExcelWriter.NO_CORNER);
					    	this.excelWriter.addHeader(sheet, 13, c, "Label 2 cluster density",false,ExcelWriter.NO_CORNER);
					    	
					    	c++;
					    	citr = el.getCorners().iterator();
							while(citr.hasNext()){
								Cell_Membrane_Corner corner=citr.next();
								
								this.excelWriter.addNumber(sheet, 0, c, corner.getNumber(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 1, c, corner.getPairedCorner().getNumber(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 2, c, corner.getDistanceToNeighbour(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 3, c, corner.getEuclidianDistanceToNeighbour(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 4, c, corner.getDistanceRatio(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 5, c, corner.getMeasurementDistanceToNeighbour(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 6, c, (corner.getMeasurementDistanceToNeighbour()/corner.getDistanceToNeighbour()),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 7, c, corner.getDilationCycles(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 8, c, corner.getDilatedEdgePixelArea(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 9, c, corner.getMeasurementDilatedPixelArea(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 10, c,  (((double)corner.getMeasurementDilatedPixelArea()/(double)corner.getDilatedEdgePixelArea())*100.0),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 11, c, corner.getTotalMeasurementIntensityWithinDilatedEdge(),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 12, c, ((double)corner.getTotalMeasurementIntensityWithinDilatedEdge()/(double)corner.getDilatedEdgePixelArea()),ExcelWriter.NO_CORNER);
								this.excelWriter.addNumber(sheet, 13, c, ((double)corner.getTotalMeasurementIntensityWithinDilatedEdge()/(double)corner.getMeasurementDilatedPixelArea()),ExcelWriter.NO_CORNER);
						    	c++;
							}
					    	
					    }
			    //}
			    
			    this.addOverallImages();
				
				this.excelWriter.writeFile();
				this.membraneTool.getTool().setTextArea("Written Excel Processed Results File to "+this.fileName+"\n");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		
		private void addOverallImages() throws Exception{
			WritableSheet sheet=this.excelWriter.getNewSheet("Membrane Image");	
			this.excelWriter.addHeader(sheet, 0, 0, "Base Image",true,ExcelWriter.NO_CORNER);
			this.excelWriter.addHeader(sheet, 1, 0, membraneTool.getImageDirectory(),true,ExcelWriter.NO_CORNER);
			this.excelWriter.addHeader(sheet, 0, 1, "Experimental procedure and comments",true,ExcelWriter.NO_CORNER);
			this.excelWriter.addHeader(sheet, 1, 1, membraneTool.getMultiChannelImage().getExperimentDescription(),true,ExcelWriter.NO_CORNER);
			
			
			BufferedImage oim=membraneTool.getOriginalImage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();   
			ImageIO.write(oim, "PNG", baos); 
			sheet.addImage(new WritableImage(0,10,oim.getWidth() / CELL_DEFAULT_WIDTH, oim.getHeight() / CELL_DEFAULT_HEIGHT,baos.toByteArray()));
			
			
			BufferedImage im= membraneTool.getMembraneCollection().getMembraneImage(true, membraneTool);
			baos = new ByteArrayOutputStream();   
			ImageIO.write(im, "PNG", baos); 
			sheet.addImage(new WritableImage((oim.getWidth() / CELL_DEFAULT_WIDTH)+2,10,(im.getWidth() / CELL_DEFAULT_WIDTH), im.getHeight() / CELL_DEFAULT_HEIGHT,baos.toByteArray()));
			
		}
}
