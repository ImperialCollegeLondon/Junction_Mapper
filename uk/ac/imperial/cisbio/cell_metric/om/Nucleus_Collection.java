package uk.ac.imperial.cisbio.cell_metric.om;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import uk.ac.imperial.cisbio.cell_metric.utilities.ImageUtilities;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.NucleusTool;

public class Nucleus_Collection extends ArrayList<Cell_Nucleus> {
	
	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	private int xDim,yDim;

	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public Nucleus_Collection() {
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
	
	
	public String getCellNamesString(){
		String str="";
		
		Iterator<Cell_Nucleus> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Nucleus el = (Cell_Nucleus)itr.next();
	        str+=el.getNumber()+", ";
	      }
	    
	    if(str.length()>1) return str;
	    return "";
	}
	
	/************************************************************/
	/* UTILITY METHODS											*/
	/************************************************************/
	
	public void associateMembranes(Membrane_Collection mc){
		ArrayList<Cell_Nucleus> list = new ArrayList<Cell_Nucleus>();
		
		Iterator<Cell_Nucleus> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Nucleus el = (Cell_Nucleus)itr.next();
	    	
	        el.setCellMembrane(mc);
	      }
	}
	
	
	public ArrayList<Cell_Nucleus> getOtherNucleusObjects(Cell_Nucleus nuc){
		ArrayList<Cell_Nucleus> list = new ArrayList<Cell_Nucleus>();
		
		Iterator<Cell_Nucleus> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Nucleus el = (Cell_Nucleus)itr.next();
	        if(el.getNumber()!=nuc.getNumber())list.add(el);
	      }
	    return list;
	}
	
	
	public BufferedImage getNucleusImage(boolean numbered, NucleusTool panel){
		
		BufferedImage target = new BufferedImage(this.getXDim(), this.getYDim(), BufferedImage.TYPE_3BYTE_BGR);
		
		Iterator<Cell_Nucleus> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Nucleus el = (Cell_Nucleus)itr.next();
	    	target=el.getNucleusImage(numbered, target);
	    }
	    
	    if(panel!=null)panel.getTool().setTextArea("Total Cells Identified = "+getNumCells()+"\n");
	    return target;
		
	}
	
	
	public BufferedImage getCombinedImage(boolean numbered){
		
		BufferedImage target = new BufferedImage(this.getXDim(), this.getYDim(), BufferedImage.TYPE_3BYTE_BGR);
		
		Iterator<Cell_Nucleus> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Nucleus el = (Cell_Nucleus)itr.next();
	    	target=el.getCombinedImage(numbered, target);
	    }
	    
	    return target;
		
	}
	

	public Cell_Nucleus getSelectedNucleus(Point p){
		Iterator<Cell_Nucleus> itr = this.iterator();
	    while(itr.hasNext()) {
	    	Cell_Nucleus el = (Cell_Nucleus)itr.next();
	        if(el.isPartofNucleus(p))return el;
	      }
	    return null;
	}
	
	
	

}
