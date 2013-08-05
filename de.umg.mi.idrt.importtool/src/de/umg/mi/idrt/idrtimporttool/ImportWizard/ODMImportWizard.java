package de.umg.mi.idrt.idrtimporttool.ImportWizard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import de.goettingen.i2b2.importtool.idrt.StatusListener.StatusListener;
import de.umg.mi.idrt.idrtimporttool.Log.Log;
import de.umg.mi.idrt.idrtimporttool.importidrt.Activator;
import de.umg.mi.idrt.idrtimporttool.importidrt.IDRTImport;
import de.umg.mi.idrt.idrtimporttool.importidrt.ServerView;
import de.umg.mi.idrt.idrtimporttool.messages.Messages;

/**
 * @author Benjamin Baum <benjamin(dot)baum(at)med(dot)uni-goettingen(dot)de>
 *         Department of Medical Informatics Goettingen
 *         www.mi.med.uni-goettingen.de
 */
public class ODMImportWizard extends Wizard {

	private Properties defaultProps;
	public static boolean started = false;
	private static Thread b;

	private HashMap<String, String> contextMap;

	protected WizardPageOne one;
	protected ODMWizardPageTwo two;

	public boolean done = false;
	
//	private static void closeBar(String msg, int status) {
//		closeBar(msg, "", status); 
//	}
//
//	private static void closeBar(String msg, final String fileName, int status) {
//		ServerView.closeBar(msg, fileName, status);
//	}

	@SuppressWarnings("deprecation")
	public static void killThread() {
		if (started) {
			b.stop();
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
//					closeBar(Messages.ODMImportWizard_ODMImportFailed, 1);
					Log.addLog(1, Messages.ODMImportWizard_ODMImportFailed); 
					MessageDialog
							.openError(
									Display.getDefault().getActiveShell(),
									Messages.ODMImportWizard_ODMImportFailed, 
									Messages.ODMImportWizard_ODMImportFailed
											+ ". (User aborted!)\nYou might want to redo your last action!"); 
				}
			});
		}
		StatusListener.notifyListener();
		started = false;
	}

	@SuppressWarnings("deprecation")
	public static void killThreadRemote(final String error,
			final String fileName) {
		if (started) {
			b.stop();

			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
//					closeBar(error, fileName, 1);
					Log.addLog(1, "ODM Import Failed: " + error); 
					MessageDialog.openError(Display.getDefault()
							.getActiveShell(),
							Messages.ODMImportWizard_ODMImportFailed, 
							Messages.ODMImportWizard_ImpFailedSeeLog);
				}
			});
		}
		StatusListener.notifyListener();
		started = false;
	}

	public static void updateStatus() {
		if (started) {
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					ServerView.setProgress((int) StatusListener.getPercentage());
					ServerView.setProgressTop(StatusListener.getFile());
					ServerView.setProgressBottom("" 
							+ StatusListener.getStatus());
				}
			});
		}
	}

	

	public ODMImportWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		one = new WizardPageOne();
		two = new ODMWizardPageTwo();
		addPage(one);
		addPage(two);
	}

	@Override
	public boolean performFinish() {
		try {
			//			System.out.println("ODM: finish"); 
			final boolean cleanUp = ODMWizardPageTwo.getCleanUp();
			final boolean terms = ODMWizardPageTwo.getTerms();
			Bundle bundle = Activator.getDefault().getBundle();
			Path path = new Path("/cfg/Default.properties"); 
			URL url = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
			URL fileUrl = FileLocator.toFileURL(url);
			File properties = new File(fileUrl.getPath());
			defaultProps = new Properties();
			defaultProps.load(new FileReader(properties));

			contextMap = new HashMap<String, String>();

			/*
			 * page 1
			 */
			contextMap.put("DBHost", WizardPageOne.getIpText()); 
			defaultProps.setProperty("DBHost", WizardPageOne.getIpText()); 

			contextMap.put("DBPassword", WizardPageOne.getDBUserPasswordText()); 
			// defaultProps.setProperty("pw",
			// WizardPageOne.getDBUserPasswordText());

			contextMap.put("DBUsername", WizardPageOne.getDBUserText()); 
//			defaultProps.setProperty("DBUsername", WizardPageOne.getDBUserText()); 

			contextMap.put("DBInstance", WizardPageOne.getDBSIDText()); 
//			defaultProps.setProperty("DBInstance", WizardPageOne.getDBSIDText()); 

			contextMap.put("DBPort", WizardPageOne.getPortText()); 
//			defaultProps.setProperty("DBPort", WizardPageOne.getPortText()); 

			contextMap.put("DBSchema", WizardPageOne.getDBSchemaText()); 
//			defaultProps.setProperty("DBSchema", WizardPageOne.getDBSchemaText()); 

			/*
			 * page 2
			 */
//			defaultProps.setProperty("importSingleFile", "false");  
			contextMap.put("importSingleFile", "false");  

			contextMap.put("folderODM", ODMWizardPageTwo.getFolderODMText()); 
//			defaultProps.setProperty("folderODM", 
//					ODMWizardPageTwo.getFolderODMText());

			Path miscPath = new Path("/misc/"); 
			URL miscUrl = FileLocator.find(bundle, miscPath,
					Collections.EMPTY_MAP);
			URL miscFileUrl = FileLocator.toFileURL(miscUrl);
			File misc = new File(miscFileUrl.getPath());
			String miscPathReplaced = misc.getAbsolutePath().replaceAll("\\\\", 
					"/") 
					+ "/"; 

			contextMap.put("folderMain", miscPathReplaced); 
//			defaultProps.setProperty("folderMain", miscPathReplaced); 

			if (ODMWizardPageTwo.getTerms()) {
				/*
				 * ST-Import
				 */
				Path cfgPath = new Path("/cfg/"); 
				URL cfgUrl = FileLocator.find(bundle, cfgPath,
						Collections.EMPTY_MAP);
				URL cfgFileUrl = FileLocator.toFileURL(cfgUrl);

				File rootDir = new File(cfgFileUrl.getPath()
						+ "/Standardterminologien/".replaceAll("\\\\", "/"));   

				System.out.println("**********************"); 
				System.out.println("STFOLDER: " 
						+ rootDir.getAbsolutePath().replaceAll("\\\\", "/"));  
				System.out.println("**********************"); 

				File icd10Dir = new File(rootDir.getAbsolutePath()
						+ "/ICD-10-GM/"); 
				contextMap.put("icd10Dir", icd10Dir.getAbsolutePath() 
						.replaceAll("\\\\", "/") + "/");   
//				defaultProps.setProperty("icd10Dir", icd10Dir.getAbsolutePath() 
//						.replaceAll("\\\\", "/") + "/");   

				File tnmDir = new File(rootDir.getAbsolutePath() + "/TNM/"); 
				contextMap.put("tnmDir", 
						tnmDir.getAbsolutePath().replaceAll("\\\\", "/") + "/");   
//				defaultProps.setProperty("tnmDir", tnmDir.getAbsolutePath() 
//						.replaceAll("\\\\", "/") + "/");   

				contextMap.put("rootDir", 
						rootDir.getAbsolutePath().replaceAll("\\\\", 
								"/") 
								+ "/"); 
//				defaultProps.setProperty("rootDir", rootDir.getAbsolutePath() 
//						.replaceAll("\\\\", "/") + "/");   

				File loincDir = new File(rootDir.getAbsolutePath() + "/LOINC/"); 
				contextMap.put("loincDir", loincDir.getAbsolutePath() 
						.replaceAll("\\\\", "/") + "/");   
//				defaultProps.setProperty("loincDir", loincDir.getAbsolutePath() 
//						.replaceAll("\\\\", "/") + "/");   

				File opsDir = new File(rootDir.getAbsolutePath() + "/OPS/"); 
				contextMap.put("opsDir", 
						opsDir.getAbsolutePath().replaceAll("\\\\", "/") + "/");   
//				defaultProps.setProperty("opsDir", opsDir.getAbsolutePath() 
//						.replaceAll("\\\\", "/") + "/");   

				File p21Dir = new File(rootDir.getAbsolutePath() + "/P21/"); 
				contextMap.put("p21Dir", 
						p21Dir.getAbsolutePath().replaceAll("\\\\", "/") + "/");   
//				defaultProps.setProperty("p21Dir", p21Dir.getAbsolutePath() 
//						.replaceAll("\\\\", "/") + "/");   

				File drgDir = new File(rootDir.getAbsolutePath() + "/DRG/"); 
				contextMap.put("drgDir", 
						drgDir.getAbsolutePath().replaceAll("\\\\", "/") + "/");   
//				defaultProps.setProperty("drgDir", drgDir.getAbsolutePath() 
//						.replaceAll("\\\\", "/") + "/");   

				File icdoDir = new File(rootDir.getAbsolutePath() + "/ICD-O-3/"); 
				contextMap.put("icdoDir", 
						icdoDir.getAbsolutePath().replaceAll("\\\\", 
								"/") 
								+ "/"); 
//				defaultProps.setProperty("icdoDir", icdoDir.getAbsolutePath() 
//						.replaceAll("\\\\", "/") + "/");   
			}

			if (cleanUp) {
				defaultProps.setProperty("cleanUp", "true");  
				contextMap.put("cleanUp", "true");  
			} else {
				defaultProps.setProperty("cleanUp", "false");  
				contextMap.put("cleanUp", "false");  
			}

			if (ODMWizardPageTwo.getIncludePids()) {
				contextMap.put("includePids", "true");  
				defaultProps.setProperty("includePids", "true");  
			} else {
				contextMap.put("includePids", "false");  
				defaultProps.setProperty("includePids", "false");  
			}

			if (ODMWizardPageTwo.getSaveContext()) {
				System.out.println("saving context"); 
				defaultProps.store(new FileWriter(properties), ""); 
			}

			if (ODMWizardPageTwo.getTruncate()) {
				System.out.println("truncating"); 
				contextMap.put("truncateProject", "true");  
//				defaultProps.setProperty("truncateProject", "true");  
			} else {
				contextMap.put("truncateProject", "false");  
//				defaultProps.setProperty("truncateProject", "false");  
			}

			done = false;

			b = new Thread(new Runnable() {

				@Override
				public void run() {
					started = true;
					final long start = System.currentTimeMillis();
					IDRTImport.setCompleteContext(contextMap);
					int exitCode = IDRTImport.runODMImport(terms);
					//					System.out.println("exitCode: " + exitCode); 
					done = true;
					started = false;
					StatusListener.setStatus(0, "", "");  
					StatusListener.setSubStatus(0.0f, "");
					StatusListener.notifyListener();

					if (exitCode == 0) {
						Display.getDefault().syncExec(new Runnable() {

							@Override
							public void run() {
								Log.addLog(0,
										Messages.ODMImportWizard_ODMImpFinished);
								long end = System.currentTimeMillis();
								long time = end - start;
								Log.addLog(0, Messages.ODMImportWizard_Duration
										+ (time / 1000) + " s"); 
								MessageDialog
										.openInformation(
												Display.getDefault()
														.getActiveShell(),
												Messages.ODMImportWizard_ODMImpFinished,
												Messages.ODMImportWizard_ODMImpFinished);  
							}
						});
					}
				}
			});
			if (!started) {
				b.start();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}