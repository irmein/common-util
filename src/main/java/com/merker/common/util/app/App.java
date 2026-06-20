package com.merker.common.util.app;

import java.io.File;

/**
 * Hello world!
 *
 */
public class App 
{
    private File file;

    public static void main( String[] args ) throws Exception {
        if(1 == args.length) {
            if ("-v".equals(args[0]) || "--version".equals(args[0])) {
                String version = App.class.getPackage().getImplementationVersion();
                System.out.println("data-morph version " + (version != null ? version : "1.0-SNAPSHOT"));
                return;
            }
            File file = new File(args[0]);
            ExcelWrapper.process(file);
        } else {
            displayError(true);
        }
    }


    private static void displayError(boolean displayHelp) {
        if(displayHelp) {
            System.out.println("Pass the file to be converted in -f option.");
            System.out.println("To check the version, use -v or --version.");
        }
    }
}
