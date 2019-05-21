package uk.ac.imperial.cisbio.cell_metric.om;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import uk.ac.imperial.cisbio.cell_metric.utilities.ImageUtilities;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.NucleusTool;

public class Cell_Nucleus {

	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	ArrayList<Point> pixels = new ArrayList<Point>(); 	//pixel points in this nucleus
	private int number=0;								//number in collection of this nucleus
	private Color colour;								//colour of this nucleus
	private Nucleus_Collection collection;				//collection of which this nucleus is a part
	private ArrayList<Intra_Cell_Distance> intraCellularDistances = new  ArrayList<Intra_Cell_Distance>();			//distances between other nucleii for this nucleus
	private Cell_Membrane myMembrane;					//cell membrane which is paired with this nucleus
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public Cell_Nucleus() {
		// TODO Auto-generated constructor stub
	}
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/
	public void addPoint(Point p){
		pixels.add(p);
	}
	
	public void setPointSet(ArrayList<Point> points){
		this.pixels=points;
	}
	
	public void setNumber(int n){
		this.number=n;
	}
	
	public int getNumber(){
		return this.number;
	}
	
	public void setColour(Color c){
		this.colour=c;
	}
	
	public Color getColour(){
		return this.colour;
	}
	
	public void setCollection(Nucleus_Collection c){
		this.collection=c;
	}
	
	public ArrayList<Intra_Cell_Distance> getIntraCellularDistances(){
		return this.intraCellularDistances;
	}
	
	public void setCellMembrane(Membrane_Collection mc){
		this.myMembrane=mc.getSelectedMembrane(this.getGeometricMean());
		if(this.myMembrane!=null)this.myMembrane.setNucleus(this);
	}
	
	public Cell_Membrane getMyMembrane(){
		return this.myMembrane;
	}
	
	/************************************************************/
	/* UTILITY METHODS											*/
	/************************************************************/
	public int getArea(){
		return pixels.size();
	}
	
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
	
	
	public boolean isPartofNucleus(Point p){
		Iterator<Point> itr = pixels.iterator();
	    while(itr.hasNext()) {
	         Point pix = (Point)itr.next();
	         if(p.x==pix.x&&p.y==pix.y)return true;
	    }
	    return false;
	}
	
	/************************************************************/
	/* IMAGE METHODS											*/
	/************************************************************/
	public BufferedImage getNucleusImage(boolean numbered, BufferedImage image){
		BufferedImage target=null;
		if(image==null)
			target = new BufferedImage(this.collection.getXDim(), this.collection.getYDim(), BufferedImage.TYPE_3BYTE_BGR);
		else target=image;
		
		Iterator<Point> itr = this.pixels.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	target.getRaster().setPixel(p.x, p.y, new int[]{this.colour.getRed(),this.colour.getGreen(),this.colour.getBlue()});
	    }
	   
	   if(numbered){
		   int[] col={255,255,255};
		   if(this.colour.getRed()>200&&this.colour.getGreen()>200&&this.colour.getBlue()>200)col=new int[]{127,127,127};
		   Graphics g=target.getGraphics();
		   Point p=this.getGeometricMean();
		   g.drawString(""+this.number, p.x, p.y);
	   }
	   
	   
	   return target;
	}
	
	
	public BufferedImage getCombinedImage(boolean numbered, BufferedImage image){
		BufferedImage target=null;
		if(image==null)
			target = new BufferedImage(this.collection.getXDim(), this.collection.getYDim(), BufferedImage.TYPE_3BYTE_BGR);
		else target=image;
		
		if(this.myMembrane!=null)target=this.myMembrane.getMembraneImage(false, target);
		Iterator<Point> itr = this.pixels.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	target.getRaster().setPixel(p.x, p.y, new int[]{this.colour.getRed(),this.colour.getGreen(),this.colour.getBlue()});
	    }
	   
	   if(numbered){
		   int[] col={255,255,255};
		   if(this.colour.getRed()>200&&this.colour.getGreen()>200&&this.colour.getBlue()>200)col=new int[]{127,127,127};
		   Graphics g=target.getGraphics();
		   Point p=this.getGeometricMean();
		   g.drawString(""+this.number, p.x, p.y);
	   }
	   
	   
	   return target;
	}
	

	public void calculateIntraCellDistances(){
		ArrayList<Cell_Nucleus> otherCells=this.collection.getOtherNucleusObjects(this);
		Point p1=this.getGeometricMean();
		Iterator<Cell_Nucleus> itr = otherCells.iterator();
		this.intraCellularDistances.clear();
	    while(itr.hasNext()) {
	    	Cell_Nucleus c = (Cell_Nucleus)itr.next();
	    	Point p2=c.getGeometricMean();
	    	double dist=Math.sqrt(Math.pow((p1.getY()-p2.getY()),2.0)+Math.pow((p1.getX()-p2.getX()),2.0));
	    	this.intraCellularDistances.add(new Intra_Cell_Distance(this,c,dist));    	
	    }
	    
	    Collections.sort(this.intraCellularDistances, new Intra_Cell_Comparator());
	   
	}


	public BufferedImage getDistanceImage(NucleusTool panel){
		BufferedImage target = new BufferedImage(this.collection.getXDim(), this.collection.getYDim(), BufferedImage.TYPE_3BYTE_BGR);
		target=collection.getNucleusImage(true,panel);
		Graphics g=target.getGraphics();
		g.setColor(Color.WHITE);
		
		Point p1=this.getGeometricMean();
	    int i=0;
	    Iterator<Intra_Cell_Distance> cditr = this.intraCellularDistances.iterator();
	    while(cditr.hasNext()) {
	    	Intra_Cell_Distance d=(Intra_Cell_Distance)cditr.next();
	    	Cell_Nucleus c=d.getToCell();
	    	Point pc=c.getGeometricMean();
	    	g.drawLine(p1.x, p1.y, pc.x, pc.y);
	    	if(panel!=null)panel.getTool().setTextArea("Intra Cell Distance : Cell "+this.getNumber()+" to Cell "+c.getNumber()+" :: "+d.getDistance()+"\n");
	    	i++;
	    	if(i==panel.getNumIntraCells())break;
	    }
    		
	    	
	    return target;
	}
	

}
