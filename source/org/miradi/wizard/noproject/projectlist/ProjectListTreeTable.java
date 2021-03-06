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
package org.miradi.wizard.noproject.projectlist;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.Position;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.miradi.dialogs.tablerenderers.BorderlessTableCellRendererFactory;
import org.miradi.dialogs.tablerenderers.DefaultTableCellRendererWithPreferredHeightFactory;
import org.miradi.dialogs.treetables.TreeTableWithColumnWidthSaving;
import org.miradi.dialogs.treetables.TreeTableWithRowHeightSaver;
import org.miradi.dialogs.treetables.VariableHeightTreeCellRenderer;
import org.miradi.files.AbstractMpfFileFilter;
import org.miradi.icons.FolderIcon;
import org.miradi.icons.IconManager;
import org.miradi.icons.LegacyMiradiApplicationIcon;
import org.miradi.icons.MiradiApplicationIcon;
import org.miradi.legacyprojects.LegacyProjectUtilities;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.project.OldProjectDirToMpfConverter;
import org.miradi.utils.ColumnSortListener;
import org.miradi.utils.SortableTable;
import org.miradi.wizard.noproject.FileSystemTreeNode;
import org.miradi.wizard.noproject.NoProjectWizardStep;

public class ProjectListTreeTable extends TreeTableWithColumnWidthSaving implements SortableTable
{
	public ProjectListTreeTable(MainWindow mainWindowToUse, ProjectListTreeTableModel treeTableModel, NoProjectWizardStep handlerToUse)
	{
		super(mainWindowToUse, treeTableModel);

		tree.setShowsRootHandles(true);
		Renderer renderer = new Renderer(this);
		tree.setCellRenderer(renderer);
		getTree().setEditable(false);
		getColumnModel().getColumn(0).setPreferredWidth(200);
		setBackground(AppPreferences.getDataPanelBackgroundColor());
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addMouseListener(new MouseHandler(this));
		addColumnSorter();
		dateRenderer = new BorderlessTableCellRendererFactory();
	}

	private void addColumnSorter()
	{
		JTableHeader columnHeader = getTableHeader();
		ColumnSortListener sortListener = new ColumnSortListener(this);
		columnHeader.addMouseListener(sortListener);
	}

	@Override
	public boolean shouldSaveRowHeight()
	{
		return false;
	}
	
	@Override
	public boolean shouldSaveColumnSequence()
	{
		return false;
	}
	
	@Override
	public boolean shouldSaveColumnWidth()
	{
		return false;
	}
	
	public File getSelectedFile()
	{
		int row = getSelectedRow();
		if(row < 0)
			return null;
		
		FileSystemTreeNode node = (FileSystemTreeNode)getRawObjectForRow(row);
		File file = node.getFile();
		return file;
	}
	
	public static boolean isProject(File file) throws Exception
	{
		if(file == null)
			return false;
		
		return file.getName().toLowerCase().endsWith(AbstractMpfFileFilter.EXTENSION.toLowerCase());
	}

	public static boolean isOldProject(File selectedFile) throws Exception
	{
		return LegacyProjectUtilities.isExistingLocalProject(selectedFile);
	}

	public static void doProjectOpen(MainWindow mainWindow, File projectFile) throws Exception
	{
		if(projectFile == null)
			return;
		
		if (isOldProject(projectFile))
		{
			projectFile = OldProjectDirToMpfConverter.convert(mainWindow, projectFile);
			if(projectFile == null)
				return;
		}
		
		if(!isProject(projectFile))
			return;
		
		Cursor cursor = mainWindow.getCursor();
		mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try
		{
			mainWindow.createOrOpenProject(projectFile);
		}
		catch(Exception e)
		{
			EAM.panic(e);
		}
		finally
		{
			mainWindow.setCursor(cursor);
		}
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		if(column == 0)
			return super.getCellRenderer(row, column);
		
		dateRenderer.setCellBackgroundColor(getBackground());
		return dateRenderer;
	}
	
	private void doContextMenu(Point point)
	{
		int row = rowAtPoint(point);
		if(row < 0 || row > getRowCount())
			return;
		
		getSelectionModel().setSelectionInterval(row, row);
		FileSystemTreeNode node = (FileSystemTreeNode)getRawObjectForRow(row);
		JPopupMenu menu = getRightClickMenu(node.getFile());
		menu.show(this, point.x, point.y);

	}

	public JPopupMenu getRightClickMenu(File selectedFile)
	{
		JPopupMenu menu = new JPopupMenu();
		menu.add(new ProjectListOpenAction(this));
		menu.addSeparator();
		menu.add(new ProjectListRenameAction(this)); 
		menu.add(new ProjectListSaveAsAction(this));
		menu.addSeparator();
		menu.add(new ProjectListDeleteAction(this));
		menu.add(new ProjectListCreateDirectoryAction(this));
		menu.add(new ProjectListMoveToDirectoryAction(this));
		
		return menu;
	}
	
	public void refresh()
	{
		Enumeration expandedPaths = getTree().getExpandedDescendants(getTree().getPathForRow(ROOT_ROW_INDEX));
		
		ProjectListTreeTableModel model = getProjectListTreeTableModel();
		model.rebuildEntireTree();
		repaint();

		if (expandedPaths != null)
			restoreExpansion(expandedPaths);
	}

	private void restoreExpansion(Enumeration expandedPaths)
	{
		getTree().expandRow(ROOT_ROW_INDEX);
		while(expandedPaths.hasMoreElements())
		{
			TreePath treePath = (TreePath)expandedPaths.nextElement();
			Object lastPathComponent = treePath.getLastPathComponent();
			if(lastPathComponent != null && !getTreeModel().isLeaf(lastPathComponent))
			{
				TreePath currentPath = getTree().getNextMatch(lastPathComponent.toString(), ROOT_ROW_INDEX, Position.Bias.Forward);
				getTree().expandPath(currentPath);
			}
		}
	}

	private TreeModel getTreeModel()
	{
		return getTree().getModel();
	}

	private ProjectListTreeTableModel getProjectListTreeTableModel()
	{
		return (ProjectListTreeTableModel) getTreeTableModel();
	}
	
	public void sort(int sortByTableColumn)
	{	
		int modelColumn = convertColumnIndexToModel(sortByTableColumn);
		getProjectListTreeTableModel().sort(modelColumn);
	}
	
	private class MouseHandler extends MouseAdapter
	{
		public MouseHandler(ProjectListTreeTable projectListTreeTableToUse)
		{
			projectListTreeTable = projectListTreeTableToUse;
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			super.mousePressed(e);
			if(e.isPopupTrigger())
				doContextMenu(e.getPoint());
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			super.mouseReleased(e);
			if(e.isPopupTrigger())
				doContextMenu(e.getPoint());
		}
		
		@Override
		public void mouseClicked(MouseEvent event)
		{
			super.mouseClicked(event);
			try
			{
				if(event.getClickCount() == 2)
					new ProjectListOpenAction(projectListTreeTable).actionPerformed();
			}
			catch(Exception e)
			{
				EAM.logException(e);
			}
		}
		
		private ProjectListTreeTable projectListTreeTable;
	}
	
	private static class ProjectListItemRenderer extends VariableHeightTreeCellRenderer
	{
		public ProjectListItemRenderer(TreeTableWithRowHeightSaver treeTableToUse)
		{
			super(treeTableToUse);
			setBackgroundNonSelectionColor(AppPreferences.getDataPanelBackgroundColor());
		}
	}
	
	private static class Renderer extends VariableHeightTreeCellRenderer
	{		
		public Renderer(TreeTableWithRowHeightSaver treeTable)
		{	
			super(treeTable);
			folderRenderer = new ProjectListItemRenderer(treeTable);
			folderRenderer.setClosedIcon(new FolderIcon());
			folderRenderer.setOpenIcon(new FolderIcon());
			folderRenderer.setLeafIcon(new FolderIcon());

			projectRenderer = new ProjectListItemRenderer(treeTable);
			projectRenderer.setClosedIcon(new MiradiApplicationIcon());
			projectRenderer.setOpenIcon(new MiradiApplicationIcon());
			projectRenderer.setLeafIcon(new MiradiApplicationIcon());

			oldProjectRenderer = new ProjectListItemRenderer(treeTable);
			oldProjectRenderer.setClosedIcon(new LegacyMiradiApplicationIcon());
			oldProjectRenderer.setOpenIcon(new LegacyMiradiApplicationIcon());
			oldProjectRenderer.setLeafIcon(new LegacyMiradiApplicationIcon());

			unusableRenderer = new ProjectListItemRenderer(treeTable);
			unusableRenderer.setClosedIcon(IconManager.getCancelIcon());
			unusableRenderer.setOpenIcon(IconManager.getCancelIcon());
			unusableRenderer.setLeafIcon(IconManager.getCancelIcon());
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocusToUse)
		{
			
			FileSystemTreeNode node = (FileSystemTreeNode) value;
			try
			{
				VariableHeightTreeCellRenderer renderer = getRenderer(node);
				JComponent configuredRenderer = (JComponent)renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocusToUse);
				return configuredRenderer;
			}
			catch(Exception e)
			{
				EAM.logException(e);
				return unusableRenderer;
			}
		}
		
		private VariableHeightTreeCellRenderer getRenderer(FileSystemTreeNode node) throws Exception
		{
			if(node.isProject())
				return projectRenderer;
			
			if(node.isLegacyProjectDirectory())
				return oldProjectRenderer;
			
			return folderRenderer;
		}
		

		private	VariableHeightTreeCellRenderer folderRenderer;
		private VariableHeightTreeCellRenderer projectRenderer;
		private	VariableHeightTreeCellRenderer oldProjectRenderer;
		private	VariableHeightTreeCellRenderer unusableRenderer;
	}

	private DefaultTableCellRendererWithPreferredHeightFactory dateRenderer;
	private static final int ROOT_ROW_INDEX = 0;
}
