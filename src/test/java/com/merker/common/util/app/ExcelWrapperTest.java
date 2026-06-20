package com.merker.common.util.app;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExcelWrapperTest {

    private File testDir;
    private File testFile;

    @Before
    public void setUp() throws IOException {
        testDir = Files.createTempDirectory("excel_test").toFile();
        testFile = new File(testDir, "test.xls");

        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TestSheet");
            
            // Header Row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Active");

            // Data Row
            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue(101);
            data.createCell(1).setCellValue("John Doe");
            data.createCell(2).setCellValue(true);

            try (FileOutputStream fileOut = new FileOutputStream(testFile)) {
                workbook.write(fileOut);
            }
        }
    }

    @After
    public void tearDown() {
        if (testDir != null && testDir.exists()) {
            File[] files = testDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            testDir.delete();
        }
    }

    @Test
    public void testProcessGeneratesFiles() throws Exception {
        ExcelWrapper.process(testFile);

        File expectedJsonFile = new File(testDir, "TestSheet.json");
        File expectedTxtFile = new File(testDir, "TestSheet.txt");

        assertTrue("JSON file should be created", expectedJsonFile.exists());
        assertTrue("TXT file should be created", expectedTxtFile.exists());

        String jsonContent = new String(Files.readAllBytes(Paths.get(expectedJsonFile.getAbsolutePath())));
        String txtContent = new String(Files.readAllBytes(Paths.get(expectedTxtFile.getAbsolutePath())));

        // JSON string formatting in ExcelWrapper
        // It creates something like: {"Row 1":{"ID":"101","Name":"John Doe","Active":"true"}}
        assertTrue("JSON content should contain data", jsonContent.contains("John Doe"));
        assertTrue("JSON content should contain boolean data", jsonContent.contains("true"));
        assertTrue("JSON content should contain numeric data", jsonContent.contains("101"));

        // TXT formatting
        // It creates something like: 
        // ID    Name    Active    
        // 101.0    John Doe    true    
        assertTrue("TXT content should contain header", txtContent.contains("ID    Name    Active"));
        assertTrue("TXT content should contain row data: " + txtContent, txtContent.contains("101.0") && txtContent.contains("John Doe") && txtContent.contains("true"));
    }
}
