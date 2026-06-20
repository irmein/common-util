# data-morph

A Java utility for processing Excel files and converting their data into JSON and formatted text files. 

## Features

- Reads both `.xls` and `.xlsx` modern Excel files.
- Extracts data from all sheets within the workbook.
- Outputs two files for each sheet (in the same directory as the source Excel file):
  - `<SheetName>.json`: Contains the sheet data formatted as a standard JSON array of objects.
  - `<SheetName>.csv`: Contains the sheet data in a standard Comma-Separated Values (CSV) format.

## Prerequisites

- Java 25 (Temurin distribution recommended)
- Maven 3.x

## Building the Project

To compile and package the project into a runnable JAR, run:

```bash
mvn clean install
```

The build includes automated security auditing via the OWASP `dependency-check-maven` plugin. The build will automatically fail if dependencies with high or critical vulnerabilities (CVSS >= 7) are detected.

This will produce an executable `data-morph-1.0-SNAPSHOT.jar` inside the `target/` directory.

## Usage

To use the utility, run the JAR and pass the absolute path to your Excel file as the first argument:

```bash
java -jar target/data-morph-1.0-SNAPSHOT.jar /path/to/your/excel_file.xlsx
```

If successful, the utility will output statements indicating that the `.json` and `.csv` files have been successfully created next to the input file.
