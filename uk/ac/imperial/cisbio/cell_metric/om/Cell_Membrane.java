package uk.ac.imperial.cisbio.cell_metric.om;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import uk.ac.imperial.cisbio.cell_metric.utilities.ImageUtilities;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MembraneTool;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.NucleusTool;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MeasurementTool;

	/************************************************************/
	/* CELL MEMBRANE CORNER										*/
	/* Represents a cell 										*/
	/************************************************************/

public class Cell_Membrane {

	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	ArrayList<Point> pixels = new ArrayList<Point>(); 		//pixel points in this membrane
	ArrayList<Point> edgePixels = new ArrayList<Point>(); 	//edge pixel points in this membrane
	private int number=0;									//id number in collection of this membrane
	private Color colour;									//colour of this membrane on screen
	protected Membrane_Collection collection;				//collection of which this membrane is a part
	private Cell_Nucleus myNucleus;							//nucleus object associated with this membrane
	ArrayList<Cell_Membrane_Corner> corners = new ArrayList<Cell_Membrane_Corner>(); 	//corner pixel points in this membrane
	ArrayList<ChainCodeElement> chainCode = new ArrayList<ChainCodeElement>(); 	//chainCode
	ArrayList <Point> cytoPlasmECadherin = new ArrayList<Point>();
	ArrayList <Point> edgeECadherin = new ArrayList<Point>();
	ArrayList <Point> cytoPlasmActin = new ArrayList<Point>();
	ArrayList <Point> edgeActin = new ArrayList<Point>();
	protected BufferedImage originalImage;
	private int eCadherinThresholdAtMeasurement;
	private int measurementThresholdAtMeasurement;
	
	private static int CORNER_SIZE=3;
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public Cell_Membrane() {
		// TODO Auto-generated constructor stub
	}
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/
	public void addPoint(Point p){
		if(!ImageUtilities.isInList(pixels, p))pixels.add(p);
	}
	
	public void addEdgePoint(Point p){
		if(!ImageUtilities.isInList(edgePixels, p))edgePixels.add(p);
	}
	
	public void setPointSet(ArrayList<Point> points){
		this.pixels=points;
	}
	
	public void setECadherinThresholdAtMeasurement(int n){
		this.eCadherinThresholdAtMeasurement=n;
	}
	
	public int getECadherinThresholdAtMeasurement(){
		return this.eCadherinThresholdAtMeasurement;
	}
	
	public void setMeasurementThresholdAtMeasurement(int n){
		this.measurementThresholdAtMeasurement=n;
	}
	
	public int getMeasurementThresholdAtMeasurement(){
		return this.measurementThresholdAtMeasurement;
	}
	
	public int getNumber(){
		return this.number;
	}
	
	public void setNumber(int n){
		this.number=n;
	}
	
	public void setColour(Color c){
		this.colour=c;
	}
	
	public Color getColour(){
		return this.colour;
	}
	
	public void setCollection(Membrane_Collection c){
		this.collection=c;
	}
	
	public String getChainCode(){
		String str="";
		for(int i=0;i<this.chainCode.size();i++)
			str+=((ChainCodeElement)this.chainCode.get(i)).getValue();
		return str;
	}
	
	
	public void setNucleus(Cell_Nucleus n){
		this.myNucleus=n;
	}
	
	
	public void setOriginalImage(BufferedImage im){
		this.originalImage=im;
	}
	public ArrayList<Cell_Membrane_Corner> getCorners(){
		return this.corners;
	}
	
	public ArrayList<Point>getEdgePixels(){
		return this.edgePixels;
	}
	
	/**
	 *  Construct ordered edge map of edge pixels
	 * @param mt
	 */
	
	public void sortEdgePixels(MembraneTool mt){
		ArrayList<Point> newEdge = new ArrayList<Point>();
		
		Point currentPixel=this.edgePixels.get(0);
		this.removePoint(this.edgePixels,currentPixel);
		newEdge.add(currentPixel);
		int dir=7; boolean done=false;
		
		while(!done){
			done=true;
			int direction=-1;
			//System.out.println("********************");
			for(int i=dir;i<=dir+8;i++){
				direction=i%8;
				Point p=isEdgePixel(currentPixel,direction);
				//System.out.print(""+direction+" : ");
				if(p!=null){
					newEdge.add(p);
					this.removePoint(this.edgePixels,p);
					currentPixel=p;
					if(direction%2==0)dir=(direction+7)%8;
					else dir=(direction+6)%8;
					done=false;
				}
				if(!done){
					//System.out.println("***********************");
					break;
				}
			}
		}
		
		this.edgePixels=newEdge;
		
	}
	
	/**
	 * Is pixel part of an edge
	 * @param p
	 * @param dir
	 * @return
	 */
	
	private Point isEdgePixel(Point p, int dir){
		Point px;
		if(dir==0){
			px=new Point(p.x+1,p.y);
			if(this.isPartofEdge(px))return px;
		}
		else if(dir==1){
			px=new Point(p.x+1,p.y-1);
			if(this.isPartofEdge(px))return px;
		}
		else if(dir==2){
			px=new Point(p.x,p.y-1);
			if(this.isPartofEdge(px))return px;
		}
		else if(dir==3){
			px=new Point(p.x-1,p.y-1);
			if(this.isPartofEdge(px))return px;
		}
		else if(dir==4){
			px=new Point(p.x-1,p.y);
			if(this.isPartofEdge(px))return px;
		}
		else if(dir==5){
			px=new Point(p.x-1,p.y+1);
			if(this.isPartofEdge(px))return px;
		}
		else if(dir==6){
			px=new Point(p.x,p.y+1);
			if(this.isPartofEdge(px))return px;
		}
		else if(dir==7){
			px=new Point(p.x+1,p.y+1);
			if(this.isPartofEdge(px))return px;
		}
	
		return null;
		
	}
	
	/************************************************************/
	/* UTILITY METHODS											*/
	/************************************************************/
	public int getArea(){
		return pixels.size();
	}
	
	public int getCircumference(){
		return this.edgePixels.size();
	}
	
	public int getInternalECadherinArea(){
		return this.cytoPlasmECadherin.size();
	}
	
	public int getInternalActinArea(){
		return this.cytoPlasmActin.size();
	}
	
	/**
	 * get centre of Membrane
	 * @return
	 */
	public Point getGeometricMean(){
		int maxX=0,minX=Integer.MAX_VALUE,maxY=0,minY=Integer.MAX_VALUE;
		
		Iterator<Point> itr = pixels.iterator();
	    while(itr.hasNext()) {
	         Point pix = (Point)itr.next();
	         if(pix.getX()<minX)minX=(int)pix.getX();
	         if(pix.getY()<minY)minY=(int)pix.getY();
	         if(pix.getX()>maxX)maxX=(int)pix.getX();
	         if(pix.getY()>maxY)maxY=(int)pix.getY();	   
	      }
	    
	    int xpos=(int)Math.round((maxX-minX)/2);
	    xpos+=minX;
	    int ypos=(int)Math.round((maxY-minY)/2);
	    ypos+=minY;

		return new Point(xpos,ypos);
	}
	
	/**
	 * Is a Point a part of the membrane
	 * @param p
	 * @return
	 */
	public boolean isPartofMembrane(Point p){
		Iterator<Point> itr = pixels.iterator();
	    while(itr.hasNext()) {
	         Point pix = (Point)itr.next();
	         if(p.x==pix.x&&p.y==pix.y)return true;
	    }
	    return false;
	}
	
	/**
	 * is a point a part of an edge
	 * @param p
	 * @return
	 */
	public boolean isPartofEdge(Point p){
		Iterator<Point> itr = this.edgePixels.iterator();
	    while(itr.hasNext()) {
	         Point pix = (Point)itr.next();
	         if(p.x==pix.x&&p.y==pix.y)return true;
	    }
	    return false;
	}
	
	
	/**
	 * is a point a part of a corner
	 * @param p
	 * @return
	 */
	public Cell_Membrane_Corner isPartofCorner(Point p){
		Iterator<Cell_Membrane_Corner> itr = this.corners.iterator();
	    while(itr.hasNext()) {
	    	Cell_Membrane_Corner pix = (Cell_Membrane_Corner)itr.next();
	         if(	(pix.getX() >= p.x - Cell_Membrane.CORNER_SIZE) && (pix.getX() <  p.x + Cell_Membrane.CORNER_SIZE) && 
	        		 (pix.getY() >= p.y - Cell_Membrane.CORNER_SIZE) && (pix.getY() <  p.y + Cell_Membrane.CORNER_SIZE))
	        		 return pix;
	    }
	    return null;
	}
	
	
	/**
	 * is a point a corner point
	 * @param p
	 * @return
	 */
	public boolean isCorner(Point p,ArrayList<Cell_Membrane_Corner>list){
		Iterator<Cell_Membrane_Corner> itr = list.iterator();
	    while(itr.hasNext()) {
	    	Point pix = (Point)itr.next().getCorner();
	        if(pix.x==p.x&&pix.y==p.y)return true;
	    }
	    return false;
	}
	
	
	/**
	 * getCorner from list
	 * @param p
	 * @return
	 */
	public Cell_Membrane_Corner getCorner(Point p,ArrayList<Cell_Membrane_Corner>list){
		Iterator<Cell_Membrane_Corner> itr = list.iterator();
	    while(itr.hasNext()) {
	    	Cell_Membrane_Corner corner = (Cell_Membrane_Corner)itr.next();
	        if(corner.getX()==p.x&&corner.getY()==p.y)return corner;
	    }
	    return null;
	}
	
	/**
	 * remove corner
	 * @param p
	 * @return
	 */
	public boolean removeCorner(Cell_Membrane_Corner p){
		boolean ret=this.corners.remove(p);
		this.numberCorners();
		return ret;
	}
	
	
	/** 
	 * add a corner based on a point
	 * 
	 * @param p
	 * @return
	 */
	public boolean addCorner(Point p){
		
		if(this.isPartofEdge(p)){
			Cell_Membrane_Corner c=new Cell_Membrane_Corner(p,this);
			boolean ret= this.corners.add(c);
			this.numberCorners();
			return ret;
		}
			
		for(int i=1;i<= Cell_Membrane.CORNER_SIZE;i++){
			for(int j=-i;j<=i;j++)
				for(int k=-i;k<=i;k++){
					Point pix=new Point(p.x+j,p.y+k);
					if(this.isPartofEdge(pix)){
						Cell_Membrane_Corner c=new Cell_Membrane_Corner(pix,this);
						boolean ret=this.corners.add(c);
						this.numberCorners();
						return ret;
					}
				}
			
		}
		
		
	    return false;
	}
	
	/**
	 * remove a point p from a list list
	 * @param list
	 * @param p
	 * @return
	 */
	private  boolean removePoint(ArrayList<Point> list,Point p){
		for(int i=0;i<list.size();i++){
			Point lp=list.get(i);
			if(p.x==lp.x && p.y==lp.y){
				list.remove(i);
				return true;
			}
		}
		return false;
	}
	
	
	
	public String toString(){
		return "Cell # "+this.getNumber()+
				"\nCell Area = "+pixels.size()+
				"\nEdge Area = "+edgePixels.size();
	}
	
	
	/**
	 * Get chain code representation of image edge
	 */
	public void calculateChainCode(MembraneTool tool) throws Exception{
		
			ArrayList<Point> edgeCopy = (ArrayList<Point>)this.edgePixels.clone();
			Point p = edgeCopy.get(0);
			edgeCopy.remove(0);
			ArrayList<Point> visited = new ArrayList<Point>();
			visited.add(p);
			int last=0;
			
			this.chainCode.clear();
			//this.chainCodeFirstDerivative.clear();
			//this.chainCodeSecondDerivative.clear();
			
			
			while(edgeCopy.size()>0){
				Point point=null;
				
				if(ImageUtilities.isInList(edgeCopy,point=new Point(p.x,p.y-1))){
					ChainCodeElement element = new ChainCodeElement(2,point);
					this.chainCode.add(element);
					//if(last==8)this.chainCodeFirstDerivative.add(element);
					//else this.chainCodeFirstDerivative.add(new ChainCodeElement((int)Math.abs(2-last),point));
					last=2;
					if(!ImageUtilities.isInList(visited, point))visited.add(point);
					removePoint(edgeCopy,point);
					p=point;
				}
				else if(ImageUtilities.isInList(edgeCopy,point=new Point(p.x+1,p.y))){
					ChainCodeElement element = new ChainCodeElement(4,point);
					this.chainCode.add(element);
					//this.chainCodeFirstDerivative.add(new ChainCodeElement((int)Math.abs(4-last),point));
					last=4;
					if(!ImageUtilities.isInList(visited, point))visited.add(point);
					removePoint(edgeCopy,point);
					p=point;
				}
				else if(ImageUtilities.isInList(edgeCopy,point=new Point(p.x,p.y+1))){
					ChainCodeElement element = new ChainCodeElement(6,point);
					this.chainCode.add(element);
					//this.chainCodeFirstDerivative.add(new ChainCodeElement((int)Math.abs(6-last),point));
					last=6;
					if(!ImageUtilities.isInList(visited, point))visited.add(point);
					removePoint(edgeCopy,point);
					p=point;
				}
				else if(ImageUtilities.isInList(edgeCopy,point=new Point(p.x-1,p.y))){
					ChainCodeElement element = new ChainCodeElement(8,point);
					this.chainCode.add(element);
					//if(last==2)this.chainCodeFirstDerivative.add(new ChainCodeElement(2,point));
					//else this.chainCodeFirstDerivative.add(new ChainCodeElement((int)Math.abs(8-last),point));
					last=8;
					if(!ImageUtilities.isInList(visited, point))visited.add(point);
					removePoint(edgeCopy,point);
					p=point;
				}
				else if(ImageUtilities.isInList(edgeCopy,point=new Point(p.x-1,p.y-1))){
					ChainCodeElement element = new ChainCodeElement(1,point);
					this.chainCode.add(element);
					//if(last==8)this.chainCodeFirstDerivative.add(element);
					//this.chainCodeFirstDerivative.add(new ChainCodeElement((int)Math.abs(1-last),point));
					last=1;
					if(!ImageUtilities.isInList(visited, point))visited.add(point);
					removePoint(edgeCopy,point);
					p=point;
				}
				else if(ImageUtilities.isInList(edgeCopy,point=new Point(p.x+1,p.y-1))){
					ChainCodeElement element = new ChainCodeElement(3,point);
					this.chainCode.add(element);
					//this.chainCodeFirstDerivative.add(new ChainCodeElement((int)Math.abs(3-last),point));
					last=3;
					if(!ImageUtilities.isInList(visited, point))visited.add(point);
					removePoint(edgeCopy,point);
					p=point;
				}
				
				else if(ImageUtilities.isInList(edgeCopy,point=new Point(p.x+1,p.y+1))){
					ChainCodeElement element = new ChainCodeElement(5,point);
					this.chainCode.add(element);
					//this.chainCodeFirstDerivative.add(new ChainCodeElement((int)Math.abs(5-last),point));
					last=5;
					if(!ImageUtilities.isInList(visited, point))visited.add(point);
					removePoint(edgeCopy,point);
					p=point;
				}
				
				else if(ImageUtilities.isInList(edgeCopy,point=new Point(p.x-1,p.y+1))){
					ChainCodeElement element = new ChainCodeElement(7,point);
					this.chainCode.add(element);
					//this.chainCodeFirstDerivative.add(new ChainCodeElement((int)Math.abs(7-last),point));
					last=7;
					if(!ImageUtilities.isInList(visited, point))visited.add(point);
					removePoint(edgeCopy,point);
					p=point;
				}
				
				else {
					//point=p=visited.get(visited.size()-1);
					break;
				}
				
			
				
		}	
			
	}
	

	
	
	
	public void getCornerDistances(MembraneTool tool) throws Exception{
		Iterator<Cell_Membrane_Corner> itr = this.corners.iterator();
	    while(itr.hasNext()) {
	    	Cell_Membrane_Corner c=(Cell_Membrane_Corner)itr.next();
	    	c.getNeighbour(tool);
	    	//tool.getTool().setTextArea(c.toString());
	    }
	}
	
	/************************************************************/
	/* GET ECADHERIN INTERNAL POINTS							*/
	/************************************************************/
	public void getECadherinPoints(MembraneTool tool) throws Exception{
		this.cytoPlasmECadherin.clear();
		this.edgeECadherin.clear();
		
		BufferedImage thresholdImage = tool.getECadherinThresholdImage();
		
		for(int i=0;i<thresholdImage.getWidth();i++)
			for(int j=0;j<thresholdImage.getHeight();j++){
				int[] pixel = thresholdImage.getRaster().getPixel(i, j, new int[]{0});
				if(pixel[0]>0){
					Point p=new Point(i,j);
					if(ImageUtilities.isInList(this.pixels, p)){
						boolean add=true;
						Iterator<Cell_Membrane_Corner> itr = this.corners.iterator();
					    while(itr.hasNext()) {
					    	Cell_Membrane_Corner c=(Cell_Membrane_Corner)itr.next();
					    	if(ImageUtilities.isInList(c.dilatedEdgePoints, p)){
					    		add=false;
					    		break;
					    	}
					    }
					if(add)this.cytoPlasmECadherin.add(p);	
					else this.edgeECadherin.add(p);
					}
				}
			}
	}
	
	
	public void getMeasurementPoints(MembraneTool tool) throws Exception{
		this.cytoPlasmActin.clear();
		this.edgeActin.clear();
		
		MeasurementTool mt = (MeasurementTool)(tool.getMultiChannelImage().getMeasurementTool());
		BufferedImage thresholdImage=mt.getImage();
		
		for(int i=0;i<thresholdImage.getWidth();i++)
			for(int j=0;j<thresholdImage.getHeight();j++){
				int[] pixel = thresholdImage.getRaster().getPixel(i, j, new int[]{0});
				if(pixel[0]>0){
					Point p=new Point(i,j);
					if(ImageUtilities.isInList(this.pixels, p)){
						boolean add=true;
						Iterator<Cell_Membrane_Corner> itr = this.corners.iterator();
					    while(itr.hasNext()) {
					    	Cell_Membrane_Corner c=(Cell_Membrane_Corner)itr.next();
					    	if(ImageUtilities.isInList(c.dilatedEdgePoints, p)){
					    		add=false;
					    		break;
					    	}
					    }
					if(add)this.cytoPlasmActin.add(p);	
					else this.edgeActin.add(p);
					}
				}
			}
	}
	
	/************************************************************/
	/* IMAGE METHODS											*/
	/************************************************************/
	public BufferedImage getMembraneImage(boolean numbered, BufferedImage image){
		BufferedImage target=null;
		if(image==null)
			target = new BufferedImage(this.collection.getXDim(), this.collection.getYDim(), BufferedImage.TYPE_3BYTE_BGR);
		else target=image;
		
		Iterator<Point> itr = this.pixels.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	target.getRaster().setPixel(p.x, p.y, new int[]{this.colour.getRed(),this.colour.getGreen(),this.colour.getBlue()});
	    }
	    
	    int[] col={255,255,255};
	    Iterator<Point> itr2 = this.edgePixels.iterator();
	    while(itr2.hasNext()) {
	    	Point p = (Point)itr2.next();
	    	target.getRaster().setPixel(p.x, p.y, col);
	    }
	   
	   if(numbered){
		   
		   if(this.colour.getRed()>200&&this.colour.getGreen()>200&&this.colour.getBlue()>200)col=new int[]{127,127,127};
		   Graphics g=target.getGraphics();
		   Point p=this.getGeometricMean();
		   g.drawString(""+this.number, p.x, p.y);
	   }
	   
	   
	   return target;
	}
	

	/************************************************************/
	/* GET BINARY IMAGE OF CELL EDGE							*/
	/************************************************************/
	protected BufferedImage getCellEdgeImage(ArrayList<Point> points){
		
		BufferedImage target = new BufferedImage(this.collection.getXDim(), this.collection.getYDim(), BufferedImage.TYPE_BYTE_BINARY);
		Iterator<Point> itr = points.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	target.getRaster().setPixel(p.x, p.y, new int[]{1});
	    }
	    
	   return target;
	}

	public BufferedImage getEdgeImage(){
		return this.getCellEdgeImage(this.edgePixels);
	}
	
	/************************************************************/
	/* GET CORNER IMAGE											*/
	/************************************************************/
	public BufferedImage getCornerImage(MembraneTool panel,int measurementChannel) throws Exception{
		
		BufferedImage target = new BufferedImage(this.collection.getXDim(), this.collection.getYDim(), BufferedImage.TYPE_3BYTE_BGR);
		
		BufferedImage image=panel.getOriginalImage();
		if(measurementChannel==0)image=((MeasurementTool)(panel.getMultiChannelImage().getMeasurementTool())).getMeasurementThresholdImage();
		else if(measurementChannel==1)image=panel.getECadherinThresholdImage();
		//else if(measurementChannel==2)image=panel.getOriginalImage();
		else if(measurementChannel==3)image=((MeasurementTool)(panel.getMultiChannelImage().getMeasurementTool())).getOriginalImage();
		//else if(measurementChannel==4)image=panel.getOriginalImage();
		
		
		for(int i=0;i<image.getWidth();i++)
			for(int j=0;j<image.getHeight();j++){
				int pixel=image.getRaster().getPixel(i, j, new int[]{0})[0];
				target.getRaster().setPixel(i,j,new int[]{pixel,pixel,pixel});
			}
		
		
		
		if(measurementChannel==2){
				BufferedImage im=this.getCellEdgeImage(this.edgePixels);
				int c=0;
				while(c<panel.getDilatedEdgeWidth()){
					im=ImageUtilities.getDilatedImage(im);
					c++;
				}
				
				for(int i=0;i<image.getWidth();i++)
					for(int j=0;j<image.getHeight();j++){
						int pixel=im.getRaster().getPixel(i, j, new int[]{0})[0];
						if(pixel==1)target.getRaster().setPixel(i,j,new int[]{0,255,0});
					}  
		}
		
		
		Iterator<Point> itr = this.edgePixels.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	target.getRaster().setPixel(p.x, p.y, new int[]{255,0,0});
	    }
		
	     Graphics g=target.getGraphics();
	     g.setColor(Color.WHITE);
	    //Color c=this.getRandomColor();
	    Color c= Color.YELLOW;
		for(int i=0;i<this.corners.size();i++){	
				Point p= this.corners.get(i).getCorner();
				for(int j=-Cell_Membrane.CORNER_SIZE;j<=Cell_Membrane.CORNER_SIZE;j++)
					for(int k=-Cell_Membrane.CORNER_SIZE;k<=Cell_Membrane.CORNER_SIZE;k++)
						if(p.x+j>=0&&p.y+k>=0&&p.x+j<this.collection.getXDim()&&p.y+k<this.collection.getYDim()){
										target.getRaster().setPixel(p.x+j,p.y+k,new int[]{c.getRed(),c.getGreen(),c.getBlue()});
						}
				
						
				g.drawString(""+this.corners.get(i).getNumber(), p.x, p.y);	
				//tool.getTool().setTextArea("Corner :: "+p.toString()+"\n");
				}
		
			
		
		return target;		
	}
	
	
	/************************************************************/
	/* CORNER FINDING METHODS									*/
	/************************************************************/
	
	public void getDouglasPeucker(double epsilon, MembraneTool tool) throws Exception{
		//
		this.calculateChainCode(tool);
		//this.corners=this.getDouglasPeucker(epsilon, this.chainCode);
		//this.corners=this.ramerDouglasPeuckerFunction(this.chainCode,0,this.chainCode.size()-1,epsilon);
		
		SPoint[] points=new SPoint[this.chainCode.size()];
		Iterator<ChainCodeElement> iter = this.chainCode.iterator();
		int c=0;
		while(iter.hasNext()){
			Point p=iter.next().getPoint();
			points[c]=new SPoint(p.x,p.y);
			c++;
		}
		
		
		SPoint[] newPoints=convexHull(points);
		this.corners.clear();
		for(int i=0;i<newPoints.length;i++){
			this.corners.add(new Cell_Membrane_Corner(new Point(newPoints[i].x,newPoints[i].y),this));
		}
		
		this.corners=this.ramerDouglasPeuckerFunction(this.corners,0,this.corners.size()-1, epsilon);
		//tool.getTool().setTextArea("Epsilon :: "+epsilon+" :: Corners :: "+corners.size()+"\n");
		
		this.numberCorners();
	}
		
	
	private void numberCorners(){
		Iterator<Cell_Membrane_Corner> cornerIterator = this.corners.iterator();
		int c=1;
		while(cornerIterator.hasNext()){
			cornerIterator.next().setNumber(c);
			c++;
		}
	}
	
	private ArrayList<Cell_Membrane_Corner> ramerDouglasPeuckerFunction(ArrayList<Cell_Membrane_Corner> points, int startIndex, int endIndex, double epsilon) throws Exception{
			  double dmax = 0;
			  int idx = 0;
			  Point p1=points.get(startIndex).getCorner();
			  Point p2=points.get(endIndex).getCorner();
			    
			    for (int i = startIndex + 1; i < endIndex-1; i++) {
			    		Point p=points.get(i).getCorner();
			    		double distance=findPerpendicularDistance(p, p1, p2);
			    		//double distance= Math.sqrt(Math.pow((p1.x-p.x), 2)+Math.pow((p1.y-p.y), 2));
			            if (distance > dmax) {
			                    idx = i;
			                    dmax = distance;
			            }
			    }
			    
			    ArrayList<Cell_Membrane_Corner> result=new ArrayList<Cell_Membrane_Corner>();
			    if (dmax >= epsilon) {	 	
			    		ArrayList<Cell_Membrane_Corner> points1=(ramerDouglasPeuckerFunction(points, startIndex, idx, epsilon));
			    		for(int i=0;i<points1.size();i++){
			    			if(!isCorner(points1.get(i).getCorner(),result))result.add(points1.get(i));
			    		}
			    		ArrayList<Cell_Membrane_Corner> points2=(ramerDouglasPeuckerFunction(points, idx, endIndex, epsilon));	 
			    		for(int i=0;i<points2.size();i++){
			    			if(!isCorner(points2.get(i).getCorner(),result))result.add(points2.get(i));
			    		}
			    } else {
			    		result.add(points.get(startIndex));
			    		result.add(points.get(endIndex));		        
			    }
			    
			    return result;
			    
	}
	
	
	private double findPerpendicularDistance(Point p, Point p1, Point p2) {			
		double result,slope,intercept;			
		if (p1.x==p2.x) {				
				result=Math.abs(p.x-p1.x);			
		}		else {				
			slope = (p2.y - p1.y) / (p2.x - p1.x);				
			intercept=p1.y-(slope*p1.x);				
			result = Math.abs(slope * p.x - p.y + intercept) / Math.sqrt(Math.pow(slope, 2) + 1);			}			
		return result;		
	}
	
	
	
	public static long cross(SPoint O, SPoint A, SPoint B) {
		return (A.x - O.x) * (B.y - O.y) - (A.y - O.y) * (B.x - O.x);
	}
 
	public static SPoint[] convexHull(SPoint[] P) {
 
		if (P.length > 1) {
			int n = P.length, k = 0;
			SPoint[] H = new SPoint[2 * n];
 
			Arrays.sort(P);
 
			// Build lower hull
			for (int i = 0; i < n; ++i) {
				while (k >= 2 && cross(H[k - 2], H[k - 1], P[i]) <= 0)
					k--;
				H[k++] = P[i];
			}
 
			// Build upper hull
			for (int i = n - 2, t = k + 1; i >= 0; i--) {
				while (k >= t && cross(H[k - 2], H[k - 1], P[i]) <= 0)
					k--;
				H[k++] = P[i];
			}
			if (k > 1) {
				H = Arrays.copyOfRange(H, 0, k - 1); // remove non-hull vertices after k; remove k - 1 which is a duplicate
			}
			return H;
		} else if (P.length <= 1) {
			return P;
		} else{
			return null;
		}
	}

	
	/************************************************************/
	/* INTERNAL CLASS											*/
	/************************************************************/
	
	class SPoint implements Comparable<SPoint> {
		int x, y;
	 
		public SPoint(int x, int y){
			this.x=x; this.y=y;
		}
		
		public int compareTo(SPoint p) {
			if (this.x == p.x) {
				return this.y - p.y;
			} else {
				return this.x - p.x;
			}
		}
	 
		public String toString() {
			return "("+x + "," + y+")";
		}	
	 
	}
	
	
	


}
