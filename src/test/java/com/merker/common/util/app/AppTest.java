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
        String expected = "Pass the file to be converted in -f option." + System.lineSeparator() +
                          "To check the version, use -v or --version." + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void testMainWithTooManyArgs() throws Exception {
        App.main(new String[]{"arg1", "arg2"});
        String expected = "Pass the file to be converted in -f option." + System.lineSeparator() +
                          "To check the version, use -v or --version." + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void testMainWithVersionFlag() throws Exception {
        App.main(new String[]{"--version"});
        assertEquals("data-morph version 1.0-SNAPSHOT" + System.lineSeparator(), outContent.toString());
    }

    @Test
    public void testMainWithShortVersionFlag() throws Exception {
        App.main(new String[]{"-v"});
        assertEquals("data-morph version 1.0-SNAPSHOT" + System.lineSeparator(), outContent.toString());
    }
}
