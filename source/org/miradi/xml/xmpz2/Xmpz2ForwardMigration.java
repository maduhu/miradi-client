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

package org.miradi.xml.xmpz2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.martus.util.inputstreamwithseek.StringInputStreamWithSeek;
import org.miradi.exceptions.XmlVersionTooOldException;
import org.miradi.migrations.forward.MigrationTo10;
import org.miradi.migrations.forward.MigrationTo11;
import org.miradi.migrations.forward.MigrationTo19;
import org.miradi.migrations.forward.MigrationTo20;
import org.miradi.questions.DayColumnsVisibilityQuestion;
import org.miradi.utils.BiDirectionalHashMap;
import org.miradi.utils.HtmlUtilities;
import org.miradi.xml.AbstractXmlImporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Xmpz2ForwardMigration
{
	public Xmpz2MigrationResult migrate(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		// note that this xmpz2 migration only handles structural migrations required to ensure that the resulting xml validates according to the current schema (rnc)
		// project migrations are handled subsequently to this as part of the project opening processes (via MigrationManager)

		Document document = convertToDocument(projectAsInputStream);
		Element rootElement = document.getDocumentElement();
		int xmpz2DocumentSchemaVersion = getXmpz2DocumentSchemaVersion(rootElement);
		boolean schemaVersionWasUpdated = updateXmpz2SchemaVersionToCurrentVersion(rootElement);
		removeLegacyTncFields(rootElement);
		removeHumanWellbeingTargetCalculatedThreatRatingElement(rootElement);
		renameTncFields(document);
		renameLeaderResourceFields(document);
		renameWhoWhenAssignedFields(document);
		addDayColumnsVisibilityField(document);
		final String migratedXmlAsString = HtmlUtilities.toXmlString(document);

		return new Xmpz2MigrationResult(new StringInputStreamWithSeek(migratedXmlAsString), schemaVersionWasUpdated, xmpz2DocumentSchemaVersion);
	}

	private void renameTncFields(Document document) throws Exception
	{
		Element rootElement = document.getDocumentElement();
		Node tncProjectDataNode = findNode(rootElement.getChildNodes(), Xmpz2XmlConstants.TNC_PROJECT_DATA);
		if (tncProjectDataNode != null)
		{	
			BiDirectionalHashMap legacyToNewTncFieldNamesMap = createLegacyTncToNewFieldNamesMap();
			renameElements(document, tncProjectDataNode, legacyToNewTncFieldNamesMap);
		}
	}
	
	private BiDirectionalHashMap createLegacyTncToNewFieldNamesMap()
	{
		BiDirectionalHashMap oldToNewTagMap = new BiDirectionalHashMap();
		oldToNewTagMap.put(Xmpz2XmlConstants.TNC_PROJECT_DATA + MigrationTo10.LEGACY_TAG_MAKING_THE_CASE, Xmpz2XmlConstants.TNC_PROJECT_DATA + MigrationTo10.TAG_OVERALL_PROJECT_GOAL);
		oldToNewTagMap.put(Xmpz2XmlConstants.TNC_PROJECT_DATA + MigrationTo10.LEGACY_TAG_CAPACITY_AND_FUNDING, Xmpz2XmlConstants.TNC_PROJECT_DATA + MigrationTo10.TAG_FINANCIAL_PLAN);
		
		return oldToNewTagMap;
	}

	private void renameElements(Document document, Node parentNode, BiDirectionalHashMap fromToNameMap) throws Exception
	{
		final String alias = getNameSpaceAliasName(document.getDocumentElement());
		HashSet<String> keys = fromToNameMap.getKeys();
		for(String fromName : keys)
		{
			Node childNode = findNode(parentNode, fromName);
			if (childNode == null)
				continue;

			String textToTransferToNewNode = childNode.getTextContent();
			parentNode.removeChild(childNode);
			final String toName = fromToNameMap.getValue(fromName);
			Node newNode = document.createElement(alias + COLON +  toName);
			newNode.setTextContent(textToTransferToNewNode);
			parentNode.appendChild(newNode);
		}
	}

	private void renameLeaderResourceFields(Document document) throws Exception
	{
		Element rootElement = document.getDocumentElement();

		Node strategyPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.STRATEGY));
		BiDirectionalHashMap oldToNewTagMap = createLeaderResourceToNewFieldNamesMap(Xmpz2XmlConstants.STRATEGY);
		renameLeaderResourceFields(document, strategyPool, oldToNewTagMap);

		Node taskPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.TASK));
		oldToNewTagMap = createLeaderResourceToNewFieldNamesMap(Xmpz2XmlConstants.TASK);
		renameLeaderResourceFields(document, taskPool, oldToNewTagMap);

		Node indicatorPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.INDICATOR));
		oldToNewTagMap = createLeaderResourceToNewFieldNamesMap(Xmpz2XmlConstants.INDICATOR);
		renameLeaderResourceFields(document, indicatorPool, oldToNewTagMap);
	}

	private void renameLeaderResourceFields(Document document, Node objectPool, BiDirectionalHashMap oldToNewTagMap) throws Exception
	{
		if (objectPool == null)
			return;

		NodeList children = objectPool.getChildNodes();
		for (int index = 0; index < children.getLength(); ++index)
		{
			Node childNode = children.item(index);
			if (childNode != null)
				replaceElements(document, childNode, oldToNewTagMap);
		}
	}

	private BiDirectionalHashMap createLeaderResourceToNewFieldNamesMap(String objectName)
	{
		BiDirectionalHashMap oldToNewTagMap = new BiDirectionalHashMap();
		oldToNewTagMap.put(objectName + MigrationTo19.LEGACY_TAG_LEADER_RESOURCE + Xmpz2XmlConstants.ID, objectName + MigrationTo19.TAG_ASSIGNED_LEADER_RESOURCE + Xmpz2XmlConstants.ID);

		return oldToNewTagMap;
	}

	private void renameWhoWhenAssignedFields(Document document)
	{
		Element rootElement = document.getDocumentElement();

		Node planningViewConfigurationPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.OBJECT_TREE_TABLE_CONFIGURATION));

		if (planningViewConfigurationPool == null)
			return;

		NodeList planningViewConfigurationNodes = planningViewConfigurationPool.getChildNodes();
		for (int index = 0; index < planningViewConfigurationNodes.getLength(); ++index)
		{
			Node planningViewConfiguration = planningViewConfigurationNodes.item(index);
			if (planningViewConfiguration != null)
			{
				Node columnNamesContainer = findNode(planningViewConfiguration.getChildNodes(), Xmpz2XmlConstants.OBJECT_TREE_TABLE_CONFIGURATION + Xmpz2XmlConstants.COLUMN_CONFIGURATION_CODES + Xmpz2XmlConstants.CONTAINER_ELEMENT_TAG);
				if (columnNamesContainer != null)
				{
					NodeList codeList = columnNamesContainer.getChildNodes();
					for (int i = 0; i < codeList.getLength(); ++i)
					{
						Node code = codeList.item(i);
						if (code.getTextContent().equals(MigrationTo20.LEGACY_READABLE_ASSIGNED_WHEN_TOTAL_CODE))
							code.setTextContent(MigrationTo20.READABLE_TIMEFRAME_TOTAL_CODE);
					}
				}
			}
		}
	}

	private void addDayColumnsVisibilityField(Document document) throws Exception
	{
		Element rootElement = document.getDocumentElement();

		Node projectPlanningElement = findNode(rootElement.getChildNodes(), Xmpz2XmlConstants.PROJECT_SUMMARY_PLANNING);

		if (projectPlanningElement == null)
			return;

		Node dayColumnsVisibilityElement = findNode(projectPlanningElement.getChildNodes(), Xmpz2XmlConstants.PROJECT_SUMMARY_PLANNING + Xmpz2XmlConstants.DAY_COLUMNS_VISIBILITY);
		if (dayColumnsVisibilityElement == null)
		{
			final String alias = getNameSpaceAliasName(document.getDocumentElement());
			Node newNode = document.createElement(alias + COLON +  Xmpz2XmlConstants.PROJECT_SUMMARY_PLANNING + Xmpz2XmlConstants.DAY_COLUMNS_VISIBILITY);
			newNode.setTextContent(DayColumnsVisibilityQuestion.SHOW_DAY_COLUMNS_CODE_READABLE);
			projectPlanningElement.appendChild(newNode);
		}
	}

	private void replaceElements(Document document, Node parentNode, BiDirectionalHashMap fromToNameMap) throws Exception
	{
		final String alias = getNameSpaceAliasName(document.getDocumentElement());
		HashSet<String> keys = fromToNameMap.getKeys();
		for(String fromName : keys)
		{
			Node childNode = findNode(parentNode, fromName);
			if (childNode == null)
				continue;

			NodeList childNodeList = childNode.getChildNodes();
			parentNode.removeChild(childNode);
			final String toName = fromToNameMap.getValue(fromName);
			Node newNode = document.createElement(alias + COLON +  toName);
			for (int index = 0; index < childNodeList.getLength(); ++index)
			{
				Node grandchildNode = childNodeList.item(index);
				newNode.appendChild(grandchildNode);
			}
			parentNode.appendChild(newNode);
		}
	}

	private Node findNode(Node parentNode, final String elementNameWithoutAlias)
	{
		return findNode(parentNode.getChildNodes(), elementNameWithoutAlias);
	}

	private void removeHumanWellbeingTargetCalculatedThreatRatingElement(Element rootElement)
	{
		Node humanWellbeingTargetPool = findNode(rootElement.getChildNodes(), Xmpz2XmlWriter.createPoolElementName(Xmpz2XmlConstants.HUMAN_WELFARE_TARGET));
		if (humanWellbeingTargetPool == null)
			return;
		
		NodeList children = humanWellbeingTargetPool.getChildNodes();
		for (int index = 0; index < children.getLength(); ++index)
		{
			Node humanWellbeingTarget = children.item(index);
			if (humanWellbeingTarget == null)
				continue;
			
			removeChildren(humanWellbeingTarget, new String[]{Xmpz2XmlConstants.HUMAN_WELFARE_TARGET + Xmpz2XmlConstants.CALCULATED_THREAT_RATING,});
		}
	}

	private void removeLegacyTncFields(Element rootElement)
	{
		Node tncProjectDataNode = findNode(rootElement.getChildNodes(), Xmpz2XmlConstants.TNC_PROJECT_DATA);
		if (tncProjectDataNode != null)
			removeLegacyTncChildren(tncProjectDataNode);
	}

	private Node findNode(NodeList children, final String elementNameWithoutAlias)
	{
		for (int index = 0; index < children.getLength(); ++index)
		{
			Node childNode = children.item(index);
			if (childNode.getNodeName().endsWith(elementNameWithoutAlias))
				return childNode;
		}
		
		return null;
	}

	private void removeLegacyTncChildren(Node tncProjectDataNode)
	{
		String[] elementNamesToRemove = new String[]{createLegacyTncOrganizationalPrioritiesElementName(), createLegacyTncProjectPlaceTypesElementName(), };
		removeChildren(tncProjectDataNode, elementNamesToRemove);
	}
	
	private void removeChildren(Node nodeToRemoveElementsFrom, String[] elementNames)
	{
		NodeList children = nodeToRemoveElementsFrom.getChildNodes();
		Vector<Node> childrenToRemove = new Vector<Node>();
		for(String elementNameToRemove : elementNames)
		{
			final Node nodeToRemove = findNode(children, elementNameToRemove);
			childrenToRemove.add(nodeToRemove);
		}
		
		childrenToRemove.removeAll(Collections.singleton(null));
		for(Node childNodeToRemove : childrenToRemove)
		{
			nodeToRemoveElementsFrom.removeChild(childNodeToRemove);
		}
	}

	public static String createLegacyTncProjectPlaceTypesElementName()
	{
		return Xmpz2XmlConstants.TNC_PROJECT_DATA + "TNC" +  MigrationTo11.LEGACY_TAG_TNC_PROJECT_TYPES + Xmpz2XmlConstants.CONTAINER_ELEMENT_TAG;
	}

	public static String createLegacyTncOrganizationalPrioritiesElementName()
	{
		return Xmpz2XmlConstants.TNC_PROJECT_DATA + "TNC" + MigrationTo11.LEGACY_TAG_TNC_ORGANIZATIONAL_PRIORITIES + Xmpz2XmlConstants.CONTAINER_ELEMENT_TAG;
	}

	private boolean updateXmpz2SchemaVersionToCurrentVersion(Element rootElement) throws Exception
	{
		boolean schemaVersionWasUpdated = false;
		int readInSchemaVersion = getXmpz2DocumentSchemaVersion(rootElement);
		if (readInSchemaVersion < LOWEST_SCHEMA_VERSION)
		{
			throw new XmlVersionTooOldException(Integer.toString(LOWEST_SCHEMA_VERSION), Integer.toString(readInSchemaVersion));
		}
		
		if (readInSchemaVersion <  Integer.parseInt(NAME_SPACE_VERSION))
		{
			setNameSpaceVersion(rootElement, NAME_SPACE_VERSION);
			schemaVersionWasUpdated = true;
		}

		return schemaVersionWasUpdated;
	}

	private int getXmpz2DocumentSchemaVersion(Element rootElement) throws Exception
	{
		final String currentNamespace = getNameSpace(rootElement);
		String readInSchemaVersionAsString = AbstractXmlImporter.getSchemaVersionToImport(currentNamespace);
		return Integer.parseInt(readInSchemaVersionAsString);
	}

	public static void setNameSpaceVersion(Element rootElement, String newNameSpaceVersion) throws Exception
	{
		final String attributeName = getNamespaceAttributeName(rootElement);
		final String nameSpaceWithVersion = PARTIAL_NAME_SPACE + newNameSpaceVersion;
		rootElement.setAttribute(attributeName, nameSpaceWithVersion);
	}

	private static String getNameSpace(Element rootElement) throws Exception
	{
		return rootElement.getAttribute(getNamespaceAttributeName(rootElement));
	}

	private static String getNamespaceAttributeName(Element rootElement) throws Exception
	{
		final String alias = getNameSpaceAliasName(rootElement);
		return  XMLNS + COLON + alias;
	}
	
	private static String getNameSpaceAliasName(Element rootElement) throws Exception
	{
		NamedNodeMap attributes = rootElement.getAttributes();
		for (int index = 0; index < attributes.getLength(); ++index)
		{
			final Node attribute = attributes.item(index);
			final String nodeName = attribute.getNodeName();
			if (nodeName.startsWith(Xmpz2XmlConstants.XMLNS))
				return extractAlias(nodeName);
		}
		throw new Exception("Could not find xmlns attribute!");
	}

	private static String extractAlias(String namespaceAttributeName)
	{
		final String aliasNameLeftOver = namespaceAttributeName.replaceAll(Xmpz2XmlConstants.XMLNS + Xmpz2XmlConstants.COLON, "");
		
		return aliasNameLeftOver;
	}

	public static Document convertToDocument(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		InputSource inputSource = new InputSource(projectAsInputStream);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputSource);
		
		return document;
	}
	
	private static final int LOWEST_SCHEMA_VERSION = Xmpz2XmlConstants.LOWEST_SCHEMA_VERSION;
	private static final String NAME_SPACE_VERSION = Xmpz2XmlConstants.NAME_SPACE_VERSION;
	private static final String XMLNS = "xmlns";
	private static final String COLON = ":";
	private static final String PARTIAL_NAME_SPACE = "http://xml.miradi.org/schema/ConservationProject/";
}
