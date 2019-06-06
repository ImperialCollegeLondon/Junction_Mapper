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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;
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
public class MeasurementTool extends ToolPanel {

	/************************************************************/
	/* STATIC VARIABLES											*/
	/************************************************************/
	private final static int COMBINE_STATE_1 =1;
	
	/************************************************************/
	/* INSTANCE VARIABLES										*/
	/************************************************************/
	private int measurementThreshold=50;
	private BufferedImage measurementThresholdImage;
	
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public MeasurementTool(BufferedImage im,Cell_Metric_Tool tool, MultiChannelImage mci){
		super(im,tool,mci);
	}
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/
	public void setImage(BufferedImage im){
		this.image.setImage(im);
		
	}
	
	public BufferedImage getImage(){
		return this.image.getImage();
	}
	
	
	public int getMeasurementThreshold(){
		return this.measurementThreshold;
	}
	
	public void setMeasurementThreshold(int n){
		this.measurementThreshold=n;
	}
	
	public BufferedImage getMeasurementThresholdImage(){
		return this.measurementThresholdImage;
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
		
		measurementThresholdImage=ImageUtilities.getThresholdImage(this.originalImage, measurementThreshold, 255);
		this.componentPanel.removeAll();	
		/**this.componentPanel.add(new JLabel("Step 1 : Apply Global Threshold"));
			
	 
		JSlider slider = new JSlider();
		slider.setMinimum(0);
		slider.setMaximum(255);
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setPaintTrack(true);
		slider.setValue(this.measurementThreshold);
		slider.setToolTipText("Actin Threshold value");
		componentPanel.add(slider);
		slider.addChangeListener(new SliderChangeListener());
		this.componentPanel.add(new JLabel("Actin Threshold value"));
			
		
		this.componentPanel.validate();
		repaint();		**/
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
	
	
		
	/************************************************************/
	/* INTERNAL CLASSES FOR HANDLING GUI EVENTS					*/
	/************************************************************/
	/** SLIDER CHANGE LISTENER **/
	
	class SliderChangeListener implements ChangeListener{
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider)e.getSource();
				if(!slider.getValueIsAdjusting()){
					measurementThreshold=slider.getValue();
					applyThreshold();
					setImage(measurementThresholdImage);
					image.validate();
					}
				}
			
		}
	
	
	public void applyThreshold(){
		measurementThresholdImage=ImageUtilities.getThresholdImage(originalImage, measurementThreshold, 255);
	}
		
		
	class ButtonListener extends MouseAdapter{		
			@Override
			public void mousePressed(MouseEvent e) {
				JButton button = (JButton) e.getSource();
				
			}
			
	}


	@Override
	protected void resetImage() {
		// TODO Auto-generated method stub
		this.image.setImage(this.originalImage);
		this.state=FILTER_STATE;
		tool.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		addComponents();
		validate();
		
	}
	
	
	
	    
	
	
	/************************************************************/
	/* THREADS FOR PERFORMING OPERATIONS						*/
	/************************************************************/
	
	
	
}
