package com.merker.common.util.app;

import java.io.File;

/**
 * Hello world!
 *
 */
public class App 
{
    private File file;

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }


    public  void  process() {
       if(null == file) {
          System.out.println("File processing aborted.");
       }
    }
}
