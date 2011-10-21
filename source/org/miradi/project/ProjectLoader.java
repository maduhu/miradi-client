/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.project;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.martus.util.UnicodeReader;
import org.martus.util.UnicodeStringReader;
import org.martus.util.UnicodeWriter;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.threatrating.RatingValueSet;
import org.miradi.project.threatrating.ThreatRatingBundle;

public class ProjectLoader
{
	private ProjectLoader(final UnicodeStringReader readerToUse, Project projectToUse) throws Exception
	{
		reader = readerToUse;
		project = projectToUse;
		
		bundleNameToBundleMap = new HashMap<String, ThreatRatingBundle>();
	}
	
	public static void loadProject(File projectFile, Project projectToLoad) throws Exception
	{
		String contents = UnicodeReader.getFileContents(projectFile);
		loadProject(new UnicodeStringReader(contents), projectToLoad);
	}

	public static void loadProject(final UnicodeStringReader reader, Project project) throws Exception
	{
		final ProjectLoader projectLoader = new ProjectLoader(reader, project);
		projectLoader.load();
	}

	private void load() throws Exception
	{
		project.clear();
		
		boolean foundEnd = false;
		String firstLine = reader.readLine();
		if(firstLine.charAt(0) != UnicodeWriter.BOM_UTF8)
			throw new IOException("Invalid project file (missing BOM)");
		firstLine = firstLine.substring(1);
		processLine(firstLine);
		
		while(true)
		{
			String line = reader.readLine();
			if(line == null)
				break;
			
			if (line.equals("--"))
			{
				foundEnd = true;
				continue;
			}
			else if(foundEnd)
			{
				throw new IOException("Project file is corrupted (data after end marker)");
			}

			processLine(line);
		}
		
		if(!foundEnd)
			throw new IOException("Project file is corrupted (no end marker found)");
	}

	private void processLine(String line) throws Exception
	{
		if (line.startsWith(ProjectSaver.UPDATE_PROJECT_VERSION_CODE))
			loadProjectVersionLine(line);

		else if (line.startsWith(ProjectSaver.UPDATE_PROJECT_INFO_CODE))
			loadProjectInfoLine(line);
		
		else if (line.startsWith(ProjectSaver.UPDATE_LAST_MODIFIED_TIME_CODE))
			loadLastModified(line);
		
		else if (line.startsWith(ProjectSaver.CREATE_OBJECT_CODE))
			loadCreateObjectLine(line);
		
		else if (line.startsWith(ProjectSaver.UPDATE_OBJECT_CODE))
			loadUpdateObjectline(line);
		
		else if (line.startsWith(ProjectSaver.CREATE_SIMPLE_THREAT_RATING_BUNDLE_CODE))
			loadCreateSimpleThreatRatingLine(line);
		
		else if (line.startsWith(ProjectSaver.UPDATE_SIMPLE_THREAT_RATING_BUNDLE_CODE))
			loadUpdateSimpleThreatRatingLine(line);
		
		else if (line.startsWith(ProjectSaver.UPDATE_QUARANTINE_CODE))
			loadQuarantine(line);
		
		else if(line.startsWith(ProjectSaver.UPDATE_EXCEPTIONS_CODE))
			loadExceptions(line);
		
		else
			throw new IOException("Unexpected action: " + line);
	}
	
	private void loadExceptions(String line) throws Exception
	{
		String[] tagValue = parseTagValueLine(line);
		String tag = tagValue[0];
		String value = tagValue[1];
		if(!tag.equals(ProjectSaver.EXCEPTIONS_DATA_TAG))
			throw new Exception("Unknown Exceptions field: " + tag);

		value = getXmlDecoded(value);
		getProject().appendToExceptionLog(getXmlDecoded(value));
	}
	
	private void loadQuarantine(String line) throws Exception
	{
		String[] tagValue = parseTagValueLine(line);
		String tag = tagValue[0];
		String value = tagValue[1];
		if(!tag.equals(ProjectSaver.QUARANTINE_DATA_TAG))
			throw new Exception("Unknown Quarantine field: " + tag);

		value = getXmlDecoded(value);
		getProject().appendToQuarantineFile(getXmlDecoded(value));
	}

	private String[] parseTagValueLine(String line) throws Exception
	{
		StringTokenizer tokenizer = new StringTokenizer(line);
		/*String command =*/ tokenizer.nextToken();
		String tag = tokenizer.nextToken(EQUALS_DELIMITER_TAB_PREFIXED);
		String value = "";
		final boolean hasData = tokenizer.hasMoreTokens();
		if (hasData)
			value = tokenizer.nextToken(EQUALS_DELIMITER_NEWLINE_POSTFIXED);
		
		return new String[] {tag, value};
	}

	private void loadProjectVersionLine(String line)
	{
	}
	
	private void loadProjectInfoLine(final String line)
	{
		String[] splitLine = line.split(ProjectSaver.TAB);
		String[] tagValue = splitLine[1].split(ProjectSaver.EQUALS);
		String tag = tagValue[0];
		String value = tagValue[1];
		if (tag.equals(ProjectInfo.TAG_PROJECT_METADATA_ID))
			getProject().getProjectInfo().setMetadataId(new BaseId(value));
		if (tag.equals(ProjectInfo.TAG_HIGHEST_OBJECT_ID))
			getProject().getProjectInfo().getNormalIdAssigner().idTaken(new BaseId(value));
	}

	private void loadLastModified(String line)
	{
	}
	
	private void loadCreateSimpleThreatRatingLine(String line) throws Exception
	{
		StringTokenizer tokenizer = new StringTokenizer(line);
		/*String command =*/ tokenizer.nextToken();
		String threatIdTargetIdString = tokenizer.nextToken();
		String[] threatIdTargetIdParts = threatIdTargetIdString.split("-");
		FactorId threatId = new FactorId(Integer.parseInt(threatIdTargetIdParts[0]));
		FactorId targetId = new FactorId(Integer.parseInt(threatIdTargetIdParts[1]));
		ThreatRatingBundle bundle = new ThreatRatingBundle(threatId, targetId, BaseId.INVALID);
		bundleNameToBundleMap.put(threatIdTargetIdString, bundle);
		getProject().getSimpleThreatRatingFramework().saveBundle(bundle);
	}

	private void loadUpdateSimpleThreatRatingLine(String line) throws Exception
	{
		StringTokenizer tokenizer = new StringTokenizer(line);
		/*String command =*/ tokenizer.nextToken();
		String threatIdTargetIdString = tokenizer.nextToken();
		ThreatRatingBundle bundleToUpdate = bundleNameToBundleMap.get(threatIdTargetIdString);
		String tag = tokenizer.nextToken(EQUALS_DELIMITER_TAB_PREFIXED);
		String value = tokenizer.nextToken(EQUALS_DELIMITER_NEWLINE_POSTFIXED);
		if (tag.equals(ThreatRatingBundle.TAG_VALUES))
		{
			RatingValueSet ratings = new RatingValueSet();
			ratings.fillFrom(value);
			bundleToUpdate.setRating(ratings);
		}
		if (tag.equals(ThreatRatingBundle.TAG_DEFAULT_VALUE_ID))
		{
			bundleToUpdate.setDefaultValueId(new BaseId(Integer.parseInt(value)));
		}
	}

	private void loadCreateObjectLine(String line) throws Exception
	{
		StringTokenizer tokenizer = new StringTokenizer(line);
		/*String command =*/ tokenizer.nextToken();
		String refString = tokenizer.nextToken();
		ORef ref = extractRef(refString);
		getProject().createObject(ref);
	}

	private void loadUpdateObjectline(String line) throws Exception
	{
		StringTokenizer tokenizer = new StringTokenizer(line);
		/*String command =*/ tokenizer.nextToken();
		String refString = tokenizer.nextToken();
		ORef ref = extractRef(refString);
		String tag = tokenizer.nextToken(EQUALS_DELIMITER_TAB_PREFIXED);
		final boolean hasData = tokenizer.hasMoreTokens();
		if (hasData)
		{
			String value = tokenizer.nextToken(EQUALS_DELIMITER_NEWLINE_POSTFIXED);
			value = getXmlDecoded(value);
			getProject().setObjectData(ref, tag, value);
		}
	}

	private String getXmlDecoded(String value)
	{
		value = value.replaceAll("<br/>", "\n");
		value = value.replaceAll("&lt;", "<");
		value = value.replaceAll("&gt;", ">");
		value = value.replaceAll("&quot;", "\"");
		value = value.replaceAll("&#39;", "'");
		value = value.replaceAll("&amp;", "&");
		return value;
	}

	public ORef extractRef(String refString)
	{
		String[] refParts = refString.split(":");
		int objectType = Integer.parseInt(refParts[0]);
		BaseId objectId = new BaseId(Integer.parseInt(refParts[1]));
		
		return new ORef(objectType, objectId);
	}

	private Project getProject()
	{
		return project;
	}

	private HashMap<String, ThreatRatingBundle> bundleNameToBundleMap;
	private UnicodeStringReader reader;
	private Project project;
	
	private static final String EQUALS_DELIMITER_TAB_PREFIXED = " \t=";
	private static final String EQUALS_DELIMITER_NEWLINE_POSTFIXED = "=\n";
}
