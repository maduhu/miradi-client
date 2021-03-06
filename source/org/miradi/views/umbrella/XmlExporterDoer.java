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
package org.miradi.views.umbrella;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.martus.util.inputstreamwithseek.ByteArrayInputStreamWithSeek;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.miradi.exceptions.ValidationException;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramObject;
import org.miradi.utils.BufferedImageFactory;
import org.miradi.utils.MiradiFileSaveChooser;
import org.miradi.utils.ProgressInterface;
import org.miradi.utils.UnicodeXmlWriter;
import org.miradi.xml.XmlExporter;

abstract public class XmlExporterDoer extends AbstractFileSaverDoer
{
	@Override
	protected boolean doWork(File destinationFile, ProgressInterface progressInterface) throws Exception
	{
		return export(destinationFile, progressInterface);
	}

	@Override
	protected String getIOExceptionErrorMessage()
	{
		return EAM.text("Unable to write XML. Perhaps the disk was full, or you " +
				"don't have permission to write to it, or you are using invalid characters in the file name.");
	}
	
	protected void addProjectAsXmlToZip(ZipOutputStream zipOut) throws Exception
	{
		byte[] projectXmlInBytes = exportProjectXmlToBytes();
		ByteArrayInputStreamWithSeek inputStream = new ByteArrayInputStreamWithSeek(projectXmlInBytes);
		try
		{
			if (!isValidXml(inputStream))
			{
				EAM.logDebug(new String(projectXmlInBytes, "UTF-8"));
				throw new ValidationException(EAM.text("Exported file does not validate."));
			}
	
			createZipEntry(zipOut, PROJECT_XML_FILE_NAME, projectXmlInBytes);
		}
		finally
		{
			inputStream.close();
		}
	}

	private byte[] exportProjectXmlToBytes() throws IOException, Exception,	UnsupportedEncodingException
	{
		UnicodeXmlWriter writer = UnicodeXmlWriter.create();
		try
		{
			createExporter().exportProject(writer);
		}
		finally
		{
			writer.close();
		}
		
		return writer.toString().getBytes("UTF-8");
	}
	
	protected void addDiagramImagesToZip(ZipOutputStream zipOut) throws Exception
	{		
		ORefList allDiagramObjectRefs = getProject().getAllDiagramObjectRefs();
		for (int refIndex = 0; refIndex < allDiagramObjectRefs.size(); ++refIndex)
		{
			DiagramObject diagramObject = (DiagramObject) getProject().findObject(allDiagramObjectRefs.get(refIndex));
			ORef diagramRef = diagramObject.getRef();
			String imageName = createImageFileName(refIndex, diagramRef);
			writeDiagramImage(zipOut, diagramObject, imageName);
		}
	}

	protected String createImageFileName(int index, ORef diagramRef)
	{
		return BufferedImageFactory.createImageFileName(index, diagramRef);
	}

	protected String getDiagramPrefix(ORef diagramObjectRef)
	{
		if (ConceptualModelDiagram.is(diagramObjectRef))
			return CM_IMAGE_PREFIX;
		
		return RC_IMAGE_PREFIX;
	}

	private void writeDiagramImage(ZipOutputStream zipOut, DiagramObject diagramObject, String imageName) throws Exception
	{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try
		{
			final BufferedImage diagramImage = BufferedImageFactory.createImageFromDiagram(getMainWindow(), diagramObject);
			new SaveImagePngDoer().saveImage(byteOut, diagramImage);
			createZipEntry(zipOut, IMAGES_DIR_NAME_IN_ZIP + imageName, byteOut.toByteArray());
		}
		finally
		{
			byteOut.close();
		}
	}
	
	protected void createZipEntry(ZipOutputStream out, String entryName, String contents) throws Exception
	{
		createZipEntry(out, entryName, contents.getBytes("UTF-8"));

	}
	
	protected void createZipEntry(ZipOutputStream zipOut, String entryName, byte[] bytes) throws FileNotFoundException, IOException
	{
		ZipEntry entry = new ZipEntry(entryName);
		entry.setSize(bytes.length);
		zipOut.putNextEntry(entry);	
		zipOut.write(bytes);
		zipOut.closeEntry();
	}
	
	@Override
	protected String getProgressTitle()
	{
		return EAM.text("Export...");
	}
	
	abstract protected XmlExporter createExporter() throws Exception;
	
	abstract protected boolean isValidXml(InputStreamWithSeek inputStream) throws Exception;

	@Override
	abstract protected MiradiFileSaveChooser createFileChooser();
	
	abstract protected boolean export(File chosen, ProgressInterface progressInterface) throws Exception;
	
	public static final String CM_IMAGE_PREFIX = "CM";
	public static final String RC_IMAGE_PREFIX = "RC";
	public static final String IMAGES_DIR_NAME_IN_ZIP = "images/";
	public static final String PROJECT_XML_FILE_NAME = "project.xml";
}
