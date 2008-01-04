/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.diagram;

import java.awt.Component;
import java.util.HashSet;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.DiagramComponent;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.EAMGraphSelectionModel;
import org.conservationmeasures.eam.diagram.cells.EAMGraphCell;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.dialogs.base.DisposablePanel;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.DiagramLink;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.diagram.DiagramLegendPanel;
import org.conservationmeasures.eam.views.diagram.DiagramSplitPane;

abstract public class DiagramPanel extends DisposablePanel 
{
	public DiagramPanel(MainWindow mainWindowToUse) throws Exception
	{
		mainWindow = mainWindowToUse;
		project = mainWindow.getProject();
		diagramSplitter = createDiagramSplitter();
		add(diagramSplitter);
	}

	public void dispose()
	{
		super.dispose();
		diagramSplitter.dispose();
	}
	
	public DiagramObject getDiagramObject()
	{
		return getDiagramModel().getDiagramObject();
	}

	private EAMGraphSelectionModel getSelectionModel()
	{
		DiagramComponent diagram = getDiagramSplitPane().getDiagramComponent();
		return (EAMGraphSelectionModel) diagram.getSelectionModel();
	}
	
	public void setSelectionModel(EAMGraphSelectionModel selectionModelToUse)
	{
		DiagramComponent diagram = getDiagramSplitPane().getDiagramComponent();
		diagram.setSelectionModel(selectionModelToUse);
	}
	
	public EAMGraphCell[] getSelectedAndRelatedCells()
	{
		return getdiagramComponent().getSelectedAndRelatedCells();
	}
	
	public void selectCells(EAMGraphCell[] cellsToSelect)
	{
		getdiagramComponent().selectCells(cellsToSelect);
	}
	
	public void selectFactor(FactorId idToUse)
	{
		getdiagramComponent().selectFactor(idToUse);
	}
	
	public HashSet<LinkCell> getOnlySelectedLinkCells()
	{
		return getdiagramComponent().getOnlySelectedLinkCells();
	}
	
	public DiagramLink[] getOnlySelectedLinks()
	{
		if(getSelectionModel() == null)
			return new DiagramLink[0];
		
		Object[] rawCells = getSelectionModel().getSelectionCells();
		return getOnlySelectedLinks(rawCells);
	}
	
	public DiagramLink[] getOnlySelectedLinks(Object [] allSelectedCells)
	{
		Vector linkages = new Vector();
		for(int i = 0; i < allSelectedCells.length; ++i)
		{
			if(((EAMGraphCell)allSelectedCells[i]).isFactorLink())
			{
				LinkCell cell = (LinkCell)allSelectedCells[i];
				linkages.add(cell.getDiagramLink());
			}
		}
		return (DiagramLink[])linkages.toArray(new DiagramLink[0]);
	}
	
	public HashSet<LinkCell> getOnlySelectedLinkCells(Object [] allSelectedCells)
	{
		return getdiagramComponent().getOnlySelectedLinkCells(allSelectedCells);
	}

	public FactorCell[] getOnlySelectedFactorCells()
	{
		if(getSelectionModel() == null)
			return new FactorCell[0];
		
		Object[] rawCells = getSelectionModel().getSelectionCells();
		return getOnlySelectedFactorCells(rawCells);
	}
	
	public HashSet<FactorCell> getOnlySelectedFactorAndGroupChildCells() throws Exception
	{
		HashSet<FactorCell> groupBoxChildrenCells = new HashSet();
		FactorCell[] selectedCells = getOnlySelectedFactorCells();
		for (int i = 0; i < selectedCells.length; ++i)
		{
			FactorCell selectedCell = selectedCells[i];
			groupBoxChildrenCells.add(selectedCell);
			if (selectedCell.getDiagramFactor().isGroupBoxFactor())
			{
				DiagramModel diagramModel = getDiagramModel();
				groupBoxChildrenCells.addAll(diagramModel.getGroupBoxFactorChildren(selectedCell));
			}		
		}

		return groupBoxChildrenCells;
	}
	
	public static FactorCell[] getOnlySelectedFactorCells(Object[] allSelectedCells)
	{
		Vector nodes = new Vector();
		for(int i = 0; i < allSelectedCells.length; ++i)
		{
			EAMGraphCell cell = ((EAMGraphCell)allSelectedCells[i]);
			if(cell.isFactor())
				nodes.add(cell);
		}
		return (FactorCell[])nodes.toArray(new FactorCell[0]);
	}
	
	public Factor[] getOnlySelectedFactors()
	{
		if (getSelectionModel() == null)
			return new Factor[0];
		
		Object[] rawCells = getSelectionModel().getSelectionCells();
		return getOnlySelectedFactors(rawCells);
	}
	
	private Factor[] getOnlySelectedFactors(Object[] allSelectedFactors)
	{
		Vector nodes = new Vector();
		for(int i = 0; i < allSelectedFactors.length; ++i)
		{
			EAMGraphCell graphCell = ((EAMGraphCell)allSelectedFactors[i]);
			if(graphCell.isFactor())
			{
				ORef ref = graphCell.getDiagramFactor().getWrappedORef();
				Factor factor = (Factor) project.findObject(ref);
				nodes.add(factor);
			}
		}
		return (Factor[])nodes.toArray(new Factor[0]);

	}
	
	public EAMGraphCell[] getOnlySelectedCells()
	{
		Object[] rawCells = getSelectionModel().getSelectionCells();
		EAMGraphCell[] cells = new EAMGraphCell[rawCells.length];
		for(int i=0; i < cells.length; ++i)
			cells[i] = (EAMGraphCell)rawCells[i];
		return cells;
	}
	
	public void moveFactors(int deltaX, int deltaY, DiagramFactorId[] ids) throws Exception 
	{
		getDiagramModel().moveFactors(deltaX, deltaY, ids);
	}

	public DiagramModel getDiagramModel()
	{
		DiagramComponent diagramComponent = getdiagramComponent();
		if (diagramComponent==null)
			return null;
		return diagramComponent.getDiagramModel();
	}
	
	public DiagramComponent[] getAllSplitterDiagramComponents()
	{
		return getDiagramSplitPane().getAllOwenedDiagramComponents();
	}
	
	public DiagramComponent getdiagramComponent()
	{
		DiagramComponent diagramComponent = getDiagramSplitPane().getDiagramComponent();
		return diagramComponent;
	}

	public String getPanelDescription()
	{
		return EAM.text("Title|Diagram Panel");
	}

	public void addFieldComponent(Component component)
	{
		throw new RuntimeException("Not yet implemented");
	}

	public DiagramSplitPane getDiagramSplitPane()
	{
		return diagramSplitter;
	}
	
	public DiagramLegendPanel getDiagramLegendPanel()
	{
		return getDiagramSplitPane().getLegendPanel();
	}
	
	public void showCurrentDiagram() throws Exception
	{
		diagramSplitter.showCurrentCard();
	}
	
	abstract protected DiagramSplitPane createDiagramSplitter() throws Exception;
	
	DiagramSplitPane diagramSplitter;
	private Project project;
	protected MainWindow mainWindow;
}
