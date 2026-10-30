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
        File expectedCsvFile = new File(testDir, "TestSheet.csv");

        assertTrue("JSON file should be created", expectedJsonFile.exists());
        assertTrue("CSV file should be created", expectedCsvFile.exists());

        String jsonContent = new String(Files.readAllBytes(Paths.get(expectedJsonFile.getAbsolutePath())));
        String csvContent = new String(Files.readAllBytes(Paths.get(expectedCsvFile.getAbsolutePath())));

        assertTrue("JSON content should contain data", jsonContent.contains("John Doe"));
        assertTrue("JSON content should contain ID", jsonContent.contains("101"));

        assertTrue("CSV content should contain header", csvContent.contains("ID,Name,Active"));
        assertTrue("CSV content should contain row data", csvContent.contains("101") && csvContent.contains("John Doe"));
    }

    @Test
    public void testFormulaInjectionNeutralizedInCSV() throws Exception {
        File formulaFile = new File(testDir, "formula_test.xls");
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("FormulaSheet");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Command");

            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue("=cmd|'/C calc'!A0");

            try (FileOutputStream fileOut = new FileOutputStream(formulaFile)) {
                workbook.write(fileOut);
            }
        }

        ExcelWrapper.process(formulaFile);
        File csvFile = new File(testDir, "FormulaSheet.csv");
        assertTrue("Formula CSV file should be created", csvFile.exists());

        String csvContent = new String(Files.readAllBytes(Paths.get(csvFile.getAbsolutePath())));
        assertTrue("Formula should be sanitized with leading quote", csvContent.contains("'=cmd|'/C calc'!A0"));
    }

    @Test
    public void testPathTraversalInSheetNameSanitized() throws Exception {
        File traversalFile = new File(testDir, "traversal_test.xls");
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(".._escape");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Col");

            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue("Val");

            try (FileOutputStream fileOut = new FileOutputStream(traversalFile)) {
                workbook.write(fileOut);
            }
        }

        ExcelWrapper.process(traversalFile);
        // Ensure no file was created outside testDir
        File parentOfTestDir = testDir.getParentFile();
        File escapedFile = new File(parentOfTestDir, "escape.json");
        assertTrue("File must not escape test directory", !escapedFile.exists());

        // File should exist safely inside testDir
        File sanitizedJson = new File(testDir, "_escape.json");
        assertTrue("Sanitized file should exist in working dir", sanitizedJson.exists());
    }
}
