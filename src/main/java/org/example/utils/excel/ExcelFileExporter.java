package org.example.utils.excel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.payload.data.ExcelData;

public class ExcelFileExporter {

	public static ByteArrayInputStream excelDataToExcelFile(List<ExcelData> excelData){
		try(Workbook workbook = new XSSFWorkbook()){
			Sheet sheet = workbook.createSheet("Студенти");

			Row       row             = sheet.createRow(0);
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			// Creating header
			Cell cell = row.createCell(0);
			cell.setCellValue("№ з/п");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(1);
			cell.setCellValue("Електронна адреса");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(2);
			cell.setCellValue("ПІБ студента");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(3);
			cell.setCellValue("Курс");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(4);
			cell.setCellValue("Група");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(5);
			cell.setCellValue("Кількість дисциплін");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(6);
			cell.setCellValue("Шифр дисципліни");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(7);
			cell.setCellValue("Назва дисципліни *");
			cell.setCellStyle(headerCellStyle);

			// Creating data rows for each customer
			for(int i = 0; i < excelData.size(); i++) {
				Row dataRow = sheet.createRow(i + 1);
				dataRow.createCell(0).setCellValue(excelData.get(i).getNo());
				dataRow.createCell(1).setCellValue(excelData.get(i).getEmail());
				dataRow.createCell(2).setCellValue(excelData.get(i).getName());
				dataRow.createCell(3).setCellValue(excelData.get(i).getCourse());
				dataRow.createCell(4).setCellValue(excelData.get(i).getGroupName());
				dataRow.createCell(5).setCellValue(excelData.get(i).getAnswersAmount());
				dataRow.createCell(6).setCellValue(excelData.get(i).getCipherName());
				dataRow.createCell(7).setCellValue(excelData.get(i).getCipherDescription());
			}

			// Making size of column auto resize to fit with data
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
