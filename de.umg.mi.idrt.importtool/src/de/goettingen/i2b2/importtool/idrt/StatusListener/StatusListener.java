package de.goettingen.i2b2.importtool.idrt.StatusListener;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;

import de.umg.mi.idrt.idrtimporttool.ImportWizard.CSVImportWizard;
import de.umg.mi.idrt.idrtimporttool.ImportWizard.DBImportWizard;
import de.umg.mi.idrt.idrtimporttool.ImportWizard.ODMImportWizard;
import de.umg.mi.idrt.idrtimporttool.ImportWizard.P21ImportWizard;
import de.umg.mi.idrt.idrtimporttool.Log.Log;
import de.umg.mi.idrt.idrtimporttool.importidrt.ServerView;

/**
 * @author Benjamin Baum <benjamin(dot)baum(at)med(dot)uni-goettingen(dot)de>
 *         Department of Medical Informatics Goettingen
 *         www.mi.med.uni-goettingen.de
 */
public class StatusListener {

	private static boolean interrupted;
	public static float perc;
	public static float subPerc;
	public static String subStatus = "";
	public static String status = "";
	public static String file = "";
	public static int logCounter = 0;
	public static boolean sleeping = false;
	public static String log;
	public static String filename;
	public static ScheduledExecutorService executor;

	/**
	 * @return the subPerc
	 */
	public static float getSubPerc() {
		return subPerc;
	}

	/**
	 * @param subPerc the subPerc to set
	 */
	public static void setSubPerc(float subPerc) {
		StatusListener.subPerc = subPerc;
	}

	public static void addError(final String logg, final String fileName) {
		
		final File file = new File(fileName);
		filename = file.getName();
		log = logg;
		logCounter++;
		System.out.println("ERRORLOG: " + logCounter + " -- " + filename + " - " + log);
	}

	public static void addLog(final String log, final String fileName) {
		final File file = new File(fileName);
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				Log.addLog(0, log + " - " + file.getName());

			}});
	}
	public static void startLogging() {
		System.out.println("START LOGGING");
		Runnable helloRunnable = new Runnable() {
			public void run() {
				try {
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							System.out.println("autolog");
							if (logCounter>0)
							Log.addLog(0, log + " - " + logCounter +" more Errors " + filename);
							logCounter=0;
						}});
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(helloRunnable, 3, 3, TimeUnit.SECONDS);

	}

	public static void stopLogging() {
		System.out.println("STOP LOGGING");
		executor.shutdown();
	}

	public static void error(final String msg, final String error,
			final String fileName) {
		// System.out.println("errorrr");
		// interrupt();

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				interruptExtern(error, fileName);

				//				CSVImportWizard.killThreadRemote(msg, fileName);
				//				ODMImportWizard.killThreadRemote(msg, fileName);
				//				ODMImportWizardSpecific.killThreadRemote(msg, fileName);
				//				DBImportWizard.killThreadRemote(msg, fileName);
				//				P21ImportWizard.killThreadRemote(msg, fileName);
				Log.addLog(1, msg + " - " + error);
			}
		});
	}

	public static void errorInfo(final String msg, final String error,
			final String fileName) {
		final File file = new File(fileName);
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				Log.addLog(1, msg + " - " + error + " @ " + file.getName());
				System.err.println(error);
			}
		});
	}

	public static String getFile() {
		return file;
	}

	public static boolean getInterrupt() {
		return interrupted;
	}

	public static float getPercentage() {
		if (perc == 0) {
			return perc + 1;
		} else {
			return perc;
		}
	}

	public static String getStatus() {
		return status;
	}

	public static void interrupt() {
		interrupted = true;
		StatusListener.stopLogging();
		CSVImportWizard.killThread();
		ODMImportWizard.killThread();
		DBImportWizard.killThread();
		P21ImportWizard.killThread();
	}
	public static void interruptExtern(String error, String fileName) {
		interrupted = true;
		CSVImportWizard.killThreadRemote(error,fileName);
		ODMImportWizard.killThreadRemote(error,fileName);
		DBImportWizard.killThreadRemote(error,fileName);
		P21ImportWizard.killThreadRemote(error,fileName);
	}

	public static void notifyListener() {
		interrupted = false;
	}

	/**
	 * @return the subStatus
	 */
	public static String getSubStatus() {
		return subStatus;
	}

	public static void setStatus(float percentage, String currentFile) {
		File fileName = new File(currentFile);
		perc = percentage;
		status = "Getting PIDs...";
		file = fileName.getName();
		ServerView.updateStatus();
		//		DBImportWizard.updateStatus();
		//		ODMImportWizard.updateStatus();
		//		ODMImportWizardSpecific.updateStatus();
		//		P21ImportWizard.updateStatus();
	}

	public static void setStatus(float percentage, final String statusMsg,
			final String currentFile) {
		File fileName = new File(currentFile);
		perc = percentage;
		status = statusMsg;
		file = fileName.getName();
		System.out.println("STATUS: " + statusMsg + " " + percentage);
		ServerView.updateStatus();
		//		DBImportWizard.updateStatus();
		//		ODMImportWizard.updateStatus();
		//		ODMImportWizardSpecific.updateStatus();
		//		P21ImportWizard.updateStatus();

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				Log.addLog(0, statusMsg + " - " + currentFile);
			}
		});
	}
	public static void setStatus(final String statusMsg) {
		//		File fileName = new File(currentFile);
		//		perc = percentage;
		status = statusMsg;
		//		file = fileName.getName();
		//		System.out.println("STATUS: " + statusMsg + " " + percentage);
		ServerView.updateStatus();
		//		DBImportWizard.updateStatus();
		//		ODMImportWizard.updateStatus();
		//		ODMImportWizardSpecific.updateStatus();
		//		P21ImportWizard.updateStatus();

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				Log.addLog(0, statusMsg);
			}
		});
	}
	public static void setSubStatus(float percentage, final String statusMsg) {
		subPerc = percentage;
		subStatus = statusMsg;
		//		System.out.println("SUBSTATUS: " + statusMsg + " " + percentage);
		ServerView.updateStatus();
		//		DBImportWizard.updateStatus();
		//		ODMImportWizard.updateStatus();
		//		ODMImportWizardSpecific.updateStatus();
		//		P21ImportWizard.updateStatus();
	}
	public static void setStatusPID(float percentage, String msg,
			String currentFile) {
		File fileName = new File(currentFile);
		perc = percentage;
		status = msg;
		file = fileName.getName();
		ServerView.updateStatus();
		//		DBImportWizard.updateStatus();
		//		ODMImportWizard.updateStatus();
		//		ODMImportWizardSpecific.updateStatus();
		//		P21ImportWizard.updateStatus();
	}
}