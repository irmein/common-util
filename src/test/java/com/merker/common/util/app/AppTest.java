package com.merker.common.util.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class AppTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testMainWithNoArgs() throws Exception {
        App.main(new String[]{});
        assertEquals("Pass the file to be converted in -f option.\n", outContent.toString());
    }

    @Test
    public void testMainWithTooManyArgs() throws Exception {
        App.main(new String[]{"arg1", "arg2"});
        assertEquals("Pass the file to be converted in -f option.\n", outContent.toString());
    }
}
