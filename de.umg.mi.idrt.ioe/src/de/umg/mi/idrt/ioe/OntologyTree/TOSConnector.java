package de.umg.mi.idrt.ioe.OntologyTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;

import de.umg.mi.idrt.idrtimporttool.importidrt.ServerView;
import de.umg.mi.idrt.idrtimporttool.server.Settings.Server;
import de.umg.mi.idrt.idrtimporttool.server.Settings.ServerList;
import de.umg.mi.idrt.ioe.Activator;
import de.umg.mi.idrt.ioe.Application;
import de.umg.mi.idrt.ioe.Console;
import de.umg.mi.idrt.ioe.Debug;


public class TOSConnector {

	public static String DEFAULT_CONTEXTNAME = "Default";

	private static String contextName;
	private static HashMap<String, String> contextVariables = new HashMap<String, String>();

	public TOSConnector() {

		System.out.println("--->> activate TOSConnector");
		Debug.d("TOSConnector: activate!!!");

		/*
		 * String [][]tmpOutput = tos.runJob((getARGV()));
		 * 
		 * Debug.d("TOSConnector: >>> output");
		 * 
		 * for ( int i = 0; i < tmpOutput.length; i++ ){
		 * 
		 * for ( int j = 0; j < tmpOutput[i].length; j++ ) {
		 * 
		 * Debug.d("tos: " + tmpOutput[i][j]); } }
		 */
	}

	public static tos.tosidrtconnector_0_4.TOSIDRTConnector getConnection() {
		Debug.d("TOSIDRTCOnnector: gettingen Connection");

		tos.tosidrtconnector_0_4.TOSIDRTConnector tos = null;

		try {
			tos = new tos.tosidrtconnector_0_4.TOSIDRTConnector();

			File file = new File("asd");

			contextName = "Default";

			// trying to get the server-data from the currently selected server
			// in the servereditor

			// VM-Server
			tos = new tos.tosidrtconnector_0_4.TOSIDRTConnector();

			// if there is a i2b2-project selected in the server view, use it as
			// the
			// editors

			Bundle bundle = Activator.getDefault().getBundle();
			Path path = new Path("/cfg/Default.properties"); //$NON-NLS-1$
			URL url = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
			URL fileUrl = null;

			File properties = null;
			Properties defaultProps = null;

			try {
				fileUrl = FileLocator.toFileURL(url);
				properties = new File(fileUrl.getPath());
				defaultProps = new Properties();
				defaultProps.load(new FileReader(properties));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			String schema = ServerView.getCurrentSchema();
//					defaultProps.getProperty("schema");
			String serverUniqueName = ServerView.getSelectedServer();
			System.out.println(serverUniqueName);
			Server currentServer = null;
			if (serverUniqueName != null) {
				currentServer = ServerList.getTargetServers().get(serverUniqueName);
			}

			System.out.println("Current server: " + currentServer.toString());

			if (currentServer != null) {
				Console.info("Using selected server \""
						+ currentServer.getName() + "(\""
						+ currentServer.getSchema() + "\")\" for db query.");


				System.out.println("currentSchema:" + schema);
				System.out.println("sid: " + currentServer.getSID());
				System.out
						.println("OracleUsername: " + currentServer.getUser());

				setContextVariable("OracleHost", currentServer.getIp());
				setContextVariable("OraclePort", currentServer.getPort());
				setContextVariable("OracleSid", currentServer.getSID());
				setContextVariable("OracleUsername", currentServer.getUser());
				setContextVariable("OraclePassword",
						currentServer.getPassword());
				setContextVariable("OracleDB", currentServer.getSID());
				// setContextVariable("Job", "ontology");
				setContextVariable("SQLTable", currentServer.getTable());
				setContextVariable("OracleSchema", schema);

				Application.getStatusView().addMessage(
						"i2b2 project \"" + schema
								+ "\"selected via ServerView.");
			}

		} catch (Exception e) {
			Console.error("Error while using a TOS-plugin: " + e.getMessage());
			Debug.e("TOS-Error: " + tos.getErrorCode());
		}

		return tos;

	}

	public void getOntology() {
		System.out.println("TOSConnector: getOntology()");
		tos.tosidrtconnector_0_4.TOSIDRTConnector tos = getConnection();

		
		
		try {

			setContextVariable("Job", "ontology");
			setContextVariable("SQLTable", "I2B2");
			/*
			 * setContextVariable("SQLCommand",
			 * "SELECT * FROM I2B2IDRT.I2B2 WHERE C_HLEVEL > 0 ORDER BY C_HLEVEL ASC"
			 * );
			 */

			tos.runJobInTOS((getARGV()));

		} catch (Exception e) {
			String message = "Error while using a TOS-plugin with function getOntology(): "
					+ e.getMessage();
			Console.error(message);
			Application.getStatusView().addErrorMessage(message);
			System.out.println("TOS-Error2: " + tos.getErrorCode() + " / "
					+ tos.getEndpoint() + " / " + tos.getException() + " / "
					+ tos.getStatus() + " / " + tos.getExceptionStackTrace()
					+ " / " + tos.getContext().SQLCommand);

		}
	}

	public static void setContextVariable(String key, String value) {
		contextVariables.put(key, value);
	}

	public static String[] getARGV() {
		List<String> parameters = new ArrayList<String>();
		if (contextName != null) {
			parameters.add("--context=" + contextName);
		}
		for (String key : contextVariables.keySet()) {
			parameters.add("--context_param");
			parameters.add(key + "=" + contextVariables.get(key));
		}

		return (String[]) parameters.toArray(new String[0]);
	}

	public static void deleteTargetOntologyFull() {

		setContextVariable("Job", "delete_target_ontology");

	}

	public static int writeTargetOntology(String targetID, String tmpDataFile) {

		setContextVariable("Job", "write_target_ontology");
		setContextVariable("Var1", "1");
		setContextVariable("DataFile", tmpDataFile);

		try {
			tos.tosidrtconnector_0_4.TOSIDRTConnector tos = getConnection();
			tos.runJobInTOS((getARGV()));
		} catch (Exception e) {
			Console.error("Error while using a TOS-plugin with function writeTargetOntology(): "
					+ e.getMessage());
			return 1;
		}
		return 0;

	}

	public static void readTargetOntology(String targetID) {

		setContextVariable("Job", "read_target_ontology");
		setContextVariable("Var1", "1");

		try {
			tos.tosidrtconnector_0_4.TOSIDRTConnector tos = getConnection();
			tos.runJobInTOS((getARGV()));
		} catch (Exception e) {
			Console.error("Error while using a TOS-plugin with function readTargetOntology(): "
					+ e.getMessage());
		}

	}

	public static boolean checkOntology() {

		setContextVariable("Job", "check_ontology_empty");
		setContextVariable("Var1", "1");

		boolean hasOntology = false;

		try {

			tos.tosidrtconnector_0_4.TOSIDRTConnector tos = getConnection();

			tos.runJobInTOS((getARGV()));

			if (tos.getErrorCode() == 0) {
				hasOntology = true;
			}

		} catch (Exception e) {

			Console.error("Error while using a TOS-plugin with function writeTargetOntology(): "
					+ e.getMessage());

		}

		return hasOntology;

	}

	public static int loadSourceToTarget(String targetID, String tmpDataFile) {

		setContextVariable("Job", "load_source_to_target");
		setContextVariable("Var1", "1");
		setContextVariable("DataFile", tmpDataFile);

		try {
			tos.tosidrtconnector_0_4.TOSIDRTConnector tos = getConnection();
			tos.runJobInTOS((getARGV()));
		} catch (Exception e) {
			Console.error("Error while using a TOS-plugin with function loadSourceToTarget(): "
					+ e.getMessage());
			return 1;
		}
		return 0;

	}

}