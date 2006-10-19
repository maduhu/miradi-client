/*
 * Copyright 2006, The Conservation Measures Partnership & Beneficent
 * Technology, Inc. (Benetech, at www.benetech.org)
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import org.conservationmeasures.eam.testall.EAMTestCase;

public class TestDelimitedFileLoader extends EAMTestCase
{

	public TestDelimitedFileLoader(String name)
	{
		super(name);
	}

	public void testNoData() throws IOException
	{
		BufferedReader reader = new BufferedReader( new StringReader("") );
		Vector myData = DelimitedFileLoader.getDelimitedContents(reader);
		assertEquals(true, myData.size()==0);
	}
	
	public void testOneElement() throws IOException
	{
		BufferedReader reader = new BufferedReader( new StringReader("xxxx") );
		Vector myData = DelimitedFileLoader.getDelimitedContents(reader);
		assertEquals(true, ((Vector)myData.elementAt(0)).size()==1);
	}
	
	public void testTwoElement() throws IOException
	{
		BufferedReader reader = new BufferedReader( new StringReader("xxxx \t yyyy") );
		Vector myData = DelimitedFileLoader.getDelimitedContents(reader);
		assertEquals(2, ((Vector)myData.elementAt(0)).size());
	}
	
	public void testTwoElementOneQouted() throws IOException
	{
		BufferedReader reader = new BufferedReader( new StringReader("xxxx \t     \"xxxx\"") );
		Vector myData = DelimitedFileLoader.getDelimitedContents(reader);
		assertEquals(2, ((Vector)myData.elementAt(0)).size());
	}
	
	public void testTwoElementOneQoutedEmpty() throws IOException
	{
		BufferedReader reader = new BufferedReader( new StringReader("xxxx \t     \"\"") );
		Vector myData = DelimitedFileLoader.getDelimitedContents(reader);
		assertEquals(2, ((Vector)myData.elementAt(0)).size());
	}
	
	public void testElementContentWithTrim() throws IOException
	{
		BufferedReader reader = new BufferedReader( new StringReader("xxxx \t     \"abcd 123\"   ") );
		Vector myData = DelimitedFileLoader.getDelimitedContents(reader);
		String data = (String)((Vector)myData.elementAt(0)).get(1);
		assertEquals("abcd 123", data);
	}
	

	public void testElementTwoRows() throws IOException
	{
		BufferedReader reader = new BufferedReader( new StringReader("124 \t     \"abcd 234\"    \n 345 \t     \"efgc 678\"  ") );
		Vector myData = DelimitedFileLoader.getDelimitedContents(reader);
		assertEquals(2, myData.size());
	}
	
	public void testElementTwoContent() throws IOException
	{
		BufferedReader reader = new BufferedReader( new StringReader("124 \t     \"abcd 234\"    \n 345 \t     \"efgc 678\"  ") );
		Vector myData = DelimitedFileLoader.getDelimitedContents(reader);
		String data = (String)((Vector)myData.elementAt(0)).get(1);
		assertEquals("abcd 234", data);
		String data2 = (String)((Vector)myData.elementAt(1)).get(1);
		assertEquals("efgc 678", data2);
	}
	
	
	
	public void testThreatFileInMain() throws Exception
	{
		Vector myData = DelimitedFileLoader.getDelimitedContents("ThreatTaxonomies.txt");
		assertEquals(41, myData.size());
	}
	
	
}
