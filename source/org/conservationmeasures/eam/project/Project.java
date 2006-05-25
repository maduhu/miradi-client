/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.project;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.conservationmeasures.eam.annotations.GoalIds;
import org.conservationmeasures.eam.annotations.GoalPool;
import org.conservationmeasures.eam.annotations.IndicatorId;
import org.conservationmeasures.eam.annotations.ObjectiveIds;
import org.conservationmeasures.eam.annotations.ObjectivePool;
import org.conservationmeasures.eam.annotations.ResourcePool;
import org.conservationmeasures.eam.annotations.TaskPool;
import org.conservationmeasures.eam.annotations.ViewPool;
import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandDiagramMove;
import org.conservationmeasures.eam.commands.CommandDoNothing;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandInsertNode;
import org.conservationmeasures.eam.commands.CommandLinkNodes;
import org.conservationmeasures.eam.commands.CommandSetNodeName;
import org.conservationmeasures.eam.commands.CommandSetNodeSize;
import org.conservationmeasures.eam.database.DataUpgrader;
import org.conservationmeasures.eam.database.LinkageManifest;
import org.conservationmeasures.eam.database.NodeManifest;
import org.conservationmeasures.eam.database.ObjectManifest;
import org.conservationmeasures.eam.database.ProjectServer;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.EAMGraphCell;
import org.conservationmeasures.eam.diagram.nodes.DiagramLinkage;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.diagram.nodes.LinkageDataMap;
import org.conservationmeasures.eam.diagram.nodes.NodeDataHelper;
import org.conservationmeasures.eam.diagram.nodes.NodeDataMap;
import org.conservationmeasures.eam.diagram.nodetypes.NodeType;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeTarget;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.exceptions.NothingToRedoException;
import org.conservationmeasures.eam.exceptions.NothingToUndoException;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.CommandExecutedListener;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.TransferableEamList;
import org.conservationmeasures.eam.main.ViewChangeListener;
import org.conservationmeasures.eam.objects.ConceptualModelLinkage;
import org.conservationmeasures.eam.objects.ConceptualModelNode;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.ObjectType;
import org.conservationmeasures.eam.objects.ProjectResource;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.utils.Logging;
import org.conservationmeasures.eam.views.diagram.DiagramView;
import org.conservationmeasures.eam.views.diagram.LayerManager;
import org.conservationmeasures.eam.views.interview.InterviewModel;
import org.conservationmeasures.eam.views.interview.InterviewStepModel;
import org.conservationmeasures.eam.views.noproject.NoProjectView;
import org.jgraph.graph.GraphSelectionModel;


public class Project
{
	public Project() throws IOException
	{
		this(new ProjectServer());
	}
	
	public Project(ProjectServer databaseToUse) throws IOException
	{
		database = databaseToUse;
		commandExecutedListeners = new Vector();
		viewChangeListeners = new Vector();
		
		clear();
	}

	private void clear() throws IOException
	{
		projectInfo = new ProjectInfo();
		nodePool = new NodePool();
		linkagePool = new LinkagePool();
		goalPool = GoalPool.createSampleGoals(getAnnotationIdAssigner());
		objectivePool = ObjectivePool.createSampleObjectives(getAnnotationIdAssigner());
		taskPool = new TaskPool();
		viewPool = new ViewPool();
		resourcePool = new ResourcePool();
		diagramModel = new DiagramModel(this);
		interviewModel = new InterviewModel();
		interviewModel.loadSteps();
		layerManager = new LayerManager();
		threatRatingFramework = new ThreatRatingFramework(this);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// simple getters
	
	public IdAssigner getNodeIdAssigner()
	{
		return projectInfo.getNodeIdAssigner();
	}
	
	public IdAssigner getAnnotationIdAssigner()
	{
		return projectInfo.getAnnotationIdAssigner();
	}
	
	protected ProjectServer getDatabase()
	{
		return database;
	}
	
	public NodePool getNodePool()
	{
		return nodePool;
	}
	
	public LinkagePool getLinkagePool()
	{
		return linkagePool;
	}
	
	public GoalPool getGoalPool()
	{
		return goalPool;
	}
	
	public ObjectivePool getObjectivePool()
	{
		return objectivePool;
	}
	
	public TaskPool getTaskPool()
	{
		return taskPool;
	}
	
	public ViewPool getViewPool()
	{
		return viewPool;
	}
	
	public ResourcePool getResourcePool()
	{
		return resourcePool;
	}

	public DiagramModel getDiagramModel()
	{
		return diagramModel;
	}
	
	public InterviewModel getInterviewModel()
	{
		return interviewModel;
	}
	
	public LayerManager getLayerManager()
	{
		return layerManager;
	}
	
	public String getCurrentView()
	{
		return projectInfo.getCurrentView();
	}
	
	public void setCurrentView(String newCurrentView)
	{
		projectInfo.setCurrentView(newCurrentView);
	}
	
	public ViewData getCurrentViewData() throws Exception
	{
		return getViewData(getCurrentView());
	}
	
	public ViewData getViewData(String viewName) throws Exception
	{
		ViewData found = viewPool.findByLabel(viewName);
		if(found != null)
			return found;
		
		int createdId = createObject(ObjectType.VIEW_DATA);
		setObjectData(ObjectType.VIEW_DATA, createdId, ViewData.TAG_LABEL, viewName);
		return viewPool.find(createdId);
	}
	
	public ThreatRatingFramework getThreatRatingFramework()
	{
		return threatRatingFramework;
	}
	
	public Task getRootTask() throws Exception
	{
		int rootTaskId = projectInfo.getRootTaskId();
		if(rootTaskId == IdAssigner.INVALID_ID)
		{
			rootTaskId = createObject(ObjectType.TASK);
			projectInfo.setRootTaskId(rootTaskId);
			getDatabase().writeProjectInfo(projectInfo);
		}
		return getTaskPool().find(rootTaskId);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// objects
	
	public int createObject(int objectType) throws Exception
	{
		return createObject(objectType, IdAssigner.INVALID_ID);
	}
	
	public int createObject(int objectType, int objectId) throws Exception
	{
		int createdId = IdAssigner.INVALID_ID;
		switch(objectType)
		{
			case ObjectType.THREAT_RATING_CRITERION:
			{
				createdId = getThreatRatingFramework().createCriterion(objectId);
				EAMObject newObject = getThreatRatingFramework().getCriterion(createdId);
				getDatabase().writeObject(newObject);
				getDatabase().writeThreatRatingFramework(getThreatRatingFramework());
				break;
			}
				
			case ObjectType.THREAT_RATING_VALUE_OPTION:
			{
				createdId = getThreatRatingFramework().createValueOption(objectId);
				EAMObject newObject = getThreatRatingFramework().getValueOption(createdId);
				getDatabase().writeObject(newObject);
				getDatabase().writeThreatRatingFramework(getThreatRatingFramework());
				break;
			}
			case ObjectType.TASK:
			{
				if(objectId == IdAssigner.INVALID_ID)
					objectId = getAnnotationIdAssigner().takeNextId();
				Task task = new Task(objectId);
				taskPool.put(task);
				getDatabase().writeObject(task);
				createdId = task.getId();
				break;
			}
			
			case ObjectType.MODEL_NODE:
			{
				createdId = insertNodeAtId(new NodeTypeTarget(), objectId);
				break;
			}
			
			case ObjectType.VIEW_DATA:
			{
				if(objectId == IdAssigner.INVALID_ID)
					objectId = getAnnotationIdAssigner().takeNextId();
				ViewData viewData = new ViewData(objectId);
				viewPool.put(viewData);
				getDatabase().writeObject(viewData);
				createdId = viewData.getId();
				break;
			}
			
			case ObjectType.MODEL_LINKAGE:
			{
				objectId = projectInfo.obtainRealLinkageId(objectId);
				ConceptualModelLinkage cmLinkage = new ConceptualModelLinkage(objectId, -2, -2);
				linkagePool.put(cmLinkage);
				database.writeObject(cmLinkage);
				createdId = cmLinkage.getId();
				break;
			}
			
			case ObjectType.PROJECT_RESOURCE:
			{
				if(objectId == IdAssigner.INVALID_ID)
					objectId = getAnnotationIdAssigner().takeNextId();
				ProjectResource resource = new ProjectResource(objectId);
				resourcePool.put(resource);
				getDatabase().writeObject(resource);
				createdId = resource.getId();
				break;
			}
				
			default:
				throw new RuntimeException("Attempted to create unknown object type: " + objectType);
		}
		
		return createdId;
	}
	
	public void deleteObject(int objectType, int objectId) throws IOException, ParseException
	{
		switch(objectType)
		{
			case ObjectType.THREAT_RATING_CRITERION:
				getThreatRatingFramework().deleteCriterion(objectId);
				getDatabase().deleteObject(objectType, objectId);
				getDatabase().writeThreatRatingFramework(getThreatRatingFramework());
				break;
				
			case ObjectType.THREAT_RATING_VALUE_OPTION:
				getThreatRatingFramework().deleteValueOption(objectId);
				getDatabase().deleteObject(objectType, objectId);
				getDatabase().writeThreatRatingFramework(getThreatRatingFramework());
				break;
				
			case ObjectType.TASK:
				taskPool.remove(objectId);
				getDatabase().deleteObject(objectType, objectId);
				break;
				
			case ObjectType.MODEL_NODE:
				nodePool.remove(objectId);
				getDatabase().deleteObject(objectType, objectId);
				break;
				
			case ObjectType.VIEW_DATA:
				viewPool.remove(objectId);
				getDatabase().deleteObject(objectType, objectId);
				break;
				
			case ObjectType.MODEL_LINKAGE:
				linkagePool.remove(objectId);
				getDatabase().deleteObject(objectType, objectId);
				break;
				
			case ObjectType.PROJECT_RESOURCE:
				resourcePool.remove(objectId);
				getDatabase().deleteObject(objectType, objectId);
				break;
				
			default:
				throw new RuntimeException("Attempted to delete unknown object type: " + objectType);
		}
	}
	
	public void setObjectData(int objectType, int objectId, String fieldTag, String dataValue) throws Exception
	{
		switch(objectType)
		{
			case ObjectType.THREAT_RATING_CRITERION:
				getThreatRatingFramework().setCriterionData(objectId, fieldTag, dataValue);
				getDatabase().writeObject(getThreatRatingFramework().getCriterion(objectId));
				break;
				
			case ObjectType.THREAT_RATING_VALUE_OPTION:
				getThreatRatingFramework().setValueOptionData(objectId, fieldTag, dataValue);
				getDatabase().writeObject(getThreatRatingFramework().getValueOption(objectId));
				break;
			
			case ObjectType.TASK:
				Task task = taskPool.find(objectId);
				task.setData(fieldTag, dataValue);
				getDatabase().writeObject(task);
				break;
				
			case ObjectType.MODEL_NODE:
				ConceptualModelNode node = getNodePool().find(objectId);
				node.setData(fieldTag, dataValue);
				getDatabase().writeNode(node);
				break;
				
			case ObjectType.VIEW_DATA:
				ViewData viewData = getViewPool().find(objectId);
				viewData.setData(fieldTag, dataValue);
				getDatabase().writeObject(viewData);
				break;
				
			case ObjectType.MODEL_LINKAGE:
				ConceptualModelLinkage linkage = getLinkagePool().find(objectId);
				linkage.setData(fieldTag, dataValue);
				getDatabase().writeLinkage(linkage);
				break;
				
			case ObjectType.PROJECT_RESOURCE:
				ProjectResource resource = getResourcePool().find(objectId);
				resource.setData(fieldTag, dataValue);
				getDatabase().writeObject(resource);
				break;
				
			default:
				throw new RuntimeException("Attempted to set data for unknown object type: " + objectType);
		}
	}
	
	public String getObjectData(int objectType, int objectId, String fieldTag)
	{
		switch(objectType)
		{
			case ObjectType.THREAT_RATING_CRITERION:
				return getThreatRatingFramework().getCriterionData(objectId, fieldTag);
				
			case ObjectType.THREAT_RATING_VALUE_OPTION:
				return getThreatRatingFramework().getValueOptionData(objectId, fieldTag);
				
			case ObjectType.TASK:
				return taskPool.find(objectId).getData(fieldTag);
				
			case ObjectType.MODEL_NODE:
				return nodePool.find(objectId).getData(fieldTag);
				
			case ObjectType.VIEW_DATA:
				return viewPool.find(objectId).getData(fieldTag);
				
			case ObjectType.MODEL_LINKAGE:
				return linkagePool.find(objectId).getData(fieldTag);
				
			case ObjectType.PROJECT_RESOURCE:
				return resourcePool.find(objectId).getData(fieldTag);
				
			default:
				throw new RuntimeException("Attempted to get data for unknown object type: " + objectType);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// database
	
	public void createOrOpen(File projectDirectory) throws Exception
	{
		clear();

		if(ProjectServer.isExistingProject(projectDirectory))
			openProject(projectDirectory);
		else
			createProject(projectDirectory);
		
		finishOpening();
	}

	private void createDefaultObjectsIfNeeded() throws Exception
	{
		if(threatRatingFramework.getCriteria().length == 0)
			threatRatingFramework.createDefaultObjects();
	}

	private void openProject(File projectDirectory) throws Exception
	{
		if(ProjectServer.readDataVersion(projectDirectory) > ProjectServer.DATA_VERSION)
			throw new FutureVersionException();

		if(ProjectServer.readDataVersion(projectDirectory) < ProjectServer.DATA_VERSION)
			DataUpgrader.attemptUpgrade(projectDirectory);
		
		if(ProjectServer.readDataVersion(projectDirectory) < ProjectServer.DATA_VERSION)
			throw new OldVersionException();

		ProjectServer db = getDatabase();
		db.open(projectDirectory);
		loadProjectInfo();
		loadThreatRatingFramework();
		loadNodePool();
		loadLinkagePool();
		loadTaskPool();
		loadViewPool();
		loadResourcePool();
		loadDiagram();
	}
	
	private void createProject(File projectDirectory) throws Exception
	{
		getDatabase().create(projectDirectory);
	}
	
	private void loadProjectInfo() throws IOException, ParseException
	{
		getDatabase().readProjectInfo(projectInfo);
	}
	
	private void loadThreatRatingFramework() throws Exception
	{
		getThreatRatingFramework().load();
	}
	
	private void loadNodePool() throws IOException, ParseException
	{
		NodeManifest nodes = getDatabase().readNodeManifest();
		int[] nodeIds = nodes.getAllKeys();
		for(int i = 0; i < nodeIds.length; ++i)
		{
			ConceptualModelNode node = getDatabase().readNode(nodeIds[i]);
			nodePool.put(node);
		}
	}
	
	private void loadLinkagePool() throws IOException, ParseException
	{
		LinkageManifest linkages = getDatabase().readLinkageManifest();
		int[] linkageIds = linkages.getAllKeys();
		for(int i = 0; i < linkageIds.length; ++i)
		{
			ConceptualModelLinkage linkage = getDatabase().readLinkage(linkageIds[i]);
			linkagePool.put(linkage);
		}
	}
	
	private void loadTaskPool() throws Exception
	{
		ObjectManifest manifest = getDatabase().readObjectManifest(ObjectType.TASK);
		int[] ids = manifest.getAllKeys();
		for(int i = 0; i < ids.length; ++i)
		{
			Task task = (Task)getDatabase().readObject(ObjectType.TASK, ids[i]);
			taskPool.put(task);
		}
	}
	
	private void loadViewPool() throws Exception
	{
		ObjectManifest manifest = getDatabase().readObjectManifest(ObjectType.VIEW_DATA);
		int[] ids = manifest.getAllKeys();
		for(int i = 0; i < ids.length; ++i)
		{
			ViewData viewData = (ViewData)getDatabase().readObject(ObjectType.VIEW_DATA, ids[i]);
			viewPool.put(viewData);
		}
	}
	
	private void loadResourcePool() throws Exception
	{
		ObjectManifest manifest = getDatabase().readObjectManifest(ObjectType.PROJECT_RESOURCE);
		int[] ids = manifest.getAllKeys();
		for(int i = 0; i < ids.length; ++i)
		{
			ProjectResource resource = (ProjectResource)getDatabase().readObject(ObjectType.PROJECT_RESOURCE, ids[i]);
			resourcePool.put(resource);
		}
	}
	
	private void loadDiagram() throws Exception
	{
		getDatabase().readDiagram(getDiagramModel());
	}

	protected void finishOpening() throws Exception
	{
		createDefaultObjectsIfNeeded();

		database.writeVersion();
		String currentView = getCurrentView();
		if(currentView.length() == 0)
			setCurrentView(DiagramView.getViewName());
		fireSwitchToView(getCurrentView());
		
		fireCommandExecuted(new CommandDoNothing());
	}
	
	public String getName()
	{
		if(isOpen())
			return getDatabase().getName();
		return EAM.text("[No Project]");
	}

	public boolean isOpen()
	{
		return getDatabase().isOpen();
	}
	
	public void close() throws Exception
	{
		if(!isOpen())
			return;
		
		try
		{
			getDatabase().close();
		}
		catch (IOException e)
		{
			EAM.logException(e);
		}
		setCurrentView(NoProjectView.getViewName());
		fireSwitchToView(getCurrentView());
	}
	
	static public boolean isValidProjectName(String candidate)
	{
		if(candidate.length() > 32)
			return false;
		
		char[] asArray = candidate.toCharArray();
		for(int i = 0; i < candidate.length(); ++i)
		{
			char c = asArray[i];
			if(c >= 128)
				continue;
			if(Character.isLetterOrDigit(c))
				continue;
			if(c == ' ' || c == '.' || c == '-')
				continue;
			
			return false;
		}
		return true;
	}

	public void applySnapToOldUnsnappedCommands(Vector commands)
	{
		for(int i=0; i < commands.size(); ++i)
		{
			Command command = (Command)commands.get(i);
			if(command instanceof CommandDiagramMove)
			{
				CommandDiagramMove unsnapped = (CommandDiagramMove)command;
				Point unsnappedPoint = new Point(unsnapped.getDeltaX(), unsnapped.getDeltaY());
				Point snappedPoint = getSnapped(unsnappedPoint);
				if(!snappedPoint.equals(unsnappedPoint))
				{
					EAM.logDebug("Adjusting " + unsnappedPoint.toString() + " to " + snappedPoint.toString());
					CommandDiagramMove snapped = new CommandDiagramMove(snappedPoint.x, snappedPoint.y, unsnapped.getIds());
					commands.set(i, snapped);
				}
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// data values

	public String getDataValue(String fieldName)
	{
		return projectInfo.getProjectData().optString(fieldName, "");
	}
	
	public void setDataValue(String fieldName, String fieldData)
	{
		EAM.logVerbose("BaseProject.setDataValue to: " + fieldData);
		projectInfo.getProjectData().put(fieldName, fieldData);
	}

	/////////////////////////////////////////////////////////////////////////////////
	// command execution

	public void executeCommand(Command command) throws CommandFailedException
	{
		try 
		{
			command.execute(this);
		} 
		catch (CommandFailedException e) 
		{
			fireCommandFailed(command, e);
			throw(e);
		}
		recordCommand(command);
	}
	
	public void replayCommand(Command command) throws CommandFailedException
	{
		command.execute(this);
		fireCommandExecuted(command);
	}
	
	public void recordCommand(Command command)
	{
		try
		{
			database.appendCommand(command);
			database.writeProjectInfo(projectInfo);
			database.writeDiagram(getDiagramModel());
		}
		catch (IOException e)
		{
			EAM.logException(e);
		}
		fireCommandExecuted(command);
	}

	public void addCommandExecutedListener(CommandExecutedListener listener)
	{
		commandExecutedListeners.add(listener);
	}
	
	public void removeCommandExecutedListener(CommandExecutedListener listener)
	{
		commandExecutedListeners.remove(listener);
	}

	void fireCommandExecuted(Command command)
	{
		CommandExecutedEvent event = new CommandExecutedEvent(command);
		for(int i=0; i < commandExecutedListeners.size(); ++i)
		{
			CommandExecutedListener listener = (CommandExecutedListener)commandExecutedListeners.get(i);
			listener.commandExecuted(event);
		}
	}
	
	void fireCommandUndone(Command command)
	{
		CommandExecutedEvent event = new CommandExecutedEvent(command);
		for(int i=0; i < commandExecutedListeners.size(); ++i)
		{
			CommandExecutedListener listener = (CommandExecutedListener)commandExecutedListeners.get(i);
			listener.commandUndone(event);
		}
	}
	
	void fireCommandFailed(Command command, CommandFailedException e)
	{
		for(int i=0; i < commandExecutedListeners.size(); ++i)
		{
			CommandExecutedListener listener = (CommandExecutedListener)commandExecutedListeners.get(i);
			listener.commandFailed(command, e);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// views
	
	public void switchToView(String viewName) throws CommandFailedException
	{
		setCurrentView(viewName);
		try
		{
			fireSwitchToView(viewName);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	public void addViewChangeListener(ViewChangeListener listener)
	{
		viewChangeListeners.add(listener);
	}
	
	void fireSwitchToView(String viewName) throws Exception
	{
		for(int i=0; i < viewChangeListeners.size(); ++i)
		{
			ViewChangeListener listener = (ViewChangeListener)viewChangeListeners.get(i);
			listener.switchToView(viewName);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// interview view
	
	public InterviewStepModel getCurrentInterviewStep()
	{
		return getInterviewModel().getCurrentStep();
	}
	
	public String getCurrentInterviewStepName()
	{
		return getInterviewModel().getCurrentStepName();
	}
	
	public void setCurrentInterviewStepName(String newStepName)
	{
		getInterviewModel().setCurrentStepName(newStepName);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// diagram view
	
	public void pasteNodesAndLinksIntoProject(TransferableEamList list, Point startPoint) throws Exception
	{
		executeCommand(new CommandBeginTransaction());
		NodeDataHelper dataHelper = new NodeDataHelper(getDiagramModel().getAllNodes());
		pasteNodesIntoProject(list, startPoint, dataHelper);
		pasteLinksIntoProject(list, dataHelper);
		executeCommand(new CommandEndTransaction());
	}
	
	public void pasteNodesOnlyIntoProject(TransferableEamList list, Point startPoint) throws Exception
	{
		executeCommand(new CommandBeginTransaction());
		NodeDataHelper dataHelper = new NodeDataHelper(getDiagramModel().getAllNodes());
		pasteNodesIntoProject(list, startPoint, dataHelper);
		executeCommand(new CommandEndTransaction());
	}

	private void pasteNodesIntoProject(TransferableEamList list, Point startPoint, NodeDataHelper dataHelper) throws Exception 
	{
		NodeDataMap[] nodes = list.getNodeDataCells();
		for (int i = 0; i < nodes.length; i++) 
		{
			NodeDataMap nodeData = nodes[i];
			int originalNodeId = nodeData.getInt(DiagramNode.TAG_ID);
			
			NodeType type = NodeDataMap.convertIntToNodeType(nodeData.getInt(DiagramNode.TAG_NODE_TYPE)); 
			int newNodeId = createNode(type);
			dataHelper.setNewId(originalNodeId, newNodeId);
			dataHelper.setOriginalLocation(originalNodeId, nodeData.getPoint(DiagramNode.TAG_LOCATION));
			
			CommandSetNodeName newNodeLabel = new CommandSetNodeName(newNodeId, nodeData.getString(DiagramNode.TAG_VISIBLE_LABEL));
			executeCommand(newNodeLabel);
			Logging.logDebug("Paste Node: " + newNodeId +":" + nodeData.getString(DiagramNode.TAG_VISIBLE_LABEL));
		}
		
		for (int i = 0; i < nodes.length; i++) 
		{
			NodeDataMap nodeData = nodes[i];
			Point newNodeLocation = dataHelper.getNewLocation(nodeData.getInt(DiagramNode.TAG_ID), startPoint);
			newNodeLocation = getSnapped(newNodeLocation);
			int newNodeId = dataHelper.getNewId(nodeData.getInt(DiagramNode.TAG_ID));
			CommandDiagramMove move = new CommandDiagramMove(newNodeLocation.x, newNodeLocation.y, new int[]{newNodeId});
			executeCommand(move);
		}
	}
	
	private void pasteLinksIntoProject(TransferableEamList list, NodeDataHelper dataHelper) throws CommandFailedException 
	{
		LinkageDataMap[] links = list.getLinkageDataCells();
		for (int i = 0; i < links.length; i++) 
		{
			LinkageDataMap linkageData = links[i];
			
			int newFromId = dataHelper.getNewId(linkageData.getFromId());
			int newToId = dataHelper.getNewId(linkageData.getToId());
			if(newFromId == IdAssigner.INVALID_ID || newToId == IdAssigner.INVALID_ID)
			{
				Logging.logWarning("Unable to Paste Link : from OriginalId:" + linkageData.getFromId() + " to OriginalId:" + linkageData.getToId()+" node deleted?");	
				continue;
			}
			CommandLinkNodes link = new CommandLinkNodes(newFromId, newToId);
			executeCommand(link);
			Logging.logDebug("Paste Link : " + link.getLinkageId() + " from:" + link.getFromId() + " to:" + link.getToId());
		}
	}

	public int createNode(NodeType nodeType) throws Exception
	{
		CommandInsertNode commandInsertNode = new CommandInsertNode(nodeType);
		executeCommand(commandInsertNode);
		int id = commandInsertNode.getId();
		Command[] commandsToAddToView = getCurrentViewData().buildCommandsToAddNode(id);
		for(int i = 0; i < commandsToAddToView.length; ++i)
			executeCommand(commandsToAddToView[i]);
		return id;
	}

	public NodeType deleteNode(int idToDelete) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramNode nodeToDelete = model.getNodeById(idToDelete);
		NodeType nodeType = nodeToDelete.getNodeType();
		model.deleteNode(nodeToDelete);

		database.deleteNode(idToDelete);
		nodePool.remove(idToDelete);
		
		return nodeType; 
	}

	public int insertNodeAtId(NodeType typeToInsert, int requestedId) throws Exception
	{
		int realId = projectInfo.obtainRealNodeId(requestedId);
		ConceptualModelNode cmObject = ConceptualModelNode.createConceptualModelObject(realId, typeToInsert);
		nodePool.put(cmObject);
		writeNode(realId);
		
		DiagramModel model = getDiagramModel();
		DiagramNode node = model.createNode(realId);
		
		int idThatWasInserted = node.getId();
		return idThatWasInserted;
	}
	
	public void deleteLinkage(int idToDelete) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramLinkage linkageToDelete = model.getLinkageById(idToDelete);
		model.deleteLinkage(linkageToDelete);

		database.deleteLinkage(idToDelete);
		linkagePool.remove(idToDelete);
	}

	public int insertLinkageAtId(int requestedLinkageId, int linkFromId, int linkToId) throws Exception
	{
		int createdId = createObject(ObjectType.MODEL_LINKAGE, requestedLinkageId);
		ConceptualModelLinkage cmLinkage = getLinkagePool().find(createdId);
		cmLinkage.setFromId(linkFromId);
		cmLinkage.setToId(linkToId);
		getDatabase().writeLinkage(cmLinkage);
		DiagramModel model = getDiagramModel();
		DiagramLinkage linkage = model.createLinkage(cmLinkage);
		return linkage.getId();
	}
	
	public void setNodeName(int nodeId, String desiredName, String expectedName) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramNode node = model.getNodeById(nodeId);
		node.setLabel(desiredName);
		Logging.logVerbose("Updating name: "+desiredName);
		model.updateCell(node);
		
		writeNode(nodeId);
	}

	public void setNodeComment(int nodeId, String desiredComment, String expectedComment) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramNode node = model.getNodeById(nodeId);
		node.setComment(desiredComment);
		Logging.logVerbose("Updating comment: "+desiredComment);
		model.updateCell(node);
		
		writeNode(nodeId);
	}

	public void setFactorType(int nodeId, NodeType desiredType) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramNode node = model.getNodeById(nodeId);
		
		node.setNodeType(desiredType);
		Logging.logVerbose("SetFactorType:" + desiredType);
		model.updateCell(node);

		writeNode(nodeId);
	}
	
	public void setIndicator(int nodeId, IndicatorId desiredIndicatorId) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramNode node = model.getNodeById(nodeId);
		
		node.setIndicator(desiredIndicatorId);
		Logging.logVerbose("SetIndicator:" + desiredIndicatorId);
		model.updateCell(node);
		
		writeNode(nodeId);
	}
	
	public void setObjectives(int nodeId, ObjectiveIds desiredObjectives) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramNode node = model.getNodeById(nodeId);

		node.setObjectives(desiredObjectives);
		Logging.logVerbose("SetObjectives:" + desiredObjectives);
		model.updateCell(node);
	
		writeNode(nodeId);
	}
	
	public void setGoals(int nodeId, GoalIds desiredGoals) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramNode node = model.getNodeById(nodeId);

		node.setGoals(desiredGoals);
		Logging.logVerbose("Updating Goals:" + desiredGoals);
		model.updateCell(node);
		
		writeNode(nodeId);
	}

	protected void writeNode(int nodeId) throws IOException, ParseException
	{
		ConceptualModelNode cmNode = getNodePool().find(nodeId);
		database.writeNode(cmNode);
	}

	public void moveNodes(int deltaX, int deltaY, int[] ids) throws Exception 
	{
		DiagramModel model = getDiagramModel();
		model.moveNodes(deltaX, deltaY, ids);
	}
	
	public void nodesWereMovedOrResized(int deltaX, int deltaY, int[] ids)
	{
		DiagramModel model = getDiagramModel();
		model.nodesWereMoved(ids);

		Vector commands = new Vector();
		Vector movedNodes = new Vector();
		for(int i = 0 ; i < ids.length; ++i)
		{
			try 
			{
				DiagramNode node = model.getNodeById(ids[i]);
				if(node.hasMoved())
					movedNodes.add(node);
				
				if(node.sizeHasChanged())
					commands.add(buildResizeCommand(node));
			} 
			catch (Exception e) 
			{
				EAM.logException(e);
			}
		}
		
		if(movedNodes.size() > 0)
		{
			int[] idsActuallyMoved = new int[movedNodes.size()];
			for(int i = 0; i < movedNodes.size(); ++i)
			{
				DiagramNode node = (DiagramNode)movedNodes.get(i);
				idsActuallyMoved[i] = node.getId();
			}
			
			commands.add(new CommandDiagramMove(deltaX, deltaY, idsActuallyMoved));			
		}
		
		if(commands.size() > 0)
		{
			recordCommand(new CommandBeginTransaction());
			for(int i=0; i < commands.size(); ++i)
				recordCommand((Command)commands.get(i));
			recordCommand(new CommandEndTransaction());
		}
	}
	
	private Command buildResizeCommand(DiagramNode node)
	{
		return new CommandSetNodeSize(node.getId(), node.getSize(), node.getPreviousSize());
	}
	
	public void setSelectionModel(GraphSelectionModel selectionModelToUse)
	{
		selectionModel = selectionModelToUse;
	}
	
	public EAMGraphCell[] getSelectedAndRelatedCells()
	{
		Object[] selectedCells = selectionModel.getSelectionCells();
		Vector cellVector = getAllSelectedCellsWithLinkages(selectedCells);
		return (EAMGraphCell[])cellVector.toArray(new EAMGraphCell[0]);
	}

	public Vector getAllSelectedCellsWithLinkages(Object[] selectedCells) 
	{
		DiagramModel model = getDiagramModel();
		Vector selectedCellsWithLinkages = new Vector();
		for(int i=0; i < selectedCells.length; ++i)
		{
			EAMGraphCell cell = (EAMGraphCell)selectedCells[i];
			if(cell.isLinkage())
			{
				if(!selectedCellsWithLinkages.contains(cell))
					selectedCellsWithLinkages.add(cell);
			}
			else if(cell.isNode())
			{
				Set linkages = model.getLinkages((DiagramNode)cell);
				for (Iterator iter = linkages.iterator(); iter.hasNext();) 
				{
					EAMGraphCell link = (EAMGraphCell) iter.next();
					if(!selectedCellsWithLinkages.contains(link))
						selectedCellsWithLinkages.add(link);
				}
				selectedCellsWithLinkages.add(cell);
			}
		}
		return selectedCellsWithLinkages;
	}
	
	public boolean isLinked(int nodeId1, int nodeId2)
	{
		return getLinkagePool().hasLinkage(nodeId1, nodeId2);
	}

	public EAMGraphCell[] getOnlySelectedCells()
	{
		Object[] rawCells = selectionModel.getSelectionCells();
		EAMGraphCell[] cells = new EAMGraphCell[rawCells.length];
		for(int i=0; i < cells.length; ++i)
			cells[i] = (EAMGraphCell)rawCells[i];
		return cells;
	}
	
	public DiagramNode[] getOnlySelectedNodes()
	{
		if(selectionModel == null)
			return new DiagramNode[0];
		
		Object[] rawCells = selectionModel.getSelectionCells();
		return getOnlySelectedNodes(rawCells);
	}

	public DiagramNode[] getOnlySelectedNodes(Object[] allSelectedCells)
	{
		Vector nodes = new Vector();
		for(int i=0; i < allSelectedCells.length; ++i)
		{
			if(((EAMGraphCell)allSelectedCells[i]).isNode())
				nodes.add(allSelectedCells[i]);
		}
		return (DiagramNode[])nodes.toArray(new DiagramNode[0]);
	}

	
	public int getGridSize()
	{
		return DEFAULT_GRID_SIZE;
	}
	
	public Point getSnapped(Point point)
	{
		int gridSize = getGridSize();
		return new Point(roundTo(point.x, gridSize), roundTo(point.y, gridSize));
	}
	
	int roundTo(int valueToRound, int incrementToRoundTo)
	{
		int sign = 1;
		if(valueToRound < 0)
			sign = -1;
		valueToRound = Math.abs(valueToRound);
		
		int half = incrementToRoundTo / 2;
		valueToRound += half;
		valueToRound -= (valueToRound % incrementToRoundTo);
		return valueToRound * sign;
	}

	/////////////////////////////////////////////////////////////////////////////////
	// undo/redo
	
	public void undo() throws CommandFailedException
	{
		Command cmd = getCommandToUndo();
		cmd.undo(this);
		fireCommandUndone(cmd);
	}
	
	public void redo() throws CommandFailedException
	{
		replayCommand(getCommandToRedo());
	}

	public Command getCommandToUndo() throws NothingToUndoException

	{
		int indexToUndo = getIndexToUndo();
		if(indexToUndo < 0)
			throw new NothingToUndoException();
		return database.getCommandAt(indexToUndo);
	}
	
	public Command getCommandToRedo() throws NothingToRedoException
	{
		int indexToRedo = getIndexToRedo();
		if(indexToRedo < 0)
			throw new NothingToRedoException();
		return database.getCommandAt(indexToRedo);
	}
	
	public int getIndexToUndo()
	{
		UndoRedoState state = new UndoRedoState(database);
		return state.getIndexToUndo();
	}

	public int getIndexToRedo()
	{
		UndoRedoState state = new UndoRedoState(database);
		return state.getIndexToRedo();
	}
	


	public static final int DEFAULT_GRID_SIZE = 15;

	ProjectInfo projectInfo;

	NodePool nodePool;
	LinkagePool linkagePool;
	GoalPool goalPool;
	ObjectivePool objectivePool;
	TaskPool taskPool;
	ViewPool viewPool;
	ResourcePool resourcePool;
	ThreatRatingFramework threatRatingFramework;
	
	ProjectServer database;
	InterviewModel interviewModel;
	DiagramModel diagramModel;

	Vector commandExecutedListeners;
	Vector viewChangeListeners;
	
	LayerManager layerManager;
	GraphSelectionModel selectionModel;

}

