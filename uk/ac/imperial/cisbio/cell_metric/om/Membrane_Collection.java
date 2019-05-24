package uk.ac.imperial.cisbio.cell_metric.om;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MembraneTool;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.NucleusTool;

	/************************************************************/
	/* COLLECTION OF CELL MEMBRANES								*/
	/************************************************************/

public class Membrane_Collection extends ArrayList<Cell_Membrane> {
	
	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	private int xDim,yDim;

	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public Membrane_Collection() {
		// TODO Auto-generated constructor stub
	}
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/
	public int getXDim(){
		return xDim;
	}
	
	public void setXDim(int n){
		xDim=n;
	}
	
	public int getYDim(){
		return yDim;
	}
	
	public void setYDim(int n){
		yDim=n;
	}
	
	public int getNumCells(){
		return this.size();
	}
	
	/************************************************************/
	/* UTILITY METHODS											*/
	/************************************************************/
	public ArrayList<Cell_Membrane> getOtherMembraneObjects(Cell_Membrane nuc){
		ArrayList<Cell_Membrane> list = new ArrayList<Cell_Membrane>();
		
		Iterator<Cell_Membrane> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Membrane el = (Cell_Membrane)itr.next();
	        if(el.getNumber()!=nuc.getNumber())list.add(el);
	      }
	    return list;
	}
	
	
	/**
	 * 
	 * @param numbered
	 * @param panel
	 * @return
	 */
	
	public BufferedImage getMembraneImage(boolean numbered, MembraneTool panel){
		
		BufferedImage target = new BufferedImage(this.getXDim(), this.getYDim(), BufferedImage.TYPE_3BYTE_BGR);
		
		for(int i=0;i<panel.getOriginalImage().getWidth();i++)
			for(int j=0;j<panel.getOriginalImage().getHeight();j++){
				int pixel=panel.getOriginalImage().getRaster().getPixel(i, j, new int[]{0})[0];
				target.getRaster().setPixel(i,j,new int[]{pixel,pixel,pixel});
			}
				
		
		Iterator<Cell_Membrane> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Membrane el = (Cell_Membrane)itr.next();
	    	target=el.getMembraneImage(numbered, target);
	    }
	    
	    if(panel !=null)panel.getTool().setTextArea("Total Cells Identified = "+getNumCells()+"\n");
	    return target;
		
	}
	
	/**
	 * get membrane that contains a point
	 * @param p
	 * @return
	 */
	public Cell_Membrane getSelectedMembrane(Point p){
		Iterator<Cell_Membrane> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Membrane el = (Cell_Membrane)itr.next();
	        if(el.isPartofMembrane(p))return el;
	      }
	    return null;
	}
	
	/**
	 * is a point a part of a cell membrane?
	 * @param p
	 * @return
	 */
	public boolean isPartofMembrane(Point p){
		Iterator<Cell_Membrane> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Membrane el = (Cell_Membrane)itr.next();
	        if(el.isPartofMembrane(p))return true;
	      }
	    return false;
	}

}
