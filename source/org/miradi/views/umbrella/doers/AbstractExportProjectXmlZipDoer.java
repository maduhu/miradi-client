/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

package org.miradi.views.umbrella.doers;

import org.martus.util.UnicodeReader;
import org.miradi.exceptions.InvalidICUNSelectionException;
import org.miradi.exceptions.ValidationException;
import org.miradi.main.EAM;
import org.miradi.main.ResourcesHandler;
import org.miradi.objecthelpers.ORef;
import org.miradi.utils.BufferedImageFactory;
import org.miradi.utils.PNGFileFilter;
import org.miradi.utils.ProgressInterface;
import org.miradi.views.umbrella.XmlExporterDoer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipOutputStream;

abstract public class AbstractExportProjectXmlZipDoer extends XmlExporterDoer
{
	@Override
	protected String createImageFileName(int index, ORef diagramRef)
	{
		return BufferedImageFactory.getDiagramPrefix(diagramRef) + "-" + diagramRef.getObjectId() + PNGFileFilter.EXTENSION;
	}

	@Override
	protected boolean export(File chosen, ProgressInterface progressInterface) throws Exception
	{
		progressInterface.setStatusMessage(EAM.text("save..."), 4);
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(chosen));
		try
		{
			addProjectAsXmlToZip(zipOut);
			progressInterface.incrementProgress();
			
			addSchemaToZip(zipOut);
			progressInterface.incrementProgress();
			
			addDiagramImagesToZip(zipOut);
			progressInterface.incrementProgress();
			
			addExceptionLogFile(zipOut);
			progressInterface.incrementProgress();
			
			return true;
		}
		catch(ValidationException e)
		{
			EAM.logException(e);
			EAM.errorDialog(e.getMessage());
		}
		catch (InvalidICUNSelectionException e)
		{
			String errorMessage = EAM.substituteSingleString(EAM.text("Please choose a specific IUCN classification (Not a category). Fix needed for Strategy named:\n'%s'"), e.getStrategy().toString());
			EAM.logException(e);
			EAM.errorDialog(errorMessage);
		}
		catch(Exception e)
		{
			// NOTE: If we created no zip entries, the finally will throw
			// a generic exception instead of this one, so log this one
			// while we have a chance
			EAM.logException(e);
			throw(e);
		}
		finally
		{
			zipOut.close();
		}
		
		return false;
	}
	
	private void addExceptionLogFile(ZipOutputStream zipOut) throws Exception
	{
		String contents = getProject().getExceptionLog();
		if(contents.length() > 0)
		{
			byte[] byteContents = contents.getBytes("UTF-8");
			createZipEntry(zipOut, EAM.EXCEPTIONS_LOG_FILE_NAME, byteContents);
		}
	}

	private void addSchemaToZip(ZipOutputStream zipOut) throws Exception
	{
		URL resourceURL = ResourcesHandler.getEnglishResourceURL(getSchemaRelativeFilePath());
		if(resourceURL == null)
			throw new Exception("Schema not found: " + getSchemaRelativeFilePath());

		createZipEntry(zipOut, SCHEMA_FILE_NAME, readAll(resourceURL));
	}

	private byte[] readAll(URL resourceURL) throws IOException
	{
		UnicodeReader reader = new UnicodeReader(resourceURL.openStream());
		try
		{
			String wholeFile = reader.readAll();
			return wholeFile.getBytes("UTF-8");
		}
		finally
		{
			reader.close();
		}
	}

	abstract protected String getSchemaRelativeFilePath();
	public static final String SCHEMA_FILE_NAME = "schema.rnc";
}
