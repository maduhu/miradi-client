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

package org.miradi.project;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import org.martus.util.UnicodeStringWriter;
import org.martus.util.UnicodeWriter;
import org.miradi.objectdata.ObjectData;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objects.BaseObject;
import org.miradi.project.threatrating.ThreatRatingBundle;

public class ProjectSaver extends AbstractMiradiProjectSaver
{
	public static String saveProject(Project projectToSave, File fileToSaveTo) throws Exception
	{
		UnicodeStringWriter stringWriter = UnicodeStringWriter.create();
		saveProject(projectToSave, stringWriter);
		UnicodeWriter fileWriter = new UnicodeWriter(fileToSaveTo);
		String contents = stringWriter.toString();
		fileWriter.write(contents);
		fileWriter.close();
		
		return stringWriter.toString();
	}
	
	public static String createSnapShot(Project projectToUse) throws Exception
	{
		UnicodeStringWriter stringWriter = UnicodeStringWriter.create();
		saveProject(projectToUse, stringWriter);
		
		return stringWriter.toString();
	}

	public static void saveProject(final Project projectToUse, final UnicodeStringWriter writerToUse) throws Exception
	{
		final ProjectSaver projectSaver = new ProjectSaver(projectToUse, writerToUse);
		projectSaver.saveProject();
	}
	
	protected ProjectSaver(final Project projectToUse, final UnicodeStringWriter writerToUse) throws Exception
	{
		super(writerToUse);
		
		project = projectToUse;
	}
	
	@Override
	protected long getLastModifiedTime()
	{
		return getProject().getLastModifiedTime();
	}

	@Override
	protected String getProjectMetadataId()
	{
		return getProject().getProjectInfo().getMetadataId().toString();
	}

	@Override
	protected int getHighestAssignedId()
	{
		return getProject().getProjectInfo().getNormalIdAssigner().getHighestAssignedId();
	}

	@Override
	protected String getQuarantineData() throws Exception
	{
		return getProject().getQuarantineFileContents();
	}

	@Override
	protected String getExceptionLog() throws Exception
	{
		return getProject().getExceptionLog();
	}

	@Override
	protected ORefList getSortedRefsForType(int type) throws Exception
	{
		EAMObjectPool pool = getProject().getPool(type);
		if (pool == null)
			return new ORefList();

		return pool.getSortedRefList();
	}

	@Override
	protected void writeObjectUpdateEntries(ORef ref) throws Exception
	{
		//FIXME medium - writeObjectUpdateEntries should be made generic and pulled up, to avoid 
		//writing in leaf classes and duplication between leaf classes. 
		BaseObject baseObject = getProject().findObject(ref);
		Vector<String> fieldTags = baseObject.getStoredFieldTags();
		Collections.sort(fieldTags);
		for(int field = 0; field < fieldTags.size(); ++field)
		{
			String tag = fieldTags.get(field);
			String data = baseObject.getData(tag);
			if (data.length() > 0)
			{
				ObjectData dataField = baseObject.getField(tag);
				if(needsEncoding(dataField))
				{
					ensureNoNonHtmlNewlinesExists(data);
				}
				MiradiProjectFileUtilities.writeUpdateObjectLine(getWriter(), ref, tag, data);
			}
		}
	}

	private boolean needsEncoding(ObjectData dataField)
	{
		return dataField.isUserText();
	}

	@Override
	protected Collection<ThreatRatingBundle> getSimpleThreatRatingBundles()
	{
		return getProject().getSimpleThreatRatingFramework().getAllBundles();
	}

	private Project getProject()
	{
		return project;
	}

	@Override
	protected int getHighVersion()
	{
		return Project.VERSION_HIGH;
	}

	@Override
	protected int getLowVersion()
	{
		return Project.VERSION_LOW;
	}
	
	private Project project;
}
