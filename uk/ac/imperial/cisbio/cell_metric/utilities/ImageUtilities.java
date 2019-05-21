/************************************************************/
/* PACKAGE													*/
/************************************************************/
package uk.ac.imperial.cisbio.cell_metric.utilities;

/************************************************************/
/* IMPORTS													*/
/************************************************************/
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Vector;

import uk.ac.imperial.cisbio.cell_metric.om.Cell_Membrane;
import uk.ac.imperial.cisbio.cell_metric.om.Cell_Nucleus;
import uk.ac.imperial.cisbio.cell_metric.om.Nucleus_Collection;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.ImagePanel;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.ToolPanel;

/************************************************************/
/* CLASS DEFINITION											*/
/************************************************************/
public class ImageUtilities  {

	/************************************************************/
	/* STATIC METHODS - PRIVATE									*/
	/************************************************************/
	/**
	 * Private method to copy an image 
	 * do not change this method and use it when you need to copy an image
	 */
	private static BufferedImage copyImage(BufferedImage source){
		int type=source.getType();
		if(type==0)type=BufferedImage.TYPE_BYTE_GRAY;
		BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(),type);
		Graphics g = target.getGraphics();
		g.drawImage(source, 0, 0, null);
		return target;
	}
	 
	
	/************************************************************/
	/* STATIC METHODS - PUBLIC									*/
	/************************************************************/
	
	/**
	 * resize image to new width and height
	 * 
	 * @param img - image
	 * @param newW - new width
	 * @param newH - new height
	 * @return resized image
	 */
	public static BufferedImage reSizeImage(BufferedImage img, int newW, int newH) {   
        int w = img.getWidth();   
        int h = img.getHeight();  
        BufferedImage dimg=null;
        if(img.getType()!=0)
        	dimg = new BufferedImage(newW, newH, img.getType());   
        else dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = dimg.createGraphics();   
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);   
        g.dispose();   
        return dimg;
	}
	
	
	  /**
	   * return gray scale channel image from colour image
	   * 
	   * @param image
	   * 
	   * @return channel image
	   */
	  public static BufferedImage getChannel(BufferedImage image, int channel){	
			BufferedImage target = new BufferedImage(image.getWidth(), image.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
			
			for(int i=0;i<image.getRaster().getWidth();i++)
				for(int j=0;j<image.getRaster().getHeight();j++){
					int[] pixel = image.getRaster().getPixel(i, j, new int[]{0,0,0,0,0,0});
					int[] np={pixel[channel]};
					target.getRaster().setPixel(i,j,np);
				}
			
			return target;
		}
	  
	 
	  
	  /**
		 * sobel edge detection operator
		 * 
		 * @param image image to edge detect
		 * 
		 * @return Vector -	contains edge image at position 0 
		 * 					array of doubles representing edge direction at position 1
		 */
		
	public static Vector sobelOperator(BufferedImage image)throws Exception {
			int[][] mx={{1,2,1},{0,0,0},{-1,-2,-1}};
			int[][] my={{1,0,-1},{2,0,-2},{1,0,-1}};
			
			double[][] gx=new double[image.getWidth()][image.getHeight()];
			double[][] gy=new double[image.getWidth()][image.getHeight()];
			
			
			WritableRaster raster=image.getRaster();
			int c=1;
			
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){	
					gx[i][j]=0;	
					gy[i][j]=0;
					for(int k=-c;k<(c+1);k++)
						for(int l=-c;l<(c+1);l++){
							int xc=k+i;int yc=l+j;
							if((xc>=0)&&(yc>=0)&&(yc<image.getHeight())&&(xc<image.getWidth())){
								//System.out.println("Coordinates :: "+xc+" :: "+yc);
								int[] pixel=raster.getPixel(xc,yc,new int[]{0});					
								gx[i][j]+=(mx[k+c][l+c]*pixel[0]);
								gy[i][j]+=(my[k+c][l+c]*pixel[0]);
								}	
						}	
				}
			
			
			
			
			BufferedImage target=ImageUtilities.copyImage(image);
			WritableRaster targetRaster=target.getRaster();
			
			double[][] pvs=new double[image.getWidth()][image.getHeight()];
			double[][] theta=new double[image.getWidth()][image.getHeight()];
			double max=0;
			
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){
					pvs[i][j]=Math.abs(Math.sqrt((gx[i][j]*gx[i][j])+(gy[i][j]*gy[i][j])));
					if(max<pvs[i][j])max=pvs[i][j];
					
					if(gx[i][j]==0){
						//System.out.println(""+i+" :: "+j+" p1="+pixel1[0]+" :: p2="+pixel2[0]);
						if(gy[i][j]==0)theta[i][j]=0;else theta[i][j]=90;
					}
					else {
						double ratio=gy[i][j]/gx[i][j];
						theta[i][j]=Math.toDegrees(Math.atan(ratio));
						//if(theta[i][j]>100||theta[i][j]<0)System.out.println("i "+i+"j "+j+" :: "+theta[i][j]);
					
					}
				}
			
			
			double ratio=max/255.0;
			for(int i=0;i<image.getWidth();i++)
				for(int j=0;j<image.getHeight();j++){
					targetRaster.setPixel(i, j, new int[]{(int)Math.round(pvs[i][j]/ratio)});
				}
		
			Vector v=new Vector();
			v.add(target);
			v.add(theta);
			
			return v;
		}
	  
	  
	  /**public static Vector sobelOperator(BufferedImage image)throws Exception {
			BufferedImage target=ImageUtilities.copyImage(image);	
			int width=image.getRaster().getWidth();
			int height=image.getRaster().getHeight();
			float[] GY = new float[width*height];
			float[] GX = new float[width*height];
			int[] total = new int[width*height];
			float[] template={-1,0,1,-2,0,2,-1,0,1};;
			//float[] template={-1,-2,-1,0,0,0,1,2,1};;
			int templateSize=3;
			int sum=0;
			int max=0;
		
			for(int x=(templateSize-1)/2; x<width-(templateSize+1)/2;x++) {
				for(int y=(templateSize-1)/2; y<height-(templateSize+1)/2;y++) {
					sum=0;
		
					for(int x1=0;x1<templateSize;x1++) {
						for(int y1=0;y1<templateSize;y1++) {
							int x2 = (x-(templateSize-1)/2+x1);
							int y2 = (y-(templateSize-1)/2+y1);
							
							int[] pv=image.getRaster().getPixel(x2, y2, new int[]{0});
							float value = (pv[0] & 0xff) * (template[y1*templateSize+x1]);
							sum += value;
						}
					}
					GY[y*width+x] = sum;
					
					sum=0;
					for(int x1=0;x1<templateSize;x1++) {
						for(int y1=0;y1<templateSize;y1++) {
							int x2 = (x-(templateSize-1)/2+x1);
							int y2 = (y-(templateSize-1)/2+y1);
							int[] pv=image.getRaster().getPixel(x2, y2, new int[]{0});
							float value = (pv[0] & 0xff) * (template[x1*templateSize+y1]);
							sum += value;
						}
					}
					GX[y*width+x] = sum;
		
				}
			}
			
			double[][] theta=new double[width][height];
			for(int x=0; x<width;x++) {
				for(int y=0; y<height;y++) {
					total[y*width+x]=(int)Math.sqrt(GX[y*width+x]*GX[y*width+x]+GY[y*width+x]*GY[y*width+x]);
					//theta[x][y]=Math.toDegrees(Math.atan2(GX[y*width+x],GY[y*width+x]));
					if(GX[y*width+x]==0.0){
						if(GY[y*width+x]==0.0)theta[x][y]=0.0;
						else theta[x][y]=90.0;
					}
					else theta[x][y]=Math.toDegrees(Math.atan(Math.abs(GY[y*width+x])/Math.abs(GX[y*width+x])));
					//direction[y*width+x] = Math.atan2(GX[y*width+x],GY[y*width+x]);
					if(max<total[y*width+x])
						max=total[y*width+x];
				}
			}
			float ratio=(float)max/255;
			
			
			
			for(int x=0; x<width;x++) {
				for(int y=0; y<height;y++) {
					sum=(int)(total[y*width+x]/ratio);
					int pv = 0xff000000 | ((int)sum << 16 | (int)sum << 8 | (int)sum);
					target.getRaster().setPixel(x,y, new int[]{pv});
				}
			}
			
			Vector v=new Vector();
			v.add(target);
			v.add(theta);
			
			return v;
			}
	  **/
	 
	
	
	/**
	 * Cannny Operator
	 * 
	 * Implementation of Canny Edge Detection algorithm
	 */

	public static BufferedImage cannyOperator(BufferedImage image,int lowThresh,int highThresh) throws Exception{
		
		//step one blur with gaussian kernel
		//int[][] kernel={{2,4,5,4,2},{4,9,12,9,4},{5,12,15,12,5},{4,9,12,9,4},{2,4,5,4,2}};
		//BufferedImage target=ImageUtilities.convolveImage(image, kernel);	
		//BufferedImage target=image;
		
		//step 2 get sobel operator
		Vector v=ImageUtilities.sobelOperator(image);
		BufferedImage target=(BufferedImage) v.get(0);
		double[][] theta= (double[][])v.get(1);
		
		//step 3 get edge directions
		int t1,t2,t3,t4;
		t1=t2=t3=t4=0;	
		for(int i=0;i<theta.length;i++)
			for(int j=0;j<theta[0].length;j++){
				//System.out.println(""+i+" : "+j+" : Theta before normalizing :: "+theta[i][j]);
				if(theta[i][j]< -67.5 || theta[i][j] >=67.5){theta[i][j]=0;t1++;}
				else if(theta[i][j]>=-67.5 && theta[i][j] < -22.5){theta[i][j]=45;t2++;}
				else if(theta[i][j]>=-22.5 && theta[i][j] < 22.5){theta[i][j]=90;t3++;}
				else if(theta[i][j]>=22.5 && theta[i][j] < 67.5){theta[i][j]=135;t4++;}
			}
		//System.out.println(" 0 degrees :: "+t1+" 45 degrees :: "+t2+ " 90 degrees :: "+t3+ " 135 degrees :: "+t4+" others :: "+t5);
		
		
		//non maximat suppression
		t1=t2=t3=t4=0;
		for(int i=0;i<target.getWidth();i++)
			for(int j=0;j<target.getHeight();j++){
				int[] pixel = target.getRaster().getPixel(i, j, new int[]{0});
				
				if(theta[i][j]==0){
					//System.out.println("Theta 0");
					int[] north={0};
					int[] south={0};		
					if(j>0)north = target.getRaster().getPixel(i, j-1, new int[]{0});
					if(j<target.getHeight()-1)south = target.getRaster().getPixel(i, j+1, new int[]{0});
					if(((south[0]>pixel[0])||(north[0]>pixel[0]))){
						target.getRaster().setPixel(i, j, new int[]{0});
						//System.out.println("Setting pixel to zero (0 degrees) "+i+" :: "+j);
						t1++;
					}
					
				}
				else if(theta[i][j]==45){
					//System.out.println("Theta 45");
					int[] northwest={0};
					int[] southeast={0};		
					if(j>0&&i>0)northwest = target.getRaster().getPixel(i-1, j-1, new int[]{0});
					if(j<target.getHeight()-1&&i<target.getWidth()-1)southeast = target.getRaster().getPixel(i+1, j+1, new int[]{0});
					if((northwest[0]>pixel[0])||(southeast[0]>pixel[0])) {
						//System.out.println("Setting pixel to zero (45 degrees) "+i+" :: "+j);
						target.getRaster().setPixel(i, j, new int[]{0});
						t2++;
					}
					
				}
				else if(theta[i][j]==90){
					//System.out.println("Theta 90");
					int[] east={0};
					int[] west={0};		
					if(i>0)west = target.getRaster().getPixel(i-1, j, new int[]{0});
					if(i<target.getWidth()-1)east = target.getRaster().getPixel(i+1, j, new int[]{0});
					if((east[0]>pixel[0])||(west[0]>pixel[0])){
						//System.out.println("Setting pixel to zero (90 degrees) "+i+" :: "+j);
						target.getRaster().setPixel(i, j, new int[]{0});
						t3++;
					}
					
				}
				else if(theta[i][j]==135){
					//System.out.println("Theta 135");
					int[] northeast={0};
					int[] southwest={0};		
					if(j>0&&i<target.getWidth()-1)northeast = target.getRaster().getPixel(i+1, j-1, new int[]{0});
					if(j<target.getHeight()-1&&i>0)southwest = target.getRaster().getPixel(i-1, j+1, new int[]{0});
					if((northeast[0]>pixel[0])||(southwest[0]>pixel[0])){
						//System.out.println("Setting pixel to zero (135 degrees) "+i+" :: "+j);
						target.getRaster().setPixel(i, j, new int[]{0});
						t4++;
					}
					
				}
			}
		
		//return target;
		/**System.out.println(" 0 degrees :: "+t1+" 45 degrees :: "+t2+ " 90 degrees :: "+t3+ " 135 degrees :: "+t4);**/
		BufferedImage highThreshImage=ImageUtilities.getThresholdImage(target, highThresh, 255);
		BufferedImage lowThreshImage=ImageUtilities.getThresholdImage(target, lowThresh, 255);
		BufferedImage resultImage=new BufferedImage(target.getWidth(),target.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
		
		
		for(int i=0;i<resultImage.getWidth();i++)
			for(int j=0;j<resultImage.getHeight();j++){
				int[] pixel=target.getRaster().getPixel(i, j, new int[]{0});
				if(pixel[0]>highThresh){
					resultImage.getRaster().setPixel(i, j, new int[]{1});
					traceEdge(i,j,lowThresh,resultImage,target);
					}
				}
				
		resultImage = ImageUtilities.removeEdgeTouchingObjects(resultImage);
		
		return resultImage;
		
	}
	
	
	 private static void traceEdge(int x, int y,int lowThresh,BufferedImage resultImage,BufferedImage target){ 	
	    	
	    	WritableRaster raster=target.getRaster();
			ArrayList <Point>pointList = new ArrayList<Point>();
			Point p=new Point(x,y);
			pointList.add(p);
			
			while(pointList.size()>0){
				Point point=pointList.get(0);
				raster.setPixel(point.x, point.y,new int[]{0});
				resultImage.getRaster().setPixel(point.x, point.y,new int[]{1});
				pointList.remove(0);
				
				for(int i=-1;i<2;i++)
					for(int j=-1;j<2;j++){
						int xc=point.x+i;
						int yc=point.y+j;
						
						if((xc>=0)&&(yc>=0)&&(xc<target.getWidth())&&(yc<target.getHeight())){
							int[] mpixels=raster.getPixel(xc,yc,new int[]{0});
							if(mpixels[0]>lowThresh){
								Point np=new Point(xc,yc);
								if(!isInList(pointList,np))pointList.add(np);
								}
							}
					}
				}
	}
	 
	 
	 
	
	
	
	public static BufferedImage getThresholdImage(BufferedImage image,int lowThreshold, int highThreshold){	
		int c=0;
		BufferedImage target = copyImage(image);
		WritableRaster raster=image.getRaster();
		WritableRaster targetRaster=target.getRaster();
		for(int i=0;i<raster.getWidth();i++)
			for(int j=0;j<raster.getHeight();j++){
				int[] pixels=raster.getPixel(i,j,new int[]{0});	
				if(		pixels.length>0&&
						(pixels[0]<=lowThreshold||pixels[0]>highThreshold)){
					pixels[0]=0;
					targetRaster.setPixel(i, j, pixels);
				}
			}
		
		return target;
	}
	
	
	  /**
	   * return thresholded image
	   * @param image, kernal size, addition to mean
	   * @return eroded image
	   */
	  public static BufferedImage getAdaptiveThresholdImage(BufferedImage image,int kernelSize,int cValue){	
			int c=0;
			BufferedImage target = new BufferedImage(image.getWidth(), image.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
			WritableRaster raster=image.getRaster();
			WritableRaster targetRaster=target.getRaster();
			
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){
					int[] pixels=raster.getPixel(i,j,new int[]{0});	
					int total=c=0;
					for(int k=-kernelSize;k<=kernelSize;k++)
						for(int l=-kernelSize;l<=kernelSize;l++){
							int xc=i+k;
							int yc=j+l;
							
							if((xc>=0)&&(yc>=0)&&(xc<image.getWidth())&&(yc<image.getHeight())){
									int[] mpixels=raster.getPixel(xc,yc,new int[]{0});
									total+=mpixels[0];
									c++;
							}
						}
				double mean=(double)total/(double)c;
				mean+=cValue;
				if((double)pixels[0]<mean)targetRaster.setPixel(i, j, new int[]{0});
				else targetRaster.setPixel(i, j, new int[]{1});
				}
			
			return target;
		}
	  
	  
	  /**
	   * return eroded image
	   * @param image
	   * @return eroded image
	   */
	  public static BufferedImage getErodedImage(BufferedImage image) throws Exception{ 
		 if(image.getType()!=BufferedImage.TYPE_BYTE_BINARY&&image.getType()!=BufferedImage.TYPE_BYTE_GRAY) throw new Exception("Cannot Erode : Image is not Binary or Grey Scale");
		  BufferedImage target = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			WritableRaster raster=image.getRaster();
			WritableRaster targetRaster=target.getRaster();
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){
					int[] pixels=raster.getPixel(i,j,new int[]{0,0,0});	
					int[] np = new int[image.getType()==BufferedImage.TYPE_BYTE_BINARY?1:3];
					if(pixels.length>0&&((image.getType()==BufferedImage.TYPE_BYTE_BINARY&&pixels[0]==1)||(image.getType()==BufferedImage.TYPE_BYTE_GRAY))){
						if(image.getType()==BufferedImage.TYPE_BYTE_BINARY)np[0]=1;
						else if(image.getType()==BufferedImage.TYPE_BYTE_GRAY)np[0]=255;
						for(int k=-1;k<2;k++)
							for(int l=-1;l<2;l++){
								int px=i+k;
								int py=j+l;
								if(px>=0&&px<image.getWidth()&&py>=0&&py<image.getHeight()){
									int[] npix=raster.getPixel(px,py,new int[]{0,0,0});	
									if(image.getType()==BufferedImage.TYPE_BYTE_BINARY&&npix[0]==0)np[0]=0;
									else if(image.getType()==BufferedImage.TYPE_BYTE_GRAY&&npix[0]<np[0])np[0]=npix[0];
								}
							}
						
					}
					else np[0]=pixels[0];
					targetRaster.setPixel(i, j, np);
					}
				
			return target;
			
		}
	  
	  
	  
	  public static BufferedImage getSkeletonImage(BufferedImage image,ImagePanel panel) throws Exception{ 
		  if(image.getType()!=BufferedImage.TYPE_BYTE_BINARY) throw new Exception("Cannot Skeletonise : Image is not Binary");
		   BufferedImage target = copyImage(image);
		   
		   boolean done = false;
		  
		   //apply skeletonisation masks
		   while(!done){
			   WritableRaster raster=image.getRaster();
			   WritableRaster targetRaster=target.getRaster();
			   for(int i=0;i<raster.getWidth();i++)
					for(int j=0;j<raster.getHeight();j++){
						int[] pixels=raster.getPixel(i,j,new int[]{0,0,0});	
						int[] np = new int[1];
						np[0]=pixels[0];
						if(pixels.length>0&&pixels[0]==1){
								try{
									int cnt=getNeighbours(image,i,j);
									int cnt2=getZeroOnePatterns(image,i,j);
									if(	(2<=cnt&&cnt<=6) &&
										(cnt2==1)			 &&
										(!getNPixel(image,i,j)||!getEPixel(image,i,j)||!getSPixel(image,i,j)) &&
										(!getEPixel(image,i,j)||!getSPixel(image,i,j)||!getWPixel(image,i,j)))np[0]=0;
								
								}catch(ArrayIndexOutOfBoundsException e){
									System.out.println("i="+i+" : j="+j+" : "+image.getWidth()+" :: "+image.getHeight());
									e.printStackTrace();
									System.out.println("*****************************************************************************");
								}
							}
						
						targetRaster.setPixel(i, j, np);
						}
			   
			   if(sameImage(image,target)){
				   done=true;
				   break;
			   }
			   
			   if(panel!=null)panel.setImage(target);
			   image=copyImage(target);
			   raster=image.getRaster();
			   targetRaster=target.getRaster();
			   
			   for(int i=0;i<raster.getWidth();i++)
					for(int j=0;j<raster.getHeight();j++){
						int[] pixels=raster.getPixel(i,j,new int[]{0,0,0});	
						int[] np = new int[1];
						np[0]=pixels[0];
						if(pixels.length>0&&pixels[0]==1){
								try{
									int cnt=getNeighbours(image,i,j);
									int cnt2=getZeroOnePatterns(image,i,j);
									if(	(2<=cnt&&cnt<=6) &&
										(cnt2==1)			 &&
										(!getNPixel(image,i,j)||!getEPixel(image,i,j)||!getWPixel(image,i,j)) &&
										(!getNPixel(image,i,j)||!getSPixel(image,i,j)||!getWPixel(image,i,j)))np[0]=0;
								
								}catch(ArrayIndexOutOfBoundsException e){
									System.out.println("i="+i+" : j="+j+" : "+image.getWidth()+" :: "+image.getHeight());
									e.printStackTrace();
									System.out.println("*****************************************************************************");
								}
							}
						
						targetRaster.setPixel(i, j, np);
						}
			   
			   if(sameImage(image,target)){
				   done=true;
				   break;
			   }
			   
			   if(panel!=null)panel.setImage(target);
			   image=copyImage(target);
		   	}
				
			return target;
			
		}
	  
	  
	  private static boolean sameImage(BufferedImage im1, BufferedImage im2) throws Exception{
		  
		  if(im1.getType()!=BufferedImage.TYPE_BYTE_BINARY || im2.getType()!=BufferedImage.TYPE_BYTE_BINARY) 
			  		throw new Exception("Cannot Compare Images : Both images are not Binary");
		  
		  WritableRaster raster1=im1.getRaster();
		  WritableRaster raster2=im2.getRaster();
		  
		  for(int i=0;i<raster1.getWidth();i++)
				for(int j=0;j<raster1.getHeight();j++){
						int[] p1=raster1.getPixel(i,j,new int[]{0,0,0});	
						int[] p2=raster2.getPixel(i,j,new int[]{0,0,0});						
						if(p1[0]!=p2[0])return false;
				}
		 
		  return true;
		  
	  }
	  
	  public static int getNeighbours(BufferedImage im,int x, int y){
		  int cnt=0;
		  
		  if(getNWPixel(im,x,y))cnt++;
		  if(getNPixel(im,x,y))cnt++;
		  if(getNEPixel(im,x,y))cnt++;
		  if(getEPixel(im,x,y))cnt++;
		  if(getSEPixel(im,x,y))cnt++;
		  if(getSPixel(im,x,y))cnt++;
		  if(getSWPixel(im,x,y))cnt++;
		  if(getWPixel(im,x,y))cnt++;	  
		  
		  return cnt;
	  }
	  
	  private static int getZeroOnePatterns(BufferedImage im,int x, int y){
		  int cnt=0;
		  
		  if(!getNPixel(im,x,y)&&getNEPixel(im,x,y))cnt++;
		  if(!getNEPixel(im,x,y)&&getEPixel(im,x,y))cnt++;
		  if(!getEPixel(im,x,y)&&getSEPixel(im,x,y))cnt++;
		  if(!getSEPixel(im,x,y)&&getSPixel(im,x,y))cnt++;
		  if(!getSPixel(im,x,y)&&getSWPixel(im,x,y))cnt++;
		  if(!getSWPixel(im,x,y)&&getWPixel(im,x,y))cnt++;
		  if(!getWPixel(im,x,y)&&getNWPixel(im,x,y))cnt++;
		  if(!getNWPixel(im,x,y)&&getNPixel(im,x,y))cnt++;
		  
		  return cnt;
	  }
	  
	  public static boolean getNWPixel(BufferedImage im,int x, int y){		  
		  if(x>0&&y>0){
			  int pix[]=im.getRaster().getPixel(x-1, y-1, new int[]{0});
			  if(pix[0]==1)return true;
		  }
		  
		  return false;
	  }
	  
	  public static boolean getNPixel(BufferedImage im,int x, int y) {		  
		  if(y>0){
			  int pix[]=im.getRaster().getPixel(x, y-1, new int[]{0});
			  if(pix[0]==1)return true;
		  }	  
		  return false;
	  }
	  
	  public static boolean getNEPixel(BufferedImage im,int x, int y) {		  
		  if(x<im.getWidth()-1&&y>0){
			  int pix[]=im.getRaster().getPixel(x+1, y-1, new int[]{0});
			  if(pix[0]==1)return true;
		  }
		  
		  return false;
	  }
	  
	  public static boolean getWPixel(BufferedImage im,int x, int y) {		  
		  if(x>0){
			  int pix[]=im.getRaster().getPixel(x-1, y, new int[]{0});
			  if(pix[0]==1)return true;
		  }
		  
		  return false;
	  }
	  
	  public static boolean getEPixel(BufferedImage im,int x, int y) {		  
		  if(x<(im.getWidth()-1)){
			  int pix[]=im.getRaster().getPixel(x+1, y, new int[]{0});
			  if(pix[0]==1)return true;
		  }
		  
		  return false;
	  }
	  
	  public static boolean getSWPixel(BufferedImage im,int x, int y)  {		  
		  if(x>0&&y<(im.getHeight()-1)){
			  int pix[]=im.getRaster().getPixel(x-1, y+1, new int[]{0});
			  if(pix[0]==1)return true;
		  }
		  
		  return false;
	  }
	  
	  public static boolean getSPixel(BufferedImage im,int x, int y)  {		  
		  if(y<(im.getHeight()-1)){
			  int pix[]=im.getRaster().getPixel(x, y+1, new int[]{0});
			  if(pix[0]==1)return true;
		  }	  
		  return false;
	  }
	  
	  public static boolean getSEPixel(BufferedImage im,int x, int y)  {		  
		  if(x<im.getWidth()-1&&y<(im.getHeight()-1)){
			  int pix[]=im.getRaster().getPixel(x+1, y+1, new int[]{0});
			  if(pix[0]==1)return true;
		  }
		  
		  return false;
	  }
	  
	  /**
	   * return dilated image
	   * @param image
	   * @return dilated image
	   */
	  public static BufferedImage getDilatedImage(BufferedImage image) throws Exception{ 
		  if(image.getType()!=BufferedImage.TYPE_BYTE_BINARY&&image.getType()!=BufferedImage.TYPE_BYTE_GRAY) throw new Exception("Cannot Dilate : Image is not Binary or Gray scale");
		  	BufferedImage target = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			WritableRaster raster=image.getRaster();
			WritableRaster targetRaster=target.getRaster();
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){
					int[] pixels=raster.getPixel(i,j,new int[]{0,0,0});	
					int[] np = new int[image.getType()==BufferedImage.TYPE_BYTE_BINARY?1:3];
					if(pixels.length>0&&((image.getType()==BufferedImage.TYPE_BYTE_BINARY&&pixels[0]==0)||(image.getType()==BufferedImage.TYPE_BYTE_GRAY))){
						np[0]=0;boolean edge=false;
						for(int k=-1;k<2;k++)
							for(int l=-1;l<2;l++){
								int px=i+k;
								int py=j+l;
								if(px>=1&&px<image.getWidth()-1&&py>=1&&py<image.getHeight()-1){
									int[] npix=raster.getPixel(px,py,new int[]{0,0,0});	
									if(image.getType()==BufferedImage.TYPE_BYTE_BINARY && npix[0]==1)np[0]=1;
									else if(image.getType()==BufferedImage.TYPE_BYTE_GRAY && npix[0]>np[0])np[0]=npix[0];
								}
								else {
									edge=true;
									np[0]=0;
								}
							}
						if(edge)np[0]=0;		
					}
					else np[0]=pixels[0];
					targetRaster.setPixel(i, j, np);
					}
				
			return target;
			
		}
	  
	  
	  /**
	   * return dilated image
	   * @param image
	   * @return dilated image
	   */
	  public static BufferedImage removeEdgeTouchingObjects(BufferedImage image) throws Exception{ 
		  if(image.getType()!=BufferedImage.TYPE_BYTE_BINARY) throw new Exception("Cannot Remove Edge Objects : Image is not Binary");
		   BufferedImage target = copyImage(image);
			WritableRaster raster=image.getRaster();
			WritableRaster targetRaster=target.getRaster();
			
			for(int i=0;i<raster.getWidth();i++){
				int[] pixels=raster.getPixel(i,0,new int[]{0,0,0});	
				if(pixels[0]==1)removeObject(target,i,0);	
				pixels=raster.getPixel(i,image.getHeight()-1,new int[]{0,0,0});	
				if(pixels[0]==1)removeObject(target,i,image.getHeight()-1);	
			}
			
			for(int j=0;j<raster.getHeight();j++){
				int[] pixels=raster.getPixel(0,j,new int[]{0,0,0});	
				if(pixels[0]==1)removeObject(target,0,j);	
				pixels=raster.getPixel(image.getWidth()-1,j,new int[]{0,0,0});	
				if(pixels[0]==1)removeObject(target,image.getWidth()-1,j);	
			}
				
			return target;
		}
	  
	  /**
	   * countObjects in image
	   * 
	   * @param image
	   * @return
	   */
	  public static ArrayList<Object> countNucleusObjects(BufferedImage image) throws Exception{
		    ArrayList<Object> returnObj=new ArrayList<Object>();
		    Nucleus_Collection nucleii=new Nucleus_Collection();
		    nucleii.setXDim(image.getWidth());
		    nucleii.setYDim(image.getHeight());
			int count=0;
			boolean done=false;
			
			BufferedImage image2=copyImage(image);
			WritableRaster raster= image2.getRaster();
			
			//convert to color image
			BufferedImage target = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			//Graphics g = target.getGraphics();
			//g.setColor(Color.BLUE);
			
			
			while(!done){
				boolean foundObject=false;
				for(int i=0;i<raster.getWidth();i++){
					for(int j=0;j<raster.getHeight();j++){
						int[] mpixels=raster.getPixel(i,j,new int[]{0});
						if(mpixels.length>0&&mpixels[0]==1){
								foundObject=true;
								Cell_Nucleus nuc=removeObject(image2,i,j);
								nucleii.add(nuc);
								Color c=copyObject(target,image,i,j);
								nuc.setColour(c);
								nuc.setCollection(nucleii);
								count++;
								nuc.setNumber(count);
								//g.drawString( ""+count,i, j);		
								break;
							}			
						}
					}
				done=!foundObject;
				}
				
			returnObj.add(target);
			returnObj.add(nucleii);
			return returnObj;
		}
	  
	  
	  /**
	   * remove object
	   * @param image, x,y coordinates
	   * @return image with object removed
	   */
	  public static Cell_Nucleus removeObject(BufferedImage image, int x,int y) throws Exception{
			try{	
				if(image.getType()!=BufferedImage.TYPE_BYTE_BINARY) throw new Exception("Image is not binary type");
				WritableRaster raster=image.getRaster();
				ArrayList <Point>returnList = new ArrayList<Point>();
				ArrayList <Point>pointList = new ArrayList<Point>();
				Cell_Nucleus nuc=new Cell_Nucleus();
				Point p=new Point(x,y);
				pointList.add(p);
				int pixels=0;
				
				while(pointList.size()>0){
					Point point=pointList.get(0);
					raster.setPixel(point.x, point.y,new int[]{0});
					returnList.add(point);
					pointList.remove(0);
					pixels++;
					for(int i=-1;i<2;i++)
						for(int j=-1;j<2;j++){
							int xc=point.x+i;int yc=point.y+j;
							
							if((xc>=0)&&(yc>=0)&&(xc<raster.getWidth())&&(yc<raster.getHeight())){
								int[] mpixels=raster.getPixel(xc,yc,new int[]{0});
								if(mpixels[0]==1){
									Point np=new Point(xc,yc);
									if(!isInList(pointList,np))pointList.add(np);
									}
								}
						}
					}
				
				nuc.setPointSet(returnList);
				return nuc;	
				
			}catch(Exception e){
				System.exit(0);
				e.printStackTrace();
				
			}
			return null;
			
			}
	  
	  /**
	   * copy object
	   * @param image, x,y coordinates
	   * @return image with object copied
	   */
	  public static Color copyObject(BufferedImage target, BufferedImage image, int x,int y){
			try{	
				WritableRaster raster=target.getRaster();
				WritableRaster raster2=image.getRaster();
				ArrayList <Point>pointList = new ArrayList<Point>();
				Point p=new Point(x,y);
				pointList.add(p);
				int pixels=0;
				int r=(int)(Math.random()*255.0);
				int g=(int)(Math.random()*255.0);
				int b=(int)(Math.random()*255.0);
				
				while(pointList.size()>0){
					Point point=pointList.get(0);
					raster.setPixel(point.x, point.y,new int[]{r,g,b});
					raster2.setPixel(point.x, point.y,new int[]{0});
					pointList.remove(0);
					pixels++;
					for(int i=-1;i<2;i++)
						for(int j=-1;j<2;j++){
							int xc=point.x+i;int yc=point.y+j;
							
							if((xc>=0)&&(yc>=0)&&(xc<raster.getWidth())&&(yc<raster.getHeight())){
								int[] mpixels=raster2.getPixel(xc,yc,new int[]{0});
								if(mpixels[0]==1){
									Point np=new Point(xc,yc);
									if(!isInList(pointList,np))pointList.add(np);
									}
								}
						}
					}
				
				return new Color(r,g,b);	
				
			}catch(Exception e){
				System.exit(0);
				e.printStackTrace();
				
			}
			return null;
			
			}
		
		public static boolean isInList(ArrayList<Point> list,Point p){
			for(int i=0;i<list.size();i++){
				Point lp=list.get(i);
				if(p.x==lp.x && p.y==lp.y)return true;
			}
			return false;
		}
		
		
		 /**
		   * remove Small objects in image
		   * 
		   * @param image
		   * @param minimum size of objects to be retained
		   * @return image without small objects
		   */
		  public static BufferedImage removeSmallObjects(BufferedImage image, int minSize) throws Exception{
			  	
			   if(image.getType()!=BufferedImage.TYPE_BYTE_BINARY) throw new Exception("Image is not binary type");
				
				
				BufferedImage image2=copyImage(image);
				WritableRaster raster= image2.getRaster();
				BufferedImage target = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
				
				
				boolean done=false;
				while(!done){
					boolean foundObject=false;
					for(int i=0;i<raster.getWidth();i++){
						for(int j=0;j<raster.getHeight();j++){
							int[] mpixels=raster.getPixel(i,j,new int[]{0});
							if(mpixels.length>0&&mpixels[0]==1){
									foundObject=true;
									Cell_Nucleus nuc=removeObject(image2,i,j);
									if(nuc.getArea()>=minSize){
										//System.out.println("Copy Object :: "+pixels+" :: "+i+" :: "+j);
										copyBinaryObject(target,image,i,j);
									}
									break;
								}			
							}
						}
					done=!foundObject;
					}
					
				return target;
			}
		  
		  
		  
		  /**
		   * remove trailing edges in image
		   * removes any objects that have a single pixel start until they meet a junction with more than one other pixel
		   * used for removing trailing edges
		   * 
		   * @param image
		   * 
		   * @return image without trailing edges
		   */
		  public static BufferedImage removeTrailingEdges(ImagePanel imagePanel) throws Exception{
			  	
			   BufferedImage image=imagePanel.getImage();
			   if(image.getType()!=BufferedImage.TYPE_BYTE_BINARY) throw new Exception("Image is not binary type");
			   BufferedImage target=copyImage(image);
			   ArrayList<Point> points=new ArrayList<Point>();
			   //System.out.println("**************************************");
				for(int i=0;i<target.getWidth();i++)
					for(int j=0;j<target.getHeight();j++){
							int[] mpixels=target.getRaster().getPixel(i,j,new int[]{0});
							if(mpixels.length>0&&mpixels[0]==1){
								 if(getNeighbours(target,i,j)<2){
									 //System.out.println("Found Trailing Edge :: "+i+" :: "+j);
									 points.add(new Point(i,j));
									 //target=removeTrailingEdge(target,i,j);
									 //imagePanel.setImage(target);
									 //foundObject=true;
								 }
								
							}
						}
					
					
				
				while(points.size()>0){
					Point point=points.remove(0);
					target=removeTrailingEdge(target,point.x,point.y);
					 //System.out.println("Removed Trailing Edge :: "+point.x+" :: "+point.y);
					imagePanel.setImage(target);
				}
					
				return target;
			}
		  
		  
		  /**
		   * remove object
		   * @param image, x,y coordinates
		   * @return image with object removed
		   */
		  public static BufferedImage removeTrailingEdge(BufferedImage image, int x,int y) throws Exception{
				try{	
					if(image.getType()!=BufferedImage.TYPE_BYTE_BINARY) throw new Exception("Image is not binary type");
					WritableRaster raster=image.getRaster();
					ArrayList <Point>pointList = new ArrayList<Point>();
					Point p=new Point(x,y);
					pointList.add(p);
					
					while(pointList.size()>0){
						Point point=pointList.get(0);
						raster.setPixel(point.x, point.y,new int[]{0});
						pointList.remove(0);
						//System.out.println("Deleted pixel :: "+point.x+" :: "+point.y);
						
						ArrayList <Point>newList = new ArrayList<Point>();
						for(int i=-1;i<2;i++)
							for(int j=-1;j<2;j++){
								int xc=point.x+i;int yc=point.y+j;						
								if((xc>=0)&&(yc>=0)&&(xc<raster.getWidth())&&(yc<raster.getHeight())){
									int[] mpixels=raster.getPixel(xc,yc,new int[]{0});
									if(mpixels[0]==1){
										newList.add(new Point(xc,yc));
										//System.out.println("Added "+xc+" :: "+yc+" to new List");
										}
									}
							}
						 if(newList.size()==1){
							 for(int i=0;i<newList.size();i++){
								 Point np=newList.get(i);
								 if(!isInList(pointList,np)){
									 pointList.add(np);
									 //System.out.println("Adding "+np.getX()+" :: "+np.getY()+" to point List");
								 }
							 }
							 
						 }
						// System.out.println("********************************************");
						}
					
					return image;	
					
				}catch(Exception e){
					System.exit(0);
					e.printStackTrace();
					
				}
				return null;
				
				}
		  
		  
		  
		  /**
		   * copy object
		   * @param image, x,y coordinates
		   * @return image with object copied
		   */
		  public static int copyBinaryObject(BufferedImage target, BufferedImage image, int x,int y){
				try{	
					WritableRaster raster=target.getRaster();
					WritableRaster raster2=image.getRaster();
					ArrayList <Point>pointList = new ArrayList<Point>();
					Point p=new Point(x,y);
					pointList.add(p);
					int pixels=0;
					
					while(pointList.size()>0){
						Point point=pointList.get(0);
						raster.setPixel(point.x, point.y,new int[]{1});
						raster2.setPixel(point.x, point.y,new int[]{0});
						pointList.remove(0);
						pixels++;
						for(int i=-1;i<2;i++)
							for(int j=-1;j<2;j++){
								int xc=point.x+i;int yc=point.y+j;
								
								if((xc>=0)&&(yc>=0)&&(xc<raster.getWidth())&&(yc<raster.getHeight())){
									int[] mpixels=raster2.getPixel(xc,yc,new int[]{0});
									if(mpixels[0]==1){
										Point np=new Point(xc,yc);
										if(!isInList(pointList,np))pointList.add(np);
										}
									}
							}
						}
					
					return pixels;	
					
				}catch(Exception e){
					System.exit(0);
					e.printStackTrace();
					
				}
				return 0;
				
				}
		  
		
		  /**
		   * Convolution Operator 
		   * Convolve Image with mask
		   * 
		   * @param image, mask
		   * 
		   * @return Buffered image
		   */
		
		public static BufferedImage convolveImage(BufferedImage image, int[][] mask) throws Exception{
			BufferedImage target=ImageUtilities.copyImage(image);
			
			if(mask==null||mask.length!=mask[0].length)throw new Exception("Mask does not exist or is not square");
			double t=0.0;
			double[][] kernel=new double[mask.length][mask.length];
			for(int i=0;i<mask.length;i++)
				for(int j=0;j<mask.length;j++)t+=mask[i][j];
			
			//System.out.println("t = "+t);
			
			for(int i=0;i<mask.length;i++)
				for(int j=0;j<mask.length;j++){
					if(t!=0.0)kernel[i][j]=mask[i][j]/t;
					else kernel[i][j]=mask[i][j];
					//System.out.println("kerenel["+i+"]["+j+"] = "+kernel[i][j]);
				}
				
			
			WritableRaster raster=image.getRaster();
			WritableRaster targetRaster=target.getRaster();
			int c=(mask.length/2);
			
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){	
					double total=0.0;	
					for(int k=-c;k<(c+1);k++)
						for(int l=-c;l<(c+1);l++){
							int xc=k+i;int yc=l+j;
							if((xc>=0)&&(yc>=0)&&(yc<image.getHeight())&&(xc<image.getWidth())){
								//System.out.println("Coordinates :: "+xc+" :: "+yc);
								int[] pixel=raster.getPixel(xc,yc,new int[]{0});					
								total+=(kernel[k+c][l+c]*pixel[0]);
								}	
				  }	
				int pixelValue=(int)Math.round(total);
				targetRaster.setPixel(i, j, new int[]{pixelValue});
				}
			return target;
		}
		
		
		 /**
		   * Invert Operator 
		   * 
		   * @param image
		   * 
		   * @return Buffered image
		   */
		
		public static BufferedImage invertImage(BufferedImage image) throws Exception{
			BufferedImage target=ImageUtilities.copyImage(image);
			
			WritableRaster raster=image.getRaster();
			WritableRaster targetRaster=target.getRaster();
			
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){
					int[] pixel=raster.getPixel(i,j,new int[]{0});					
					int pixelValue=255-pixel[0];				
					targetRaster.setPixel(i, j, new int[]{pixelValue});
				}
			return target;
		}
		
		
		public static BufferedImage scaleImage(BufferedImage image) throws Exception{
			BufferedImage target=ImageUtilities.copyImage(image);
			
			WritableRaster raster=image.getRaster();
			WritableRaster targetRaster=target.getRaster();
			int min=255,max=0; 
			
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){
					int[] pixel=raster.getPixel(i,j,new int[]{0});					
					if(pixel[0]<min)min=pixel[0];
					else if(pixel[0]>max)max=pixel[0];
				}
			
			int range=max-min;
			double scale=255.0/(double)range;
			
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){
					int[] pixel=raster.getPixel(i,j,new int[]{0});					
					int pix=(int)Math.round((pixel[0]-min)*scale);
					if(pix>255)pix=255;
					else if(pix<0)pix=0;
					targetRaster.setPixel(i, j, new int[]{pix});
				}
			return target;
		}
		
		
		/**
		 * median Filter - perform median filter on image
		 * @param image - the image
		 * @param maskSize - the maskSize (i.e. 3= 3x3, 5 = 5x5)
		 * @return - the filtered image
		 * @throws Exception
		 */
		
		public static BufferedImage medianFilter(BufferedImage image, int maskSize) throws Exception{
			BufferedImage target=ImageUtilities.copyImage(image);
			
			if(maskSize%2!=1)throw new Exception("Mask dimension is not an odd number");
			WritableRaster raster=image.getRaster();
			WritableRaster targetRaster=target.getRaster();
			int c=(maskSize/2);
			int[] pixels=new int[maskSize*maskSize];
			for(int i=0;i<raster.getWidth();i++)
				for(int j=0;j<raster.getHeight();j++){
					int cnt	=0;
					for(int k=0;k<pixels.length;k++)pixels[k]=0;
					for(int k=-c;k<(c+1);k++)
						for(int l=-c;l<(c+1);l++){
							int xc=k+i;int yc=l+j;
							if((xc>=0)&&(yc>=0)&&(xc<image.getWidth())&&(yc<image.getHeight())){
								int[] pixel=raster.getPixel(xc,yc,new int[]{0});					
								pixels[cnt]=pixel[0];
								cnt++;
							}	
				  }	
				
				targetRaster.setPixel(i, j, new int[]{getMedianValue(pixels,cnt)});
				}
			return target;
		}
		
		private static int getMedianValue(int[] pixels,int c){
			//sort values
			//bubble sort
			for(int i=0;i<pixels.length;i++){
				for(int j=i;j<pixels.length;j++){
					if(pixels[j]>pixels[i]){
						int tmp=pixels[j];
						pixels[j]=pixels[i];
						pixels[i]=tmp;
					}
				}
			}
			
			int index=c/2;
			return pixels[index];
		}
		
		
		
		public static BufferedImage superImposeImage(BufferedImage binaryImage, BufferedImage greyScaleImage){
			
			BufferedImage target=new BufferedImage(binaryImage.getWidth(),binaryImage.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
			WritableRaster targetRaster=target.getRaster();
			
			for(int i=0;i<targetRaster.getWidth();i++)
				for(int j=0;j<targetRaster.getHeight();j++){	
					int binaryValue=binaryImage.getRaster().getPixel(i, j, new int[]{0})[0];
					if(binaryValue==1)targetRaster.setPixel(i, j, new int[]{255, 255, 255});
					else {
						int pix=greyScaleImage.getRaster().getPixel(i, j, new int[]{0})[0];
						if(pix==255)pix=254;
						targetRaster.setPixel(i, j, new int[]{pix,pix,pix});	
					}
				}
			
			return target;
		}
		
		
		public static BufferedImage reverseSuperImposeImage(BufferedImage greyScaleImage){
			
			BufferedImage target=new BufferedImage(greyScaleImage.getWidth(),greyScaleImage.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
			WritableRaster targetRaster=target.getRaster();
			
			for(int i=0;i<targetRaster.getWidth();i++)
				for(int j=0;j<targetRaster.getHeight();j++){		
					int binaryValue1=greyScaleImage.getRaster().getPixel(i, j, new int[]{0,0,0})[0];
					int binaryValue2=greyScaleImage.getRaster().getPixel(i, j, new int[]{0,0,0})[1];
					int binaryValue3=greyScaleImage.getRaster().getPixel(i, j, new int[]{0,0,0})[2];
					if(binaryValue1==255&&binaryValue2==255&&binaryValue3==255)targetRaster.setPixel(i, j, new int[]{ 1});
					else targetRaster.setPixel(i, j, new int[]{0});	
				}
			
			return target;
		}
		
		
		public static BufferedImage combineImage(BufferedImage edgeImage,BufferedImage nucleusImage){
			
			BufferedImage target=new BufferedImage(edgeImage.getWidth(),edgeImage.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
			WritableRaster targetRaster=target.getRaster();
			
			for(int i=0;i<targetRaster.getWidth();i++)
				for(int j=0;j<targetRaster.getHeight();j++){			
					int[] binaryValue=edgeImage.getRaster().getPixel(i, j, new int[]{0,0,0});
					if(binaryValue[0]==1)targetRaster.setPixel(i, j, new int[]{255,255,255});
					else {
						int[] red=nucleusImage.getRaster().getPixel(i, j, new int[]{0,0,0});
						if(red[0]>0||red[1]>0||red[2]>0)targetRaster.setPixel(i, j, new int[]{255,0,0});	
					}
				}
			
			return target;
		}
		
		
		/**
		 * getLocalMaxima - perform local maxima on binary image
		 * @param binaryImage - the binary image (edge map)
		 * @param greyScaleImage - greyscale immge
		 * @param maskSize - the maskSize (i.e. 3= 3x3, 5 = 5x5)
		 * @return - binary image with points moved to local maxima
		 * @throws Exception
		 */
		
		public static BufferedImage getLocalMaxima(BufferedImage binaryImage,BufferedImage grayScaleImage, int maskSize) throws Exception{
			
			BufferedImage target=new BufferedImage(binaryImage.getWidth(),binaryImage.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
			WritableRaster targetRaster=target.getRaster();
			int c=(maskSize/2);
			
			for(int i=0;i<targetRaster.getWidth();i++)
				for(int j=0;j<targetRaster.getHeight();j++){			
					int[] binaryValue=binaryImage.getRaster().getPixel(i, j, new int[]{0});
					if(binaryValue[0]==1){
						int maxX=i; int maxY=j;int maxVal=0;
						for(int k=-c;k<(c+1);k++)
							for(int l=-c;l<(c+1);l++){
								int xc=k+i;int yc=l+j;
								if((xc>=0)&&(yc>=0)&&(xc<binaryImage.getWidth())&&(yc<binaryImage.getHeight())){
									int[] pixel=grayScaleImage.getRaster().getPixel(xc,yc,new int[]{0});
									if(pixel[0]>maxVal){
										maxX=xc;maxY=yc;maxVal=pixel[0];
									}
						
								}
							}
						targetRaster.setPixel(maxX, maxY, new int[] {1});
					}
				}
			
			for(int i=0;i<c;i++)target=getDilatedImage(target);
			for(int i=0;i<c;i++)target=getErodedImage(target);
			
			return target;
		}
		
		

		/**
		 * selectCell - select a cell for processing
		 * @param rgbImage - the rgbimage with edgemap and edge staining (edge map)
		 * @param x,y  - the x and y coordinates of the seed point
		 *
		 * @return - image 
		 * @throws Exception
		 */
		
		public static Cell_Membrane selectCell(ImagePanel panel, BufferedImage binaryImage, int x, int y) throws Exception{
		//public static Cell_Membrane selectCell(ToolPanel panel, BufferedImage binaryImage, int x, int y) throws Exception{
			
			Cell_Membrane membrane=new Cell_Membrane();
			BufferedImage target = panel.getImage();
			WritableRaster targetRaster=target.getRaster();
			ArrayList <Point>pointList = new ArrayList<Point>();
			ArrayList <Point>visitedList = new ArrayList<Point>();
			Point p=new Point(x,y);
			pointList.add(p);
			
			int red=(int)(Math.random()*255.0);
			int green=(int)(Math.random()*255.0);
			int blue=(int)(Math.random()*255.0);
			membrane.setColour(new Color(red,green,blue));
			
			while(pointList.size()>0){
				Point point=pointList.get(0);
				pointList.remove(0);
				membrane.addPoint(point);
				visitedList.add(point);
				
				int[] pix=binaryImage.getRaster().getPixel(point.x, point.y, new int[]{0});
				int[] visited=targetRaster.getPixel(point.x,point.y,new int[]{0,0,0});
				
				if(pix[0]==0&&!(visited[0]==red&&visited[1]==green&&visited[2]==blue)) {	
					targetRaster.setPixel(point.x,point.y,new int[]{red,green,blue});
					panel.repaint();
					if(		!getNPixel(binaryImage,point.x,point.y)&&
							!getNEPixel(binaryImage,point.x,point.y)&&
							!getEPixel(binaryImage,point.x,point.y)&&
							!getSEPixel(binaryImage,point.x,point.y)&&
							!getSPixel(binaryImage,point.x,point.y)&&
							!getSWPixel(binaryImage,point.x,point.y)&&
							!getWPixel(binaryImage,point.x,point.y)&&
							!getNWPixel(binaryImage,point.x,point.y)
					){
						for(int i=-1;i<=1;i++)
							for(int j=-1;j<=1;j++){
								int xc=point.x+i;int yc=point.y+j;
								if((xc>=0)&&(yc>=0)&&(xc<binaryImage.getWidth())&&(yc<binaryImage.getHeight())){
									if(!(i==0&&j==0)){
										Point np=new Point(xc,yc);
										pointList.add(np);
									}
									
								}
							}	
					}
					else {
						
						membrane.addEdgePoint(point);
					}
					
				}
				
			}
			
			
			return membrane;
		}
				
		
		
		
}
