package de.umg.mi.idrt.idrtimporttool.server.serverWizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import de.umg.mi.idrt.idrtimporttool.importidrt.Application;
import de.umg.mi.idrt.idrtimporttool.importidrt.IDRTImport;
import de.umg.mi.idrt.idrtimporttool.server.Settings.Server;
import de.umg.mi.idrt.idrtimporttool.server.Settings.ServerList;

/**
 * @author Benjamin Baum <benjamin(dot)baum(at)med(dot)uni-goettingen(dot)de>
 *         Department of Medical Informatics Goettingen
 *         www.mi.med.uni-goettingen.de
 */
public class AddSourceServerWizard extends Wizard {

	protected AddServerPageOne one;
private String activator;
	public AddSourceServerWizard(String activator) {
		super();
		setNeedsProgressMonitor(true);
		this.activator = activator;
	}

	@Override
	public void addPages() {
		one = new AddServerPageOne(activator);
		// two = new CSVWizardPageTwo();
		addPage(one);
		// addPage(two);
	}

	@Override
	public boolean performFinish() {
		Server newServer = new Server(AddServerPageOne.getUniqueIDText(),
				AddServerPageOne.getIpText(), AddServerPageOne.getPortText(),
				AddServerPageOne.getDBUserText(),
				AddServerPageOne.getDBUserPasswordText(),
				AddServerPageOne.getDBSIDText(),AddServerPageOne.getDBType(),AddServerPageOne.getCheckUseWinAuth(),AddServerPageOne.getCheckStorePassword());

		if (IDRTImport.testDB(newServer)) {
			ServerList.addSourceServer(newServer);
		} else {
			boolean result = MessageDialog
					.openConfirm(
							Application.getShell(),
							"Server not Responding!",
							"The Server cannot be reached. There might be an error in the settings.\nSave anyway?");
			if (result) {
				ServerList.addSourceServer(newServer);
			} else {
				return false;
			}
		}
		return true;
	}

}