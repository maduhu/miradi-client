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

package org.miradi.migrations.forward;

import java.io.File;
import java.util.Vector;

import org.martus.util.UnicodeReader;
import org.martus.util.UnicodeStringReader;
import org.miradi.exceptions.ProjectFileTooNewException;
import org.miradi.exceptions.ProjectFileTooOldException;
import org.miradi.exceptions.XmlVersionTooOldException;
import org.miradi.main.EAM;
import org.miradi.migrations.AbstractMigration;
import org.miradi.migrations.AbstractMigrationManager;
import org.miradi.migrations.MigrationResult;
import org.miradi.migrations.RawProject;
import org.miradi.migrations.RawProjectLoader;
import org.miradi.migrations.VersionRange;
import org.miradi.project.Project;
import org.miradi.utils.FileUtilities;
import org.miradi.xml.xmpz2.Xmpz2XmlConstants;

public class MigrationManager extends AbstractMigrationManager
{
	public MigrationManager()
	{
	}
	
	public MigrationResult migrate(RawProject rawProject, VersionRange desiredVersion) throws Exception
	{
		MigrationResult migrationResult = MigrationResult.createUninitializedResult();
		if (rawProject.getCurrentVersionRange().isEntirelyNewerThan(desiredVersion))
		{
			migrationResult.merge(migrateReverse(rawProject, desiredVersion));
		}
		if (rawProject.getCurrentVersionRange().isEntirelyOlderThan(desiredVersion))
		{
			migrationResult.merge(migrateForward(rawProject, desiredVersion));
		}		
		
		return migrationResult;
	}

	private MigrationResult migrateForward(RawProject rawProject, VersionRange desiredVersion) throws Exception
	{
		MigrationResult migrationResult = MigrationResult.createUninitializedResult();
		Vector<AbstractMigration> migrations = createMigrations(rawProject);
		for(AbstractMigration migration : migrations)
		{
			migrationResult.merge(migration.forwardMigrateIfPossible(desiredVersion));
		}

		return migrationResult;
	}

	private MigrationResult migrateReverse(RawProject rawProject, VersionRange desiredVersion) throws Exception
	{
		MigrationResult migrationResult = MigrationResult.createUninitializedResult();
		Vector<AbstractMigration> migrations = createMigrations(rawProject);
		for(int index = migrations.size() - 1; index >= 0; --index)
		{
			AbstractMigration migration = migrations.get(index);
			migrationResult.merge(migration.reverseMigrateIfPossible(desiredVersion));
		}
		
		return migrationResult;
	}
	
	private Vector<AbstractMigration> createMigrations(RawProject rawProject)
	{
		// note: when adding new migration make sure to update getLatestMigrationForDocumentSchemaVersion below for xml migrations

		Vector<AbstractMigration> migrations = new Vector<AbstractMigration>();
		migrations.add(new MigrationTo4(rawProject));
		migrations.add(new MigrationTo5(rawProject));
		migrations.add(new MigrationTo6(rawProject));
		migrations.add(new MigrationTo7(rawProject));
		migrations.add(new MigrationTo8(rawProject));
		migrations.add(new MigrationTo9(rawProject));
		migrations.add(new MigrationTo10(rawProject));
		migrations.add(new MigrationTo11(rawProject));
		migrations.add(new MigrationTo12(rawProject));
		migrations.add(new MigrationTo13(rawProject));
		migrations.add(new MigrationTo14(rawProject));
		migrations.add(new MigrationTo15(rawProject));
		migrations.add(new MigrationTo16(rawProject));
		migrations.add(new MigrationTo17(rawProject));
		migrations.add(new MigrationTo18(rawProject));
		migrations.add(new MigrationTo19(rawProject));
		migrations.add(new MigrationTo20(rawProject));
		migrations.add(new MigrationTo21(rawProject));
		migrations.add(new MigrationTo22(rawProject));
		migrations.add(new MigrationTo23(rawProject));
		migrations.add(new MigrationTo24(rawProject));
		migrations.add(new MigrationTo25(rawProject));
		migrations.add(new MigrationTo26(rawProject));
		migrations.add(new MigrationTo27(rawProject));
		migrations.add(new MigrationTo28(rawProject));
		migrations.add(new MigrationTo29(rawProject));
		migrations.add(new MigrationTo30(rawProject));
		migrations.add(new MigrationTo31(rawProject));
		migrations.add(new MigrationTo32(rawProject));

		return migrations;
	}

	public static int getLatestMigrationForDocumentSchemaVersion(int documentSchemaVersion) throws Exception
	{
		String documentSchemaVersionAsString = String.valueOf(documentSchemaVersion);

		// 4.4 -> 236
		if (documentSchemaVersionAsString.equals(Xmpz2XmlConstants.NAME_SPACE_VERSION_236))
			return MigrationTo32.VERSION_TO;

		// 4.3.x -> 234/235
		if (documentSchemaVersionAsString.equals(Xmpz2XmlConstants.NAME_SPACE_VERSION_234) || documentSchemaVersionAsString.equals(Xmpz2XmlConstants.NAME_SPACE_VERSION_235))
			return MigrationTo18.VERSION_TO;

		// 4.2.1 -> 233
		if (documentSchemaVersionAsString.equals(Xmpz2XmlConstants.NAME_SPACE_VERSION_233))
			return MigrationTo16.VERSION_TO;

		// 4.1.x -> 228/232
		if (documentSchemaVersionAsString.equals(Xmpz2XmlConstants.NAME_SPACE_VERSION_228) || documentSchemaVersionAsString.equals(Xmpz2XmlConstants.NAME_SPACE_VERSION_232))
			return MigrationTo9.VERSION_HIGH;

		throw new XmlVersionTooOldException(Integer.toString(Xmpz2XmlConstants.LOWEST_SCHEMA_VERSION), documentSchemaVersionAsString);
	}

	public static void createBackup(File projectFile) throws Exception
	{
		FileUtilities.createMpfBackup(projectFile, getBackupFolderTranslatedName());
	}

	private static String getBackupFolderTranslatedName()
	{
		return EAM.substituteSingleString(EAM.text("(%s)"), "Automated-Migration-Backups");
	}

	public boolean needsMigration(final File projectFile) throws Exception
	{
		String contents = UnicodeReader.getFileContents(projectFile);
		VersionRange mpfVersionRange = RawProjectLoader.loadVersionRange(new UnicodeStringReader(contents));
		final int migrationType = getMigrationType(Project.getMiradiVersionRange(), mpfVersionRange);
		
		return migrationType == MIGRATION;
	}
	
	public void validateProjectVersion(final File projectFile) throws Exception
	{
		String contents = UnicodeReader.getFileContents(projectFile);
		VersionRange mpfVersionRange = RawProjectLoader.loadVersionRange(new UnicodeStringReader(contents));
		final VersionRange miradiVersionRange = Project.getMiradiVersionRange();
		final int migrationType = getMigrationType(miradiVersionRange, mpfVersionRange);

		if (migrationType == TOO_OLD_TO_MIGRATE)
			throw new ProjectFileTooOldException(mpfVersionRange.getHighVersion(), miradiVersionRange.getLowVersion());
		
		if (migrationType == TOO_NEW_TO_MIGRATE)
			throw new ProjectFileTooNewException(mpfVersionRange.getLowVersion(), miradiVersionRange.getHighVersion());
	}
	
	public static int getMigrationType(VersionRange miradiVersionRange, VersionRange mpfVersionRange) throws Exception
	{
		if (mpfVersionRange.getHighVersion() < OLDEST_VERSION_TO_HANDLE)
			return TOO_OLD_TO_MIGRATE;
		
		if (mpfVersionRange.isEntirelyOlderThan(miradiVersionRange))
			return MIGRATION;
		
		if (mpfVersionRange.isEntirelyNewerThan(miradiVersionRange))
			return TOO_NEW_TO_MIGRATE;
		
		return NO_MIGRATION;
	}

	public static final int NO_MIGRATION = 0;
	public static final int MIGRATION = 1;
	public static final int TOO_NEW_TO_MIGRATE = 2;
	public static final int TOO_OLD_TO_MIGRATE = 3;
	
	public static final int OLDEST_VERSION_TO_HANDLE = 3;
}
