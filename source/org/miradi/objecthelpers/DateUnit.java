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
package org.miradi.objecthelpers;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.martus.util.MultiCalendar;
import org.miradi.main.EAM;
import org.miradi.utils.DateRange;
import org.miradi.utils.EnhancedJsonObject;

public class DateUnit implements Comparable<DateUnit>
{
	public DateUnit()
	{
		this("");
	}
	
	public DateUnit(EnhancedJsonObject json) throws Exception
	{
		this(createFromJson(json));
	}
	
	private DateUnit(DateUnit otherDateUnit)
	{
		this(otherDateUnit.toString());
	}
	
	public DateUnit(String dateUnitToUse)
	{
		dateUnit = dateUnitToUse;
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		json.put(TAG_DATE_UNIT_CODE, toString());
		return json;
	}
	
	public static DateUnit createFromJson(EnhancedJsonObject json) throws Exception 
	{
		if (json == null)
			return null;
	
		if (!json.has(TAG_DATE_UNIT_CODE))
			return null;
		
		String dateUnitAsString = json.get(TAG_DATE_UNIT_CODE).toString();
		return new DateUnit(dateUnitAsString);
	}
		
	public static DateUnit createFromDateRange(DateRange dateRange)
	{
		MultiCalendar startDate = dateRange.getStartDate();
		String startIso = startDate.toIsoDateString();
		String yearString = startIso.substring(0, 4);
		int startingYear = startDate.getGregorianYear();
		int startingMonth = startDate.getGregorianMonth();

		if(dateRange.isDay())
			return createDayDateUnit(startIso);
		
		if(dateRange.isMonth())
			return createMonthDateUnit(startIso);
		
		if(dateRange.isQuarter())
			return createQuarterDateUnit(yearString, startingMonth);
		
		if(dateRange.isYear())
			return createFiscalYear(startingYear, startingMonth);
		
		return new DateUnit();
	}

	private static DateUnit createQuarterDateUnit(String yearString, int startingMonth)
	{
		int startingQuarter = (startingMonth - 1) / 3 + 1;
	
		return createQuarterDateUnit(Integer.parseInt(yearString), startingQuarter);
	}

	public static DateUnit createQuarterDateUnit(int year, int startingQuarter)
	{
		if(startingQuarter < 1 || startingQuarter > 4)
			throw new RuntimeException("Unknown quarter: " + year + " " + startingQuarter);
		return new DateUnit(year + QUARTER_PREFIX_CODE + startingQuarter);
	}

	public static DateUnit createMonthDateUnit(String isoDateString)
	{
		return new DateUnit(isoDateString.substring(0, 7));
	}

	public static DateUnit createDayDateUnit(String isoDateString)
	{
		return new DateUnit(isoDateString);
	}

	public static DateUnit createFiscalYear(Integer startingYear, int startingMonth)
	{
		return new DateUnit(YEAR_PREFIX_CODE + asFourDigitString(startingYear) + "-" + asTwoDigitString(startingMonth));
	}

	public String getDateUnitCode()
	{
		return dateUnit;
	}
	
	public boolean isValid()
	{
		return (isProjectTotal() || isYear() || isQuarter() || isMonth() || isDay());
	}

	public boolean isProjectTotal()
	{
		return getDateUnitCode().length() == 0;
	}
	
	public boolean isYear()
	{
		return getDateUnitCode().matches("YEARFROM:\\d\\d\\d\\d-\\d\\d");
	}
	
	public boolean isQuarter()
	{
		String code = getDateUnitCode();
		return code.matches("\\d\\d\\d\\dQ\\d");
	}
	
	public boolean isMonth()
	{
		String code = getDateUnitCode();
		return code.matches("\\d\\d\\d\\d-\\d\\d");
	}
	
	public boolean isDay()
	{
		String code = getDateUnitCode();
		return code.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d");
	}

	public DateRange asDateRange() throws Exception
	{
		if (isYear())
			return getYearDateRange(); 
		
		if (isQuarter())
			return getQuarterDateRange();
		
		if (isMonth())
			return getMonthDateRange();
		
		if (isDay())
			return getDayDateRange();
			
		throw new Exception(EAM.text("No date range for date Unit = " + dateUnit.toString()));
	}
	
	private DateRange getMonthDateRange() throws Exception
	{	
 		int year = getYear();
		int month = getMonth();
	    int daysInMonth = getNumberOfDaysInMonth(year, month); 
	    
		MultiCalendar startDate = MultiCalendar.createFromGregorianYearMonthDay(year, month, 1);
		MultiCalendar endDate = MultiCalendar.createFromGregorianYearMonthDay(year, month, daysInMonth);
		
		return new DateRange(startDate, endDate);			
	}
	
	private DateRange getDayDateRange() throws Exception
	{
		MultiCalendar date = MultiCalendar.createFromGregorianYearMonthDay(getYear(), getMonth(), getDay());
		return new DateRange(date, date);
	}

	private int getNumberOfDaysInMonth(int year, int month)
	{
		int zeroOffsetMonth = month - 1;
		Calendar calendar = new GregorianCalendar(year, zeroOffsetMonth, 1);
	    int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return daysInMonth;
	}

	private DateRange getYearDateRange() throws Exception
	{
		int year = getYearYear();
		int month = getYearStartMonth();
		MultiCalendar startDate = MultiCalendar.createFromGregorianYearMonthDay(year, month, 1);
		MultiCalendar endDate = MultiCalendar.createFromGregorianYearMonthDay(year + 1, month, 1);
		endDate.addDays(-1);
		return new DateRange(startDate, endDate);
	}

	public int getYearYear()
	{
		return Integer.parseInt(getYearYearString());
	}

	public String getYearYearString()
	{
		int startAt = YEAR_PREFIX_CODE.length();
		return getDateUnitCode().substring(startAt, startAt+4);
	}
	
	public int getYearStartMonth()
	{
		int startAt = YEAR_PREFIX_CODE.length() + 5;
		return Integer.parseInt(getDateUnitCode().substring(startAt, startAt+2));
	}
	
	public DateRange getQuarterDateRange() throws Exception
	{
		int quarter = getQuarter();
		int startYear = getYear();
		int startMonth = (quarter - 1) * 3;
		MultiCalendar startDate = MultiCalendar.createFromGregorianYearMonthDay(startYear, startMonth+1, 1);
		
		int pastEndMonth = (startMonth + 3) % 12;
		int endYear = (pastEndMonth == 0 ? startYear+1 : startYear);
		MultiCalendar endDate = MultiCalendar.createFromGregorianYearMonthDay(endYear, pastEndMonth+1, 1);
		endDate.addDays(-1);
		
		return new DateRange(startDate, endDate);
	}

	public int getYear()
	{
		return parseEmbeddedInt(0, 4);
	}
	
	public int getMonth()
	{
		return parseEmbeddedInt(5, 7);
	}
	
	public int getDay()
	{
		return parseEmbeddedInt(8, 10);
	}

	private int parseEmbeddedInt(int start, int length)
	{
		return Integer.parseInt(getDateUnitCode().substring(start, length));
	}

	public int getQuarter()
	{
		int quarter = Integer.parseInt(getDateUnitCode().substring(5));
		return quarter;
	}
	
	public boolean hasSubDateUnits()
	{
		if(isYear())
			return true;
		
		if(isQuarter())
			return true;
		
		if (isMonth())
			return true;
		
		return false;
	}
	
	public boolean contains(DateUnit otherDateUnit) throws Exception
	{
		if (isProjectTotal())
			return true;
		
		if (otherDateUnit.isProjectTotal())
			return false;
		
		return asDateRange().contains(otherDateUnit.asDateRange());
	}

	private DateUnit getSafeSuperDateUnit(int fiscalYearFirstMonth)
	{
		if (isProjectTotal())
			return null;
		
		return getSuperDateUnit(fiscalYearFirstMonth);
	}
	
	public Vector<DateUnit> getSuperDateUnitHierarchy(int fiscalYearFirstMonth)
	{
		Vector<DateUnit> superDateUnits = new Vector<DateUnit>();
		DateUnit superDateUnit = getSafeSuperDateUnit(fiscalYearFirstMonth);
		while(superDateUnit != null)
		{
			superDateUnits.add(superDateUnit);
			superDateUnit = superDateUnit.getSafeSuperDateUnit(fiscalYearFirstMonth);
		}
		
		return superDateUnits;
	}
	
	public DateUnit getSuperDateUnit(int fiscalYearFirstMonth)
	{
		if(isDay())
			return new DateUnit(getDateUnitCode().substring(0, 7));
		
		if(isMonth())
			return getMonthSuper();
		
		if(isQuarter())
			return getQuarterSuper(fiscalYearFirstMonth);
		
		if(isYear())
			return new DateUnit("");
		
		throw new RuntimeException("getSuperDateUnit called for unknown date unit: " + getDateUnitCode());
	}

	private DateUnit getQuarterSuper(int fiscalYearFirstMonth)
	{
		Vector<DateUnit> monthSubUnits = getQuarterSubDateUnits();
		DateUnit firstMonthOfQuarterDateUnit = monthSubUnits.get(0);
		int firstMonthOfQuarter = firstMonthOfQuarterDateUnit.getMonth();
		int year = getYear();
		if (fiscalYearFirstMonth > firstMonthOfQuarter)
			year -= 1;
		
		return new DateUnit(YEAR_PREFIX_CODE + year + "-" + asTwoDigitString(fiscalYearFirstMonth));
	}

	private DateUnit getMonthSuper()
	{
		int quarter = (getMonth() - 1) / 3;
		String quarterString = QUARTER_PREFIX_CODE + (quarter + 1);
		return new DateUnit(getDateUnitCode().substring(0, 4) + quarterString);
	}
	
	public Vector<DateUnit> getSubDateUnitsWithin(DateRange dateRange) throws Exception
	{
		Vector<DateUnit> rawSubDateUnits = getSubDateUnits();
		Vector<DateUnit> boundedSubDateUnits = new Vector<DateUnit>();
		for(DateUnit thisDateUnit : rawSubDateUnits)
		{
			if (thisDateUnit.asDateRange().overlaps(dateRange))
				boundedSubDateUnits.add(thisDateUnit);
		}
		
		return boundedSubDateUnits;
	}
	
	public Vector<DateUnit> getSubDateUnits() throws Exception
	{
		if(isYear())
			return getYearSubDateUnits();
		
		if(isQuarter())
			return getQuarterSubDateUnits();
		
		if(isMonth())
			return getMonthSubDateUnits();
		
		throw new Exception("Can't call getSubDateUnits for DateUnit: " + getDateUnitCode());
	}

	private Vector<DateUnit> getYearSubDateUnits()
	{
		Vector<DateUnit> quarters = new Vector<DateUnit>();
		int year = getYearYear();
		int startMonth = getYearStartMonth();
		for(int index = 0; index < 4; ++index)
		{
			int quarterIndex = (startMonth-1) / 3;
			int unboundedQuarter = quarterIndex + index;
			if(unboundedQuarter == 4)
				++year;
			int quarter = unboundedQuarter % 4;
			quarters.add(new DateUnit(asFourDigitString(year) + QUARTER_PREFIX_CODE + (quarter+1)));
		}
		
		return quarters;
	}
	
	private Vector<DateUnit> getQuarterSubDateUnits()
	{
		Vector<DateUnit> months = new Vector<DateUnit>();
		int quarter = getQuarter();
		String[] monthStrings = monthsPerQuarter[quarter-1];
		for(int index = 0; index < monthStrings.length; ++index)
		{
			months.add(new DateUnit(getYear() + "-" + monthStrings[index]));
		}
		
		return months;
	}
	
	private Vector<DateUnit> getMonthSubDateUnits()
	{
		Vector<DateUnit> days = new Vector<DateUnit>();
		int year = getYear();
		int daysInMonth = getNumberOfDaysInMonth(year, getMonth());
		for(int day = 1; day <= daysInMonth; ++day)
		{
			days.add(new DateUnit(getYear() + "-" + asTwoDigitString(getMonth()) + "-" + asTwoDigitString(day)));
		}
		
		return days;
	}

	public static String asTwoDigitString(int numberToFormat)
	{
		return new DecimalFormat("00").format(numberToFormat);
	}

	private static String asFourDigitString(int numberToFormat)
	{
		return new DecimalFormat("0000").format(numberToFormat);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof DateUnit))
			return false;
		
		DateUnit thisDateUnit = (DateUnit) obj;
		return thisDateUnit.getDateUnitCode().equals(getDateUnitCode());
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	@Override
	public String toString()
	{
		return getDateUnitCode().toString();
	}
	
	public int compareTo(DateUnit other)
	{
		return toString().compareTo(other.toString());
	}
	
	private static final String[][] monthsPerQuarter = { 
		{"01", "02", "03"}, 
		{"04", "05", "06"}, 
		{"07", "08", "09"}, 
		{"10", "11", "12"}, 
	};
	
	private static final String TAG_DATE_UNIT_CODE = "DateUnitCode";
	private static final String YEAR_PREFIX_CODE = "YEARFROM:";
	private static final String QUARTER_PREFIX_CODE = "Q";
	private String dateUnit;
}
