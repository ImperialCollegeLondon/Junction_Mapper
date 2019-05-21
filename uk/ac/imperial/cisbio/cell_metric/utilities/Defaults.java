package uk.ac.imperial.cisbio.cell_metric.utilities;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

public class Defaults {
	/*****************************************************************************/
	/* Defaults.java                                                        	 */
	/* file containing default settings                        					 */
	/*****************************************************************************/

	
	 //default sleep time for experiments used in auto run mode (2000 ms= 2 seconds)
	 //also used in experimentModule
	 public static final long default_Sleep_Time=2000;
	 
	 //default font settings
	 public static final Font default_Font = new Font("Dialog", Font.PLAIN, 12);
	 public static final Font bold_Font = new Font("Dialog", Font.BOLD, 14);
	 public static final Font big_bold_Font = new Font("Dialog", Font.BOLD, 16);
	 public static final Font medium_Font = new Font("Dialog", Font.PLAIN, 10);
	 public static final Font small_Font = new Font("Dialog", Font.PLAIN, 8);
	 public static final Font tiny_Font = new Font("Dialog", Font.PLAIN, 6);
	 public static final Font logo_Font = new Font("Dialog", Font.ITALIC, 30);
	 public static final Font small_Courier = new Font("Courier", Font.PLAIN, 9);
	 public static final int label_Height=26;
	 
	 //number formatting types for general use in experiments
	 public static java.text.DecimalFormat doubleFormat=new java.text.DecimalFormat("0.00");
	 public static java.text.DecimalFormat percentFormat=new java.text.DecimalFormat("0%");
	 public static java.text.DecimalFormat double3DP=new java.text.DecimalFormat("0.000");
	 public static java.text.DecimalFormat thousandPoundFormat=new java.text.DecimalFormat("£000.00");
	 public static java.text.DecimalFormat poundFormat=new java.text.DecimalFormat("£0.00");
	 public static java.text.DecimalFormat dollarFormat=new java.text.DecimalFormat("$0.00");
	 public static java.text.DecimalFormat millionDollarFormat=new java.text.DecimalFormat("$0M");
	 public static java.text.DecimalFormat integerFormat=new java.text.DecimalFormat("00");
	 public static java.text.DecimalFormat intFormat=new java.text.DecimalFormat("0");
	 public static java.text.DecimalFormat largeIntegerFormat=new java.text.DecimalFormat("0000");
	 public static java.text.DecimalFormat penceFormat=new java.text.DecimalFormat("0p");
	 
	 public static String getDateTime() {
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy_MM_dd_HHmm");
			return formatter.format(new Date());
		}
	 
	 public static void copyFile(File sourceFile, File destFile) throws IOException {     
		 if(!destFile.exists()) {         
			 destFile.createNewFile();     
			 }      
		 FileChannel source = null;     
		 FileChannel destination = null;      
		 try {         
			 source = new FileInputStream(sourceFile).getChannel();         
			 destination = new FileOutputStream(destFile).getChannel();         
			 destination.transferFrom(source, 0, source.size());     
			 }     
		 finally {         
			 if(source != null) {             
				 source.close();         
				 }         
			 if(destination != null) {             
				 destination.close();         
				 }     
	} } 

}
