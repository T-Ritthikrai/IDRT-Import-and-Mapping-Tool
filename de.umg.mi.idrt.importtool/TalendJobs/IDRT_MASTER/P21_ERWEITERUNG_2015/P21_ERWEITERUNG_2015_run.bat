%~d0
cd %~dp0
java -Xms256M -Xmx1024M -cp .;../lib/routines.jar;../lib/commons-collections-3.2.2.jar;../lib/log4j-1.2.15.jar;../lib/log4j-1.2.16.jar;../lib/dom4j-1.6.1.jar;../lib/talendcsv.jar;../lib/trove.jar;../lib/talend_file_enhanced_20070724.jar;../lib/OpenCSV.jar;../lib/StatusListener29.jar;../lib/advancedPersistentLookupLib-1.0.jar;../lib/jboss-serialization.jar;p21_erweiterung_2015_0_1.jar;ops_3_0_1.jar;icd_3_0_1.jar;fab_3_0_1.jar; i2b2transmart.p21_erweiterung_2015_0_1.P21_ERWEITERUNG_2015 --context=Default %* 