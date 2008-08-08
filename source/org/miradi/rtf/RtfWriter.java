/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.rtf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;

import org.martus.util.UnicodeWriter;
import org.miradi.utils.AbstractTableExporter;
import org.miradi.utils.BufferedImageFactory;
import org.miradi.views.umbrella.SaveImageJPEGDoer;

public class RtfWriter
{
	public RtfWriter(File file) throws IOException
	{
		writer = new UnicodeWriter(file);
	}
	
	public void close() throws Exception
	{
		getWriter().close();
	}
	
	public void writeEncoded(String data) throws Exception
	{
		writeln(encode(data));
	}
	
	public void writeRtfCommand(String rtfComand) throws Exception
	{
		writeln(rtfComand);
	}
	
	public void writeln(String textToWrite) throws Exception
	{
		getWriter().writeln(textToWrite);
	}
	
	public void writeImage(BufferedImage bufferedImage) throws Exception
	{
		startBlock();
		
		String jpegHeader = "\\pict\\piccropl0\\piccropr0\\piccropt0\\piccropb0\\picw"+bufferedImage.getWidth()+"\\pich"+bufferedImage.getHeight()+"\\jpegblip ";
		getWriter().writeln(jpegHeader);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SaveImageJPEGDoer.saveJpeg(baos, bufferedImage);	
		byte[] imageAsBytes = baos.toByteArray();
		baos.close();
		final int BYTES_PER_LINE = 32;
		for (int i = 0; i < imageAsBytes.length; ++i)
		{
			byte b = imageAsBytes[i];
			getWriter().write(toHex(b));
			
			if(i % BYTES_PER_LINE == 0)
				  getWriter().writeln();
		}
		
		endBlock();
	}

	private void startBlock() throws Exception
	{
		writeRtfCommand("{");
	}
	
	private void endBlock() throws Exception
	{
		writeRtfCommand("}");
	}

	static public String toHex(byte b)
	{
		int i = b & 0xFF;
		int firstNibble = i/16;
		String firstDigit = Integer.toHexString(firstNibble);
		
		int secondNibble = i%16;
		String secondDigit = Integer.toHexString(secondNibble);
		
		return firstDigit + secondDigit;
	}
	
	public void writeRtfTable(AbstractTableExporter exportableTable) throws Exception
	{
		writeTableHeader(exportableTable);
		writeTableBody(exportableTable);
	}

	private void writeTableBody(AbstractTableExporter exportableTable) throws Exception
	{
		for (int row = 0; row < exportableTable.getRowCount(); ++row)
		{
			writeTableRow(exportableTable, row);
		}
	}

	private void writeTableRow(AbstractTableExporter exportableTable, int row) throws Exception
	{
		startBlock();
		
		writeRowData(exportableTable, row);
		writeRowCommands(exportableTable);
			
		endBlock();
	}

	private void writeRowData(AbstractTableExporter exportableTable, int row) throws Exception
	{
		startBlock();
		
		writeRtfCommand("\\trowd \\trautofit1 \\intbl ");
		for (int column = 0; column < exportableTable.getColumnCount(); ++column)
		{
			int paddingCount = exportableTable.getDepth(row);
			writeEncoded(createPadding(paddingCount, column));

			Icon cellIcon = exportableTable.getIconAt(row, column);
			if (cellIcon != null)
				writeImage(BufferedImageFactory.getImage(cellIcon));

			writeEncoded(exportableTable.getTextAt(row, column));
			writeRtfCommand(" \\cell ");
		}

		endBlock();
	}
	
	private void writeRowCommands(AbstractTableExporter exportableTable) throws Exception
	{
		startBlock();
		for (int column = 0; column < exportableTable.getColumnCount(); ++column)
		{
			writeRtfCommand("\\cellx" + column + " ");	
		}

		writeRtfCommand(" \\row ");
		endBlock();
	}

	private void writeTableHeader(AbstractTableExporter exportableTable) throws Exception
	{
		startBlock();

		writeRtfCommand("\\trowd \\trhdr \\trautofit1 \\intbl ");
		for (int columnIndex = 0; columnIndex < exportableTable.getColumnCount(); ++columnIndex)
		{
			String header = exportableTable.getHeaderFor(columnIndex);
			writeEncoded(header);
			writeRtfCommand(" \\cell ");
		}
		
		writeRowCommands(exportableTable);

		endBlock();
	}
	
	public static String encode(String stringToEncode)
	{
		String encodedString = stringToEncode.replaceAll("\\\\", "\\\\\\\\");
		encodedString = encodedString.replaceAll("\\}", "\\\\}");
		encodedString = encodedString.replaceAll("\\{", "\\\\{");
		
		StringBuffer buffer = new StringBuffer(encodedString);
		for(int i = 0; i < buffer.length(); ++i)
		{
			char c = buffer.charAt(i);
			if (c >= 128)
			{
				String hexValue = toHex(c);
				buffer.replace(i, i+1, "\\u" + hexValue.toUpperCase());
			}
		}
		
		return buffer.toString();
	}
	
	static public String toHex(char c)
	{
		int i = c & 0xFFFF;
		
		int firstNibble = (i & 0xF000) / 0x1000;
		String firstDigit = Integer.toHexString(firstNibble);
		
		int secondNibble = (i & 0x0F00) / 0x0100;
		String secondDigit = Integer.toHexString(secondNibble);
		
		int thirdNibble = (i & 0x00F0) / 0x0010;
		String thirdDigit = Integer.toHexString(thirdNibble);
		
		int forthNibble = (i & 0x000F) / 0x001;
		String forthDigit = Integer.toHexString(forthNibble);
		
		return firstDigit + secondDigit + thirdDigit + forthDigit;
	}
	
	public String createPadding(int padCount, int column) throws IOException
	{
		final String FIVE_SPACES = "     ";
		String padding = "";
		for (int i = 0; i < padCount; ++i)
		{
			padding += FIVE_SPACES;
		}
		
		return padding;
	}
	
	public void startRtf() throws Exception
	{
		getWriter().write("{\\rtf ");
	}
	
	public void endRtf() throws Exception
	{
		endBlock();
	}	

	public void newParagraph() throws Exception
	{
		writeSingleCommand("\\par ");	
	}
	
	public void landscapeMode() throws Exception
	{
		writeRtfCommand("\\landscape\\paperh12240\\paperw15840\\margl1134\\margr1134\\margt1134\\margb1134\\sectd\\sbknone\\lndscpsxn ");
	}
	
	public void writeSingleCommand(String command) throws Exception
	{
		startBlock();
		writeRtfCommand(command);
		endBlock();
	}
	
	public UnicodeWriter getWriter()
	{
		return writer;
	}
	
	private UnicodeWriter writer;
}
