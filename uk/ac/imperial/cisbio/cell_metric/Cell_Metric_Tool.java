package uk.ac.imperial.cisbio.cell_metric;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileSystemView;

import uk.ac.imperial.cisbio.imaging.cell_metric.gui.MultiChannelImage;
import uk.ac.imperial.cisbio.imaging.cell_metric.gui.Params_Dialog;
import uk.ac.imperial.cisbio.cell_metric.utilities.Palette;
import uk.ac.imperial.cisbio.cell_metric.utilities.Defaults;
import uk.ac.imperial.cisbio.cell_metric.utilities.ExtensionFileFilter;




public class Cell_Metric_Tool extends JFrame{


		/****************************************************************************************/
		/* CONSTANTS 																			*/
		/****************************************************************************************/
		public final static String TITLE="Junction Mapper";
		
		public final static String EDGE="Junction 1 Channel";
		public final static String NUCLEUS="Nucleus";
		public final static String MEASUREMENT="Junction 2 Channel";
		public final static String NOT_USED="Not used";
		
		/****************************************************************************************/
		/* INSTANCE VARIABLES 																	*/
		/****************************************************************************************/
		private File currentDirectory;
		private String outputDirectory=null;
		
		
		/****************************************************************************************/
		/* GUI COMPONENTS 																		*/
		/****************************************************************************************/
		private JMenuBar menuBar = new JMenuBar();    //JMenuBar that contains three the menus
		private JMenu fileMenu = new JMenu();               //Menu1 - File Menu
		private JMenuItem quit = new JMenuItem();           //Quit the Server
		private JMenuItem loadSingleImage = new JMenuItem();        //load a singleImage
		//private JMenuItem loadDirectory = new JMenuItem();        	//load a directory
		//private JMenu processMenu = new JMenu();               //Menu1 - File Menu
		//private JMenu resetMenu = new JMenu();               //Menu1 - File Menu
		//private JMenuItem totalReset = new JMenuItem();       		//reset processing sequence
		private JTabbedPane tabbedPane=new JTabbedPane();
		private JTextArea textArea = new JTextArea();
		
		
		/****************************************************************************************/
		/* CLASS CONSTRUCTOR																	*/
		/****************************************************************************************/
		public Cell_Metric_Tool(){
			 this.setTitle(TITLE); 
			  
			  // set layout. 
			  this.getContentPane().setLayout(new BorderLayout()); 
			  
			  // Set the closing operation so the application is finished. 	  
			  this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			  Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
			  this.setSize(new Dimension((int)d.getWidth()-50,(int)d.getHeight()-50));
			  this.addWindowListener(new WindowListener());//add window listener
			  this.addComponents();
			  this.setVisible(true);
		}
		
		
		private void addComponents(){
			//set Menu Objects
			this.setJMenuBar(menuBar);
			MenuAction menuAction=new MenuAction();
			
			//add File Menu
			fileMenu.setText("File");
			menuBar.add(fileMenu);	
			
			//menuBar.add
			this.loadSingleImage.setText("Load Single Image");
			this.loadSingleImage.setToolTipText("Load a Single Image into the analysis environment"); 
			fileMenu.add(this.loadSingleImage);
			this.loadSingleImage.addActionListener(menuAction);
			
			//menuBar.add
			/**this.loadDirectory.setText("Load Directory");
			this.loadDirectory.setToolTipText("Load a Directory into the analysis environment"); 
			fileMenu.add(this.loadDirectory);
			this.loadDirectory.addActionListener(menuAction);**/
			
			//menuBar.add
			this.quit.setText("Exit");
			this.quit.setToolTipText("Exit from "+TITLE+"?"); 
			fileMenu.add(this.quit);
			this.quit.addActionListener(menuAction);
			
			//menuBar.add
			this.quit.setText("Exit");
			this.quit.setToolTipText("Exit from "+TITLE+"?"); 
			fileMenu.add(this.quit);
			this.quit.addActionListener(menuAction);
			
			//add Process Menu
			//processMenu.setText("Process");
			//menuBar.add(processMenu);	
			//processMenu.setEnabled(true);
			
			
			//add Process Menu
			/**resetMenu.setText("Reset");
			menuBar.add(resetMenu);	
			resetMenu.setEnabled(false);
		
			
			this.totalReset.setText("Reset Processing Sequence");
			this.totalReset.setToolTipText("Reset Processing Sequence");
			this.totalReset.addActionListener(menuAction);
			resetMenu.add(this.totalReset);
			this.totalReset.setEnabled(false);**/
			
			JPanel p=new JPanel();
			p.setLayout(new BorderLayout());
			this.getContentPane().add(p);
			p.add(this.tabbedPane,BorderLayout.CENTER);
			
			JScrollPane spane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			spane.setPreferredSize(new Dimension(800,100));
			spane.setMaximumSize(new Dimension(800,100));
			textArea.setForeground(Palette.ELSE_Blue);
			textArea.setFont(Defaults.default_Font);
			textArea.setBackground(Palette.White);
			p.add(spane,BorderLayout.SOUTH);
			
			this.validate();
		}
		
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			//System.setProperty("com.sun.media.jai.disableMediaLib", "true"); 
			try {
				  	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				  	System.setProperty("com.sun.media.jai.disableMediaLib", "true");
			    } catch(Exception e) {
			    	System.out.println("Error setting LAF: " + e);
			    }
			 
			 System.out.println("Starting "+TITLE+" ... ");
			 new Cell_Metric_Tool();
			  
		}
		
		/**
		 * Global exception handler
		 * Unhandled exceptions thrown up to this method
		 * @param e
		 */
		
		public void handleException(Exception e){
			JOptionPane.showMessageDialog(this, e.toString(), "ERROR : Exception Thrown", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();   
	        setTextArea("EXCEPTION HAS OCCURRED :: "+e.toString()+"\n");
		}

		/****************************************************************************************/
		/* INTERFACE METHODS																	*/
		/****************************************************************************************/
		
		/**
		 * append text to scrolling text area at bottom of interface
		 * @param s : text to be appended
		 */
		
		public void setTextArea(String s){
			//final String st=s;
			//Thread t = new Thread(){   
			   // public void run(){   
			    	textArea.append(s);
					textArea.setCaretPosition(textArea.getText().length());
			    //}   
			//};   
			//try{   
				//t.setPriority(Thread.MAX_PRIORITY) ;
				//t.start();
			   
			//} catch(Exception e){
				//handleException(e);
			//}
		}
		
		
		public String getOutputDirectory(){
			return this.outputDirectory;
		}
		
		
		public File getCurrentDirectory(){
			return this.currentDirectory;
		}
		
		public JTabbedPane getTabbedPane(){
			return this.tabbedPane;
		}
		
		/****************************************************************************************/
		/* PRIVATE METHODS																		*/
		/****************************************************************************************/
		private void exitApplication(){
		try {
			// Beep
			java.awt.Toolkit.getDefaultToolkit().beep();
			    	
			// Show a confirmation dialog
			int reply = JOptionPane.showConfirmDialog(this, 
			    	                                          "Exit "+TITLE+"?", 
			    	                                          TITLE , 
			    	                                          JOptionPane.YES_NO_OPTION, 
			    	                                          JOptionPane.QUESTION_MESSAGE);
					
			// If the confirmation was affirmative,disconnect and exit.
			if (reply == JOptionPane.YES_OPTION){
			                dispose();    
			                System.exit(0);     
					        }
			
			}
		catch (Exception e) {}
		} 
		
		
		//experiment loaded as a thread as a file is read.
		private void startLoadThread(){
		    //Thread t=new Thread(new loadImage());
			Thread t=new Thread(new loadSingleFile());
			t.start();
		}
		
		
		
		
		/**private void totalReset(){
			this.tabbedPane.removeAll();
			this.totalReset.setEnabled(false);
			this.resetMenu.setEnabled(false);
			System.gc();
		}**/
		
		
		
		private void createOutputDirectory(){
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy_MM_dd_HHmm");
			java.util.Date date = new java.util.Date();
			this.outputDirectory="./"+formatter.format(date);
			new File(this.outputDirectory).mkdir();
		}
		
		/****************************************************************************************/
		/* INNER CLASSES																		*/
		/****************************************************************************************/
		
		/****************************************************************************************/
		//LISTENER CLASSES FOR THE SERVER JFRAME AND COMPONENTS
		/****************************************************************************************/ 
		   
		//window adapter class
		class WindowListener extends java.awt.event.WindowAdapter
		{
				public void windowClosing(java.awt.event.WindowEvent event)
				{
					Object object = event.getSource();
					if (object == Cell_Metric_Tool.this)exitApplication();
				}
		}
		
		//action listener for menu items
		class MenuAction implements java.awt.event.ActionListener
		{
		public void actionPerformed(java.awt.event.ActionEvent event)
		        {
					Object object = event.getSource();
					if (object == quit)exitApplication();	
					else if (object == loadSingleImage)startLoadThread();	
					//else if (object == totalReset)totalReset();
		        }
		}
	
		
		
		/****************************************************************************************/
		/* THREAD TO LOAD IN A SINGLE IMAGE FILE												*/
		/****************************************************************************************/
		class loadSingleFile implements Runnable{
		
			public void run(){
				try{
						 //user chooses a single file with extension .tif
						if(outputDirectory==null)createOutputDirectory();
					  	JFileChooser chooser=new JFileChooser(".",FileSystemView.getFileSystemView());
				        ExtensionFileFilter filter = new ExtensionFileFilter(true); 
				        filter.addExtension("tif",false); 
				        filter.addExtension("tiff",false); 
				        filter.setDescription("Tif Image Files"); 
				        chooser.setFileFilter(filter); 
				        if(currentDirectory!=null)chooser.setCurrentDirectory(currentDirectory);
				        chooser.setDialogTitle("Select a single tif file"); 
				        int returnVal = chooser.showOpenDialog(Cell_Metric_Tool.this); 
				        if(returnVal == JFileChooser.APPROVE_OPTION) {	
				        	MultiChannelImage panel=new MultiChannelImage(Cell_Metric_Tool.this);
				        	Params_Dialog dialog=new Params_Dialog(Cell_Metric_Tool.this,"Choose the channels for this image",panel);	
				        	currentDirectory=chooser.getCurrentDirectory();
				        	 try {
					    		   BufferedImage img = ImageIO.read(chooser.getSelectedFile());
					    		   panel.setName(chooser.getSelectedFile().getName());
					    		   panel.setDirectory(chooser.getSelectedFile().getAbsolutePath());
					    		   panel.setImage(img);
					    		   //System.out.println(panel.toString());
					    		   panel.setImages();
					    		   tabbedPane.addTab(panel.getName(), panel);
					    		   tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
					    	       validate();
					    	   } catch (Exception e) {
					    		   JOptionPane.showMessageDialog(
					    				   Cell_Metric_Tool.this, 
				                           e.toString(), 
				                           getTitle() , 
				                           JOptionPane.ERROR_MESSAGE);
					    		   e.printStackTrace();
					    	   }
				        }
				        
				            
				}
				catch(Exception e){
						JOptionPane.showMessageDialog(Cell_Metric_Tool.this, e.getMessage(), "ERROR - An exception has occurred", JOptionPane.ERROR_MESSAGE);
				        e.printStackTrace();   
				        tabbedPane.removeAll();
				        validate();
				    }
				finally{
					//totalReset.setEnabled(true);	
					}
				}
		}
		

}
