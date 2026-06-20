package com.merker.common.util.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExcelWrapper {

    public static void process(File file) {
       creteJSONAndTextFileFromExcel(file);
    }

    private static void creteJSONAndTextFileFromExcel(File file) {
        try (InputStream fInputStream = new FileInputStream(file);
             Workbook excelWorkBook = WorkbookFactory.create(fInputStream)) {

            int totalSheetNumber = excelWorkBook.getNumberOfSheets();
            for(int i = 0; i < totalSheetNumber; i++) {
                Sheet sheet = excelWorkBook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                if(sheetName != null && sheetName.length() > 0) {
                    List<List<String>> sheetDataTable = getSheetDataList(sheet);

                    String jsonString = getJSONStringFromList(sheetDataTable);
                    String jsonFileName = sheetName + ".json";
                    writeStringToFile(file.getParentFile().getAbsolutePath(), jsonString, jsonFileName);

                    String textTableString = getTextTableStringFromList(sheetDataTable);
                    String textTableFileName = sheetName + ".csv";
                    writeStringToFile(file.getParentFile().getAbsolutePath(), textTableString, textTableFileName);
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private static List<List<String>> getSheetDataList(Sheet sheet) {
        List<List<String>> ret = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        if(lastRowNum > 0) {
            for(int i = firstRowNum; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int lastCellNum = row.getLastCellNum();
                if (lastCellNum < 0) continue;

                List<String> rowDataList = new ArrayList<>();
                for(int j = 0; j < lastCellNum; j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = formatter.formatCellValue(cell);
                    rowDataList.add(cellValue);
                }
                ret.add(rowDataList);
            }
        }
        return ret;
    }

    private static String getJSONStringFromList(List<List<String>> dataTable) {
        if(dataTable == null || dataTable.size() <= 1) {
            return "[]";
        }
        List<Map<String, String>> list = new ArrayList<>();
        List<String> headerRow = dataTable.get(0);
        int columnCount = headerRow.size();

        for(int i = 1; i < dataTable.size(); i++) {
            List<String> dataRow = dataTable.get(i);
            Map<String, String> rowMap = new LinkedHashMap<>();
            for(int j = 0; j < columnCount; j++) {
                String columnName = headerRow.get(j);
                String columnValue = (dataRow.size() > j) ? dataRow.get(j) : "";
                rowMap.put(columnName, columnValue);
            }
            list.add(rowMap);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list);
    }

    private static String getTextTableStringFromList(List<List<String>> dataTable) {
        StringBuilder strBuf = new StringBuilder();
        if(dataTable != null) {
            for(List<String> row : dataTable) {
                for(int j = 0; j < row.size(); j++) {
                    String column = row.get(j);
                    if (column != null && (column.contains(",") || column.contains("\"") || column.contains("\n"))) {
                        strBuf.append("\"").append(column.replace("\"", "\"\"")).append("\"");
                    } else if (column != null) {
                        strBuf.append(column);
                    }
                    
                    if (j < row.size() - 1) {
                        strBuf.append(",");
                    }
                }
                strBuf.append("\r\n");
            }
        }
        return strBuf.toString();
    }

    private static void writeStringToFile(String workingDir, String data, String fileName) {
        Path filePath = Paths.get(workingDir, fileName);
        try (BufferedWriter buffWriter = Files.newBufferedWriter(filePath)) {
            buffWriter.write(data);
            System.out.println(filePath.toAbsolutePath().toString() + " has been created.");
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
