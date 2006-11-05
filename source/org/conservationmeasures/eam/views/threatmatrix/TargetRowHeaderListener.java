/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.threatmatrix;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.conservationmeasures.eam.objects.ConceptualModelNode;
import org.conservationmeasures.eam.objects.ViewData;




public class TargetRowHeaderListener extends ColumnHeaderListener
{
	public TargetRowHeaderListener(MyThreatGirdPanel threatGirdPanelInUse)
	{
		super(threatGirdPanelInUse);
		sortToggle = new boolean[] {true};
	}

	
	public  boolean toggle(int sortColumn) {
		sortToggle[0] = !sortToggle[0];
		return sortToggle[0];
	}
	
	
	public static void sort(MyThreatGirdPanel threatGirdPanel, String currentSortBy, String currentSortDirection) 
	{
		NonEditableThreatMatrixTableModel modelToSort = 
			(NonEditableThreatMatrixTableModel)threatGirdPanel.globalTthreatTable.getModel();
		
		ConceptualModelNode[] threatList = modelToSort.getDirectThreats();
		
		Arrays.sort(threatList, getComparator());
		
		if (currentSortDirection.equals(ViewData.SORT_ASCENDING)) 
			threatList = reverseSort(threatList);
		
		modelToSort.setThreatRows(threatList);
	}
	
	
	public void sort(int sortColumn) 
	{
		
		NonEditableThreatMatrixTableModel modelToSort = 
			(NonEditableThreatMatrixTableModel)threatGirdPanel.globalTthreatTable.getModel();
		
		ConceptualModelNode[] threatList = modelToSort.getDirectThreats();
		
		Comparator comparator = getComparator();
		
		Arrays.sort(threatList, comparator );
		
		if ( toggle(sortColumn) )  
			threatList = reverseSort(threatList);

		modelToSort.setThreatRows(threatList);
		
		saveState(sortColumn,null);
		
	}


	private void saveState(int sortColumn,  NonEditableThreatMatrixTableModel modelToSort)
	{
		saveSortState(sortToggle[0], ViewData.SORT_TARGETS);
	}


	private static Comparator getComparator()
	{
		Comparator comparator;
		comparator = new IgnoreCaseStringComparator();
		return comparator;
	}


	private static ConceptualModelNode[] reverseSort(ConceptualModelNode[] threatList)
	{
		Vector list = new Vector(Arrays.asList(threatList));
		Collections.reverse(list);
		threatList = (ConceptualModelNode[]) list.toArray(new ConceptualModelNode[0]);
		return threatList;
	}

	
	boolean sortToggle[];

}