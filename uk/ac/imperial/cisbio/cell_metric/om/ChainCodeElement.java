package uk.ac.imperial.cisbio.cell_metric.om;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
public class ChainCodeElement {
	
	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	private int value=0;								//value of chain code
	private Point point;								//colour of this nucleus
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public ChainCodeElement(int val, Point p) {
		this.value=val;
		point=p;
	}
	
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/

	public int getValue(){
		return this.value;
	}
	
	public Point getPoint(){
		return this.point;
	}
	
	
	

}
