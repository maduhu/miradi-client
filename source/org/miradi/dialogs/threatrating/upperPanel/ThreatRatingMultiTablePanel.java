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
package org.miradi.dialogs.threatrating.upperPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;

import org.miradi.dialogs.base.ColumnMarginResizeListenerValidator;
import org.miradi.dialogs.base.MiradiPanel;
import org.miradi.dialogs.base.MultiTablePanel;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.dialogs.treetables.MultiTreeTablePanel.ScrollPaneWithHideableScrollBar;
import org.miradi.layout.OneRowGridLayout;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORefList;
import org.miradi.utils.*;
import org.miradi.views.umbrella.ObjectPicker;

public class ThreatRatingMultiTablePanel extends MultiTablePanel implements ListSelectionListener 
{
	public ThreatRatingMultiTablePanel(MainWindow mainWindowToUse) throws Exception
	{
		super(mainWindowToUse);
		
		createTables();
		
		addTable();
		addTablesToSelectionController();
		synchTableColumns();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();

        threatIconTable.dispose();
		threatNameTable.dispose();
		targetThreatLinkTable.dispose();
		threatSummaryColumnTable.dispose();
		targetSummaryRowTable.dispose();
		overallProjectSummaryCellTable.dispose();
	}
	
	public void updateAllTables() throws Exception
	{
		updateTable(threatIconTable);
		updateTable(threatNameTable);
		updateTable(targetThreatLinkTable);
		updateTable(threatSummaryColumnTable);
		updateTable(targetSummaryRowTable);
		updateTable(overallProjectSummaryCellTable);
	}
	
	private void updateTable(TableWithColumnWidthAndSequenceSaver tableToUpdate) throws Exception
	{
		tableToUpdate.updateAutomaticRowHeights();
		tableToUpdate.updateToReflectNewColumns();
	}
	
	private void addTablesToSelectionController()
	{
		selectionController.addTable(threatIconTable);
		selectionController.addTable(threatNameTable);
		selectionController.addTable(targetThreatLinkTable);
		selectionController.addTable(threatSummaryColumnTable);
	}
	
	private void createTables() throws Exception
	{
        threatIconTableModel = new ThreatIconColumnTableModel(getProject());
		threatNameTableModel = new ThreatNameColumnTableModel(getProject());
		threatIconTable = new ThreatIconColumnTable(getMainWindow(), threatIconTableModel);
		threatNameTable = new ThreatNameColumnTable(getMainWindow(), threatNameTableModel);

        addRowHeightControlledTable(threatIconTable);
        addRowSortControlledTable(threatIconTable);
        listenForColumnWidthChanges(threatIconTable);

		addRowHeightControlledTable(threatNameTable);
		addRowSortControlledTable(threatNameTable);
		listenForColumnWidthChanges(threatNameTable);

		targetThreatLinkTableModel = new TargetThreatLinkTableModel(getProject());
		targetThreatLinkTable = new TargetThreatLinkTable(getMainWindow(), targetThreatLinkTableModel);
		addRowHeightControlledTable(targetThreatLinkTable);
		addRowSortControlledTable(targetThreatLinkTable);
		listenForColumnWidthChanges(targetThreatLinkTable);

		threatSummaryColumnTableModel = new ThreatSummaryColumnTableModel(getProject());
		threatSummaryColumnTable = new ThreatSummaryColumnTable(getMainWindow(), threatSummaryColumnTableModel);
		addRowHeightControlledTable(threatSummaryColumnTable);
		addRowSortControlledTable(threatSummaryColumnTable);
		listenForColumnWidthChanges(threatSummaryColumnTable);

		targetSummaryRowTableModel = new TargetSummaryRowTableModel(getProject());
		targetSummaryRowTable = new TargetSummaryRowTable(getMainWindow(), targetSummaryRowTableModel, targetThreatLinkTable);
		targetSummaryRowTable.resizeTable(1);
		
		overallProjectSummaryCellTableModel = new OverallProjectSummaryCellTableModel(getProject());
		overallProjectSummaryCellTable = new OverallProjectSummaryCellTable(getMainWindow(), overallProjectSummaryCellTableModel);
		overallProjectSummaryCellTable.resizeTable(1);

	}

	public TableExporter createTableForExporting()
	{
		ThreatRatingMultiTableAsOneExporter exporter = new ThreatRatingMultiTableAsOneExporter(getProject());
		exporter.addAsTopRowTable(new ThreatIconTableModelExporter(threatIconTableModel));
		exporter.addAsTopRowTable(new ThreatNameTableModelExporter(threatNameTableModel));
		exporter.addAsTopRowTable(new ThreatTargetTableModelExporter(targetThreatLinkTableModel));
		exporter.addAsTopRowTable(new MainThreatTableModelExporter(threatSummaryColumnTableModel));
		exporter.setTargetSummaryRowTable(new MainThreatTableModelExporter(targetSummaryRowTableModel));
		exporter.setOverallSummaryRowTable(new MainThreatTableModelExporter(overallProjectSummaryCellTableModel));
		
		return exporter;
	}	

	private void listenForColumnWidthChanges(JTable table)
	{
		table.getColumnModel().addColumnModelListener(new ColumnMarginResizeListenerValidator(this));
	}
	
	public static String getTargetSummaryRowHeaderLabel()
	{
		return EAM.text("Summary Target Ratings:");
	}
	
	class ScrollPaneWithSizeConstraints extends ScrollPaneWithHideableScrollBar
	{
		public ScrollPaneWithSizeConstraints(Component view)
		{
			super(view);
		}
		
		public ScrollPaneWithSizeConstraints(Component view, MouseWheelListener masterMouseWheelListener)
		{
			this(view);
			
			addMouseWheelListener(masterMouseWheelListener);
		}
		
		public void capMaxWidth()
		{
			capMaxWidth = true;
		}
		
		public void capMaxHeight()
		{
			capMaxHeight = true;
		}
		
		public void capMinWidth()
		{
			capMinWidth = true;
		}
		
		public void capMinHeight()
		{
			capMinHeight = true;
		}
		
		@Override
		public Dimension getSize()
		{
			final Dimension size = super.getSize();
			return size;
		}
		
		@Override
		public Dimension getPreferredSize()
		{
			lastKnownPreferredSize = super.getPreferredSize();
			return lastKnownPreferredSize;
		}
		
		@Override
		public Dimension getMaximumSize()
		{
			final Dimension max = super.getMaximumSize();
			int width = max.width;
			if(capMaxWidth && lastKnownPreferredSize != null)
				width = Math.min(width, lastKnownPreferredSize.width);
			int height = max.height;
			if(capMaxHeight && lastKnownPreferredSize != null)
				height = Math.min(height, lastKnownPreferredSize.height);
			return new Dimension(width, height);
		}

		@Override
		public Dimension getMinimumSize()
		{
			final Dimension min = super.getMinimumSize();
			int width = min.width;
			if(capMinWidth && lastKnownPreferredSize != null)
				width = Math.max(width, lastKnownPreferredSize.width);
			int height = min.height;
			if(capMinHeight && lastKnownPreferredSize != null)
				height = Math.max(height, lastKnownPreferredSize.height);
			return new Dimension(width, height);
		}

		private boolean capMaxWidth;
		private boolean capMaxHeight;
		private boolean capMinWidth;
		private boolean capMinHeight;
		private Dimension lastKnownPreferredSize;
	}
	
	static class CornerFillerComponent extends FlexibleWidthHtmlViewer
	{
		public CornerFillerComponent(MainWindow mainWindow, JComponent matchWidthOfComponent, JComponent matchHeightOfComponent, String htmlText)
		{
			super(mainWindow, htmlText);
			
			matchWidthOf = matchWidthOfComponent;
			matchHeightOf = matchHeightOfComponent;
			
			ResizeHandlerToForceLayoutWhenDraggingColumnWidths handler = new ResizeHandlerToForceLayoutWhenDraggingColumnWidths();
			matchWidthOf.addComponentListener(handler);
			matchHeightOf.addComponentListener(handler);
			
			setBackground(mainWindow.getAppPreferences().getDataPanelBackgroundColor());
		}

		@Override
		public int getWidth()
		{
			return getSize().width;
		}
		
		@Override
		public int getHeight()
		{
			return getSize().height;
		}
		
		@Override
		public Dimension getSize()
		{
			return new Dimension(matchWidthOf.getPreferredSize().width, matchHeightOf.getPreferredSize().height);
		}
		
		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(matchWidthOf.getPreferredSize().width, matchHeightOf.getPreferredSize().height);
		}
		
		@Override
		public Dimension getMaximumSize()
		{
			return new Dimension(matchWidthOf.getMaximumSize().width, matchHeightOf.getMaximumSize().height);
		}
		
		@Override
		public Dimension getMinimumSize()
		{
			return new Dimension(matchWidthOf.getMinimumSize().width, matchHeightOf.getMinimumSize().height);
		}
		
		class ResizeHandlerToForceLayoutWhenDraggingColumnWidths implements ComponentListener
		{
			public void componentResized(ComponentEvent e)
			{
				getParent().invalidate();
			}

			public void componentHidden(ComponentEvent e)	{}
			public void componentMoved(ComponentEvent e)	{}
			public void componentShown(ComponentEvent e)	{}
		}
		
		private JComponent matchWidthOf;
		private JComponent matchHeightOf;
	}
	
	static class OverallProjectRatingScrollPane extends ScrollPaneWithHideableScrollBar
	{
		public OverallProjectRatingScrollPane(Component component, JComponent matchWidthOfComponent)
		{
			super(component);
			matchWidthOf = matchWidthOfComponent;

			ResizeHandlerToForceLayoutWhenDraggingColumnWidths handler = new ResizeHandlerToForceLayoutWhenDraggingColumnWidths();
			matchWidthOf.addComponentListener(handler);
		}

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(matchWidthOf.getPreferredSize().width, super.getPreferredSize().height);
		}
		
		@Override
		public Dimension getMaximumSize()
		{
			return new Dimension(matchWidthOf.getMaximumSize().width, super.getMaximumSize().height);
		}

		@Override
		public Dimension getMinimumSize()
		{
			return new Dimension(matchWidthOf.getMinimumSize().width, super.getMinimumSize().height);
		}
		
		class ResizeHandlerToForceLayoutWhenDraggingColumnWidths implements ComponentListener
		{
			public void componentResized(ComponentEvent e)
			{
				getParent().invalidate();
			}

			public void componentHidden(ComponentEvent e)	{}
			public void componentMoved(ComponentEvent e)	{}
			public void componentShown(ComponentEvent e)	{}
		}
		
		private JComponent matchWidthOf;
	}
	
	static class ScrollPaneWithWidthMatchingForSingleRowTable extends ScrollPaneWithHideableScrollBar
	{
		public ScrollPaneWithWidthMatchingForSingleRowTable(JTable view, JComponent matchWidthOfComponent)
		{
			super(view);
			table = view;
			matchWidthOf = matchWidthOfComponent;
		}
		
		@Override
		public int getWidth()
		{
			return getSize().width;
		}
		
		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(matchWidthOf.getPreferredSize().width, super.getPreferredSize().height);
		}
		
		@Override
		public Dimension getMaximumSize()
		{
			int maxHeight = table.getPreferredScrollableViewportSize().height + getHorizontalScrollBar().getPreferredSize().height;
			maxHeight = Math.min(maxHeight, super.getMaximumSize().height);
			return new Dimension(matchWidthOf.getMaximumSize().width, maxHeight);
		}
		
		@Override
		public Dimension getMinimumSize()
		{
			final int matchWidth = matchWidthOf.getMinimumSize().width;
			final int superHeight = super.getMinimumSize().height;
			return new Dimension(matchWidth, superHeight);
		}
		
		private JTable table;
		private JComponent matchWidthOf;
	}
	
	private void addTable()
	{
		ScrollPaneWithSizeConstraints threatSummaryColumnTableScroller = new ScrollPaneWithSizeConstraints(threatSummaryColumnTable);
		threatSummaryColumnTableScroller.capMinWidth();
		threatSummaryColumnTableScroller.capMaxWidth();
		addToVerticalController(threatSummaryColumnTableScroller);
		threatSummaryColumnTableScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		threatSummaryColumnTableScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		MouseWheelListener masterMouseWheelListener = getFirstMasterWheelListener(threatSummaryColumnTableScroller);

        ScrollPaneWithSizeConstraints threatIconScroller = new ScrollPaneWithSizeConstraints(threatIconTable, masterMouseWheelListener);
        threatIconScroller.capMinWidth();
        threatIconScroller.capMaxWidth();
        threatIconScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        threatIconScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        addToVerticalController(threatIconScroller);

		ScrollPaneWithSizeConstraints threatTableScroller = new ScrollPaneWithSizeConstraints(threatNameTable, masterMouseWheelListener);
		threatTableScroller.capMinWidth();
		threatTableScroller.capMaxWidth();
		threatTableScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		threatTableScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		addToVerticalController(threatTableScroller);
		
		ScrollPaneWithSizeConstraints targetThreatLinkTableScroller = new ScrollPaneWithSizeConstraints(targetThreatLinkTable, masterMouseWheelListener);
		targetThreatLinkTableScroller.capMaxWidth();
		targetThreatLinkTableScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		targetThreatLinkTableScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		targetThreatLinkTableScroller.hideVerticalScrollBar();
		addToVerticalController(targetThreatLinkTableScroller);
		addToHorizontalController(targetThreatLinkTableScroller);
		
		ScrollPaneWithHideableScrollBar targetSummaryRowTableScroller = new ScrollPaneWithWidthMatchingForSingleRowTable(targetSummaryRowTable, targetThreatLinkTableScroller);
		addToHorizontalController(targetSummaryRowTableScroller);
		targetSummaryRowTableScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		targetSummaryRowTableScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		targetSummaryRowTableScroller.hideVerticalScrollBar();

		final OneRowGridLayout overallPanelLayout = new OneRowGridLayout();
		overallPanelLayout.setGaps(5);
		overallPanelLayout.setMargins(5);
		MiradiPanel overallPanel = new MiradiPanel(overallPanelLayout);
		overallPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		overallPanel.add(new PanelTitleLabel(EAM.text("<html><b>Overall<br>Project<br>Rating</b></html>")));
		overallPanel.add(overallProjectSummaryCellTable);
		JScrollPane overallProjectSummaryCellTableScroller = new OverallProjectRatingScrollPane(overallPanel, threatSummaryColumnTableScroller);
		overallProjectSummaryCellTableScroller.setBorder(null);
		overallProjectSummaryCellTableScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		overallProjectSummaryCellTableScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		overallProjectSummaryCellTableScroller.setMinimumSize(overallProjectSummaryCellTableScroller.getPreferredSize());
		overallProjectSummaryCellTableScroller.setMaximumSize(overallProjectSummaryCellTableScroller.getPreferredSize());

		CornerFillerComponent firstLowerLeftCell = new CornerFillerComponent(getMainWindow(), threatIconScroller, targetSummaryRowTableScroller, "");
		CornerFillerComponent secondLowerLeftCell = new CornerFillerComponent(getMainWindow(), threatTableScroller, targetSummaryRowTableScroller, "<html><div class='DataPanel'><b>" + getTargetSummaryRowHeaderLabel() + "<br><br><br></html>");

		Box hBoxTop = Box.createHorizontalBox();
		hBoxTop.add(threatIconScroller);
		hBoxTop.add(threatTableScroller);
		hBoxTop.add(targetThreatLinkTableScroller);
		hBoxTop.add(threatSummaryColumnTableScroller);
		hBoxTop.add(Box.createHorizontalGlue());
		
		Box hBoxBottom = Box.createHorizontalBox();
		hBoxBottom.add(firstLowerLeftCell);
		hBoxBottom.add(secondLowerLeftCell);
		hBoxBottom.add(targetSummaryRowTableScroller);
		hBoxBottom.add(overallProjectSummaryCellTableScroller);
		hBoxBottom.add(Box.createHorizontalGlue());
		
		Box vbox = Box.createVerticalBox();
		vbox.add(hBoxTop);
		vbox.add(hBoxBottom);
		
		add(vbox);
	}

	private MouseWheelListener getFirstMasterWheelListener(ScrollPaneWithSizeConstraints threatSummaryColumnTableScroller)
	{
		final int FIRST_ARRAY_INDEX = 0;
		MouseWheelListener[] mouseWheelListeners = threatSummaryColumnTableScroller.getMouseWheelListeners();
		if (mouseWheelListeners.length > 0)
			return mouseWheelListeners[FIRST_ARRAY_INDEX];
		
		return null;
	}

	private void synchTableColumns()
	{
		ColumnChangeSyncer columnWidthSyncer = new ColumnChangeSyncer(targetSummaryRowTable);
		targetThreatLinkTable.getColumnModel().addColumnModelListener(columnWidthSyncer);
		
		columnWidthSyncer.syncPreferredColumnWidths(targetThreatLinkTable.getColumnModel());
	}
	
	public ObjectPicker getObjectPicker()
	{
		return this;
	}
	
	public ORefList[] getSelectedHierarchies()
	{
		return targetThreatLinkTable.getSelectedHierarchies();
	}
	
	@Override
	public void addSelectionChangeListener(ListSelectionListener listener)
	{
		targetThreatLinkTable.getSelectionModel().addListSelectionListener(listener);
		targetThreatLinkTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
	}

	@Override
	public void removeSelectionChangeListener(ListSelectionListener listener)
	{
		targetThreatLinkTable.getSelectionModel().removeListSelectionListener(listener);
	}

    private ThreatIconColumnTableModel threatIconTableModel;
	private ThreatNameColumnTableModel threatNameTableModel;
	private ThreatNameColumnTable threatNameTable;
	private ThreatIconColumnTable threatIconTable;
	private TargetThreatLinkTableModel targetThreatLinkTableModel;
	private TargetThreatLinkTable targetThreatLinkTable;
	
	private ThreatSummaryColumnTableModel threatSummaryColumnTableModel;
	private ThreatSummaryColumnTable threatSummaryColumnTable;
	
	private TargetSummaryRowTableModel targetSummaryRowTableModel;
	private TargetSummaryRowTable targetSummaryRowTable;
	
	private OverallProjectSummaryCellTable overallProjectSummaryCellTable;
	private OverallProjectSummaryCellTableModel overallProjectSummaryCellTableModel;
}