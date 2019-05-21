package uk.ac.imperial.cisbio.imaging.cell_metric.gui;


import java.awt.BorderLayout;   
import java.awt.Dimension;
import java.awt.Frame;   
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;   
import javax.swing.*; 
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.imperial.cisbio.cell_metric.Cell_Metric_Tool;



public class Adaptive_Threshold_Dialog extends JDialog {
	
	/****************************************************************************************/
	/* INSTANCE VARIABLES 																	*/
	/****************************************************************************************/
	   private Cell_Metric_Tool tool;
	   private ToolPanel panel;
	   private int cValue;
	   private int filterDim;
	  
	/****************************************************************************************/
	/* CLASS CONSTRUCTOR 																	*/
	/****************************************************************************************/
	    
	   public Adaptive_Threshold_Dialog(Frame owner, ToolPanel panel) {   
		   super(owner, "Adaptive thresholding Parameters", true);  
		   this.panel=panel;
		   Dimension dim = new Dimension(400,200);
		   this.setSize(dim);
		   this.setPreferredSize(dim);
		   super.setLocationRelativeTo(owner);
		   this.tool=(Cell_Metric_Tool)owner;
		   this.addComponents();
		   pack();   
		   this.setVisible(true);
		   System.out.println("Parameters window instantiated ....");
	   }   
	   
	   
	   private void addComponents(){
		   JPanel btnPanel = new JPanel();   
		   JButton okBtn   = new JButton("OK");   
		   JButton noBtn   = new JButton("Cancel");   
		   ButtonGroup bg= new ButtonGroup();
		      btnPanel.add(okBtn);   
		      okBtn.addActionListener(new ActionListener() {   
		         public void actionPerformed(java.awt.event.ActionEvent ae) { 
		        	try{
		        		okButton();   
		        	}
		        	catch(Exception e){
		        		tool.handleException(e);
		        	}
		         }   
		      });   
		      noBtn.addActionListener(new ActionListener() {   
		         public void actionPerformed(java.awt.event.ActionEvent ae) {   
		            noButton();   
		         }   
		      });   
		      btnPanel.add(noBtn);   
		      
		      JPanel componentPanel=new JPanel();
		      componentPanel.setLayout(new GridLayout(2,2));
		      JLabel label = new JLabel("C Value");
		      label.setToolTipText("C value - Value added to local mean to exceed threshold");
		      componentPanel.add(label);
		      
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
		      slider.setToolTipText("C value - Value added to local mean to exceed threshold");
			  componentPanel.add(slider);
			  slider.addChangeListener(new SliderChangeListener());
			
			  label=new JLabel("Filter Size");
			  label.setToolTipText("Filter Size - the size of the window from which the mean value is calculated");
		      componentPanel.add(label); String[] filterSizes = { "3x3", "5x5", "7x7", "9x9", "11x11", "21x21", "35x35", "51x51","75x75","99x99" };
			  JComboBox filterSizeCombo = new JComboBox(filterSizes);
			  filterSizeCombo.setSelectedIndex(5);
			  filterDim=9;
			  filterSizeCombo.setToolTipText("Filter Size - the size of the window from which the mean value is calculated");
			  filterSizeCombo.addActionListener(new ComboChangeListener());
			  componentPanel.add(filterSizeCombo);
		      
		      getContentPane().add(componentPanel, BorderLayout.NORTH);   
		      getContentPane().add(btnPanel, BorderLayout.SOUTH);   
		   
	   }
	   
	   
	   
		/****************************************************************************************/
		/* INTERACE METHODS 																	*/
		/****************************************************************************************/
	     
	   private void okButton() throws Exception{  
		 setVisible(false);   
	     this.dispose();
	     panel.getAdaptiveThreshold(filterDim, cValue);
	   }   
	  
	   private void noButton() {   
	      this.setVisible(false);  
	      this.dispose();
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
		
		class ComboChangeListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox cb = (JComboBox)e.getSource();
		        String filterSize = (String)cb.getSelectedItem();
		        int ind= filterSize.indexOf("x");
		        String filter=filterSize.substring(0, ind);
		        filterDim=new Integer(filter).intValue();
		        //System.out.println("Filter Dim "+filterDim);
			}
			
		}
	   
	  
}  



