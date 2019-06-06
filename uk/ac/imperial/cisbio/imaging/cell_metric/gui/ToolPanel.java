package uk.ac.imperial.cisbio.imaging.cell_metric.gui;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import uk.ac.imperial.cisbio.cell_metric.Cell_Metric_Tool;
import uk.ac.imperial.cisbio.cell_metric.utilities.ImageUtilities;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.ImagePanel;

public abstract class ToolPanel extends JPanel {
	
	/************************************************************/
	/* STATIC VARIABLES											*/
	/************************************************************/
	protected final static int FILTER_STATE = 1;
	protected final static int BINARY_STATE = 2;
	protected final static int COMPLETE_STATE = 3;
	protected final static int EDGE_STATE = 4;
	protected final static int CORNER_STATE = 5;
	protected final static int MEASURE_STATE = 6;
	
	
	/****************************************************************************************/
	/* INSTANCE VARIABLES 																	*/
	/****************************************************************************************/
	protected Cell_Metric_Tool tool;
	protected MultiChannelImage imagePanel;
	protected BufferedImage originalImage;
	protected BufferedImage binaryImage, lastBinaryImage;
	protected ImagePanel image;
	protected int filterDim, cValue, objectSize;
	protected int state=FILTER_STATE;
	protected JPanel componentPanel = new JPanel();
	private JButton resetButton;
	
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public ToolPanel(BufferedImage im,Cell_Metric_Tool tool, MultiChannelImage mcimage){
		this.tool=tool;
		this.imagePanel=mcimage;
		this.originalImage=im;
		this.image=new ImagePanel(im);
		this.addComponents();
	}
	
	/************************************************************/
	/* ABSTRACT METHODS											*/
	/************************************************************/
	protected abstract void getAdaptiveThreshold(int filterSize, int cValue);
	
	protected abstract void endAdaptiveThreshold();
	
	protected abstract void addComponents();
	
	protected abstract void resetImage();
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/	
	public Cell_Metric_Tool getTool(){
		return this.tool;
	}
	
	public MultiChannelImage getMultiChannelImage(){
		return this.imagePanel;
	}
	
	public int getState(){
		return this.state;
	}
	
	public BufferedImage getOriginalImage(){
		return this.originalImage;
	}
	
	public String getImageName(){
		return this.imagePanel.getName();
	}
	
	public String getImageDirectory(){
		return this.imagePanel.getDirectory();
	}
	
	public void setImage(final BufferedImage im){
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
				ToolPanel.this.remove(ToolPanel.this.image);	
				ToolPanel.this.image.setImage(im);
				ToolPanel.this.add(ToolPanel.this.image,BorderLayout.WEST);		
				ToolPanel.this.validate();
				ToolPanel.this.repaint();	
			}
		});
		thread.start();
	}
	
	public BufferedImage getImage(){
		return this.imagePanel.getImage();
	}
	
	/************************************************************/
	/* PRIVATE METHODS											*/
	/************************************************************/	
	
	
	/************************************************************/
	/* PROTECTED METHODS										*/
	/************************************************************/
	protected void addResetButton(){
		resetButton=new JButton("Reset");
		resetButton.addMouseListener(new ButtonListener());
		this.componentPanel.add(resetButton);
	}
	
	protected void removeSmallObjectsFromImage(){
		BufferedImage im;
		try {
			if(image.getImage().getType()==BufferedImage.TYPE_BYTE_BINARY){
				Object[] possibilities = {"10","20","30","40","50","100","150","200","250","300","350","400","450","500"};
				String s = (String)JOptionPane.showInputDialog(
				                    this,
				                    "Remove Objects Smaller than (pixels):\n",
				                    "Remove Small Objects",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    "50");

				//If a string was returned, say so.
				if ((s != null) && (s.length() > 0)) {	   
					objectSize=new Integer(s).intValue();
					Thread t=new Thread(new smallObjectThread());
					t.start();
				}		
			}
		} catch (Exception e) {
			tool.handleException(e);
		}
	}
	
	
	protected void dilateImage(){
		BufferedImage im;
		try {
			im = ImageUtilities.getDilatedImage(this.image.getImage());
			this.setImage(im);
		} catch (Exception e) {
			tool.handleException(e);
		}
		
	}
	
	protected void erodeImage(){
		BufferedImage im;
		try {
			im = ImageUtilities.getErodedImage(this.image.getImage());
			this.setImage(im);
		} catch (Exception e) {
			tool.handleException(e);
		}
	}
	
	/************************************************************/
	/* THREADS FOR PERFORMING OPERATIONS						*/
	/************************************************************/
	
	class thresholdThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			image.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			BufferedImage cells=ImageUtilities.getAdaptiveThresholdImage(image.getImage(),filterDim,cValue);
			ToolPanel.this.setImage(cells);
			endAdaptiveThreshold();
			image.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	
	class smallObjectThread implements Runnable{

		@Override
		public void run() {
		 try{
			// TODO Auto-generated method stub
			image.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			BufferedImage im=ImageUtilities.removeSmallObjects(image.getImage(), objectSize);		
			ToolPanel.this.setImage(im);	
			binaryImage=im;
			image.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 	} catch (Exception e) {
				tool.handleException(e);
			}
		}
		
	}
	
	/************************************************************/
	/* INTERNAL CLASSES : COMPONENT LISTENER					*/
	/************************************************************/
	class ButtonListener extends MouseAdapter{		
		@Override
		public void mousePressed(MouseEvent e) {
			JButton button = (JButton) e.getSource();
			if(button==resetButton)resetImage();
		}
		
}

}
