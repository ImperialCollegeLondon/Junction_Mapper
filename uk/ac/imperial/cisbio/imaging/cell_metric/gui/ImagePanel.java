/************************************************************/
/* PACKAGE													*/
/************************************************************/
package uk.ac.imperial.cisbio.imaging.cell_metric.gui;

/************************************************************/
/* IMPORTS													*/
/************************************************************/
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/************************************************************/
/* CLASS DEFINITION											*/
/************************************************************/
public class ImagePanel extends JPanel implements MouseMotionListener {
	
	/************************************************************/
	/* INSTANCE VARIABLE										*/
	/************************************************************/
	private BufferedImage image;
	
	/************************************************************/
	/* CLASS CONSTRUCTOR										*/
	/************************************************************/
	public ImagePanel(BufferedImage im){
		this.image=im;
		this.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
	}
	
	/************************************************************/
	/* ACCESSOR METHODS											*/
	/************************************************************/
	public void setImage(BufferedImage im){
		this.removeAll();
		this.image=im;
		this.setPreferredSize(new Dimension(this.image.getWidth(),this.image.getHeight()));
		this.repaint();
	}
	
	public BufferedImage getImage(){
		return this.image;
	}
	
	/************************************************************/
	/* PAINT IMAGE ON SCREEN									*/
	/************************************************************/
	 public void paint(Graphics g) {
		  g.drawImage(this.image, 0, 0, null);
	  }
	 
	/************************************************************/
	/* MOUSE MOTION METHODS FOR MOUSEMOTIONLISTENER INTERFACE	*/
	/************************************************************/
		
	public void mouseMoved(MouseEvent e) {
		      System.out.println("Mouse Move :: "+e.getX()+" :: "+e.getY());
			}

	public void mouseDragged(MouseEvent e) {
		 System.out.println("Mouse Move :: "+e.getX()+" :: "+e.getY());
		    }
	 
}
