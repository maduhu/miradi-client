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

package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.XslTemplateSchema;

public class XslTemplate extends BaseObject
{
	public XslTemplate(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, createSchema(objectManager));
	}

	public static XslTemplateSchema createSchema(Project projectToUse)
	{
		return createSchema(projectToUse.getObjectManager());
	}

	public static XslTemplateSchema createSchema(ObjectManager objectManager)
	{
		return (XslTemplateSchema) objectManager.getSchemas().get(ObjectType.XSL_TEMPLATE);
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	public static boolean is(BaseObject object)
	{
		return is(object.getRef());
	}

	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == XslTemplateSchema.getObjectType();
	}
	
	public static XslTemplate find(ObjectManager objectManager, ORef xslTemplateRef)
	{
		return (XslTemplate) objectManager.findObject(xslTemplateRef);
	}
	
	public static XslTemplate find(Project project, ORef xslTemplateRef)
	{
		return find(project.getObjectManager(), xslTemplateRef);
	}
	
	public static final String TAG_TEMPLATE_CONTENTS = "TemplateContents";
	public static final String TAG_FILE_EXTENSION = "FileExtension";
	public static final String TAG_INCLUDE_IMAGES = "IncludeImages";
}
