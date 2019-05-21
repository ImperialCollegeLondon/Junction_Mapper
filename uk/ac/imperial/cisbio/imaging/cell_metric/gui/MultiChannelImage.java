package uk.ac.imperial.cisbio.imaging.cell_metric.gui;

import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
/************************************************************/
/* IMPORTS													*/
/************************************************************/
import javax.swing.JPanel;

import uk.ac.imperial.cisbio.cell_metric.Cell_Metric_Tool;
import uk.ac.imperial.cisbio.cell_metric.utilities.ImageUtilities;

public class MultiChannelImage extends JPanel {
	
	/****************************************************************************************/
	/* INSTANCE VARIABLES 																	*/
	/****************************************************************************************/
	 private String[] channelType={Cell_Metric_Tool.EDGE,Cell_Metric_Tool.NUCLEUS,Cell_Metric_Tool.MEASUREMENT,Cell_Metric_Tool.NOT_USED,Cell_Metric_Tool.NOT_USED};	
	 private ImagePanel originalPanel;
	 private ImagePanel[] channel=new ImagePanel[5];
	 
	 private BufferedImage image;
	 private JTabbedPane tabbedPane;
	 
	 private NucleusTool nucleusTool;
	 private MembraneTool membraneTool;
	 private MeasurementTool measurementTool;
	 private Cell_Metric_Tool tool;
	 private String name;
	 private String directory;
	 private String experimentDescription;
	  
	/****************************************************************************************/
	/* CLASS CONSTRUCTOR 																	*/
	/****************************************************************************************/
	 public MultiChannelImage(final Cell_Metric_Tool tool){
			this.tool=tool;
			this.setLayout(new BorderLayout(0,0));
			
		}
	 
	
	 
	 public void setImages(){
		 tabbedPane = new JTabbedPane();
		 this.add(tabbedPane,BorderLayout.WEST);
		 
		 JPanel panel=new JPanel();
		 //panel.setPreferredSize(new Dimension(200,30));
		 panel.setOpaque(false);
		 //JLabel titleLbl = new JLabel("Close");
		 //panel.add(titleLbl);
		 JButton closeButton = new JButton("x");
		 closeButton.addMouseListener(new MouseAdapter(){
			   public void mouseClicked(MouseEvent e){
			    //tool.remove(MultiChannelImage.this);
				   int reply = JOptionPane.showConfirmDialog(MultiChannelImage.this, 
                        "Close "+MultiChannelImage.this.getName()+"?", 
                        Cell_Metric_Tool.TITLE, 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
			   
			   if (reply == JOptionPane.YES_OPTION){
				    tool.getTabbedPane().remove(MultiChannelImage.this);   
				    tool.getTabbedPane().validate();     
			        }
			  }});
		 panel.add(closeButton);
		 
		 JPanel outer=new JPanel();
		 //outer.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		 outer.setLayout(new BoxLayout(outer, BoxLayout.LINE_AXIS));
		 originalPanel=new ImagePanel(this.image);
		
		 outer.add(originalPanel);
		 outer.add(panel);
		 //originalPanel.add(panel);
		 tabbedPane.addTab("Original Image", new JScrollPane(outer));
		 for(int i=0;i<channel.length;i++){
			 addChannel(i);
		 }
		 //this.nucleusTool=new NucleusTool(ImageUtilities.getChannel(this.image, 2),tool);
		 //JScrollPane spane=new JScrollPane(this.nucleusTool);
		 //spane.setPreferredSize(new Dimension(1000,600));
		 //spane.setMaximumSize(new Dimension(1000,600));
		 //tabbedPane.addTab("Nucleus Tool", spane);
		 //this.membraneTool= new MembraneTool(ImageUtilities.getChannel(this.image, 0),tool,this.nucleusTool);
		 //tabbedPane.addTab("Membrane Tool",new JScrollPane(this.membraneTool));
	 }
	 
	 private void addChannel(int n) {
		 try{
			 if(!channelType[n].equals(Cell_Metric_Tool.NOT_USED)){
				channel[n]=new ImagePanel(ImageUtilities.getChannel(this.image, n));
			    tabbedPane.addTab("Channel "+(n+1)+" : "+this.channelType[n], new JScrollPane(channel[n]));
			    if(this.channelType[n].equals(Cell_Metric_Tool.NUCLEUS))addNucleusTool(n);
			    else if(this.channelType[n].equals(Cell_Metric_Tool.EDGE))addMembraneTool(n);
			    else if(this.channelType[n].equals(Cell_Metric_Tool.MEASUREMENT))addMeasurementTool(n);
			 }
			 else channel[n]=null;
		 }
		 catch(Exception e){
			 tool.handleException(e);
		 }
	 }
	 
	 private void addNucleusTool(int n){
		 this.nucleusTool=new NucleusTool(ImageUtilities.getChannel(this.image, n),tool,this);
		 JScrollPane spane=new JScrollPane(this.nucleusTool);
		 spane.setPreferredSize(new Dimension(1000,600));
		 spane.setMaximumSize(new Dimension(1000,600));
		 tabbedPane.addTab("Channel "+(n+1)+" :: Nucleus Tool", spane);
	 }
	 
	 private void addMembraneTool(int n){
		 this.membraneTool= new MembraneTool(ImageUtilities.getChannel(this.image, n),tool,this);
		 tabbedPane.addTab("Channel "+(n+1)+" :: Membrane Tool",new JScrollPane(this.membraneTool));
	 }
	 
	 private void addMeasurementTool(int n){
		 this.measurementTool= new MeasurementTool(ImageUtilities.getChannel(this.image, n),tool,this);
		 //tabbedPane.addTab("Channel "+(n+1)+" :: Measurement Tool",new JScrollPane(this.measurementTool));
	 }
	
	
	/****************************************************************************************/
	/* ACCESSOR METHODS 																	*/
	/****************************************************************************************/
	
	public BufferedImage getImage(){
			return this.image;
	}
	
	public void setImage(BufferedImage im){
		 this.image=im;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getDirectory(){
		return this.directory;
	}
	
	public void setDirectory(String name){
		this.directory=name;
	}
	
	public String getExperimentDescription(){
		return this.experimentDescription;
	}
	
	public void setExperimentDescription(String name){
		this.experimentDescription=name;
	}
	 
	public String getChannelType(int n){
		return this.channelType[n];
	}
	
	public void setChannelType(String s,int n){
		if(n<channelType.length)this.channelType[n]=s;
	}
	
	public ToolPanel getMembraneTool(){
		return this.membraneTool;
	}
	
	public ToolPanel getMeasurementTool(){
		return this.measurementTool;
	}
	
	
	public Cell_Metric_Tool getCellMetricTool(){
		return this.tool;
	}
	
	/****************************************************************************************/
	/* OTHER METHODS 																		*/
	/****************************************************************************************/
	
	public String toString(){
		String str="";
		if(image!=null)str+=image.toString()+"\n";
		for(int i=0;i<channelType.length;i++)str+="C"+(i+1)+" = "+this.channelType[i]+" :: ";
		return 	super.toString()+"\n"+str.substring(0,str.length()-3);			
	}
}
