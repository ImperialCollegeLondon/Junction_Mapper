package uk.ac.imperial.cisbio.imaging.cell_metric.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
/************************************************************/
/* IMPORTS													*/
/************************************************************/
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.imperial.cisbio.cell_metric.Cell_Metric_Tool;
import uk.ac.imperial.cisbio.cell_metric.om.Cell_Nucleus;
import uk.ac.imperial.cisbio.cell_metric.om.Membrane_Collection;
import uk.ac.imperial.cisbio.cell_metric.om.Nucleus_Collection;
import uk.ac.imperial.cisbio.cell_metric.output.Nucleus_SpreadSheet;
import uk.ac.imperial.cisbio.cell_metric.utilities.Defaults;
import uk.ac.imperial.cisbio.cell_metric.utilities.ImageUtilities;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MembraneTool.ButtonListener;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MembraneTool.thresholdThread;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.ToolPanel.smallObjectThread;

/************************************************************/
/* CLASS DEFINITION											*/
/************************************************************/
public class NucleusTool extends ToolPanel {
	
	

	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	private BufferedImage finalImage;
	private Nucleus_Collection nucleusObjects;
	private Nucleus_Collection clickedOnObjects;
	private JButton threshButton, erodeButton, dilateButton, removeButton, removeSmallButton, countButton, meanButton, saveButton, combineButton;
	private JRadioButton individualButton;
	private JTextArea selectedCells;
	boolean removeCells=false;
	boolean cellDistance=false;
	private int numIntraCells=5;
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public NucleusTool(BufferedImage im,Cell_Metric_Tool tool,MultiChannelImage mci){
		super(im,tool,mci);
		this.setLayout(new BorderLayout());
		componentPanel.setLayout(new GridLayout(10,1,20,20));
		componentPanel.setPreferredSize(new Dimension(200,400));
		componentPanel.setMaximumSize(new Dimension(200,400));
		this.add(this.componentPanel,BorderLayout.EAST);
		this.add(this.image,BorderLayout.WEST);	
		this.image.addMouseListener(new MouseListener());	
	}
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/
	
	public BufferedImage getImage(){
		return this.image.getImage();
	}
	
	public int getNumIntraCells(){
		return this.numIntraCells;
	}
	
	public Nucleus_Collection getNucleusCollection(){
		return this.nucleusObjects;
	}
	
	public Nucleus_Collection getClickedOnObjects(){
		return this.clickedOnObjects;
	}
	
	
	
	/************************************************************/
	/* INHERITED ABSTRACT METHODS								*/
	/************************************************************/
	protected void resetImage(){
		this.image.setImage(this.originalImage);
		this.state=FILTER_STATE;
		addComponents();
		validate();
	}
	/************************************************************/
	/* ADD COMPONENTS											*/
	/************************************************************/
	protected void addComponents(){
		this.addControls();
		this.addResetButton();
		validate();
	}
	
	
	private void addControls(){
		
		this.componentPanel.removeAll();
		
		if(this.state==FILTER_STATE){
			this.componentPanel.add(new JLabel("Step 1 : Adaptive Thresholding"));
			
			threshButton=new JButton("Apply Adaptive Threshold Filter");
			threshButton.addMouseListener(new ButtonListener());
			threshButton.setSize(new Dimension(50,20));
			threshButton.setPreferredSize(new Dimension(50,20));
			threshButton.setToolTipText("Create a binary image using adaptive thresholding technique. Calculate mean pixel intensity in window area and set pixel to a 1 if it equals or exceeds(mean + C value)");
			this.componentPanel.add(threshButton);
		}
		
		else if(this.state==BINARY_STATE){
			this.componentPanel.add(new JLabel("Step 2 : Tidy Cell Image"));
			
			erodeButton=new JButton("Erode");
			erodeButton.addMouseListener(new ButtonListener());
			erodeButton.setToolTipText("Erode Image : can be used to separate touching cells");	
			this.componentPanel.add(erodeButton);
			
			dilateButton=new JButton("Dilate");
			dilateButton.addMouseListener(new ButtonListener());
			dilateButton.setToolTipText("Dilate Image : can be used to remove internal holes");	
			this.componentPanel.add(dilateButton);
			
			removeButton=new JButton("Remove Edge Objects");
			removeButton.setToolTipText("Remove Nucleii touching the edge");
			removeButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(removeButton);
			
			removeSmallButton=new JButton("Remove Small Objects");
			removeSmallButton.setToolTipText("Remove objects from the image by clicking on them");
			removeSmallButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(removeSmallButton);	
			
			individualButton=new JRadioButton("Remove Individual Cells");
			individualButton.setToolTipText("Remove individual cells in the image by clicking on them");
			individualButton.addMouseListener(new RadioButtonListener());
			this.componentPanel.add(individualButton);
			
			countButton=new JButton("Count Cells");
			countButton.setToolTipText("Count Cells in Cell Image");
			countButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(countButton);
		}
		
		
		else if(this.state==COMPLETE_STATE){	
			this.removeCells=false;
			image.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			this.clickedOnObjects=new Nucleus_Collection();
			this.clickedOnObjects.setXDim(this.image.getImage().getWidth());
			this.clickedOnObjects.setYDim(this.image.getImage().getHeight());
			
			this.componentPanel.add(new JLabel("Step 3 : Measure Inter Cell Distance"));
			
			meanButton= new JButton("Set # Cells");
			meanButton.setToolTipText("Set the number of cells to get the intercell distance");
			meanButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(meanButton);
			
			saveButton= new JButton("Save as Spreadsheet");
			saveButton.setToolTipText("Save results as a spreadsheet");
			saveButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(saveButton);
			
			selectedCells=new JTextArea(getSelectedCellsString());
			selectedCells.setLineWrap(true);
			selectedCells.setEditable(false);
			selectedCells.setFont(Defaults.medium_Font);
			this.componentPanel.add(selectedCells);
			
			if(this.getMultiChannelImage().getMembraneTool().getState()==MembraneTool.COMPLETE_STATE){
				combineButton= new JButton("Combine with Edge map");
				combineButton.setToolTipText("Combine selected cells with edge map");
				combineButton.addMouseListener(new ButtonListener());
				this.componentPanel.add(combineButton);
			}
			
			
		}
		
		this.componentPanel.validate();
		repaint();		
	}
	
	
	protected void getAdaptiveThreshold(int filterDim, int cValue){
		this.filterDim=filterDim;
		this.cValue=cValue;
		Thread t=new Thread(new thresholdThread());
		t.start();
	}
	
	protected void endAdaptiveThreshold(){
		this.state=BINARY_STATE;
		addComponents();
		validate();
	}
	
	
	private void removeEdgeTouchingCells(){
		Thread t=new Thread(new edgeObjectThread());
		t.start();
	}
	
	
	
	private void countCells(){
		Thread t=new Thread(new countObjectThread());
		t.start();
	}
	
	private void startRemoveIndividualCells(){
		this.removeCells=true;
		image.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	private void endRemoveIndividualCells(){
		this.removeCells=false;
		image.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	private void getIntraNucleusDistance(){
		BufferedImage im;
		try {
			Object[] possibilities = new Object[this.nucleusObjects.size()-1];
			for(int i=1;i<this.nucleusObjects.size()-1;i++)possibilities[i]=new String(""+i);
			String s = (String)JOptionPane.showInputDialog(
				                    this,
				                    "Select the number of closest cells that you want to measure:",
				                    "Get Inter Nucleus Distance",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    new String(""+this.numIntraCells));

			//If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {	   
					this.numIntraCells=new Integer(s).intValue();
					getTool().setTextArea("Num Cells to measure distance to changed to "+s+"\n");
				}		
		} catch (Exception e) {
			tool.handleException(e);
		}
	}
	
	
	private void saveResults(){
		try{
			Thread t=new Thread(new outputResultsThread());
			t.start();
			
		}catch(Exception e){
			this.tool.handleException(e);
		}
	}
	
	private void combineImages(){
		try{
			this.clickedOnObjects.associateMembranes(((MembraneTool)this.imagePanel.getMembraneTool()).getMembraneCollection());
			this.setImage(this.clickedOnObjects.getCombinedImage(true));
		}catch(Exception e){
			this.tool.handleException(e);
		}
	}
	
	private String getSelectedCellsString(){
		return "Selected Cells :: "+this.clickedOnObjects.getCellNamesString();
	}
	
	
	/************************************************************/
	/* INTERNAL CLASSES FOR HANDLING GUI EVENTS					*/
	/************************************************************/
	/** SLIDER CHANGE LISTENER **/
	
	
	class ButtonListener extends MouseAdapter{		
		@Override
		public void mousePressed(MouseEvent e) {
			JButton button = (JButton) e.getSource();
			if(button==threshButton){
				Adaptive_Threshold_Dialog dialog=new Adaptive_Threshold_Dialog(tool,NucleusTool.this);
			}
			else if(button==dilateButton)dilateImage();
			else if(button==erodeButton)erodeImage();
			else if(button==removeButton)removeEdgeTouchingCells();
			else if(button==removeSmallButton)removeSmallObjectsFromImage();
			else if(button==countButton)countCells();
			else if(button==meanButton)getIntraNucleusDistance();
			else if(button==saveButton)saveResults();
			else if(button==combineButton)combineImages();
		}
		
	}
	
	
	class RadioButtonListener extends MouseAdapter{		
		@Override
		public void mousePressed(MouseEvent e) {
			JRadioButton button = (JRadioButton) e.getSource();
			if(button==individualButton){
				if(!button.isSelected())startRemoveIndividualCells();
				else endRemoveIndividualCells();
			}
		}
		
	}
	

	class MouseListener extends MouseAdapter {
      
		public void mouseClicked(MouseEvent e) {
			 mouseEvent(e);
		 }
		
	    public void mousePressed(MouseEvent e) {
	    	//mouseEvent(e);
	    }

	   private void mouseEvent(MouseEvent e) {
		   System.out.println("Got Mouse Click");	
	    	if(removeCells){
	    		System.out.println("Started Remove Cell Thread");
	    		Thread t=new Thread(new removeCellThread(e));
	    		t.start();	      		
		         }
	    	else if(cellDistance){
	    		System.out.println("Started Cel Distance Thread");
	    		Thread t=new Thread(new cellDistanceThread(e));
	    		t.start();	   	
		        }
	    	}
    
    	}
	
	

	
	/************************************************************/
	/* THREADS FOR IMAGE PROCESSING OPERATIONS					*/
	/************************************************************/
	class removeCellThread implements Runnable{
		MouseEvent evt;
		
		public removeCellThread(MouseEvent e){evt=e;}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			image.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			BufferedImage cells;
			try {
     			ImageUtilities.removeObject(image.getImage(),evt.getX(),evt.getY());
     			image.repaint();
			} catch (Exception e) {
				tool.handleException(e);
			}
			image.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	class cellDistanceThread implements Runnable{
		
	  	MouseEvent evt;
		
		public cellDistanceThread(MouseEvent e){evt=e;}
		
		@Override
		public void run() {
		try{
			System.out.println("Started Cell Distance");
   			Cell_Nucleus c=nucleusObjects.getSelectedNucleus(new Point(evt.getX(),evt.getY()));
   			if(c!=null){
   				c.calculateIntraCellDistances();
   	   			//System.out.println("Got Cell Nucleus Object :: "+c.toString());
   	 			NucleusTool.this.setImage(c.getDistanceImage(NucleusTool.this));
   	 			//System.out.println("Got Distance Image");
   	 			//image.repaint();
   	 			if(clickedOnObjects.contains(c))clickedOnObjects.remove(c);
   	 			else clickedOnObjects.add(c);
   	 			selectedCells.setText(getSelectedCellsString());
   			}
   			
 			
 		} catch (Exception ex) {
 			tool.handleException(ex);
 			}
		}
		
	}
	
	class edgeObjectThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			image.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			BufferedImage cells;
			try {
				cells = ImageUtilities.removeEdgeTouchingObjects(image.getImage());
				NucleusTool.this.setImage(cells);
			} catch (Exception e) {
				tool.handleException(e);
			}
			image.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	class countObjectThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			image.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			BufferedImage cells;
			try {
				System.out.println("Counting Cells ...");
				finalImage=image.getImage();
				ArrayList<Object> nucleii = ImageUtilities.countNucleusObjects(image.getImage());
				cells=(BufferedImage)nucleii.get(0);
				nucleusObjects=(Nucleus_Collection)nucleii.get(1);
				NucleusTool.this.setImage(nucleusObjects.getNucleusImage(true, NucleusTool.this));
				//JOptionPane.showConfirmDialog(tool, message)
			} catch (Exception e) {
				tool.handleException(e);
			}
			cellDistance=true;
			state=COMPLETE_STATE;
			addComponents();
			image.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	class outputResultsThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			saveButton.setEnabled(false);
			NucleusTool.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy_MM_dd_HHmm");
				java.util.Date date = new java.util.Date();
				String time=formatter.format(date);
				String name=NucleusTool.this.getImageName();
				Nucleus_SpreadSheet ss=new Nucleus_SpreadSheet(getTool().getOutputDirectory()+"/"+name+"_nucleusObjects_"+time+".xls",NucleusTool.this); 
				ss.makeNucleusSheet();
				
			} catch (Exception e) {
				tool.handleException(e);
			}
			NucleusTool.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			saveButton.setEnabled(true);
		}
		
	}
	
}
