# Project Objective: Data Morph

## Intent
The primary intent of the `data-morph` project is to provide a collection of robust, standardized utility applications and wrappers designed to simplify common data processing tasks. The current focal point of the project is data transformation, specifically facilitating the extraction and conversion of data stored within Excel spreadsheets into easily interoperable formats such as JSON and structured text.

## Key Objectives

1. **Data Interoperability & Transformation:** 
   - Provide seamless tools to convert proprietary or common business file formats (e.g., `.xls`, `.xlsx`) into open standard formats (like JSON and CSV).
   - Enable downstream consumption of spreadsheet data by APIs, data pipelines, and reporting tools.

2. **Reusability & Modularity:** 
   - Develop modular, independent utility classes (like `ExcelWrapper`) that can be executed as standalone applications or easily integrated into larger Java projects.
   - Maintain a clean separation of concerns between data parsing and data serialization.

3. **Performance, Stability, & Security:** 
   - Ensure utility processes execute efficiently and manage memory appropriately, especially when dealing with large datasets.
   - Handle edge cases gracefully, such as varied cell formatting, mixed data types, and blank fields.
   - Actively maintain dependencies to prevent security vulnerabilities within third-party libraries (e.g., Apache POI, Gson).

4. **Modernization & Best Practices:** 
   - Continually evolve the toolset to adopt modern Java features and industry-standard libraries.
   - Utilize standard serialization tools (e.g., Gson) to ensure output structures conform to expected schemas and are easily consumable by modern web and backend technologies.

## Future Vision
While currently centered around Excel manipulation, the `common-util` project is intended to act as a foundational repository that can expand to include other common data wrangling domains, such as:
- File format conversions (e.g., CSV to XML, JSON to Parquet).
- Standardized configuration parsing and validation utilities.
- General-purpose file I/O handling and data cleansing workflows.
