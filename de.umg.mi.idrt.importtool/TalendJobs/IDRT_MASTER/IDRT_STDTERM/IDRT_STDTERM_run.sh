#!/bin/sh
cd `dirname $0`
ROOT_PATH=`pwd`
java -Xms256M -Xmx1024M -cp .:$ROOT_PATH:$ROOT_PATH/../lib/routines.jar:$ROOT_PATH/../lib/commons-collections-3.2.2.jar:$ROOT_PATH/../lib/log4j-1.2.15.jar:$ROOT_PATH/../lib/log4j-1.2.16.jar:$ROOT_PATH/../lib/dom4j-1.6.1.jar:$ROOT_PATH/../lib/postgresql-9.3-1102.jdbc4.jar:$ROOT_PATH/../lib/ojdbc14.jar:$ROOT_PATH/../lib/StatusListener29.jar:$ROOT_PATH/../lib/OpenCSV.jar:$ROOT_PATH/../lib/jaxen-1.1.1.jar:$ROOT_PATH/../lib/advancedPersistentLookupLib-1.0.jar:$ROOT_PATH/../lib/jboss-serialization.jar:$ROOT_PATH/../lib/thashfile.jar:$ROOT_PATH/../lib/talendcsv.jar:$ROOT_PATH/../lib/saxon9.jar:$ROOT_PATH/../lib/trove.jar:$ROOT_PATH/../lib/talend_file_enhanced_20070724.jar:$ROOT_PATH/../lib/xpathutil-1.0.0.jar:$ROOT_PATH/../lib/ojdbc6.jar:$ROOT_PATH/idrt_stdterm_0_1.jar:$ROOT_PATH/database_alter_tables_0_1.jar: transmart_etl.idrt_stdterm_0_1.IDRT_STDTERM --context=Default "$@" 