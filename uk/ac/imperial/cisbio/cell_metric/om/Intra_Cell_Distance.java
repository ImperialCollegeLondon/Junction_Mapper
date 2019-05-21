package uk.ac.imperial.cisbio.cell_metric.om;


public class Intra_Cell_Distance {

	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	private Cell_Nucleus fromCell,toCell;
	private double distance;
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public Intra_Cell_Distance(Cell_Nucleus from, Cell_Nucleus to, double distance){
		this.fromCell=from;
		this.toCell=to;
		this.distance=distance;
	}
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/
	public Cell_Nucleus getFromCell(){
		return this.fromCell;
	}
	
	public Cell_Nucleus getToCell(){
		return this.toCell;
	}
	
	public double getDistance(){
		return this.distance;
	}
}

