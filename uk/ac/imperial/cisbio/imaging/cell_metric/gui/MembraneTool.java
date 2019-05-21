package uk.ac.imperial.cisbio.imaging.cell_metric.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

import uk.ac.imperial.cisbio.cell_metric.Cell_Metric_Tool;
import uk.ac.imperial.cisbio.cell_metric.om.Cell_Membrane;
import uk.ac.imperial.cisbio.cell_metric.om.Cell_Membrane_Corner;
import uk.ac.imperial.cisbio.cell_metric.om.Membrane_Collection;
import uk.ac.imperial.cisbio.cell_metric.output.Membrane_SpreadSheet;
import uk.ac.imperial.cisbio.cell_metric.output.Nucleus_SpreadSheet;
import uk.ac.imperial.cisbio.cell_metric.output.PDFWriter;
import uk.ac.imperial.cisbio.cell_metric.utilities.ExtensionFileFilter;
import uk.ac.imperial.cisbio.cell_metric.utilities.ImageUtilities;
/************************************************************/
/* IMPORTS													*/
/************************************************************/
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.Adaptive_Threshold_Dialog.SliderChangeListener;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.NucleusTool.ButtonListener;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.NucleusTool.outputResultsThread;

/************************************************************/
/* CLASS DEFINITION											*/
/************************************************************/
public class MembraneTool extends ToolPanel  {

	/************************************************************/
	/* STATIC VARIABLES											*/
	/************************************************************/
	private final static int COMBINE_STATE_1 =1;
	private final static int COMBINE_STATE_2 =2;
	private final static int COMBINE_STATE_3 =3;
	
	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	private NucleusTool nucleusTool;
	private Membrane_Collection collection=new Membrane_Collection();
	private Cell_Membrane currentMembrane;
	private Cell_Membrane_Corner currentCorner;
	private int currentCornerCount =0;
	private int dilatedEdgeWidth=2;
	private int measurementChannel=0;
	
	
	
	private JButton threshButton, sobelButton, gaussianButton, medianButton, resetButton, dilateButton, reverseButton,
					erodeButton, sharpenButton, skeletonButton, cannyButton, superImposeButton, combineButton, removeSmallButton,
					finishButton, maximaButton, trailingEdgesButton, measureButton, showEdgeButton, cornerButton, saveButton, saveEdgeMapButton, loadButton, switchImageButton;
	private JRadioButton individualButton, removeLineButton;
	private JLabel eCadherinThresholdLabel, measurementThresholdLabel;
	private int cannyLow=10,cannyHigh=30;
	private int combineState=COMBINE_STATE_1;
	private boolean drawLines=false;
	
	private Point startPoint, endPoint;
	private int seedx,seedy;
	private int eCadherinThreshold=50;
	private BufferedImage eCadherinThresholdImage=ImageUtilities.getThresholdImage(originalImage, eCadherinThreshold, 255);
	
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public MembraneTool(BufferedImage im,Cell_Metric_Tool tool, MultiChannelImage mci){
		super(im,tool,mci);
		this.image.addMouseListener(new MouseListener());
		this.image.addMouseMotionListener(new MouseMotion());
		collection.setXDim(im.getWidth());
		collection.setYDim(im.getHeight());
	}
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/
	
	
	public BufferedImage getImage(){
		return this.image.getImage();
	}
	
	public Membrane_Collection getMembraneCollection(){
		return this.collection;
	}
	
	public int getDilatedEdgeWidth(){
		return this.dilatedEdgeWidth;
	}
	
	public Cell_Membrane getCurrentMembrane(){
		return this.currentMembrane;
	}
	
	/************************************************************/
	/* ADD COMPONENTS											*/
	/************************************************************/
	protected void addComponents(){
		this.removeAll();
		this.setLayout(new BorderLayout());
		this.add(this.image,BorderLayout.WEST);	
		componentPanel.setLayout(new GridLayout(12,1,20,20));
		componentPanel.setPreferredSize(new Dimension(200,400));
		componentPanel.setMaximumSize(new Dimension(200,400));
		this.addControls();
		this.add(this.componentPanel,BorderLayout.EAST);
		this.addResetButton();
		validate();
	}
	
	
	private void addControls(){
		
		this.componentPanel.removeAll();
		
		if(this.state==FILTER_STATE){
			
			this.componentPanel.add(new JLabel("Load existing edge map"));
			
			loadButton=new JButton("Load Existing");
			loadButton.setToolTipText("Load an existing edge map for this image");
			loadButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(loadButton);
		
			
			this.componentPanel.add(new JLabel("Step 1 : Isolate Edges"));
			
			
			gaussianButton=new JButton("Gaussian");
			gaussianButton.setToolTipText("Blur the gray scale image with a gaussian filter");
			gaussianButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(gaussianButton);
			
			medianButton=new JButton("Median Filter");
			medianButton.setToolTipText("Blur the gray scale image with median filter");
			medianButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(medianButton);
			
			sharpenButton=new JButton("Sharpen Filter");
			sharpenButton.setToolTipText("Sharpen the gray scale image by enhancing edges");
			sharpenButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(sharpenButton);
			
			
			this.componentPanel.add(new JLabel("Step 2 : Edge Detection"));
			
			/**sobelButton=new JButton("Sobel Operator");
			sobelButton.setToolTipText("Perform edge extraction using sobel operator");
			sobelButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(sobelButton);**/
			
			dilateButton=new JButton("Grey Scale Dilate");
			dilateButton.setToolTipText("Dilate the cell edges to join the cell corners");
			dilateButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(dilateButton);
			
			threshButton=new JButton("Apply Adaptive Threshold");
			threshButton.setToolTipText("Create a binary image using adaptive thresholding technique. Calculate mean pixel intensity in window area and set pixel to a 1 if it equals or exceeds(mean + C value)");
			threshButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(threshButton);
		}
			
		else if(this.state==EDGE_STATE){
			this.componentPanel.add(new JLabel("Step 3 : Build the Edge Map"));
			
			trailingEdgesButton=new JButton("Trailing Edges");
			trailingEdgesButton.setToolTipText("Remove Trailing Edges");
			trailingEdgesButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(trailingEdgesButton);
			
			removeSmallButton=new JButton("Remove Small Objects");
			removeSmallButton.setToolTipText("Remove small objects from the binary image");
			removeSmallButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(removeSmallButton);
			
			dilateButton=new JButton("Dilate");
			dilateButton.setToolTipText("Dilate the cell edges to join the cell corners");
			dilateButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(dilateButton);
			
			skeletonButton=new JButton("Skeletonise");
			skeletonButton.setToolTipText("Get the skeleton of the image");
			skeletonButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(skeletonButton);
			
			maximaButton=new JButton("Local Maxima for Edges");
			maximaButton.setToolTipText("Morph the skeleton to the local maxima of the gray scale image within a defined neighbourhood - join edges using dilate and then skeletonise");
			maximaButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(maximaButton);
			
			
			superImposeButton=new JButton("Edge Map Toggle");
			superImposeButton.setToolTipText("Change the view of the image by superimposing over the original or just viewing the binary image");
			superImposeButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(this.superImposeButton);	
			
			
			
			
			JPanel panel=new JPanel();
			panel.setLayout(new GridLayout(1,2));
		
			individualButton=new JRadioButton("Add Cell Boundary");
			individualButton.setToolTipText("Add new cell boundarys to the combined image");
			individualButton.addMouseListener(new RadioButtonListener());
			panel.add(individualButton);	
			
			this.removeLineButton=new JRadioButton("Remove Pixels");
			this.removeLineButton.setToolTipText("Remove Pixels from the image");
			this.removeLineButton.addMouseListener(new RadioButtonListener());
			panel.add(this.removeLineButton);	
			
			ButtonGroup group = new ButtonGroup();
			group.add(this.individualButton);
			group.add(this.removeLineButton);
			
			
			reverseButton=new JButton("Reverse Last Step");
			reverseButton.setToolTipText("Reverse Last Line Drawn");
			reverseButton.addMouseListener(new ButtonListener());
			panel.add(reverseButton);
			
			this.componentPanel.add(panel);
			
			saveEdgeMapButton=new JButton("Save Edge Map");
			saveEdgeMapButton.setToolTipText("Save the current edge map");
			saveEdgeMapButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(saveEdgeMapButton);	
			
			finishButton=new JButton("Finish");
			finishButton.setToolTipText("Move onto the next step");
			finishButton.addMouseListener(new ButtonListener());
			finishButton.setEnabled(false);
			this.componentPanel.add(finishButton);
		}
		
		else if(this.state==BINARY_STATE){
			this.componentPanel.add(new JLabel("Step 4 : Select Cells to Measure"));
			image.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			
			finishButton=new JButton("Finish");
			finishButton.setToolTipText("Select cells by clicking on them and then click finish");
			finishButton.addMouseListener(new ButtonListener());
			this.componentPanel.add(finishButton);
			
		}
		
		
		else if(this.state==COMPLETE_STATE){
			this.componentPanel.add(new JLabel("Step 5 : Measure each cell (click on them one by one)"));
			
			 
			image.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			MembraneTool.this.image.setImage(collection.getMembraneImage(true, MembraneTool.this));		
		}
		
		else if(this.state==CORNER_STATE){
			this.componentPanel.add(new JLabel("Step 6 : Define corners of cell  (use slider for auto and mouse for manual - right click removes corner, left click adds corner)"));
			
			image.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			 JSlider slider = new JSlider();
		     slider.setMinimum(0);
		     slider.setMaximum(50);
		     slider.setMajorTickSpacing(10);
		     slider.setMinorTickSpacing(5);
		     slider.setPaintTicks(true);
		     slider.setPaintLabels(true);
		     slider.setPaintTrack(true);
		     slider.setValue(0);
		     cValue=0;
		     slider.setToolTipText("Epsilon value");
			 componentPanel.add(slider);
			 slider.addChangeListener(new EpsilonChangeListener());
			 this.componentPanel.add(new JLabel("Epsilon Value for Corner Detection"));
				
			 
			 slider = new JSlider();
		     slider.setMinimum(1);
		     slider.setMaximum(9);
		     slider.setMajorTickSpacing(1);
		     slider.setMinorTickSpacing(1);
		     slider.setPaintTicks(true);
		     slider.setPaintLabels(true);
		     slider.setPaintTrack(true);
		     slider.setValue(this.dilatedEdgeWidth);
		     slider.setToolTipText("Number of dilations applied to edge");
			 componentPanel.add(slider);
			 slider.addChangeListener(new DilatedEdgeListener());
			 this.componentPanel.add(new JLabel("Number of Dilations applied to edge"));
			 
			 slider = new JSlider();
		     slider.setMinimum(0);
		     slider.setMaximum(255);
		     slider.setMajorTickSpacing(50);
		     slider.setMinorTickSpacing(10);
		     slider.setPaintTicks(true);
		     slider.setPaintLabels(true);
		     slider.setPaintTrack(true);
		     slider.setValue(this.eCadherinThreshold);
		     slider.setToolTipText("E-Cadherin Threshold value");
			 componentPanel.add(slider);
			 slider.addChangeListener(new ThresholdChangeListener());
			 this.eCadherinThresholdLabel=new JLabel("E-Cadherin Threshold : "+slider.getValue());
			 this.componentPanel.add(this.eCadherinThresholdLabel);
			 
			 MeasurementTool mt=((MeasurementTool)MembraneTool.this.getMultiChannelImage().getMeasurementTool());
			 slider = new JSlider();
		     slider.setMinimum(0);
		     slider.setMaximum(255);
		     slider.setMajorTickSpacing(50);
		     slider.setMinorTickSpacing(10);
		     slider.setPaintTicks(true);
		     slider.setPaintLabels(true);
		     slider.setPaintTrack(true);
		     slider.setValue(mt.getMeasurementThreshold());
		     slider.setToolTipText("Measurement Channel Threshold value");
			 componentPanel.add(slider);
			 slider.addChangeListener(new MeasurementChangeListener());
			 this.measurementThresholdLabel=new JLabel("Measurement Threshold : "+slider.getValue());
			 this.componentPanel.add(this.measurementThresholdLabel);
			 
			
			 this.switchImageButton=new JButton("Switch Background");
			 this.switchImageButton.setToolTipText("Switch Background Image");
			 this.switchImageButton.addMouseListener(new ButtonListener());
			 this.componentPanel.add(this.switchImageButton);	
			 
			 measureButton=new JButton("Measure");
			 measureButton.setToolTipText("Measure Cell Properties");
			 measureButton.addMouseListener(new ButtonListener());
			 this.componentPanel.add(measureButton);		
		}
		else if(this.state==MEASURE_STATE){
			this.componentPanel.add(new JLabel("Step 7 : Measure Cell Junctions"));
			
			 image.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			 
			
				
			 
			/** slider = new JSlider();
		     slider.setMinimum(0);
		     slider.setMaximum(255);
		     slider.setMajorTickSpacing(50);
		     slider.setMinorTickSpacing(10);
		     slider.setPaintTicks(true);
		     slider.setPaintLabels(true);
		     slider.setPaintTrack(true);
		     slider.setValue(this.actinThreshold);
		     slider.setToolTipText("Actin Threshold value");
			 componentPanel.add(slider);
			 slider.addChangeListener(new ThresholdChangeListener());
			 this.componentPanel.add(new JLabel("Actin Threshold value"));**/
			 
			 
			 showEdgeButton=new JButton("Show Edge");
			 showEdgeButton.setToolTipText("Show Each Edge");
			 showEdgeButton.addMouseListener(new ButtonListener());
			 this.componentPanel.add(showEdgeButton);
			 
			 saveButton= new JButton("Save as Spreadsheet");
			 saveButton.setToolTipText("Save results for this cell as a spreadsheet");
			 saveButton.addMouseListener(new ButtonListener());
			 this.componentPanel.add(saveButton);
			 
			 cornerButton=new JButton("Back to Cell State");
			 cornerButton.setToolTipText("");
			 cornerButton.addMouseListener(new ButtonListener());
			 this.componentPanel.add(cornerButton);
		}
			
		
		this.componentPanel.validate();
		repaint();		
	}
	
	
	@Override
	protected void getAdaptiveThreshold(int filterSize, int cValue) {
		// TODO Auto-generated method stub
		this.filterDim=filterSize;
		this.cValue=cValue;
		Thread t=new Thread(new thresholdThread());
		t.start();	
	}
	
	protected void endAdaptiveThreshold(){
		
	}
	
	
	private void getSobelImage(){
		this.binaryImage=this.image.getImage();
		this.sharpenButton.setEnabled(false);
		this.medianButton.setEnabled(false);
		this.gaussianButton.setEnabled(false);
		this.sobelButton.setEnabled(false);
		Thread t=new Thread(new sobelThread());
		t.start();
		
	}
	
	private void getGaussianImage(){
		Thread t=new Thread(new gaussianThread());
		t.start();
		
	}
	
	private void getLaplaceImage(){
		Thread t=new Thread(new laplaceThread());
		t.start();
		
	}
	
	public BufferedImage getECadherinThresholdImage(){
		return this.eCadherinThresholdImage;
	}
	
	public int getECadherinThreshold(){
		return this.eCadherinThreshold;
	}
	
	
	protected void resetImage(){
		if(this.state==MEASURE_STATE || this.state==CORNER_STATE){
			this.image.setImage(collection.getMembraneImage(true,this));
			this.state=COMPLETE_STATE;
		}
		else{
			this.image.setImage(this.originalImage);
			this.state=FILTER_STATE;
			collection=new Membrane_Collection();
			collection.setXDim(this.originalImage.getWidth());
			collection.setYDim(this.originalImage.getHeight());
			}
		this.startPoint=this.endPoint=null;
		tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		addComponents();
		validate();
	}
	
	private void getMedianImage(){
		Thread t=new Thread(new medianThread());
		t.start();	
	}
	
	
	
	private void sharpenImage(){
		BufferedImage im;
		try {
			im = ImageUtilities.convolveImage(this.image.getImage(), new int[][]{{-1,-1,-1},{-1,12,-1},{-1,-1,-1}});
			this.image.setImage(im);
		} catch (Exception e) {
			tool.handleException(e);
		}
	}
	
	private void skeletoniseImage(){
		Thread t=new Thread(new skeletonThread());
		t.start();
	}
	
	private void cannyImage(){
		Object[] possibilities = new Object[190];
		for(int i=0;i<190;i++)possibilities[i]=""+(i+10);
		String s = (String)JOptionPane.showInputDialog(
		                    this,
		                    "Select High Threshold :\n",
		                    "Canny Operator : High Threshold",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    possibilities,
		                    "50");
		this.cannyHigh=new Integer(s).intValue();
		
		possibilities = new Object[this.cannyHigh];
		for(int i=0;i<this.cannyHigh;i++)possibilities[i]=""+(i+1);
		s = (String)JOptionPane.showInputDialog(
		                    this,
		                    "Select Low Threshold :\n",
		                    "Canny Operator : Low Threshold",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    possibilities,
		                    "10");
		this.cannyLow=new Integer(s).intValue();
		this.binaryImage=this.image.getImage();

		Thread t=new Thread(new cannyThread());
		t.start();	
		
	}
	
	private void superImposeImage(){
		BufferedImage im = null;
		try {
			this.combineState++;
			if(this.combineState>COMBINE_STATE_3)this.combineState=COMBINE_STATE_1;
			if(this.combineState==COMBINE_STATE_2)im = ImageUtilities.superImposeImage(this.binaryImage,this.originalImage);
			else if(this.combineState==COMBINE_STATE_1)im = this.binaryImage;
			else if(this.combineState==COMBINE_STATE_3){
				BufferedImage im2=((MeasurementTool)(this.getMultiChannelImage().getMeasurementTool())).getImage();
				im = ImageUtilities.superImposeImage(this.binaryImage,im2);
			}
			this.image.setImage(im);
			validate();
		} catch (Exception e) {
			tool.handleException(e);
		}
	}
	
	private void combineImage(){
		 try {
			BufferedImage im = ImageUtilities.combineImage(this.binaryImage,this.nucleusTool.getImage());
			this.image.setImage(im);
			
		} catch (Exception e) {
			tool.handleException(e);
		}
	}
	
	
	
	private void removeTrailingEdgesFromImage(){
		try {
			Thread t=new Thread(new trailingEdgeThread());
			t.start();	
		} catch (Exception e) {
			tool.handleException(e);
		}
	}
	
	
	private void startLine(){
		startPoint=endPoint=null;
		this.drawLines=true;
		tool.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	private void endLine(){
		startPoint=endPoint=null;
		this.drawLines=false;
		tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
	}
	
	private void reverseLastAction(){
		if(lastBinaryImage!=null){
			this.binaryImage=this.lastBinaryImage;
			this.image.setImage(ImageUtilities.superImposeImage(binaryImage,originalImage));
			this.image.repaint();
		}
	}
	
	private void changeState(){
		if(this.state==MembraneTool.BINARY_STATE){
			this.state=MembraneTool.COMPLETE_STATE;
			this.image.setImage(collection.getMembraneImage(true, this));
		}
		else if(this.state==MembraneTool.EDGE_STATE){
			this.saveEdgeMap();
			this.state=MembraneTool.BINARY_STATE;
			this.image.setImage(ImageUtilities.superImposeImage(binaryImage,originalImage));
		}
		
		this.addComponents();
	}
	
	private void measureButton(){
		
		try {
			Thread t=new Thread(new measureThread());
			t.start();	
		} catch (Exception e) {
			tool.handleException(e);
		}
		
	}
	
	
	private void showEdgeButton() {	
		if(currentCornerCount >= this.currentMembrane.getCorners().size())currentCornerCount=0;
		this.currentCorner=this.currentMembrane.getCorners().get(currentCornerCount);
		try {	
			this.currentCorner.getECadherinPoints(this);
			this.currentCorner.getMeasurementPoints(this);
			this.currentCorner.getSkeletonisedECadherinEdgeImage();
			this.setImage(this.currentCorner.getCornerEdgeImage(MembraneTool.this));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			tool.handleException(e);
		}
		this.tool.setTextArea(this.currentCorner.toString()+"\n");
		currentCornerCount++;
	}
	
	private void cornerButton(){
		this.state=MembraneTool.COMPLETE_STATE;
		changeState();
	}
	
	private void getLocalMaxima(){
		
		try {
			if(image.getImage().getType()==BufferedImage.TYPE_BYTE_BINARY){
				Object[] possibilities = {"3","5","7","9","11","15","21","25"};
				String s = (String)JOptionPane.showInputDialog(
				                    this,
				                    "Neighbourhood size (pixels):\n",
				                    "Revert to local maxima",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    "9");

				//If a string was returned, say so.
				if ((s != null) && (s.length() > 0)) {	   
					int size=new Integer(s).intValue();
					BufferedImage im = ImageUtilities.getLocalMaxima(this.image.getImage(),this.originalImage,size);
					this.image.setImage(im);
				}
			}
			
		} catch (Exception e) {
			tool.handleException(e);
		}
	}
	
	
	private void switchCornerImage() throws Exception{
		this.measurementChannel++;
		this.setImage(currentMembrane.getCornerImage(MembraneTool.this,this.measurementChannel%5));
	}
	
	
	
	private void saveResults(){
		try{
			Thread t=new Thread(new outputResultsThread());
			t.start();
			
		}catch(Exception e){
			this.tool.handleException(e);
		}
	}
	
	private void saveEdgeMap(){
		try{
			Thread t=new Thread(new saveEdgeMapThread());
			t.start();
			
		}catch(Exception e){
			this.tool.handleException(e);
		}
	}
	
	
	private void loadEdgeMap(){
		try{
			Thread t=new Thread(new loadEdgeMapThread());
			t.start();
			
		}catch(Exception e){
			this.tool.handleException(e);
		}
	}
	
	
	
	/************************************************************/
	/* INTERNAL CLASSES FOR HANDLING GUI EVENTS					*/
	/************************************************************/
	
	/** SLIDER CHANGE LISTENER **/
	
	class SliderChangeListener implements ChangeListener{
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider)e.getSource();
				if(!slider.getValueIsAdjusting())cValue=slider.getValue();
				}
			
		}	
		
		
	class ButtonListener extends MouseAdapter{		
			@Override
			public void mousePressed(MouseEvent e) {
			
				try{
					JButton button = (JButton) e.getSource();
					if(button==threshButton){
						Adaptive_Threshold_Dialog dialog=new Adaptive_Threshold_Dialog(tool,MembraneTool.this);
					}
					else if(button==sobelButton)getSobelImage();
					else if(button==gaussianButton)getGaussianImage();
					else if(button==resetButton)resetImage();
					else if(button==medianButton)getMedianImage();
					else if(button==dilateButton)dilateImage();
					else if(button==erodeButton)erodeImage();
					else if(button==sharpenButton)sharpenImage();
					else if(button==skeletonButton)skeletoniseImage();
					else if(button==cannyButton)cannyImage();
					else if(button==superImposeButton)superImposeImage();
					else if(button==combineButton)combineImage();
					else if(button==removeSmallButton)removeSmallObjectsFromImage();
					else if(button==finishButton)changeState();
					else if(button==maximaButton)getLocalMaxima();
					else if(button==trailingEdgesButton)removeTrailingEdgesFromImage();
					else if(button==reverseButton)reverseLastAction();
					else if(button==measureButton)measureButton();
					else if(button==showEdgeButton)showEdgeButton();
					else if(button==cornerButton)cornerButton();
					else if(button==saveButton)saveResults();
					else if(button==saveEdgeMapButton)saveEdgeMap();
					else if(button==loadButton)loadEdgeMap();
					else if(button==switchImageButton)switchCornerImage();
				}
				catch(Exception ex){
					MembraneTool.this.getTool().handleException(ex);
				}
			}
			
	}
	
	class RadioButtonListener extends MouseAdapter{		
		@Override
		public void mousePressed(MouseEvent e) {
			JRadioButton button = (JRadioButton) e.getSource();
			if(button==individualButton){
				if(!button.isSelected())startLine();
				else endLine();
			}
			else if(button==removeLineButton){
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		}
	}
	
	class MouseListener extends MouseAdapter {
	    
		public void mousePressed(MouseEvent e) {

	    }
	    
	    public void mouseClicked(MouseEvent e) {
	    try{	
	    	switch(state){
	    	
	    	case EDGE_STATE :
			      if(drawLines){
			    	  try {
			  			 if(SwingUtilities.isRightMouseButton(e)){
			  				 startPoint=null;
			  				 endPoint=null;
			  			 }
			  			 else if(startPoint==null){
			  				startPoint=new Point(e.getX(),e.getY());		 
			  			 }
			  			 else {
			  				 BufferedImage im;
			  				 endPoint=new Point(e.getX(),e.getY());
			  				 if(image.getImage().getType()!=BufferedImage.TYPE_BYTE_BINARY){
			  					 im = image.getImage();
			  					 lastBinaryImage=binaryImage;
				  				 Graphics g= im.getGraphics();
				  				 g.setColor(Color.WHITE);
				  				 g.setPaintMode();
				  			     g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
				  			     image.repaint();
				  				 binaryImage=ImageUtilities.reverseSuperImposeImage(image.getImage());
				  			     startPoint=endPoint;endPoint=null;
			  				 }
			  			 }
			  		} catch (Exception ex) {
			  			tool.handleException(ex);
			  		}
			      }
			     break;
			     
	    		case BINARY_STATE :
	      			seedx=e.getX();
	      			seedy=e.getY();
	      			if(!collection.isPartofMembrane(new Point(seedx,seedy))){
	      				Thread t=new Thread(new fillCellThread());
	      				t.start();
	      			}	
	      		break;
	      		
	      		
	    		case COMPLETE_STATE :
	    			if(SwingUtilities.isRightMouseButton(e)){
	    				MembraneTool.this.setImage(collection.getMembraneImage(true, MembraneTool.this));
	    				state=COMPLETE_STATE;
	    				addComponents();
	    			}
	    			else{
		      			seedx=e.getX();
		      			seedy=e.getY();
		      			Point p=new Point(seedx,seedy);
		      			if(collection.isPartofMembrane(p)){
		      				state=CORNER_STATE;
		      				currentMembrane=collection.getSelectedMembrane(p);
		      				MembraneTool.this.setImage(currentMembrane.getCornerImage(MembraneTool.this,measurementChannel));
		      				addComponents();	      				
		      			}
	    			}
	      		break;
	      		
	    		case CORNER_STATE :	
	    			seedx=e.getX();
	      			seedy=e.getY();
	      			Point p=new Point(seedx,seedy);
	      			
	      			
	      			if(SwingUtilities.isRightMouseButton(e)){
	      					Cell_Membrane_Corner corner=currentMembrane.isPartofCorner(p);
	      					boolean removed=currentMembrane.removeCorner(corner);
	      					MembraneTool.this.setImage(currentMembrane.getCornerImage(MembraneTool.this,measurementChannel));
		      				addComponents();	
	      					//JOptionPane.showMessageDialog(tool, corner.toString(), removed?"Corner Removed" : "Corner not removed", JOptionPane.INFORMATION_MESSAGE);   				
	      				}
	      			else if(SwingUtilities.isLeftMouseButton(e)){
		      				boolean added=currentMembrane.addCorner(p);
	      					MembraneTool.this.setImage(currentMembrane.getCornerImage(MembraneTool.this,measurementChannel));
		      				addComponents();	
	      					//JOptionPane.showMessageDialog(tool, p.toString(), added?"Corner Added" : "Corner not added", JOptionPane.INFORMATION_MESSAGE);   		
	      			}
	    		break;	
	      	
	    		default :
	      		break;
		    }
	    }catch(Exception ex){
	    	MembraneTool.this.getTool().handleException(ex);
	    }
	    	
	    }
	}
	
	    
	class MouseMotion implements MouseMotionListener {

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			//MembraneTool.this.tool.setTextArea("Mouse Dragged "+e.getX()+" :: "+e.getY());
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			if(state==EDGE_STATE){
				if(removeLineButton.isSelected()){
					BufferedImage im=null;
					int pix=MembraneTool.this.binaryImage.getRaster().getPixel(e.getX(), e.getY(), new int[] {0})[0];	
					 //if(image.getImage().getType()==BufferedImage.TYPE_BYTE_BINARY && pix==1){
					if(pix==1){
						 for(int i=-5;i<=5;i++)
							 for(int j=-5;j<=5;j++){
								 int xc=e.getX()+i;int yc=e.getY()+j;
									if((xc>=0)&&(yc>=0)&&(yc<binaryImage.getHeight())&&(xc<binaryImage.getWidth()))
										MembraneTool.this.binaryImage.getRaster().setPixel(xc, yc, new int[] {0});		 
										//MembraneTool.this.tool.setTextArea("Changed Pixel "+xc+" :: "+yc+":: pix == "+pix+"\n");
										//MembraneTool.this.image.repaint();
							 		}
						 
					if(MembraneTool.this.combineState==COMBINE_STATE_2)im = ImageUtilities.superImposeImage(MembraneTool.this.binaryImage,MembraneTool.this.originalImage);
					else if(MembraneTool.this.combineState==COMBINE_STATE_1)im = MembraneTool.this.binaryImage;
					else if(MembraneTool.this.combineState==COMBINE_STATE_3){
						BufferedImage im2=((MeasurementTool)(MembraneTool.this.getMultiChannelImage().getMeasurementTool())).getImage();
						im = ImageUtilities.superImposeImage(MembraneTool.this.binaryImage,im2);
					}
					MembraneTool.this.setImage(im);
					 }
				}
			}
		}
		
	}
	
	/************************************************************/
	/* THREADS FOR PERFORMING OPERATIONS						*/
	/************************************************************/
	
	class thresholdThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			tool.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			BufferedImage cells=ImageUtilities.getAdaptiveThresholdImage(MembraneTool.this.image.getImage(),MembraneTool.this.filterDim,MembraneTool.this.cValue);
			MembraneTool.this.setImage(cells);
			state=EDGE_STATE;
			addComponents();
			tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	
	class sobelThread implements Runnable{

		@Override
		public void run() {
			try{
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Vector v = ImageUtilities.sobelOperator(image.getImage());
				MembraneTool.this.setImage((BufferedImage)v.get(0));
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}catch(Exception e){
				tool.handleException(e);
			}
			
		}
		
	}
	
	
	class gaussianThread implements Runnable{
		@Override
		public void run() {
			try{
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				int [][] mask={{1,4,7,4,1},{4,16,26,16,4},{7,26,41,26,7},{4,16,26,16,4},{1,4,7,4,1}};
				MembraneTool.this.setImage(ImageUtilities.convolveImage(image.getImage(),mask));
				validate();
				//state=BINARY_STATE;
				//addControls();
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}catch(Exception e){
				tool.handleException(e);
			}
			
		}
		
	}
	
	
	class laplaceThread implements Runnable{

		@Override
		public void run() {
			try{
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				/**int [][] mask={	{0,1,1,2,2,2,1,1,0}, 
								{1,2,4,5,5,5,4,2,1},
								{1,4,5,3,0,3,5,4,1},
								{2,5,3,-12,-24,-12,3,5,2},
								{2,5,0,-24,-40,-24,0,5,2},
								{2,5,3,-12,-24,-12,3,5,2},
								{1,4,5,3,0,3,5,4,1},
								{1,2,4,5,5,5,4,2,1},
								{0,1,1,2,2,2,1,1,0}};**/
				
				int [][] mask={	{-1,-1,-1},{-1,8,-1},{-1,-1,-1}};
				BufferedImage membranes = ImageUtilities.convolveImage(image.getImage(),mask);
				membranes=ImageUtilities.scaleImage(membranes);
				membranes=ImageUtilities.invertImage(membranes);
				
				MembraneTool.this.setImage(membranes);
				validate();
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}catch(Exception e){
				tool.handleException(e);
			}
			
		}
		
	}
	
	class medianThread implements Runnable{

		@Override
		public void run() {
			try{
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
				BufferedImage membranes = ImageUtilities.medianFilter(image.getImage(),5);
				MembraneTool.this.setImage(membranes);
				validate();
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}catch(Exception e){
				tool.handleException(e);
			}
			
		}
		
	}
	
	
	class skeletonThread implements Runnable{

		@Override
		public void run() {
			try{
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
				BufferedImage membranes = ImageUtilities.getSkeletonImage(image.getImage(),MembraneTool.this.image);
				MembraneTool.this.setImage(membranes);
				finishButton.setEnabled(true);
				validate();
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				binaryImage=getImage();
			}catch(Exception e){
				tool.handleException(e);
			}
			
		}
		
	}
	
	class cannyThread implements Runnable{

		@Override
		public void run() {
			try{
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
				BufferedImage membranes = ImageUtilities.cannyOperator(image.getImage(),cannyLow,cannyHigh);
				MembraneTool.this.setImage(membranes);
				validate();
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}catch(Exception e){
				tool.handleException(e);
			}
			
		}
		
	}
	
	class trailingEdgeThread implements Runnable{

		@Override
		public void run() {
			try{
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				MembraneTool.this.setImage(binaryImage);
				BufferedImage membranes = ImageUtilities.removeTrailingEdges(MembraneTool.this.image);
				MembraneTool.this.setImage(membranes);
				validate();
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				binaryImage=getImage();
			}catch(Exception e){
				tool.handleException(e);
			}
			
		}
		
	}
	
	class fillCellThread implements Runnable{
	
	public void run() {
			try {
				 Cell_Membrane m=ImageUtilities.selectCell(image,binaryImage,seedx,seedy);
				 collection.add(m);
				 m.setCollection(collection);
				 m.setNumber(collection.size());
				 m.sortEdgePixels(MembraneTool.this);
				 m.setOriginalImage(MembraneTool.this.originalImage);
				 //m.calculateChainCode(MembraneTool.this);
				 //image.setImage(m.getEdgeImage());
				 tool.setTextArea("Added membrane to collection :: # "+m.getNumber()+"\n");
				 tool.setTextArea(m.toString()+"\n");
				 }catch (Exception ex) {
		      			 tool.handleException(ex);
		      		 }
			  }
			 
	}
	
	class measureThread implements Runnable{	
		public void run(){
			try {
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				measureButton.setEnabled(false);
				currentMembrane.setECadherinThresholdAtMeasurement(eCadherinThreshold);
				MeasurementTool mt=((MeasurementTool)MembraneTool.this.getMultiChannelImage().getMeasurementTool());
				currentMembrane.setMeasurementThresholdAtMeasurement(mt.getMeasurementThreshold());
				
				currentMembrane.getCornerDistances(MembraneTool.this);
				currentMembrane.getECadherinPoints(MembraneTool.this);
				currentMembrane.getMeasurementPoints(MembraneTool.this);
				state=MembraneTool.MEASURE_STATE;
				changeState();
				measureButton.setEnabled(true);
				tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} catch (Exception e) {
				tool.handleException(e);
			}
		}
	}
	
	class EpsilonChangeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e) {
			try {
				JSlider slider = (JSlider)e.getSource();
				if(!slider.getValueIsAdjusting()){
					int eValue=slider.getValue();
					currentMembrane.getDouglasPeucker((double)eValue,MembraneTool.this);
					MembraneTool.this.setImage(currentMembrane.getCornerImage(MembraneTool.this,measurementChannel));
					validate();
					}		
			}catch (Exception ex) {
  			 tool.handleException(ex);
  		 }
		}
		
	}	
	
	
	class DilatedEdgeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e) {
			try {
				JSlider slider = (JSlider)e.getSource();
				if(!slider.getValueIsAdjusting()){
					dilatedEdgeWidth=slider.getValue();
					
					MembraneTool.this.setImage(currentMembrane.getCornerImage(MembraneTool.this,2));
					}		
			}catch (Exception ex) {
  			 tool.handleException(ex);
  		 }
		}
		
	}	
	
	
	
	class ThresholdChangeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e) {
			try {
				JSlider slider = (JSlider)e.getSource();
				if(!slider.getValueIsAdjusting()){
					eCadherinThreshold=slider.getValue();	
					eCadherinThresholdImage=ImageUtilities.getThresholdImage(originalImage, eCadherinThreshold, 255);
					eCadherinThresholdLabel.setText("ECadherin Threshold :: "+eCadherinThreshold);
					//if(currentCorner!=null)MembraneTool.this.currentCorner.getECadherinPoints(MembraneTool.this);
					//MembraneTool.this.currentMembrane.getECadherinPoints(MembraneTool.this);
					//MembraneTool.this.setImage(MembraneTool.this.currentCorner.getCornerEdgeImage());		
					MembraneTool.this.setImage(MembraneTool.this.currentMembrane.getCornerImage(MembraneTool.this, 1));
					validate();
					}		
			}catch (Exception ex) {
  			 tool.handleException(ex);
  		 }
		}
		
	}	

	
	class MeasurementChangeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e) {
			try {
				JSlider slider = (JSlider)e.getSource();
				if(!slider.getValueIsAdjusting()){
					MeasurementTool mt=((MeasurementTool)MembraneTool.this.getMultiChannelImage().getMeasurementTool());
					mt.setMeasurementThreshold(slider.getValue());
					mt.applyThreshold();
					measurementThresholdLabel.setText("Measurement Threshold :: "+slider.getValue());
					//if(currentCorner!=null)MembraneTool.this.currentCorner.getECadherinPoints(MembraneTool.this);
					//MembraneTool.this.currentMembrane.getECadherinPoints(MembraneTool.this);
					//MembraneTool.this.setImage(MembraneTool.this.currentCorner.getCornerEdgeImage());		
					MembraneTool.this.setImage(MembraneTool.this.currentMembrane.getCornerImage(MembraneTool.this, 0));
					validate();
					}		
			}catch (Exception ex) {
  			 tool.handleException(ex);
  		 }
		}
		
	}	
	
	class outputResultsThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			try {
				saveButton.setEnabled(false);
				MembraneTool.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy_MM_dd_HHmm");
				java.util.Date date = new java.util.Date();
				String time=formatter.format(date);
				String name=MembraneTool.this.getImageName();
				String iname=getTool().getOutputDirectory()+"/"+name+"_"+time+"_Cell_"+currentMembrane.getNumber();
				Membrane_SpreadSheet ss=new Membrane_SpreadSheet(iname+".xls",MembraneTool.this); 
				ss.makeMembraneSheet();
				iname=getTool().getOutputDirectory()+"/"+name+"_EDGE_MAP_"+time;
				ImageIO.write(binaryImage, "PNG", new File(iname+".png")); 		
				getTool().setTextArea("Edge Map image written to "+iname+".png\n");
				PDFWriter.writeDocument(MembraneTool.this);
				MembraneTool.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				saveButton.setEnabled(true);
			} catch (Exception e) {
				tool.handleException(e);
			}
			
		}
		
	}
	
	
	class saveEdgeMapThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(saveEdgeMapButton!=null)saveEdgeMapButton.setEnabled(false);
			MembraneTool.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy_MM_dd_HHmm");
				java.util.Date date = new java.util.Date();
				String time=formatter.format(date);
				String name=MembraneTool.this.getImageName();
				String iname=getTool().getOutputDirectory()+"/"+name+"_EDGE_MAP_"+time;
				ImageIO.write(binaryImage, "PNG", new File(iname+".png")); 	
				tool.setTextArea("Written Edge Map Image :: "+iname+".png");
				
			} catch (Exception e) {
				tool.handleException(e);
			}
			MembraneTool.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			if(saveEdgeMapButton!=null)saveEdgeMapButton.setEnabled(true);
		}
		
	}
	
	
	class loadEdgeMapThread implements Runnable{
		
		public void run(){
			try{
					 //user chooses a single file with extension .tif
					JFileChooser chooser=new JFileChooser(".",FileSystemView.getFileSystemView());
			        ExtensionFileFilter filter = new ExtensionFileFilter(true); 
			        filter.addExtension("png",false); 
			        filter.setDescription("png Edge Map Files"); 
			        chooser.setFileFilter(filter); 
			        if(tool.getOutputDirectory()!=null)chooser.setCurrentDirectory(new File(tool.getOutputDirectory()));
			        chooser.setDialogTitle("Select a single tif file"); 
			        int returnVal = chooser.showOpenDialog(tool); 
			        if(returnVal == JFileChooser.APPROVE_OPTION) {	
			        	 try {
				    		   BufferedImage img = ImageIO.read(chooser.getSelectedFile());
				    		   if(img.getType()!=BufferedImage.TYPE_BYTE_BINARY)
				    			   throw new Exception("Error : Edge map image is not binary type");
				    		   binaryImage = img;
				    		   setImage(binaryImage);
				    		   state=EDGE_STATE;
				   			   addComponents();
				    		   collection.clear();
				    		   currentMembrane=null;
				    		   finishButton.setEnabled(true);
				    	   } catch (Exception e) {
				    		   JOptionPane.showMessageDialog(
				    				   tool, 
			                           e.toString(), 
			                           tool.getTitle() , 
			                           JOptionPane.ERROR_MESSAGE);
				    		   e.printStackTrace();
				    	   }
			        }
			        
			            
			}
			catch(Exception e){
					JOptionPane.showInputDialog(tool, e.getMessage(), "ERROR - An exception has occurred", JOptionPane.ERROR_MESSAGE);
			        e.printStackTrace();   
			        validate();
			    }
			finally{	
				}
			}
	}	
	
}
