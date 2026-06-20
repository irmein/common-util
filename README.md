# common-util

A Java utility for processing Excel files and converting their data into JSON and formatted text files. 

## Features

- Reads `.xls` (HSSFWorkbook) Excel files.
- Extracts data from all sheets within the workbook.
- Outputs two files for each sheet (in the same directory as the source Excel file):
  - `<SheetName>.json`: Contains the sheet data formatted as a JSON object, where the first row acts as keys.
  - `<SheetName>.txt`: Contains the sheet data in a plain text, tab-separated format.

## Prerequisites

- Java 8 or higher
- Maven 3.x

## Building the Project

To compile and package the project into a runnable JAR, run:

```bash
mvn clean install
```

This will produce an executable `common-util-1.0-SNAPSHOT.jar` inside the `target/` directory.

## Usage

To use the utility, run the JAR and pass the absolute path to your Excel file as the first argument:

```bash
java -jar target/common-util-1.0-SNAPSHOT.jar /path/to/your/excel_file.xls
```

If successful, the utility will output statements indicating that the `.json` and `.txt` files have been successfully created next to the input file.
