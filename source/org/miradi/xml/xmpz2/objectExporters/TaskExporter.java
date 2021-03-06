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

package org.miradi.xml.xmpz2.objectExporters;

import org.miradi.objects.BaseObject;
import org.miradi.objects.Task;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;

public class TaskExporter extends BaseObjectWithLeaderResourceFieldExporter
{
	public TaskExporter(Xmpz2XmlWriter writerToUse)
	{
		super(writerToUse, TaskSchema.getObjectType());
	}
	
	@Override
	protected void writeFields(final BaseObject baseObject,	BaseObjectSchema baseObjectSchema) throws Exception
	{
		super.writeFields(baseObject, baseObjectSchema);

		writeSubTaskIds((Task)baseObject);
		writeOptionalCalculatedTimePeriodCosts(baseObject, baseObjectSchema);
	}

	@Override
	protected boolean doesFieldRequireSpecialHandling(final String tag)
	{
		if (tag.equals(Task.TAG_SUBTASK_IDS))
			return true;

		return super.doesFieldRequireSpecialHandling(tag);
	}
	
	private void writeSubTaskIds(Task baseObject) throws Exception
	{
		getWriter().writeReflist(TASK + SUB_TASK_IDS, SUB_TASK, baseObject.getChildTaskRefs());
	}
}
