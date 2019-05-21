package uk.ac.imperial.cisbio.cell_metric.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelWriter {
	
	public final static int NO_CORNER=0;
	public final static int TOP_LEFT=1;
	public final static int TOP_RIGHT=2;
	public final static int BOTTOM_LEFT=3;
	public final static int BOTTOM_RIGHT=4;
	public final static int TOP=5;
	public final static int BOTTOM=6;
	public final static int LEFT=7;
	public final static int RIGHT=8;
	
	private WritableWorkbook workbook;
	private WritableFont font = new WritableFont(WritableFont.ARIAL, 8);
	private WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD, false,UnderlineStyle.NO_UNDERLINE);
	private WritableFont bigBoldFont = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false,UnderlineStyle.NO_UNDERLINE);

	private WritableCellFormat cellFormat;
	private WritableCellFormat topLeftCellFormat;
	private WritableCellFormat topRightCellFormat;
	private WritableCellFormat bottomLeftCellFormat;
	private WritableCellFormat bottomRightCellFormat;
	private WritableCellFormat topCellFormat;
	private WritableCellFormat leftCellFormat;
	private WritableCellFormat rightCellFormat;
	private WritableCellFormat bottomCellFormat;
	
	private WritableCellFormat boldCellFormat;
	private WritableCellFormat boldTopLeftCellFormat;
	private WritableCellFormat boldTopRightCellFormat;
	private WritableCellFormat boldBottomLeftCellFormat;
	private WritableCellFormat boldBottomRightCellFormat;
	private WritableCellFormat boldTopCellFormat;
	private WritableCellFormat boldLeftCellFormat;
	private WritableCellFormat boldRightCellFormat;
	private WritableCellFormat boldBottomCellFormat;
	
	private WritableCellFormat bigBoldCellFormat;
	
	
	public ExcelWriter(String fileName){
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("en", "EN"));
		
		try {
			cellFormat = new WritableCellFormat(font);
			topRightCellFormat= new WritableCellFormat(font);
			bottomLeftCellFormat= new WritableCellFormat(font);
			bottomRightCellFormat = new WritableCellFormat(font);
			topCellFormat= new WritableCellFormat(font);
			leftCellFormat= new WritableCellFormat(font);
			rightCellFormat= new WritableCellFormat(font);
			bottomCellFormat= new WritableCellFormat(font);
			topLeftCellFormat= new WritableCellFormat(font);
			
			topLeftCellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
			topLeftCellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
			topRightCellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			topRightCellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
			bottomLeftCellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
			bottomLeftCellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);	
			bottomRightCellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			bottomRightCellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
			topCellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
			bottomCellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
			leftCellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
			rightCellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			
			boldCellFormat= new WritableCellFormat(boldFont);
			boldTopRightCellFormat= new WritableCellFormat(boldFont);
			boldBottomLeftCellFormat= new WritableCellFormat(boldFont);
			boldBottomRightCellFormat = new WritableCellFormat(boldFont);
			boldTopCellFormat= new WritableCellFormat(boldFont);
			boldLeftCellFormat= new WritableCellFormat(boldFont);
			boldRightCellFormat= new WritableCellFormat(boldFont);
			boldBottomCellFormat= new WritableCellFormat(boldFont);
			bigBoldCellFormat=new WritableCellFormat(bigBoldFont);;
			boldTopLeftCellFormat= new WritableCellFormat(boldFont);		
			
			boldTopLeftCellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
			boldTopLeftCellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
			boldTopRightCellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			boldTopRightCellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
			boldBottomLeftCellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
			boldBottomLeftCellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);	
			boldBottomRightCellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			boldBottomRightCellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
			boldTopCellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
			boldBottomCellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
			boldLeftCellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
			boldRightCellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			
			File file=new File(fileName);
			Workbook eworkbook;
			if(file.exists()){
				eworkbook=Workbook.getWorkbook(file);
				file.delete();
				workbook = Workbook.createWorkbook(new File(fileName), eworkbook); 
			}
			else workbook = Workbook.createWorkbook(file, wbSettings);
		}
		catch(FileNotFoundException e){
			JOptionPane.showMessageDialog(null, "!!ERROR File "+fileName+" is currently open so cannot be written. Please close the results Excel File and try again.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	public void writeFile(){
	try{
		workbook.write();
		workbook.close();
	}
	catch(Exception e){
		e.printStackTrace();
	}
	}
	
	
	public WritableWorkbook getWorkbook(){
		return this.workbook;
	}
	
	public WritableSheet getNewSheet(String name){	
		boolean done=false;
		int c=1;
		String oSheetName=name;
			
		while(!done){
			if(workbook.getSheet(name)==null){
				done=true;
				}
			else{
				name=oSheetName+"_"+c;
				c++;
				}
			}
		workbook.createSheet(name, 0);	
		return workbook.getSheet(0);		
	}

	

	public void addHeader(WritableSheet sheet, int column, int row, String s, boolean big, int corner) throws RowsExceededException, WriteException {
		WritableCellFormat format=cellFormat;
		if(big)
			format=bigBoldCellFormat;
		else format=getCellFormat(corner);
		
		Label label = new Label(column, row, s, format);
		sheet.addCell(label);
	}

	public void addNumber(WritableSheet sheet, int column, int row, int val, int corner) throws WriteException, RowsExceededException {
		WritableCellFormat format=getCellFormat(corner);
		Integer integer = new Integer(val);
		Number number = new Number(column, row, integer, format);
		sheet.addCell(number);
	}
	
	public void addNumber(WritableSheet sheet, int column, int row, double val, int corner) throws WriteException, RowsExceededException {
		WritableCellFormat format=getCellFormat(corner);
		Double doub=new Double(val);
		Number number = new Number(column, row, doub, format);
		sheet.addCell(number);
	}

	public void addLabel(WritableSheet sheet, int column, int row, String s, int corner) throws WriteException, RowsExceededException {
		WritableCellFormat format=getCellFormat(corner);
		Label label = new Label(column, row, s, format);
		sheet.addCell(label);
	}
	
	private WritableCellFormat getCellFormat(int corner){
		WritableCellFormat format=cellFormat;
		switch(corner){
			case TOP_LEFT:
				format=topLeftCellFormat;
			break;	
			case TOP_RIGHT:
				format=topRightCellFormat;
			break;	
			case BOTTOM_LEFT:
				format=bottomLeftCellFormat;
			break;	
			case BOTTOM_RIGHT:
				format=bottomRightCellFormat;
			break;	
			case TOP:
				format=topCellFormat;
			break;	
			case BOTTOM:
				format=bottomCellFormat;
			break;	
			case LEFT:
				format=leftCellFormat;
			break;	
			case RIGHT:
				format=rightCellFormat;
			break;	
		}
		return format;
	}

}
