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

package org.miradi.schemas;

import org.miradi.objects.Assignment;

abstract public class AbstractAssignmentSchema extends AbstractPlanningObjectSchema
{
	public AbstractAssignmentSchema()
	{
		super();
	}
	
	@Override
	protected void fillFieldSchemas()
	{
		super.fillFieldSchemas();
		
		createFieldSchemaOptionalRef(Assignment.TAG_CATEGORY_ONE_REF);
		createFieldSchemaOptionalRef(Assignment.TAG_CATEGORY_TWO_REF);
		createTaxonomyClassificationSchemaField();
		createAccountingClassificationSchemaField();
	}

	protected void createAccountingClassificationSchemaField()
	{
		createAccountingClassifications(TAG_ACCOUNTING_CLASSIFICATION_CONTAINER);
	}

	public static final String TAG_ACCOUNTING_CLASSIFICATION_CONTAINER = "AccountingClassificationContainer";
}
