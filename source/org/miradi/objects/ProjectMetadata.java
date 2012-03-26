/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

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
package org.miradi.objects;

import java.util.Vector;

import org.martus.util.MultiCalendar;
import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.StringRefMap;
import org.miradi.project.ObjectManager;
import org.miradi.questions.BudgetTimePeriodQuestion;
import org.miradi.questions.CountriesQuestion;
import org.miradi.questions.CurrencyTypeQuestion;
import org.miradi.questions.DiagramObjectDataInclusionQuestion;
import org.miradi.questions.FiscalYearStartQuestion;
import org.miradi.questions.FontFamiliyQuestion;
import org.miradi.questions.FontSizeQuestion;
import org.miradi.questions.LanguageQuestion;
import org.miradi.questions.PlanningTreeTargetPositionQuestion;
import org.miradi.questions.ProtectedAreaCategoryQuestion;
import org.miradi.questions.QuarterColumnsVisibilityQuestion;
import org.miradi.questions.TargetModeQuestion;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.questions.TncFreshwaterEcoRegionQuestion;
import org.miradi.questions.TncMarineEcoRegionQuestion;
import org.miradi.questions.TncOperatingUnitsQuestion;
import org.miradi.questions.TncTerrestrialEcoRegionQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.DoubleUtilities;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.OptionalDouble;

public class ProjectMetadata extends BaseObject
{
	public ProjectMetadata(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse);
		clear();
	}

	public ProjectMetadata(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json);
	}
	
	@Override
	public int getType()
	{
		return getObjectType();
	}

	@Override
	public String getTypeName()
	{
		return OBJECT_NAME;
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	public static int getObjectType()
	{
		return ObjectType.PROJECT_METADATA;
	}
	
	@Override
	public String getPseudoData(String fieldTag)
	{
		if(fieldTag.equals(PSEUDO_TAG_PROJECT_FILENAME))
			return objectManager.getFileName();
		
		if(fieldTag.equals(PSEUDO_TAG_ALL_THREAT_CLASSIFICATIONS))
			return getStandardClassifications();
		
		return super.getPseudoData(fieldTag);
	}
	
	private String getStandardClassifications()
	{
		Vector<Cause> allThreats = getProject().getCausePool().getDirectThreatsAsVector();
		CodeList threatClassificationCodes = new CodeList();
		for(Cause threat : allThreats)
		{
			String code = threat.getData(Cause.TAG_TAXONOMY_CODE);
			if (code.length() > 0)
				threatClassificationCodes.add(code);
		}
		
		return threatClassificationCodes.withoutDuplicates().toString();
	}

	public ORefList getAllDiagramObjectRefs()
	{
		return objectManager.getAllDiagramObjectRefs(); 
	}

	public String getCurrentWizardScreenName()
	{
		return getData(TAG_CURRENT_WIZARD_SCREEN_NAME);
	}
	
	public String getProjectName()
	{
		return getData(TAG_PROJECT_NAME);
	}
	
	public String getProjectScope()
	{
		return getData(TAG_PROJECT_SCOPE);
	}
	
	public String getProjectDescription()
	{
		return getData(TAG_PROJECT_DESCRIPTION);
	}
	
	public String getShortProjectScope()
	{
		return getData(TAG_SHORT_PROJECT_SCOPE);
	}
	
	public String getProjectVision()
	{
		return getData(TAG_PROJECT_VISION);
	}
	
	public MultiCalendar getWorkPlanStartDate()
	{
		return getDateData(TAG_WORKPLAN_START_DATE);
	}
	
	public String getWorkPlanStartDateAsString()
	{
		return getData(TAG_WORKPLAN_START_DATE);
	}
	
	public String getWorkPlanEndDate()
	{
		return getData(TAG_WORKPLAN_END_DATE);
	}
	
	public MultiCalendar getProjectStartDate()
	{
		return getDateData(TAG_START_DATE);
	}
	
	public String getStartDate()
	{
		return getData(TAG_START_DATE);
	}
	
	public String getExpectedEndDate()
	{
		return getData(TAG_EXPECTED_END_DATE);
	}
	
	public String getEffectiveDate()
	{
		return getData(TAG_DATA_EFFECTIVE_DATE);
	}
	
	public OptionalDouble getSizeInHectares()
	{
		try
		{
			String size = getData(TAG_PROJECT_AREA);
			if (size.length() == 0)
				return new OptionalDouble();
			
			return new OptionalDouble(DoubleUtilities.toDoubleFromDataFormat(size));
		}
		catch (Exception e)
		{
			EAM.logDebug("Exception ocurred while trying to parse project area.");
			return new OptionalDouble();
		}
	}
	
	public StringRefMap getXenodataStringRefMap()
	{
		return getStringRefMapData(TAG_XENODATA_STRING_REF_MAP);
	}
	
	public boolean shouldIncludeResultsChain()
	{
		String diagramInclusionCode = getData(TAG_WORK_PLAN_DIAGRAM_DATA_INCLUSION);;
		return DiagramObjectDataInclusionQuestion.shouldIncludeResultsChain(diagramInclusionCode);
	}

	public boolean shouldIncludeConceptualModelPage()
	{
		String diagramInclusionCode = getData(TAG_WORK_PLAN_DIAGRAM_DATA_INCLUSION);;
		return DiagramObjectDataInclusionQuestion.shouldIncludeConceptualModelPage(diagramInclusionCode);
	}
	
	public boolean shouldPutTargetsAtTopLevelOfTree()
	{
		String code = getData(TAG_PLANNING_TREE_TARGET_NODE_POSITION);
		return PlanningTreeTargetPositionQuestion.shouldPutTargetsAtTopLevelOfTree(code);
	}
	
	public double getLongitudeAsFloat()
	{
		return getFloatData(TAG_PROJECT_LONGITUDE);
	}
	
	public double getLatitudeAsFloat()
	{
		return getFloatData(TAG_PROJECT_LATITUDE);
	}
	
	public String getLongitude()
	{
		return getData(TAG_PROJECT_LONGITUDE);
	}
	
	public String getLatitude()
	{
		return getData(TAG_PROJECT_LATITUDE);
	}
	
	public int getCurrencyDecimalPlaces()
	{
		return getIntegerData(TAG_CURRENCY_DECIMAL_PLACES);
	}

	public int getDiagramFontSize()
	{
		String sizeAsString = getData(TAG_DIAGRAM_FONT_SIZE);
		if(sizeAsString.length() == 0)
			return 0;
		return Integer.parseInt(sizeAsString);
	}
	
	public String getDiagramFontFamily()
	{
		return getData(TAG_DIAGRAM_FONT_FAMILY);
	}
	
	public CodeList getTncTerrestrialEcoRegion() throws Exception
	{
		return getCodeList(TAG_TNC_TERRESTRIAL_ECO_REGION);
	}
	
	public CodeList getTncFreshwaterEcoRegion() throws Exception
	{
		return getCodeList(TAG_TNC_FRESHWATER_ECO_REGION);
	}

	public CodeList getTncMarineEcoRegion() throws Exception
	{
		return getCodeList(TAG_TNC_MARINE_ECO_REGION);
	}
	
	public boolean isStressBasedThreatRatingMode()
	{
		if (getThreatRatingMode().equals(ThreatRatingModeChoiceQuestion.STRESS_BASED_CODE))
			return true;
		
		return false;
	}
	
	public boolean isHumanWelfareTargetMode()
	{
		String code = getData(TAG_HUMAN_WELFARE_TARGET_MODE);
		return code.equals(TargetModeQuestion.HUMAN_WELFARE_TARGET_CODE);
	}
		
	public String getThreatRatingMode()
	{
		return getData(TAG_THREAT_RATING_MODE);
	}

	public String getFullTimeEmployeeDaysPerYear()
	{
		final String DEFAULT_FULL_TIME_EMPLOYEE_DAYS_PER_YEAR = "240";
		String fullTimeEmployeeDaysPerYearAsString = getData(TAG_FULL_TIME_EMPLOYEE_DAYS_PER_YEAR);
		if (fullTimeEmployeeDaysPerYearAsString.length() == 0)
			return DEFAULT_FULL_TIME_EMPLOYEE_DAYS_PER_YEAR;
		
		return fullTimeEmployeeDaysPerYearAsString;
	}
	
	public int getFiscalYearFirstMonth()
	{
		return getFiscalYearFirstMonth(getData(TAG_FISCAL_YEAR_START));
	}
	
	public static int getFiscalYearFirstMonth(String fiscalYearStartCode)
	{
		if(fiscalYearStartCode.length() == 0)
			fiscalYearStartCode = "1";
		int month = Integer.parseInt(fiscalYearStartCode);
		return month;
	}

	public boolean areQuarterColumnsVisible()
	{
		return getData(TAG_QUARTER_COLUMNS_VISIBILITY).equals(QuarterColumnsVisibilityQuestion.SHOW_QUARTER_COLUMNS_CODE);
	}
	
	public boolean isBudgetTimePeriodQuarterly()
	{
		return getData(TAG_WORKPLAN_TIME_UNIT).equals(BudgetTimePeriodQuestion.BUDGET_BY_QUARTER_CODE);
	}

	public boolean isBudgetTimePeriodYearly()
	{
		return getData(TAG_WORKPLAN_TIME_UNIT).equals(BudgetTimePeriodQuestion.BUDGET_BY_YEAR_CODE);
	}
	
	public CodeList getCountryCodes()
	{
		return getCodeListData(TAG_COUNTRIES);
	}
	
	public static boolean is(BaseObject baseObject)
	{
		if(baseObject == null)
			return false;
		return is(baseObject.getRef());
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == getObjectType();
	}
	
	@Override
	public String toString()
	{
		return getProjectName();
	}
	
	@Override
	void clear()
	{
		super.clear();
		
		createSingleLineUserTextField(TAG_PROJECT_NAME);
		createChoiceField(TAG_PROJECT_LANGUAGE, new LanguageQuestion());
		createMultiLineUserTextField(TAG_PROJECT_DESCRIPTION);
		createSingleLineUserTextField(TAG_PROJECT_URL);
		createMultiLineUserTextField(TAG_PROJECT_SCOPE);
		createSingleLineUserTextField(TAG_SHORT_PROJECT_SCOPE);
		createMultiLineUserTextField(TAG_SCOPE_COMMENTS);
		createMultiLineUserTextField(TAG_PROJECT_VISION);
		
		createDateField(TAG_START_DATE);
		createDateField(TAG_EXPECTED_END_DATE);
		createDateField(TAG_DATA_EFFECTIVE_DATE);
		
		createIntegerField(TAG_CURRENCY_DECIMAL_PLACES);
		createChoiceField(TAG_CURRENCY_TYPE, getQuestion(CurrencyTypeQuestion.class));
		createSingleLineUserTextField(TAG_CURRENCY_SYMBOL);
		createNumberField(TAG_TOTAL_BUDGET_FOR_FUNDING);
		createPercentageField(TAG_BUDGET_SECURED_PERCENT);
		createChoiceField(TAG_FISCAL_YEAR_START, getQuestion(FiscalYearStartQuestion.class));
		createNumberField(TAG_FULL_TIME_EMPLOYEE_DAYS_PER_YEAR);
		createChoiceField(TAG_QUARTER_COLUMNS_VISIBILITY, getQuestion(QuarterColumnsVisibilityQuestion.class));
		createChoiceField(TAG_PLANNING_TREE_TARGET_NODE_POSITION, getQuestion(PlanningTreeTargetPositionQuestion.class));
		
		createFloatField(TAG_PROJECT_LATITUDE);
		createFloatField(TAG_PROJECT_LONGITUDE);
		// NOTE: ProjectArea should be a float, but can't be for legacy reasons
		createSingleLineUserTextField(TAG_PROJECT_AREA);
		createMultiLineUserTextField(TAG_PROJECT_AREA_NOTES);

		createCodeListField(TAG_COUNTRIES, getQuestion(CountriesQuestion.class));
		createSingleLineUserTextField(TAG_STATE_AND_PROVINCES);
		createSingleLineUserTextField(TAG_MUNICIPALITIES);
		createSingleLineUserTextField(TAG_LEGISLATIVE_DISTRICTS);
		createMultiLineUserTextField(TAG_LOCATION_DETAIL);
		createMultiLineUserTextField(TAG_LOCATION_COMMENTS);

		createMultiLineUserTextField(TAG_KEY_FUNDING_SOURCES);
		createMultiLineUserTextField(TAG_FINANCIAL_COMMENTS);
		createDateField(TAG_WORKPLAN_START_DATE);
		createDateField(TAG_WORKPLAN_END_DATE);
		createChoiceField(TAG_WORKPLAN_TIME_UNIT, getQuestion(BudgetTimePeriodQuestion.class));
		createMultiLineUserTextField(TAG_PLANNING_COMMENTS);
		
		createIntegerField(TAG_HUMAN_POPULATION);
		createMultiLineUserTextField(TAG_HUMAN_POPULATION_NOTES);
		createMultiLineUserTextField(TAG_SOCIAL_CONTEXT);
		createSingleLineUserTextField(TAG_SITE_MAP_REFERENCE);

		createCodeListField(TAG_PROTECTED_AREA_CATEGORIES, getQuestion(ProtectedAreaCategoryQuestion.class));
		createMultiLineUserTextField(TAG_PROTECTED_AREA_CATEGORY_NOTES);

		createMultiLineUserTextField(TAG_PROJECT_STATUS);
		createMultiLineUserTextField(TAG_NEXT_STEPS);

		createCodeField(TAG_CURRENT_WIZARD_SCREEN_NAME);
		setIsNavigationField(TAG_CURRENT_WIZARD_SCREEN_NAME);


		createMultiLineUserTextField(TAG_TNC_LESSONS_LEARNED);
		createSingleLineUserTextField(TAG_TNC_WORKBOOK_VERSION_NUMBER);
		createDateField(TAG_TNC_WORKBOOK_VERSION_DATE);
		createDateField(TAG_TNC_DATABASE_DOWNLOAD_DATE);
		createMultiLineUserTextField(TAG_TNC_PLANNING_TEAM_COMMENTS);
		createCodeListField(TAG_TNC_OPERATING_UNITS, getQuestion(TncOperatingUnitsQuestion.class));
		createCodeListField(TAG_TNC_TERRESTRIAL_ECO_REGION, getQuestion(TncTerrestrialEcoRegionQuestion.class));
		createCodeListField(TAG_TNC_MARINE_ECO_REGION, getQuestion(TncMarineEcoRegionQuestion.class));
		createCodeListField(TAG_TNC_FRESHWATER_ECO_REGION, getQuestion(TncFreshwaterEcoRegionQuestion.class));
		
		createSingleLineUserTextField(TAG_OTHER_ORG_PROJECT_NUMBER);
		createSingleLineUserTextField(TAG_OTHER_ORG_RELATED_PROJECTS);
		
		createChoiceField(TAG_DIAGRAM_FONT_SIZE, getQuestion(FontSizeQuestion.class));
		createChoiceField(TAG_DIAGRAM_FONT_FAMILY, getQuestion(FontFamiliyQuestion.class));
		createChoiceField(TAG_THREAT_RATING_MODE, getQuestion(ThreatRatingModeChoiceQuestion.class));
		createStringRefMapField(TAG_XENODATA_STRING_REF_MAP);
		createChoiceField(TAG_HUMAN_WELFARE_TARGET_MODE, getQuestion(TargetModeQuestion.class));
		createChoiceField(TAG_WORK_PLAN_DIAGRAM_DATA_INCLUSION, getQuestion(DiagramObjectDataInclusionQuestion.class));
		
		createPseudoStringField(PSEUDO_TAG_PROJECT_FILENAME);
		createPseudoStringField(PSEUDO_TAG_ALL_THREAT_CLASSIFICATIONS);
	}

	public static final String TAG_CURRENT_WIZARD_SCREEN_NAME = "CurrentWizardScreenName";
	public static final String TAG_PROJECT_NAME = "ProjectName";
	public static final String TAG_PROJECT_LANGUAGE = "ProjectLanguage";
	public static final String TAG_PROJECT_SCOPE = "ProjectScope";
	public static final String TAG_SHORT_PROJECT_SCOPE = "ShortProjectScope";
	public static final String TAG_PROJECT_VISION = "ProjectVision";
	public static final String TAG_START_DATE = "StartDate";
	public static final String TAG_EXPECTED_END_DATE = "ExpectedEndDate";
	public static final String TAG_DATA_EFFECTIVE_DATE = "DataEffectiveDate";
	public static final String TAG_CURRENCY_DECIMAL_PLACES = "CurrencyDecimalPlaces";
	public static final String TAG_PROJECT_LATITUDE = "ProjectLatitude";
	public static final String TAG_PROJECT_LONGITUDE = "ProjectLongitude";
	public static final String TAG_TOTAL_BUDGET_FOR_FUNDING = "TotalBudgetForFunding";
	public static final String TAG_BUDGET_SECURED_PERCENT = "BudgetSecuredPercent";
	public static final String TAG_CURRENCY_TYPE = "CurrencyType";
	public static final String TAG_CURRENCY_SYMBOL = "CurrencySymbol";
	public static final String TAG_FISCAL_YEAR_START = "FiscalYearStart";
	public static final String TAG_QUARTER_COLUMNS_VISIBILITY = "QuarterColumnsVisibility";
	public static final String TAG_PLANNING_TREE_TARGET_NODE_POSITION = "PlanningTreeTargetNodePosition";
	public static final String TAG_FULL_TIME_EMPLOYEE_DAYS_PER_YEAR = "FullTimeEmployeeDaysPerYear";
	public static final String TAG_PROJECT_DESCRIPTION = "ProjectDescription";
	public static final String TAG_PROJECT_URL = "ProjectURL";
	public static final String TAG_PROJECT_AREA = "ProjectArea";
	public static final String TAG_PROJECT_AREA_NOTES = "ProjectAreaNote";
	public static final String TAG_SCOPE_COMMENTS = "ScopeComments";
	public static final String TAG_COUNTRIES = "Countries";
	public static final String TAG_STATE_AND_PROVINCES = "StateAndProvinces";
	public static final String TAG_MUNICIPALITIES = "Municipalities";
	public static final String TAG_LEGISLATIVE_DISTRICTS = "LegislativeDistricts";
	public static final String TAG_LOCATION_DETAIL = "LocationDetail";
	public static final String TAG_LOCATION_COMMENTS = "LocationComments";
	public static final String TAG_KEY_FUNDING_SOURCES = "KeyFundingSources";
	public static final String TAG_FINANCIAL_COMMENTS = "FinancialComments";
	public static final String TAG_WORKPLAN_START_DATE = "WorkPlanStartDate";
	public static final String TAG_WORKPLAN_END_DATE = "WorkPlanEndDate";
	public static final String TAG_WORKPLAN_TIME_UNIT = "WorkPlanTimeUnit";
	public static final String TAG_PLANNING_COMMENTS = "PlanningComments";
	public static final String TAG_HUMAN_POPULATION = "HumanPopulation";
	public static final String TAG_HUMAN_POPULATION_NOTES = "HumanPopulationNotes";
	public static final String TAG_SOCIAL_CONTEXT = "SocialContext";
	public static final String TAG_SITE_MAP_REFERENCE = "SiteMapReference";
	public static final String TAG_PROTECTED_AREA_CATEGORIES = "ProtectedAreaCategories";
	public static final String TAG_PROTECTED_AREA_CATEGORY_NOTES = "ProtectedAreaCategoryNotes";
	public static final String TAG_PROJECT_STATUS = "ProjectStatus";
	public static final String TAG_NEXT_STEPS = "NextSteps";
	
	public static final String PSEUDO_TAG_PROJECT_FILENAME = "PseudoTagProjectFilename";
	public static final String PSEUDO_TAG_ALL_THREAT_CLASSIFICATIONS = "AllThreatClassifications";
	
	public static final String TAG_TNC_LESSONS_LEARNED = "TNC.LessonsLearned";
	public static final String TAG_TNC_WORKBOOK_VERSION_NUMBER = "TNC.WorkbookVersionNumber";
	public static final String TAG_TNC_WORKBOOK_VERSION_DATE = "TNC.WorkbookVersionDate";
	public static final String TAG_TNC_DATABASE_DOWNLOAD_DATE = "TNC.DatabaseDownloadDate";
	public static final String TAG_TNC_PLANNING_TEAM_COMMENTS = "TNC.PlanningTeamComment";
	public static final String TAG_TNC_OPERATING_UNITS = "TNC.OperatingUnitList";
	public static final String TAG_TNC_TERRESTRIAL_ECO_REGION = "TNC.TerrestrialEcoRegion";
	public static final String TAG_TNC_MARINE_ECO_REGION = "TNC.MarineEcoRegion";
	public static final String TAG_TNC_FRESHWATER_ECO_REGION = "TNC.FreshwaterEcoRegion";

	public static final String TAG_OTHER_ORG_PROJECT_NUMBER = "OtherOrgProjectNumber";
	public static final String TAG_OTHER_ORG_RELATED_PROJECTS = "OtherOrgRelatedProjects";	
	
	public static final String TAG_DIAGRAM_FONT_FAMILY = "DiagramFontFamily";
	public static final String TAG_DIAGRAM_FONT_SIZE = "DiagramFontSize";
	public static final String TAG_THREAT_RATING_MODE = "ThreatRatingMode";
	public static final String TAG_WORK_PLAN_DIAGRAM_DATA_INCLUSION = "WorkPlanDiagramDataInclusion";
	
	public static final String TAG_XENODATA_STRING_REF_MAP = "XenodataRefs";
	public static final String TAG_HUMAN_WELFARE_TARGET_MODE = "TargetMode";
	
	public static final String OBJECT_NAME = "ProjectMetadata";
}
