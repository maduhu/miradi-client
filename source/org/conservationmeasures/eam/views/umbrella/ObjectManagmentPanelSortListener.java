/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.umbrella;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JTable;

import org.conservationmeasures.eam.objects.EAMObject;
import org.martus.swing.UiTable.UiTableHeader;

public class ObjectManagmentPanelSortListener  extends MouseAdapter
{	
	public void mouseClicked(MouseEvent e) 
	{
		UiTableHeader tableHeader = (UiTableHeader)e.getSource();
		JTable table = tableHeader.getTable();
		int clickedColumn = tableHeader.columnAtPoint(e.getPoint());
		int sortColumn = tableHeader.getColumnModel().getColumn(clickedColumn).getModelIndex();
		
		AnnotationTableModel annotationTableModel = (AnnotationTableModel)table.getModel();
		EAMObject rowsToSort[] = annotationTableModel.getEAMObjectRows();

		Arrays.sort(rowsToSort,  new EAMObjectComparator(annotationTableModel, sortColumn));
		
		if ( toggle(sortColumn) )  
		{
			Vector list = new Vector(Arrays.asList(rowsToSort));
			Collections.reverse(list);
			rowsToSort = (EAMObject[])list.toArray(new EAMObject[0]);
		}

		annotationTableModel.setEAMObjectRows(rowsToSort);
		
		table.revalidate();
		table.repaint();
	}
	
	public  boolean toggle(int sortColumn) {
		sortToggle = !sortToggle;
		return sortToggle;
	}

	boolean sortToggle;

}
