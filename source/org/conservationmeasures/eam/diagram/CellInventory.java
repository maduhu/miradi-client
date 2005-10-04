/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import java.util.Iterator;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.nodes.EAMGraphCell;
import org.conservationmeasures.eam.diagram.nodes.Node;

class CellInventory
{
	public CellInventory()
	{
		cells = new Vector();
	}
	
	void add(EAMGraphCell cell, int id)
	{
		if(id == Node.INVALID_ID)
		{
			id = nextId++;
		}
		else
		{
			if(nextId < id+1)
				nextId = id + 1;
		}
		
		if(getById(id) != null)
			throw new RuntimeException("Can't add over existing id " + id);
		
		cell.setId(id);
		cells.add(cell);
	}
	
	public int size()
	{
		return cells.size();
	}
	
	public Vector getAllNodes()
	{
		Vector nodes = new Vector();
		for(int i=0; i < size(); ++i)
		{
			EAMGraphCell cell = getCellByIndex(i);
			if(cell.isNode())
				nodes.add(cell);
		}	
		return nodes;
	}
	
	public Vector getAllLinkages()
	{
		Vector linkages = new Vector();
		for(int i=0; i < size(); ++i)
		{
			EAMGraphCell cell = getCellByIndex(i);
			if(cell.isLinkage())
				linkages.add(cell);
		}	
		return linkages;
	}
	
	public EAMGraphCell getById(int id)
	{
		for (Iterator iter = cells.iterator(); iter.hasNext();) 
		{
			EAMGraphCell cell = (EAMGraphCell) iter.next();
			if(cell.getId() == id)
				return cell;
		}
		return null;
	}
	
	public void remove(EAMGraphCell cell)
	{
		cells.remove(cell);
	}
	
	public void clear()
	{
		nextId = 0;
		cells.clear();
	}

	private EAMGraphCell getCellByIndex(int index)
	{
		return (EAMGraphCell)cells.get(index);
	}
	
	private int nextId;
	Vector cells;
}