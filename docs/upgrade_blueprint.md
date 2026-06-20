# ExcelWrapper Upgrade Blueprint & Code Review

## 1. Current State Analysis & Code Review

A review of `ExcelWrapper.java` and `App.java` reveals several areas for improvement:

### **A. Functional Limitations**
- **Format Support:** The code explicitly uses `HSSFWorkbook`, which restricts it to older Excel formats (`.xls`). Modern `.xlsx` files are not supported.
- **Data Formatting:** The extraction of cell values relies on manual checking of `CellType`. While it uses `BigDecimal` to prevent scientific notation, it misses Excel's built-in formatting rules (e.g., date formats, currency formats).
- **JSON Structure:** The generated JSON maps rows as distinct properties (`"Row 1"`, `"Row 2"`) instead of using a standard JSON Array (`[]`). This makes parsing the resulting JSON significantly harder for downstream applications.
- **Text File Generation:** The text file generation simply appends columns separated by four spaces, which might misalign if data varies in length. A standard CSV format or tab-separated values (TSV) would be more robust.

### **B. Code Quality & Maintenance**
- **File I/O:** Uses legacy `java.io.File`, `FileInputStream`, and manual path concatenation (`System.getProperty("file.separator")`). 
- **Exception Handling:** Uses broad `catch(Exception ex)` and simply prints to `System.err`, swallowing exceptions and hiding potential stack traces.
- **Resource Leaks:** While `Workbook.close()` is called, it is not inside a `finally` block or a try-with-resources statement, meaning an error during processing could leave the file stream or workbook open.
- **Library Usage:** The JSON generation manually creates `JsonObject`s instead of leveraging standard serialization (e.g., `new Gson().toJson(list)`).

---

## 2. Upgrade Options & New Strategies

Based on the review, here are the probable upgrade options for `ExcelWrapper`.

### **Strategy 1: Modernize the Existing Implementation (Incremental Upgrade)**

This strategy keeps the core logic but modernizes the APIs to fix the current issues.

*   **Support Both `.xls` and `.xlsx`:** Replace `new HSSFWorkbook(fInputStream)` with `WorkbookFactory.create(file)`. This automatically detects the file format and returns the appropriate `Workbook` instance (`HSSFWorkbook` or `XSSFWorkbook`).
*   **Use `DataFormatter`:** Replace the manual cell type `if-else` block with Apache POI's `DataFormatter.formatCellValue(cell)`. This will format the cell exactly as it appears in Excel, including dates and numbers, removing the need for manual `BigDecimal` hacks.
*   **Standardize JSON Output:** Instead of `{"Row 1": {...}}`, generate a standard JSON array `[ {...}, {...} ]`. This can be done simply by returning a `List<Map<String, String>>` and letting Gson serialize it: `new Gson().toJson(sheetDataList)`.
*   **Modern File I/O:** Use `java.nio.file.Files` and `java.nio.file.Path` for path resolution and writing files, which is cleaner and safer.
*   **Try-With-Resources:** Wrap `Workbook`, `FileInputStream`, and `FileWriter` in try-with-resources blocks to ensure they are automatically closed.

### **Strategy 2: Adopt a High-Performance/Streaming Library (Advanced Upgrade)**

For larger Excel files, Apache POI's DOM approach (loading the whole workbook into memory) can cause `OutOfMemoryError`s. 

*   **Alibaba EasyExcel:** Replace raw Apache POI with [EasyExcel](https://github.com/alibaba/easyexcel). It uses a streaming approach (SAX) which is extremely memory efficient.
    *   *Implementation:* You can read the Excel file line by line, converting each row to a `Map` or a specific POJO, and directly stream that into a JSON or CSV file.
*   **Poiji / Fastexcel:** Other lightweight alternatives that map Excel sheets directly to Java Lists/Streams, significantly reducing boilerplate code.

### **Strategy 3: Adopt Standard Output Formats (CSV)**

Instead of a custom spacing format (`    `), change the text generation to a standard CSV (Comma Separated Values) or TSV (Tab Separated Values) format.
*   Use a library like `OpenCSV` or `Apache Commons CSV` to handle escaping commas and quotes correctly.

### **Strategy 4: Implement Continuous Security Auditing (CVE Checking)**

To prevent introducing vulnerable libraries in the future, integrate automated security scanning into the build process.
*   **OWASP Dependency-Check:** Add the `dependency-check-maven` plugin to `pom.xml`. This will cross-reference all dependencies against the National Vulnerability Database (NVD) and fail the build if vulnerabilities with a specific severity (e.g., High or Critical) are found.

---

## 3. Recommended Blueprint (Action Plan)

If we proceed with the upgrade, the recommended **Strategy 1** (Modernization) is the most straightforward while offering massive improvements.

**Phase 1: Refactor File I/O and Resource Management**
1. Update `ExcelWrapper.process(File)` to use `try-with-resources` for the `FileInputStream` and `Workbook`.
2. Replace manual path building with `Paths.get(file.getParent(), fileName)`.

**Phase 2: Enhance Excel Parsing**
1. Switch to `WorkbookFactory.create(inputStream)` to support all Excel formats.
2. Introduce `DataFormatter formatter = new DataFormatter();` to extract cell strings perfectly without complex logic.

**Phase 3: Standardize Outputs**
1. Change `getJSONStringFromList` to serialize a `List<Map<String, String>>` using `new Gson().setPrettyPrinting().create().toJson(...)`.
2. Change the `.txt` output to a proper `.csv` output with comma separation.

**Phase 4: Error Handling**
1. Replace `System.err.println` with a proper logging framework (e.g., SLF4J/Logback) or at least `ex.printStackTrace()` for better debuggability.

**Phase 5: Security Automation**
1. Integrate the `org.owasp:dependency-check-maven` plugin into the `pom.xml` build lifecycle.
2. Configure the plugin to fail the build automatically if dependencies with a high or critical CVSS score are detected.

---

## 4. Implementation Status

**Status: COMPLETED** (as of current date)

The recommended **Strategy 1** has been fully implemented, resolving the issues identified during the analysis:
* **Phase 1 (Resource Management):** `try-with-resources` blocks and `java.nio.file.Paths` are now used in `ExcelWrapper.java` for all file and stream operations.
* **Phase 2 (Excel Parsing):** Replaced `HSSFWorkbook` with `WorkbookFactory.create()` to parse `.xls` and `.xlsx` correctly, and utilized `DataFormatter` to capture cell values exactly as they appear.
* **Phase 3 (Standardize Outputs):** JSON parsing was migrated to generate a proper JSON array of objects using `Gson`, and the text generator now produces standard Comma-Separated Values (`.csv`).
* **Phase 4 (Error Handling):** Errors are now properly logged via stack traces for enhanced debuggability. 
* **Phase 5 (Security Automation):** The OWASP `dependency-check-maven` plugin is successfully hooked into the build lifecycle.
* **Verification:** Unit and end-to-end (E2E) test cases have been refactored to align with the new `.csv` and structured `.json` output standards, and they are executing without failure.
