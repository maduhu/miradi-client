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
package org.miradi.dialogs.planning.propertiesPanel;

import org.miradi.dialogs.planning.treenodes.UnspecifiedBaseObject;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.Assignment;
import org.miradi.objects.BaseObject;
import org.miradi.objects.BudgetCategoryOne;
import org.miradi.objects.BudgetCategoryTwo;
import org.miradi.objects.FundingSource;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceItemBaseObjectWrapper;
import org.miradi.schemas.AccountingCodeSchema;
import org.miradi.schemas.BudgetCategoryOneSchema;
import org.miradi.schemas.BudgetCategoryTwoSchema;
import org.miradi.schemas.FundingSourceSchema;
import org.miradi.utils.ModelColumnTagProvider;

abstract public class AbstractSummaryTableModel extends PlanningViewAbstractAssignmentTableModel implements ModelColumnTagProvider
{
	public AbstractSummaryTableModel(Project projectToUse)
	{
		super(projectToUse);
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return true;
	}
	
	@Override
	public String getColumnName(int column)
	{
		if (isAccountingCodeColumn(column))
			return EAM.text("Acct Code");
		
		if (isFundingSourceColumn(column))
			return EAM.text("Funding Source");
		
		if (isBudgetCategoryOneColumn(column))
			return EAM.text("Category One");
		
		if (isBudgetCategoryTwoColumn(column))
			return EAM.text("Category Two");
		
		return null;
	}
	
	public Object getValueAt(int row, int column)
	{
		return getCellValue(row, column);
	}
	
	protected Object getCellValue(int row, int column)
	{
		ORef baseObjectRefForRow = getRefForRow(row);
		BaseObject baseObjectForRow = getProject().findObject(baseObjectRefForRow);
		if (isFundingSourceColumn(column))
			return new ChoiceItemBaseObjectWrapper(getFundingSource(baseObjectForRow));
		
		if (isAccountingCodeColumn(column))
			return new ChoiceItemBaseObjectWrapper(getAccountingCode(baseObjectForRow));
		
		if (isBudgetCategoryOneColumn(column))
			return new ChoiceItemBaseObjectWrapper(getBaseObject(baseObjectForRow, Assignment.TAG_CATEGORY_ONE_REF, BudgetCategoryOneSchema.getObjectType(), BudgetCategoryOneSchema.OBJECT_NAME));
		
		if (isBudgetCategoryTwoColumn(column))
			return new ChoiceItemBaseObjectWrapper(getBaseObject(baseObjectForRow, Assignment.TAG_CATEGORY_TWO_REF, BudgetCategoryTwoSchema.getObjectType(), BudgetCategoryTwoSchema.OBJECT_NAME));
		
		return null;
	}
	
	private BaseObject getBaseObject(BaseObject baseObjectForRow, String tagForRef, int objectType, String objectTypeName)
	{
		ORef ref = baseObjectForRow.getRef(tagForRef);
		if (ref.isInvalid())
			return createInvalidObject(getObjectManager(), objectType, objectTypeName);
			
		return BaseObject.find(getProject(), ref);
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		ORef refForRow = getRefForRow(row);
		if (isAccountingCodeColumn(column))
			setAccountingCode(ORef.createFromString(((ChoiceItem) value).getCode()), refForRow, column);
		
		if (isFundingSourceColumn(column))
			setFundingSource(ORef.createFromString(((ChoiceItem) value).getCode()), refForRow, column);
		
		if (isBudgetCategoryOneColumn(column))
			setRefValue(refForRow, Assignment.TAG_CATEGORY_ONE_REF, (ChoiceItem) value);
		
		if (isBudgetCategoryTwoColumn(column))
			setRefValue(refForRow, Assignment.TAG_CATEGORY_TWO_REF, (ChoiceItem) value);
	}
	
	private void setRefValue(ORef refForRow, String destinationTag, ChoiceItem choiceItem)
	{
		setValueUsingCommand(refForRow, destinationTag, ORef.createFromString(choiceItem.getCode()));
	}

	public String getColumnTag(int modelColumn)
	{
		if (isAccountingCodeColumn(modelColumn))
			return AccountingCodeSchema.OBJECT_NAME;
		
		if (isFundingSourceColumn(modelColumn))
			return FundingSourceSchema.OBJECT_NAME;
		
		if (isBudgetCategoryOneColumn(modelColumn))
			return BudgetCategoryOneSchema.OBJECT_NAME;
		
		if (isBudgetCategoryTwoColumn(modelColumn))
			return BudgetCategoryTwoSchema.OBJECT_NAME;
		
		return "";
	}
	
	public static BaseObject createInvalidObject(ObjectManager objectManager, int objectType, String objectTypeName)
	{
		return new UnspecifiedBaseObject(objectManager, objectType, objectTypeName);
	}
	
	public boolean isColumnForType(int column, int objectType)
	{
		if (FundingSource.is(objectType))
			return isFundingSourceColumn(column);
		
		if (AccountingCode.is(objectType))
			return isAccountingCodeColumn(column);
		
		if (BudgetCategoryOne.is(objectType))
			return isBudgetCategoryOneColumn(column);
		
		if (BudgetCategoryTwo.is(objectType))
			return isBudgetCategoryTwoColumn(column);
		
		return false;
	}
	
	abstract protected BaseObject getFundingSource(BaseObject assignment);
	
	abstract protected BaseObject getAccountingCode(BaseObject assignment);

	abstract public boolean isFundingSourceColumn(int column);

	abstract public boolean isAccountingCodeColumn(int column);
	
	abstract public boolean isBudgetCategoryOneColumn(int column);
	
	abstract public boolean isBudgetCategoryTwoColumn(int column);

	abstract public int getColumnCount();
	
	abstract public boolean isResourceColumn(int column);
	
	abstract protected String getAccountingCodeTag();
	
	abstract protected String getFundingSourceTag();
	
	abstract protected void setAccountingCode(ORef accountingCodeRef, ORef assignmentRefForRow, int column);

	abstract protected void setFundingSource(ORef fundingSourceRef, ORef assignmentRefForRow, int column);
}
