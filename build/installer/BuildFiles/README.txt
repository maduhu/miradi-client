Miradi (TM)

Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

VERSION 4.4.0

License and Copying

Miradi is distributed under the terms of the GNU General Public License 
version 3, as published by the Free Software Foundation. Miradi is 
distributed in the hope that it will be useful, but without any warranty; 
without even the implied warranty of merchantability or fitness for a 
particular purpose.

----------------------------------------------------------------------


Updates and More Information:

Before running this program, check that you have the latest version at 

  https://miradi.org/download

You can also find more information about this software program 
at www.miradi.org or by e-mailing info@miradi.org. 
Please send suggestions and bug reports to feedback@miradi.org. 

About Miradi

This software program is being developed to assist conservation 
practitioners go through the adaptive management process outlined 
in the CMP's Open Standards for the Practice of Conservation. 
To obtain a copy of the latest version of these standards, 
go to www.conservationmeasures.org.


Third-Party Software Credits

Portions of the code are from the Martus(tm) free, social justice 
documentation and monitoring software, Copyright (C) 2001-2007, 
Beneficent Technology, Inc. ("The Benetech Initiative").

Miradi relies on the following third-party libraries and tools: 
BrowserLauncher2, jcalendar, jgraph, jing, jhlabs, 
jtreetable, jarbuilder, install4j, jortho, shef, apache commons.


---------------------
PREREQUISITES

For MS Windows:
- Windows XP or later
- 1 GB RAM recommended
- 80 MB available disk space

For Mac OS X:
- Java 1.8 or later
- 1 GB RAM recommended
- 30 MB available disk space

For GNU/Linux:
- Java 1.8 or later
- 1 GB RAM recommended
- 30 MB available disk space

---------------------
INSTALLING

Projects created with earlier versions of Miradi can be opened 
with later versions. They will be migrated automatically when 
you first open them with the new version.



MS Windows:
    IMPORTANT NOTE: If you have Miradi 2.3.1 or earlier installed on 
    Windows, you MUST uninstall it before installing this version. 
    Your existing projects will not be affected by the uninstall, 
    and will be available when you start the new Miradi version.
    If you have been Miradi 2.4 or later, you do not need to 
    uninstall it first. Just install this version over it.
    
- Download and run MiradiSetup.exe
- A copy of the latest Marine Example project will be installed automatically
- You do not need to have Java already installed on your system, because 
  the installer will offer to automatically download one if necessary.
  If you choose to automatically install Java, it will be (by default) at:
     C:\Program Files\Common Files\i4j_jres


Mac OS X:
    IMPORTANT NOTE: If you had manually installed Miradi 2.3.1 or earlier 
    (from a .zip file) on a Mac, you MUST uninstall the third-party 
    components before installing this new version. To uninstall them, 
    open each of these folders:
      - System/Library/Java/Extensions
      - Users/[user]/Library/Java/Extensions
    and if any of these files exist, delete them:
      - BrowserLauncher2-all-10rc4.jar
      - jcalendar-1.3.2.jar
      - jing.jar
      - jxl.jar
      - miradi-jgraph.jar
      - TableLayout.jar
    If you do not have the Martus (Human Rights) application on your system,
    delete the following files also:
      - icu4j_3_2_calendar.jar
      - martus-swing.jar
      - martus-utils.jar
      - persiancalendar.jar

- Before installing Miradi, make sure Oracle's official Java J2SE8 is installed. As of these release notes
  it can be downloaded from http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html.
  Miradi will no longer work with Apple's 1.6 version of Java (which is no longer maintained by Apple).
  
  
- Download the Miradi-4.4.0.dmg file
- Using the mac Finder navigate to the folder containing Miradi-4.4.0.dmg
- Double click the file Miradi-4.4.0.dmg
- In the Finder's left panel select the Miradi "Miradi 4.4.0 Mac Install" drive
- In the right you will see the contents of the drive
- Copy the Miradi app into your applications folder
- Select the Miradi app file and bring up the context menu 
  (click and hold, or right click) and select open. A warning 
  dialog will appear. Select open.	
- Note: You can also double click the .app file. However 
  depending on security settings, the warning dialog might 
  not provide an open option,  in which case, use the context menu step instead.
- MarineExample.Miradi is a sample project that can be imported into Miradi
  after you start the app


GNU/Linux:

- Make sure Oracle's official Java 1.8 (aka Java 8) or later is installed.
  Miradi will not work with Java 1.4, and has not been tested extensively 
  with OpenJDK or any other non-Oracle Java alternatives.

- Obtain Miradi-Linux.zip from http://miradi.org and unzip the contents 
  into a directory, perhaps /usr/local/lib or ~/Miradi-4.4.0
  (We recommend not installing to ~/Miradi because that is the default 
  location for project data).

- To run Miradi, use this command line:
    java -Xmx512m -jar <path-to-miradi.jar>
    
- MarineExample.Miradi is a sample project that can be imported into Miradi
  after you start the app



---------------------
Changes

Projects from earlier versions of Miradi can be opened, and will
automatically be migrated to the latest data format. These projects
cannot be directly opened by earlier versions of Miradi, but 4.1.2 and
4.1.0 include the ability to export projects in Miradi 4.0 format, or
in the MPZ format used by Miradi 3.3. In some cases, data may be
lost when exporting to these older formats, but a warning dialog will
indicate when that is the case.


Notable changes in 4.4.0:

  TBD

Notable changes in 4.3.1:

Diagram:
- Fixed issue with show / hide of Biophysical Factors / Results on diagrams.
- Fixed issue with cut / copy / paste of Biophysical Factors / Results between CM and RC diagrams.


Notable changes in 4.3.0:

Diagram:
- HTML formatting added to a Tag name now renders correctly in the right-click context menu.
- Biophysical Factors / Results added to the list of diagram objects.

Import / Export
- Deprecated formats .xmpz, .cpmz (ConPro) and .zip (WCS) have been removed from the import / export menu.
- Export to Miradi 4.0 has been brought forward to Miradi 4.2.
- Export to legacy .mpz format has been removed, but import has been retained.


Notable changes in 4.2.1:

Installer:
- The Mac installer now requires the installation of the 1.8 version of the Java SE runtime (JRE) from Oracle. As of
  these release notes it can be downloaded from http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html.


Notable changes in 4.2.0:

Installer:
- The Mac installer is now code signed to minimize security warnings related to Gatekeeper on OS X 10.7.5 (and later).
- Similarly the Windows installer should not display warnings related to the Program Compatibility
  Assistant (PCA) on Windows 7 (and later).

Appwide:
- On launching Miradi if the preferred data directory is not accessible (e.g. a disconnected network drive),
  then the end-user receives a notification, and the default location is restored.

Diagram:
- The default relevancy rule for Activities has been changed such that all Activities are marked as relevant when
  associating a relevant Strategy to an Objective.
- Improvements have been made to the Strategic Plan view to ensure that Objective - Activity relationships are
  displayed correctly.
- To aid diagram layout, a new field has been added to the Group Box Properties window to allow the end-user to adjust
  the amount of vertical space that the header text occupies.

Miradi Share Integration:
- Copying and pasting diagram objects across projects now ensures that only valid taxonomy classifications are copied.



Notable changes in 4.1.3:

Appwide:
- Fixed errors that were causing portions of all language packs to not display all of the available translated text.


Notable changes in 4.1.2 (compared to 4.1.0, includes changes in the aborted 4.1.1 release):

Appwide:
- Menu items under mac are no longer wrapped with HTML tags.
- Adjusting width of Threat Name column in Threat Summary View 
  no longer widens the threat icon column. 

Export/Import:
- Export of Miradi Share file (xmpz2) is simplified.
- Create from Existing Indicator no longer permits loss of 
  Future Status data.
- CalculatedWho now exports properly to xmpz2 when WHO is selected 
  for a Factor and no Work Units are specified.
- Export/import of xmpz2 files no longer changes the display and 
  behavior of group box members.
- Export/import of xmpz2 files now correctly retains once-removed 
  objective-strategy relevancy information.



Notable changes in 4.1.0 (compared to 4.0) include:

Appwide:
- New 2 state Miradi Share tab in Summary View
- Support for Miradi Share taxonomies for several object types
- Indicators can now have multiple Desired Future Statuses
- Data loss loss migration error message shows what data will be lost
- New TNC fields added and a few removed
- Improvements to logging and debug output
- Miradi Share tab has two modes
- Can select leader for objects with finer assignments
- Single click to edit progress report table's details cell

Export/Import:
- Can now export projects to Miradi 4.0 format (reverse migration)
- Importing now allows overwriting an existing project by that name
- Importing XMPZ2 files is now much faster 
- XMPZ2 schema changes to accommodate Miradi Share data
- Now have an icon for XMPZ2 files



Miradi 4.0.0 was a major release, and we recommend that all users 
upgrade to it. 

Projects from Miradi 3.x can be read by Miradi 4.x, and will be 
converted to a completely new data format. If you need to move 
your projects back to Miradi 3.x for some reason, you can do so 
by exporting them as MPZ files.

Notable changes in 4.0.0 include:

Appwide:
- New project data format
  + Avoids conflicts with some anti-virus software
  + Can be backed up faster
  + Can be emailed without having to export to MPZ format first
  + In the future, can be much faster
- Many fields now allow basic formatting (bold, bullets, etc.)
  + NOTE: Pasting formatted text may result in temporary extra newlines
  + NOTE: Under Mac OSX, paste from other apps will lose any formatting 
- Most tables now allow editing data directly within the table
- Strategies, Activities, Methods, and Tasks now have a Leader field
- Many minor text changes and improved translatability

Summary View:
- "Lessons Learned" field moved from TNC to Summary tab
- TNC tab now has a Business Planning side-tab

Diagram:
- Factor Properties now display basic work plan data (Who and When)
- Behavior of double-clicking on factors inside Group Boxes is improved 
- Create From... now includes more of the old data in the newly created item

Planning Views:
- Can now create Measurement rows

Reports:
- New XSLT-based reporting system (BETA status)

Export:
- New XMPZ2 data format (BETA status)

Translations:
- Several strings that were previously untranslatable are now translatable
- New feature that allows translators to test their work much more easily




Miradi 3.3.4 is a minor fix release for compatibility with Java 7.
Projects are fully compatible between all versions 3.3.X, but 
we recommend that all users upgrade to the latest version. 

Notable changes in 3.3.4 include:

Appwide:
- Buttons on Indicator tab were incorrectly disabled under Java 7

Import/Export:
- Was defaulting to the wrong file filter, which could cause 
  errors, or exported files to not have file extensions



Miradi 3.3.2 is a minor fix release. Projects are fully 
compatible between version 3.3, 3.3.1, and 3.3.2, but we  
recommend that all users upgrade to 3.3.2 as soon as possible. 

Notable changes in 3.3.2 include:

Appwide:
- Popup text editors now support Undo/Redo (Ctrl-Z/Ctrl-Y)
- All numeric fields now handle data input in the current 
  computer locale numeric format, such as 1.234,56
- Miradi no longer exits immediately if another app locks the clipboard

Diagram:
- Conceptual Model Properties panel now includes a list of 
  Draft Strategies on that diagram
- Zoom did not work correctly if the computer was set to 
  a locale that used numeric formatting like 1.234.56

Threat Rating:
- Export to image (JPEG/PNG) was omitting the table headers

Work Plan:
- Values were not saved correctly if the computer was set to 
  a locale that used numeric formatting like 1.234.56

CPMZ Import/Export:
- Import now preserves Parent-Child project relationship 
  changes that were made in ConPro
- Export no longer fails for projects that have a legacy 
  Operating Unit value
  
  

Miradi 3.3.1 is a minor fix release. Projects are fully 
compatible between version 3.3 and 3.3.1, but we strongly 
recommend that all users upgrade to 3.3.1 as soon as possible. 

Notable changes in 3.3.1 include:

Preferences:
- Planning tab: Choosing to include Conceptual Model and/or 
  Results Chain data now works properly 

Appwide:
- Fixed diagrams appearing blank on some Windows 7 computers  
- Objective Properties: Activity list now shows the Id field 
- Create Progress Report button was sometimes disabled for no reason 
- Lists of Indicators, Activities, or Methods that appear within
  a single table cell are now sorted alphabetically

Threat Rating:
- Threat Table names sometimes did not match the rest of the row

Strategic Plan:
- Custom: Now sort measurements by date 

Import/Export:
- Miscellaneous internal changes to the CMPZ and XMPZ export code



Miradi 3.3 was a major upgrade, with many new features and enhancements. 
These are the most notable:

Appwide:
- Wizard content has been updated
- New "Dashboard" feature shows a quick view of the Miradi project status
- Long actions, such as Open, Import, and Export, now show a progress bar
- All sortable tables now save thier sort order 
- Allow users to choose relevant Objectives and Goals when editing Strategies and Activities
- Mouse wheel now works in all pop up editors
- Mouse wheel now works in selection lists like TNC Operating Unit 
- Sort Progress Report/Percent Status column based on rating rather than alphabetical 
- Now have consistent cursor position behavior after clicking "Create"
- Fixed memory leak exporting diagrams, including inside CPMZ/XMPZ
- Prevent corruption due to power loss while writing project data
- Migrations to repair existing minor damage in some projects 

Summary View:
- New Conservation Coaches Network field on the Team tab 
- Moved the Data Sharing radio button field from TNC tab to Project tab
- The TNC Operating Unit list has been updated
- The TNC "Planning Team (legacy)" field is now editable

Diagram:
- Export image now allows setting the output image size/resolution
- Paste into a different diagram is now always as-shared
- Links can now be bidirectional on some diagrams and unidirectional on others 
- Can now choose relevant Objectives and Goals when editing Strategies and Activities
- Prevent mostly-blank output exporting a diagram image with hidden factors
- Can now manage tags for taggable items (activities, stresses) 
- Can now edit stress/activity properties from the  diagram

Threat Rating:
- Target Summary Threat rating sometimes showed incorrect values in threat rating table 
- Simple threat rating table -> Overall Project Rating cell now works without a stress 

Strategic Plan:
- New option to flip strategy/objective parent child relationship
- Consolidate most options into a Customize dialog, and simplify the UI
- Monitoring Plan now has several pre-built tables
- Significant speed optimization on larger projects
- Objectives are now displayed in related items for Strategies

Work Plan:
- Consolidate most options into a Customize dialog, and simplify the UI
- Significant speed optimization for Work Plan data entry
- Avoid certain cases of duplication of Activities in the tree
- Details and Comments are now available as columns 
- Fixed a problem that prevented entering "When" values using specific day(s) 
- Improved behavior when a day is selected but no days are chosen

Reports:
- Custom Strategic Plan tables are now available as sections for reports
- Target habitat Association field now prints values instead of internal codes 

CPMZ:
- Export now checks for required values before continuing
- Goals associated with Human Welfare Targets are now exported
- Strategy - Goal Association are now exported

XMPZ:
- Many changes were made to the XMPZ schema. 
  NOTE: This only affects developers who are writing code to read XMPZ files.



Miradi 3.2.3 has the following improvements:

- Eliminate incorrect warning that Miradi should be upgraded
- Improvements to the Spanish translations
- Fixed potential data corruption problem with "Create Indicator From..."
- Work Plan "When" column didn't allow entry of specific days
- Export to CPMZ didn't include Strategy-Goal association data
- Updates to the list of Operating Units on the TNC tab 



Miradi 3.2.2 fixes some minor problems that were in 3.2.x:

Application-wide:
- Pasting factors that contain work planning data between projects 
  could cause warnings about project corruption
- Threat Rating view in Simple mode could display incorrect 
  summary ratings for Targets



Miradi 3.2.1 fixes two minor problems that were in 3.2.0:

Compatibility:
- On a computer configured to use commas as decimal separators, 
  zooming a diagram could cause the project to become corrupted
- On a Mac/Linux computer that had never run an earlier version, 
  Miradi 3.2.0 would refuse to run 



Miradi 3.2 has several significant new features, in addition to many 
minor fixes and improvements. Here are the highlights:

Application-wide:
- New Spell Check feature (currently only for English)
- Improved error handling
- Some speed improvements, especially in Work Plan
- BETA: Ability to import Miradi XMPZ project files 

Summary:
- Can now specify which language the project data is in
- Changes to the organization-specific tab for Rare

Diagram:
- Fixed some errors related to copy/paste

Work Plan:
- Two new tabs with custom categories for work and expenses
- New Analysis tab allows summarizing work and expense data in a 
  variety of ways. It is similar to cross-tabs and pivot tables.
- RTF export now respects the Resource filter, if active



Miradi 3.1.1 was a minor release with these changes compared to 3.1:

Application-wide:
- Clicking on a date editor button when there were unsaved changes in 
  another field would give an error, even though the operation worked

Diagram:
- Creating the first Results Chain from the Results Chain tab would 
  display an error, even though the operation worked
  
Strategic Plan:
- On the Custom tab, the Activities column for Goals and Objectives 
  was also displaying relevant Strategies, instead of just Activities

Work Plan:
- The "When" column editor offered the wrong choices for months if 
  quarter columns were being hidden
  
 
 
When comparing Miradi 3.1 to 3.0, there were hundreds of changes, 
but these are the most notable:

Application-wide:
- support for native system fonts - will improve the quality of image exports
- New pop up text editing windows for all multi line text fields -  allows 
  for easier editing of large text entries
- New table-based entry for Progress Reports and Progress Percents 
  that are used to report progress throughout the application.
  Miradi can now be opened by clicking .mpz and .cpmz files and 
  these files will have the Miradi icon
- Extensive changes and improvements to all export schemas

Diagram View:
- Zoom settings are now saved for each page 
- Extensive improvements to Group Boxes, including automatic link creation
- Increased the overall responsiveness of actions in Diagram View
- Projects imported from ConPro have much cleaner diagrams

Viability View:
- New “Expand to” feature allows for quick configuration of Viability tree
- Improvements to overall appearance of upper tree

Threat Rating View:
- New rating selection interface displays the full definitions
- Improvements to ensure that sorting actions are saved and apply to exports

Strategic Plan View:
- New option added to preferences allow the user to specify whether Goals 
  and Objectives should appear within diagrams or at the top of the tree
- Eliminated some cases where items would appear more than once in the tree
- Added Indicators and Activities as selectable rows in the Custom tab
- Custom tab now has more column choices available

Work Plan View:
- New preference to include work plan data from Conceptual Model pages, 
  Results Chains or Both
- Budget Total columns added to the Resources tab
- New pop up “when” editor for easier high-level planning
- High-level estimates that have been overridden by a finer level of detail 
  are now shown with strike-through formatting
- New preference to allow the exclusion of quarters as columns
- Fixed some Fiscal Year related issues.
- Improvements to the handling of changes to column widths


Version 3.0 was a significant upgrade, with these fixes over 2.4.1:

INSTALLER
- On Windows, now requires Java 6 (will download it if necessary)
- Updated Marine Example project

APPWIDE
- Lower panel sections are now "side-tabs" to improve usability
- Normal data migrations no longer ask about replacing existing copy
- Trees now have tooltips for entries in the Item column
- Now allow entering pre-1970 dates for measurements, progress reports, etc
- File/Save As dialog now treats Enter as OK instead of cancel
- Clicking in a multi-line field no longer auto-selects all the text
- Updated wizard and help content to reflect new features

WELCOME
- Project tree now allows create/delete/rename folders
- Project tree allows moving projects between folders

SUMMARY
- TNC tab:
  - Updated Operating Unit and Ecoregion lists
  - Project Priority and Type fields 

DIAGRAM
- Now allow Human Welfare Targets (as opposed to Biodiversity Targets)
  (Must enable HWT mode via Edit/Preferences)
- Pasted factors now retain their tags
- Can now toggle factors between Direct Threat and Contributing Factor
- Scope Boxes are now manually controlled instead of automatic
- Can now have multiple Scope Boxes per diagram
- Fixed various minor issues including bend point dragging
- Fixed export as image to show bidirectional links correctly
- Group Boxes now reserve space for as many lines of text as the 
  name has hard line breaks

VIABILITY
- Project overall rollup now includes Simple-mode Target ratings

THREAT RATING
- No longer allow creating new links, because Threats no longer have to 
  be linked directly to Targets

STRATEGIC PLAN
- Renamed Planning View to Strategic Plan View
- Now has tabs for Custom and Lists subviews
- No longer have Control Bar at left side of view
- Streamlined buttons that create/delete objects
- Reduced duplication of objects in the tree
- Progress columns in tables now show shorter text strings
- Now show Percent Complete data for Goal rows
- No longer show "Not Specified" in Progress column for Task rows

WORK PLAN
- Entirely new view to handle budgeting
- New concept: Expenses, which are monetary expenditures that are not 
  tied to a Resource; eliminated "Materials" resource type because those 
  generally would be better recorded as expenses
- Shows Work Units, Expenses, and Budget Totals in columns that can be 
  expanded or collapsed to view data at different time scales (years, 
  quarters, months, or individual days)
- Budget data can be edited directly in the upper table in some cases
- Replaced old High Level Estimate feature with the ability to assign 
  resources directly to Strategies and Indicators
- Budget values can be filtered to only show data for specific resources
- Resources tab shows budgets by resource, along with work that has not 
  yet been assigned to any resource
- Accounting Codes tab now shows budgets totaled by accounting code
- Funding Sources tab now shows budgets totaled by funding source
- All budgeting is now done in days, so no longer support resource costs
  in other units
- Pop-up calculator allows entering work unit data as Full Time 
  Employee (FTE) fractions instead of days

REPORTS/RTF EXPORT
- New Threat Rating Details section
- Hard line breaks are now preserved

CPMZ IMPORT/EXPORT
- New CPMZ file version (can no longer import/export old format CPMZ files)
- Many more fields and objects transfer between Miradi and ConPro
- Imported "all on one page" diagram is now automatically arranged, 
  reducing the number and crossing of links

Version 2.4.1 was a minor release with these fixes over 2.4:

PLANNING VIEW
- Projects that were originally created in The Nature Conservancy's
  ConPro system, and then imported into Miradi as CPMZ files, 
  could show incorrect relationships between Targets, Threats, and 
  Strategies. It is NOT necessary to re-import these projects. 
  Simply upgrading to Miradi 2.4.1 will correct the Planning View tree.
  

Here are the most notable improvements of version 2.4 over 2.3.1:

INSTALLERS
- Windows Installer is 85% smaller, for faster downloads
- Much easier Mac installation (using a standard DMG file)

WELCOME
- Welcome page project list now shows Last Modified date for each project
- Welcome page project list can now be sorted by clicking on headers

DIAGRAM
- Ability to have links on some diagrams and not others
- Diagram Factor Properties dialog allows switching between factors
- Create Bendpoint Junction now preserves existing bend points
- Select link, Create Factor gets "inserted" into that link
- Group Boxes can now have custom font and color

THREAT RATING
- Lower panel in Simple mode has been rearranged
- Upper grid visual improvements
- Upper grid now saves column widths and sequence

PLANNING VIEW
- New row and column types available in Planning View
- WorkPlan: Better display of shared/allocated budget values
- Planning tree: Indicators now appear within IR's and TRR's
- Planning tree now remembers width of Item column

REPORTS/RTF
- RTF reports now color entire rating cells

APPWIDE
- New "Save As" functionality replaces "Save Copy To..."
- Row heights are now saved per-table within each project
- Table column widths and sequence are now saved per-project
- Factors now show separate lists for "appears on" Conceptual Model 
  pages and Results Chains

NATURE CONSERVANCY
- TNC tab now has a "data sharing" field
- ConPro (CPMZ) export now includes goals and IR/TRR objectives
- ConPro (CPMZ) import has several minor improvements



Version 2.3.1 was a minor release with these fixes over 2.3:

DIAGRAM:
- FIXED: Deleting a Strategy no longer removes any other shares of that 
  strategy from associated objectives and goals
- FIXED: Planning View export to RTF no longer gets errors for 
  Assignment rows
- Changed Spanish translations: 
  "Aceptable" -> "Regular", and "Malo" -> "Pobre"
- FIXED: Spanish translation was causing Diagram View Control Bar to 
  be too wide
  

Version 2.3 had these enhancements over version 2.2.2:

APPWIDE
- Objectives are now only initally associated with strategies 
  that are directly linked to the containing factor
- Goals now behave more like objectives, so have a Percent Complete 
  field, and can be associated with indicators, strategies, and 
  activities
- ConPro (CPMZ) import/export is now officially supported
- Activities, Methods, and Tasks now have an Id field
- Text fields now support Ctrl-Z/Y for field-level undo/redo
- Date fields now allow abbreviated date entry: entering just a year, 
  or just a year and month, will fill in default values for the rest
- News on the welcome page news is only displayed when it is updated
- Improved Automatic Row Height mode
- Tree expand/collapse now works reliably when items appear more than 
  once within the tree
- Now remember previous language setting when restarting Miradi
- Installing language packs can now be done from within 
  Miradi, avoiding the need to manually copy files around

SUMMARY:
- TNC tab now shows Lessons Learned field
- TNC tab no longer has obsolete readonly legacy Country, 
  Operating Unit, and Ecoregion fields

DIAGRAM:
- New Tagging feature:
  - Can create any number of tags (tag sets)
  - Can assign any factors to any tags
  - Each diagram page can show only factors with certain tags
- Export JPEG/PNG improvements:
  - Can control image resolution by zooming in/out
  - Image size is consistent regardless of which factors are shown 
    or hidden (handy for creating slide show presentations)
  - Works correctly in Brainstorm Mode
- New "Create Margin" feature, which automatically nudges all the 
  factors right/down as needed to create space to insert new factors
- Switching in/out of Brainstorm Mode now automatically resizes 
  group boxes

VIABILITY:
- Target properties can now be viewed/edited in the lower panel
- Targets can be switched between Simple and KEA mode
- Now shows indicators for simple-mode targets, and allows new 
  indicators to be created for simple-mode targets

THREAT RATING:
- Simple and Stress-Based modes now use the same user interface
- Stress-based grid is now sortable
- Stress-based table has better width controls
- No longer shows targets that are only on Results Chains 
  (since they can't be linked to Direct Threats)

PLANNING:
- Table to manage assignments has much better width resizing tools
- Assignments table now shows resource names in addition to their ids
- Tree now lists each Conceptual Model page separately
- New rows available for custom subviews: Contributing Factor
  and Assignment
- New Work Units column for assignments, included in Work Plan subview
- Removed Measurements-only, and added Targets-only and Threats-only
- Can now create and delete objectives
- Work Plan no longer includes Conceptual Model elements
- Tree now includes related indicators, strategies, and activities, 
  even if they are on a different diagram from the objective/goal
- Planning View Activities-only and Methods-only subviews now 
  display more sensible cost totals for shared items

REPORTS:
- Reports (custom) now offers a Viability Details section
- Various minor improvements to report output
  

Version 2.2.2 was a minor release with these enhancements:

- The Planning View tree is now built differently, presenting the rows in 
  a different order to reduce duplication
- Planning View sometimes didn't list KEA Indicators 
- Diagram layer checkboxes were not being respected during print/export
- Internal changes to prepare for a Spanish version
- Fixed a typo in the report output ("Verion")


Version 2.2.1 was a minor release that fixed a few problems with 2.2.0:

- Deleting KEA Indicators in Planning View could delete other data
  within the KEA
- Fixed a compatibility problem with Java 5 on Mac/Linux
- Threat Rating View (Stress-based) now wraps Threat Name column
- Fixed minor problem exporting non-ASCII characters to RTF


Here are the most notable improvements of Miradi 2.2 over Miradi 2.1:

- Can now import and export ConPro projects (BETA)
- Can now export almost any tab of any view as an RTF document
- Optional Automatic Row Height mode for all tables
- Objectives can now be associated with Activities, as well as Strategies
- Objective-Indicator relevancy dialog now lists associated factors
- Activity, Method, and Task names can now have hard line breaks

DIAGRAM:
- Paste now preserves Group Box Links
- Select Chain and Create Results Chain now include Group Boxes
- Checkboxes to hide/show layers is now saved per-diagram
- Now offer better selection of factor and link colors

VIABILITY:
- Tree now has Expand All and Collapse All buttons

THREAT RATING:
- Stress-based rating table now allows much better resizing
- FIXED: The "Majority Override" rule was not applied correctly in 
  certain rare cases

PLANNING VIEW:
- Custom views can now include rows for Intermediate Results 
- Progress column now shows Objective Percent Complete values
- When Measurement rows are not shown, Indicator row shows latest value
- Tree now has Expand All and Collapse All buttons
- Can now edit basic factor details from this view
- FIXED: Shared objects were sometimes omitted from the tree
- FIXED: Budget table now rounds instead of truncating

REPORTS:
- Replaced entire Reports View with a new RTF-based system
- Significant changes to Strategic Plan and Monitoring Plan 
- New "Progress Report" standard template


Here are the most notable improvements of Miradi 2.1 over Miradi 2.0:

APPLICATION-WIDE
- Much improved handling of project names during creation, rename, and import
- Improved sorting rules for Measurements and Progress Reports
- Objectives have a percent-complete table

DIAGRAM
- Can now set the color of links
- New command to create "bendpoint junctions" near factors
- Stresses are available even when in Simple Threat Rating mode
- Stresses can now be displayed in movable bubbles in the Conceptual Model
- Activities can now be displayed in movable bubbles in Results Chains
- Improved behavior of double-click within Group Boxes and Scope Box
- Group Boxes are now selected as part of a chain
- Can now Copy/Paste factors between two running copies of Miradi
- Can paste a factor's attributes into another existing factor of the 
  same type

THREAT RATING
- Fixed some errors in the Stress-Based calculations

PLANNING
- Row and column headers are now "locked" during scrolling

EXPORTING
- Tab-delimited files now handle factor names containing newlines

REPORTS
- Several minor improvements to formatting

WINDOWS INSTALLER
- Installer itself can now run in Spanish, Portuguese, and Indonesian


NOTE: Miradi 2.2.x uses different rules than 2.0 for wrapping and 
truncating text in factors and group boxes, so some boxes may need to 
be resized to re-format the text.


NOTES FOR MIRADI 1.0 USERS ONLY: 

1. On MS Windows, if your Documents directory is not on your C: drive, 
you may get a warning during installation. In this case, you will need to 
manually copy any old projects from the old project data location to the 
new one. Both directories will be mentioned in the warning message.

2. If you created your own shortcut to run Miradi, it will need to be 
updated. Instead of launching java manually, the shortcut should now 
execute one of the miradi launchers, which have .vbs extensions and 
can be found in the same directory as miradi.jar.

  
---------------------

For an up-to-date list of known issues and possible 
workarounds, see: http://miradi.org/help
