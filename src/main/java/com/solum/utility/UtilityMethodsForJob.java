package com.solum.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.solum.entity.label.RmaLabels;

@Component
public class UtilityMethodsForJob {
	
	@Value("${aims.directory.of.reports}")
	private String reportGenerationPath;

	
	public void createExcel(List<RmaLabels> labels, String jobId) {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("JobStatus");
		sheet.setColumnWidth(0, 4000);
		sheet.setColumnWidth(1, 4000);

		Row header = sheet.createRow(0);

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		headerStyle.setFont(font);
		
		Cell headerCell = header.createCell(0);
		headerCell.setCellValue("JobId");
		headerCell.setCellStyle(headerStyle);

//		headerCell = header.createCell(1);
//		headerCell.setCellValue("JobStatus");
//		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(1);
		headerCell.setCellValue("Code");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(2);
		headerCell.setCellValue("Name");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(3);
		headerCell.setCellValue("Company");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(4);
		headerCell.setCellValue("Country");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(5);
		headerCell.setCellValue("Region");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(6);
		headerCell.setCellValue("City");
		headerCell.setCellStyle(headerStyle);

//		headerCell = header.createCell(8);
//		headerCell.setCellValue("LabelCode");
//		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(7);
		headerCell.setCellValue("State");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(8);
		headerCell.setCellValue("Type");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(9);
		headerCell.setCellValue("Display_Height");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(10);
		headerCell.setCellValue("Display_Width");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(11);
		headerCell.setCellValue("Process_Update_Time");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(12);
		headerCell.setCellValue("Status_Update_Time");
		headerCell.setCellStyle(headerStyle);
		
		headerCell = header.createCell(13);
		headerCell.setCellValue("Firmware_Version");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(14);
		headerCell.setCellValue("Battery_Level");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(15);
		headerCell.setCellValue("Data_Channel_Rssi");
		headerCell.setCellStyle(headerStyle);
		
		headerCell = header.createCell(16);
		headerCell.setCellValue("Wakeup_Channel_Rssi");
		headerCell.setCellStyle(headerStyle);

		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		
		AtomicInteger counter = new AtomicInteger(1);
		
		labels.forEach((i) -> {
			Row row = sheet.createRow(counter.getAndIncrement());
			
			for (int columnCount = 0; columnCount < 17; columnCount++) {
				if (columnCount == 0) {
					// JobId
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(jobId);
					cell.setCellStyle(style);
				}
//				if (columnCount == 1) {
//					// JobStatus
//					Cell cell = row.createCell(columnCount);
//					cell.setCellValue(jobStatus+"");
//					cell.setCellStyle(style);
//				}
				if (columnCount == 1) {
					// Code
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getCode());
					cell.setCellStyle(style);
				}
				if (columnCount == 2) {
					// Name
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getName());
					cell.setCellStyle(style);
				}
				if (columnCount == 3) {
					// Company
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getCompany());
					cell.setCellStyle(style);
				}
				if (columnCount == 4) {
					// Country
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getCountry());
					cell.setCellStyle(style);
				}
				if (columnCount == 5) {
					// Region
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getRegion());
					cell.setCellStyle(style);
				}
				if (columnCount == 6) {
					// City
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getCity());
					cell.setCellStyle(style);
				}
//				if (columnCount == 8) {
//					// LabelCode
//					Cell cell = row.createCell(columnCount);
//					//cell.setCellValue(i.getLabelcode());
//					cell.setCellStyle(style);
//				}
				if (columnCount == 7) {
					// State
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getState());
					cell.setCellStyle(style);
				}
				if (columnCount == 8) {
					// Type
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getType());
					cell.setCellStyle(style);
				}
				if (columnCount == 9) {
					// Display_Height
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getDisplay_height());
					cell.setCellStyle(style);
				}
				if (columnCount == 10) {
					// Display_Width
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getDisplay_width());
					cell.setCellStyle(style);
				}
				if (columnCount == 11) {
					// Process_Update_Time
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getProcess_update_time());
					cell.setCellStyle(style);
				}
				if (columnCount == 12) {
					// Status Update Time
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getStatus_update_time()+"");
					cell.setCellStyle(style);
				}
				if (columnCount == 13) {
					// Firmvare_Version
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getFirmware_version());
					cell.setCellStyle(style);
				}
				if (columnCount == 14) {
					// Battery_level
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getBattery_level());
					cell.setCellStyle(style);
				}
				if (columnCount == 15) {
					// Data_Channel_Rssi
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getData_channel_rssi());
					cell.setCellStyle(style);
				}
				if (columnCount == 16) {
					// Wakeup_Channel_Rssi
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(i.getWakeup_channel_rssi());
					cell.setCellStyle(style);
				}
			}
			
		});
		
		File currDir = new File(".");
		String path = reportGenerationPath;
		String location = path + "/" + jobId + ".xlsx";
		File file = new File(location);

		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(location);
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
