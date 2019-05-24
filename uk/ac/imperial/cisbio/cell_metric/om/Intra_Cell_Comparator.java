package uk.ac.imperial.cisbio.cell_metric.om;

import java.util.Comparator;

/************************************************************/
/* CELL DISTANCE COMPARATOR									*/
/************************************************************/
public class Intra_Cell_Comparator implements Comparator<Intra_Cell_Distance> {
	
	public int compare(Intra_Cell_Distance d1, Intra_Cell_Distance d2) {
			return (d1.getDistance()<d2.getDistance())?-1:1;
	    }


}
