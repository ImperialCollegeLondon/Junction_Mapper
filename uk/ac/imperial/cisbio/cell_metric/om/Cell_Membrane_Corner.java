package uk.ac.imperial.cisbio.cell_metric.om;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.imperial.cisbio.cell_metric.utilities.ImageUtilities;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MeasurementTool;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MembraneTool;

public class Cell_Membrane_Corner {
	
	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	private Point cornerPoint;						//corner coordinates						
	private Cell_Membrane_Corner neighbour;			//neighbour corner for edge calculations						
	private double distanceToNeighbour;				//distance to neighbour
	private double euclidianDistanceToNeighbour;	//euclidian distance to neighbour
	private Cell_Membrane membrane;					//cell membrane this is a part of
	private int number;								//id of membrane
	
	/* EDGE POINT VARIABLES										*/
	private int dilationCycles;												//number of dilation cycles used
	private ArrayList<Point> edgePoints = new ArrayList<Point>();			//points in single pixel wide membrane edge, will be in contiguous order
	protected ArrayList<Point> dilatedEdgePoints = new ArrayList<Point>(); 	//edge points after edge is dilated
	
	/* ECADHERIN POINTS										*/
	protected ArrayList<Point> eCadherinEdgePoints = new ArrayList<Point>();	//ecadherin points above ecad threshold along the edge
	protected ArrayList<Point> eCadherinDilatedEdgePoints = new ArrayList<Point>(); //ecadherin points above ecad threshold in the dilated edge area
	protected ArrayList<Point> skeletonisedECadherinEdgePoints = new ArrayList<Point>();
	private double eCadherinDistanceToNeighbour;	//piecemeal distance from first ecad point to last ecad point along edge
													//must be <= to distanceToNeighbour
	private double eCadherinEuclidianDistance;		//euclidian distance from first ecad point to last ecad point
													//must be <= to euclidianDistanceToNeighbour;
	private double fragmentedJunctionLength;		//sum of ecad fragment distances along the edge. Does not measure unconnected distances between fragments.
													//must be <= to eCadherinDistanceToNeighbour
	
	private int totalEcadherinIntensityWithinDilatedEdge=0;	
	
	/* MEASUREMENT CHANNEL POINTS										*/
	protected ArrayList<Point> measurementEdgePoints = new ArrayList<Point>();
	protected ArrayList<Point> measurementDilatedEdgePoints = new ArrayList<Point>();
	protected ArrayList<Point> skeletonisedMeasurementEdgePoints = new ArrayList<Point>();
	private double measurementDistanceToNeighbour;
	private int totalMeasurementIntensityWithinDilatedEdge=0;	
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public Cell_Membrane_Corner(Point p, Cell_Membrane m) {
		this.cornerPoint=p;
		this.membrane=m;
	}
	
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/

	public Point getCorner(){
		return this.cornerPoint;
	}
	
	public int getX(){
		return this.cornerPoint.x;
	}
	
	public int getY(){
		return this.cornerPoint.y;
	}
	
	public void setNumber(int n){
		this.number=n;
	}
	
	public int getNumber(){
		return this.number;
	}
	
	public Cell_Membrane_Corner getPairedCorner(){
		return this.neighbour;
	}
	
	public double getDistanceToNeighbour(){
		return this.distanceToNeighbour;
	}
	
	public double getEuclidianDistanceToNeighbour(){
		return this.euclidianDistanceToNeighbour;
	}
	
	public double getECadherinEuclidianDistance(){
		return this.eCadherinEuclidianDistance;
	}
	
	public double getFragmentedJunctionLength(){
		return this.fragmentedJunctionLength;
	}
	
	public double getDistanceRatio(){
		 return this.distanceToNeighbour/this.euclidianDistanceToNeighbour;
	}
	
	public int getSkeletonisedECadPixelLength(){
		return this.skeletonisedECadherinEdgePoints.size();
	}
	
	public int getEdgePixelLength(){
		return this.edgePoints.size();
	}
	
	public double getJunctionCovereage(){
		return this.getSkeletonisedECadPixelLength()*100.0/this.getEdgePixelLength();
	}
	
	public int getDilatedEdgePixelArea(){
		return this.dilatedEdgePoints.size();
	}
	
	public int getECadherinPixelArea(){
		return this.eCadherinEdgePoints.size();
	}
	
	public int getECadherinDilatedPixelArea(){
		return this.eCadherinDilatedEdgePoints.size();
	}
	
	public double getECadherinDistanceToNeighbour(){
		return this.eCadherinDistanceToNeighbour;
	}
	
	public int getTotalEcadherinIntensityWithinDilatedEdge(){
		return this.totalEcadherinIntensityWithinDilatedEdge;
	}
	
	public int getMeasurementPixelArea(){
		return this.measurementEdgePoints.size();
	}
	
	public int getMeasurementDilatedPixelArea(){
		return this.measurementDilatedEdgePoints.size();
	}
	
	public double getMeasurementDistanceToNeighbour(){
		return this.measurementDistanceToNeighbour;
	}
	
	public int getTotalMeasurementIntensityWithinDilatedEdge(){
		return this.totalMeasurementIntensityWithinDilatedEdge;
	}	
	
	public int getDilationCycles(){
		return this.dilationCycles;
	}
	
	public ArrayList<Point> getDilatedEdgePoints(){
		return this.dilatedEdgePoints;
	}
	
	
	/************************************************************/
	/* UTILITY METHODS											*/
	/************************************************************/
	
	
	/************************************************************/
	/* FIND NEIGHBOURING CORNER AND EDGE POINTS					*/
	/************************************************************/
	public void getNeighbour(MembraneTool tool) throws Exception{
		boolean start=false;
		this.edgePoints.clear();									//clear edge points
		Iterator<Point> iter = membrane.getEdgePixels().iterator();	//get all edge points
		
				/**
				 * Iterate through all cell edge points until I find
				 * this corner - when I do set start to true
				 * Membrane Edge point vector is preordered to be contiguous
				 * so once this corner is found add edge points to this edge until 
				 * the next corner is found
				 */
				while(iter.hasNext()){			
					Point p = iter.next();	
					
					if(start&&!membrane.isCorner(p, membrane.getCorners())){
						this.edgePoints.add(p);
					}
				 	else if(start&&membrane.isCorner(p, membrane.getCorners())){
				 		this.edgePoints.add(p);
				 		this.neighbour=membrane.getCorner(p, membrane.getCorners());
				 		break;
				 	}
				 	
				 	if(!start&&p.x==this.getX()&&p.y==this.getY()){
				 		this.edgePoints.add(p);
				 		start=true; 	
				 	}
				 	if(!iter.hasNext()&&start)iter=membrane.getEdgePixels().iterator();
				}
				
				
				/**
				 * Edge points have been set now and are contiguous
				 * Iterate through them to calculate distance to neighbour
				 */
				iter=this.edgePoints.iterator();
				Point last=null, next=null;
				double dist=0;
				
				while(iter.hasNext()){
					last=next;
					next=iter.next();		
					if(last!=null&&next!=null)dist+=this.calculateEuclidianDistanceToNeighbour(last, next);
				}
				
				//set distance to neighbour
				this.distanceToNeighbour=dist;
				
				//set euclidian distance
				this.euclidianDistanceToNeighbour=this.calculateEuclidianDistanceToNeighbour(this.cornerPoint,this.neighbour.cornerPoint);
				
				//dilate edge points and put in vector
				this.getDilatedEdgePoints(tool.getDilatedEdgeWidth(),tool);
				
				//find ecadherin edge points under dilated edge
				this.getECadherinPoints(tool);
				
				//find measurement points under dilated edge
				this.getMeasurementPoints(tool);
	}
	
	/**
	 * Calculate euclidian distance between two points using pythagoras theorum
	 * @param p1
	 * @param p2
	 * @return euclidian distance
	 */
	private double calculateEuclidianDistanceToNeighbour(Point p1,Point p2){	
		double side1=(double)(p1.getX()-p2.getX());
		double side2=(double)(p1.getY()-p2.getY());	
		return Math.sqrt((side1*side1)+(side2*side2));
	}
		
	/************************************************************/
	/* GET DILATED EDGE POINTS									*/
	/************************************************************/
	public void getDilatedEdgePoints(int dilate, MembraneTool tool) throws Exception{	
		//initialise variables as this may be done more than once
		this.dilationCycles=dilate;
		this.dilatedEdgePoints.clear();
		this.eCadherinDilatedEdgePoints.clear();
		this.measurementDilatedEdgePoints.clear();		
		this.totalEcadherinIntensityWithinDilatedEdge=0;
		this.totalMeasurementIntensityWithinDilatedEdge=0;
		
		//dilate binary edge image for this edge and add dilated edge points to vector
		BufferedImage dilatedImage = this.getDilatedEdgeImage(dilate);		
		for(int i=0;i<dilatedImage.getWidth();i++)
			for(int j=0;j<dilatedImage.getHeight();j++){
				int[] pixel = dilatedImage.getRaster().getPixel(i, j, new int[]{0});
				if(pixel[0]==1)this.dilatedEdgePoints.add(new Point(i,j));
			}
		
		//get ecad threshold image
		BufferedImage thresholdImage = tool.getECadherinThresholdImage();
		Iterator<Point> iter=this.dilatedEdgePoints.iterator();
		MeasurementTool mt=((MeasurementTool)tool.getMultiChannelImage().getMeasurementTool());
		//get measurement image
		BufferedImage measurementThresholdImage = mt.getMeasurementThresholdImage();
		
		
		//build ecad point collection and measurement point collection point by point under dilated edge
		while(iter.hasNext()){
				Point next=iter.next();
				int[] pixel1 = thresholdImage.getRaster().getPixel(next.x, next.y, new int[]{0});
				if(pixel1[0]>0)this.eCadherinDilatedEdgePoints.add(next);
				this.totalEcadherinIntensityWithinDilatedEdge+=pixel1[0];
				
				int[] pixel2 = measurementThresholdImage.getRaster().getPixel(next.x, next.y, new int[]{0});
				if(pixel2[0]>0)this.measurementDilatedEdgePoints.add(next);
				this.totalMeasurementIntensityWithinDilatedEdge+=pixel2[0];
			}
	}
	
	/************************************************************/
	/* ECADHERIN MEASUREMENTS									*/
	/************************************************************/
	public void getECadherinPoints(MembraneTool tool) throws Exception{
		//initialise
		this.eCadherinEdgePoints.clear();
		BufferedImage thresholdImage = tool.getECadherinThresholdImage();
		
		double dist=0, frag=0;
		Point last=null, firstPoint=null;
		boolean first=false;
		Iterator<Point> iter=this.edgePoints.iterator();
		
		/** 
		 * for points under the edge only
		 * iterate through edge points and build ecadherin totals and vector
		 * 
		 */
		while(iter.hasNext()){
				Point next=iter.next();		
				int[] pixel = thresholdImage.getRaster().getPixel(next.x, next.y, new int[]{0});
				if(pixel[0]>0){
					this.eCadherinEdgePoints.add(next);
					if(!first){
						firstPoint=next;
						first=true;
					}
					if(last!=null&&next!=null){
						//calculate distance
						dist+=this.calculateEuclidianDistanceToNeighbour(last, next);
						
						// calculate fragmented ecadherin distances
						// only calculate distance if pixels are connected
						// as fragmented distance is only for clusters of ecadherin
						// and not between clusters
						if(next.x==last.x-1||next.x==last.x+1||next.y==last.y-1||next.y==last.y+1)
							frag+=this.calculateEuclidianDistanceToNeighbour(last, next);
						}	
					last=next;
				}
				
			}
		
		this.eCadherinDistanceToNeighbour=dist;
		this.fragmentedJunctionLength=frag;
		
		//ecadherin euclidian distance is the distance from first ecadherin point to the last ecadherin point
		// along the edge
		if(firstPoint!=null && last!=null)this.eCadherinEuclidianDistance=this.calculateEuclidianDistanceToNeighbour(firstPoint, last);
		else this.eCadherinEuclidianDistance=0;
	}
	
	/************************************************************/
	/* MEASUREMENT CHANNEL MEASUREMENTS							*/
	/* same principle as ecadherin measurements above			*/
	/************************************************************/
	public void getMeasurementPoints(MembraneTool tool) throws Exception{
		this.measurementEdgePoints.clear();
		
		MeasurementTool mt=((MeasurementTool)tool.getMultiChannelImage().getMeasurementTool());
		BufferedImage thresholdImage = mt.getMeasurementThresholdImage();
		
		double dist=0;
		Point last=null;
		Iterator<Point> iter=this.edgePoints.iterator();
		
		while(iter.hasNext()){
				Point next=iter.next();
				int[] pixel = thresholdImage.getRaster().getPixel(next.x, next.y, new int[]{0});
				if(pixel[0]>0){
					this.measurementEdgePoints.add(next);
					if(last!=null&&next!=null&&next.x>=last.x-1&&next.x<=last.x+1&&next.y>=last.y-1&&next.y<=last.y+1)
						dist+=this.calculateEuclidianDistanceToNeighbour(last, next);
				}
				last=next;
			}
		this.measurementDistanceToNeighbour=dist;
	}
	
	/************************************************************/
	/* IMAGE METHODS											*/
	/************************************************************/
	
	public BufferedImage getCornerEdgeImage(MembraneTool panel) throws Exception{
		
		BufferedImage target=this.membrane.getCornerImage(panel,1);
			
		Iterator<Point> itr = this.edgePoints.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	target.getRaster().setPixel(p.x, p.y, new int[]{255,0,0});
	    }
	        
	    
	    itr = this.dilatedEdgePoints.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	int[] pixel = target.getRaster().getPixel(p.x, p.y, new int[]{0,0,0});
	    	if(!ImageUtilities.isInList(this.edgePoints, p))target.getRaster().setPixel(p.x, p.y, new int[]{0,255,0});
	    }
	    
	    itr = this.skeletonisedECadherinEdgePoints.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	if(!ImageUtilities.isInList(this.edgePoints, p))target.getRaster().setPixel(p.x, p.y, new int[]{0,0,255});
	    }
	    
	    
	    itr = this.membrane.cytoPlasmECadherin.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	if(!ImageUtilities.isInList(this.dilatedEdgePoints, p))target.getRaster().setPixel(p.x, p.y, new int[]{255,127,0});
	    }
	    
	    
	   
		
	    return target;
	}
	
	public BufferedImage getDilatedEdgeImage(int dilate) throws Exception{
		BufferedImage target = new BufferedImage(this.membrane.collection.getXDim(), this.membrane.collection.getYDim(), BufferedImage.TYPE_BYTE_BINARY);
		
		Iterator<Point> itr = this.edgePoints.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	target.getRaster().setPixel(p.x, p.y, new int[]{1});
	    }
	    
	    for(int i=0;i<dilate;i++){
	    	target=ImageUtilities.getDilatedImage(target);
	    }
		
	    return target;
	}
	
	
	public BufferedImage getSkeletonisedECadherinEdgeImage() throws Exception{
		BufferedImage target = new BufferedImage(this.membrane.collection.getXDim(), this.membrane.collection.getYDim(), BufferedImage.TYPE_BYTE_BINARY);
		
		Iterator<Point> itr = this.eCadherinEdgePoints.iterator();
	    while(itr.hasNext()) {
	    	Point p = (Point)itr.next();
	    	target.getRaster().setPixel(p.x, p.y, new int[]{1});
	    }	
	    
	    target=ImageUtilities.getSkeletonImage(target, null);
	    
	    this.skeletonisedECadherinEdgePoints.clear();
	    for(int i=0;i<target.getRaster().getWidth();i++)
	    	for(int j=0;j<target.getRaster().getHeight();j++){
	    		if(target.getRaster().getPixel(i, j, new int[]{0})[0]==1)
	    			this.skeletonisedECadherinEdgePoints.add(new Point(i,j));
	    	}
	    return target;
	}

	/************************************************************/
	/* OVERIDDEN METHODS										*/
	/************************************************************/
	public String toString(){
		String str="# "+this.number+" :: "+this.cornerPoint.toString();
		if(this.neighbour!=null)str+=" :: neighbour "+this.neighbour.getNumber()+" :: "+this.neighbour.getCorner().toString()+" :: Distance to Neighbour : "+this.distanceToNeighbour+" :: Euclidian Distance to Neighbour :"+this.euclidianDistanceToNeighbour+" :: Ratio : "+this.getDistanceRatio();
		//str+="\n";
		str+=" :: Dilated Edge Size : "+this.dilatedEdgePoints.size()+" :: ECadherin Points : "+this.eCadherinEdgePoints.size()+" :: Skeletonised ECadherin Points : "+this.skeletonisedECadherinEdgePoints.size()+" :: measurement Points : "+this.measurementEdgePoints.size();
		return str;
	}
}
