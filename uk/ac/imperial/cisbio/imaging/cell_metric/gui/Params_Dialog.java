package uk.ac.imperial.cisbio.imaging.cell_metric.gui;


import java.awt.BorderLayout;   
import java.awt.Dimension;
import java.awt.Frame;   
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;   
import javax.swing.*; 

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import uk.ac.imperial.cisbio.cell_metric.Cell_Metric_Tool;



public class Params_Dialog extends JDialog {
	
	/****************************************************************************************/
	/* INSTANCE VARIABLES 																	*/
	/****************************************************************************************/
	   private Cell_Metric_Tool tool;
	   private JComboBox channel1;
	   private JComboBox channel2;
	   private JComboBox channel3;
	   private JComboBox channel4;
	   private JComboBox channel5;
	   private MultiChannelImage panel;
	   private JTextArea textArea=new JTextArea();
	   
	   
	  
	/****************************************************************************************/
	/* CLASS CONSTRUCTOR 																	*/
	/****************************************************************************************/
	    
	   public Params_Dialog(Frame owner, String title, MultiChannelImage panel) {   
		   super(owner, title, true);  
		   this.panel=panel;
		   Dimension dim = new Dimension(600,400);
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
		      
		      JPanel textPanel=new JPanel();
		      textPanel.setPreferredSize(new Dimension(400,200));
		      textPanel.setLayout(new GridLayout(6,2));
		      
		      textPanel.add(new JLabel("Channel 1 (red)"));
		      channel1=makeComboBox(Cell_Metric_Tool.EDGE);
		      textPanel.add(channel1);
		      textPanel.add(new JLabel("Channel 2 (green)"));
		      channel2=makeComboBox(Cell_Metric_Tool.MEASUREMENT);
		      textPanel.add(channel2);
		      textPanel.add(new JLabel("Channel 3 (blue)"));
		      channel3=makeComboBox(Cell_Metric_Tool.NUCLEUS);
		      textPanel.add(channel3);
		      textPanel.add(new JLabel("Channel 4"));
		      channel4=makeComboBox(Cell_Metric_Tool.NOT_USED);
		      textPanel.add(channel4);
		      textPanel.add(new JLabel("Channel 5"));
		      channel5=makeComboBox(Cell_Metric_Tool.NOT_USED);
		      textPanel.add(channel5);
		      textPanel.add(new JLabel("Experimental procedure and comments"));
		      textArea.setSize(new Dimension(200,100));
		      JScrollPane scroll = new JScrollPane(textArea);
		      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		      textPanel.add(this.textArea);
		      
		      
		      getContentPane().add(textPanel, BorderLayout.CENTER);   
		      getContentPane().add(btnPanel, BorderLayout.SOUTH);   
		   
	   }
	   
	   
	   private JComboBox makeComboBox(String value){
		  String[] options={Cell_Metric_Tool.EDGE, Cell_Metric_Tool.NUCLEUS, Cell_Metric_Tool.MEASUREMENT, Cell_Metric_Tool.NOT_USED};
		  int index=0;
		  JComboBox combo = new JComboBox(options);
		  for(int i=0;i<options.length;i++){
			  if(value.equals(options[i])){
				  index=i;
				  break;
			  }
		  }
		  combo.setSelectedIndex(index);
		  
		  return combo;
	   }

		/****************************************************************************************/
		/* INTERACE METHODS 																	*/
		/****************************************************************************************/
	     
	   private void okButton() throws Exception{  
		 panel.setChannelType((String)channel1.getSelectedItem(),0);
		 panel.setChannelType((String)channel2.getSelectedItem(),1);
		 panel.setChannelType((String)channel3.getSelectedItem(),2);
		 panel.setChannelType((String)channel4.getSelectedItem(),3);
		 panel.setChannelType((String)channel5.getSelectedItem(),4);
		 panel.setExperimentDescription(textArea.getText());
	     setVisible(false);   
	     this.dispose();
	   }   
	  
	   private void noButton() {   
	      this.setVisible(false);  
	      this.dispose();
	   }   
	   
	  
}  



