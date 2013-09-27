package de.umg.mi.idrt.idrtimporttool.ImportWizard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

import de.umg.mi.idrt.idrtimporttool.importidrt.Activator;
import de.umg.mi.idrt.idrtimporttool.messages.Messages;

/**
 * @author Benjamin Baum <benjamin(dot)baum(at)med(dot)uni-goettingen(dot)de>
 *         Department of Medical Informatics Goettingen
 *         www.mi.med.uni-goettingen.de
 */
public class ODMWizardPage2 extends WizardPage {

	private Composite container;
	private static Text folderODMText;
	private static Text folderMainText;
	private static String odmPath = ""; 
	private static String completeCodelist;
	// private static String mainPath = "";
	private static Button checkIncludePids;
	private static Button checkContext;
	private static Button checkTruncate;
	private static Button checkTerms;
	private static Button checkCleanUp;
	private static Button checkCompleteCodelists;

	public static boolean getCleanUp() {
		return checkCleanUp.getSelection();
	}
	
	/**
	 * @return the completeCodelist
	 */
	public static boolean getCompleteCodelist() {
		return checkCompleteCodelists.getSelection();
	}

	/**
	 * @return the folderMainText
	 */
	public static String getFolderMainText() {
		return folderMainText.getText();
	}

	/**
	 * @return the folderText
	 */
	public static String getFolderODMText() {
		return folderODMText.getText();
	}

	/**
	 * @return the check
	 */
	public static boolean getIncludePids() {
		return checkIncludePids.getSelection();
	}

	/**
	 * @return the path
	 */
	public static String getPath() {
		return odmPath;
	}


	/**
	 * @return the checkContext
	 */
	public static boolean getSaveContext() {
		return checkContext.getSelection();
	}

	public static boolean getTerms() {
		return checkTerms.getSelection();
	}

	public static boolean getTruncate() {
		return checkTruncate.getSelection();
	}

	public ODMWizardPage2() {
		super(Messages.ODMWizardPageTwo_ODMImportSettings);
		setTitle(Messages.ODMWizardPageTwo_ODMImportSettings); 
		setDescription(Messages.ODMWizardPageTwo_ODMImportSettings); 
	}

	@Override
	public void createControl(Composite parent) {
		try {
			Bundle bundle = Activator.getDefault().getBundle();
			Path path = new Path("/cfg/Default.properties"); 
			URL url = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
			URL fileUrl = null;

			fileUrl = FileLocator.toFileURL(url);
			File properties = new File(fileUrl.getPath());
			Properties defaultProps = new Properties();
			defaultProps.load(new FileReader(properties));
			container = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout(3, true);
			container.setLayout(layout);

			Label truncateLabel = new Label(container, SWT.FILL | SWT.CENTER);
			truncateLabel.setText(Messages.ODMWizardPageTwo_TruncateProject);
			truncateLabel.setToolTipText(Messages.ODMWizardPageTwo_TruncateProjToolTip);
			checkTruncate = new Button(container, SWT.CHECK);
			checkTruncate.setSelection(false);

			new Label(container, SWT.NONE);

			Label cleanUplabel = new Label(container, SWT.SHADOW_IN | SWT.CENTER);
			cleanUplabel.setText(Messages.ODMWizardPageTwo_CleanUp);

			checkCleanUp = new Button(container, SWT.CHECK);
			checkCleanUp.setSelection(Boolean.parseBoolean(defaultProps
					.getProperty("cleanUp"))); 
			new Label(container, SWT.NONE);

			Label folder = new Label(container, SWT.FILL | SWT.CENTER);
			folder.setText(Messages.ODMWizardPageTwo_ODMFolder);
			folder.setToolTipText(Messages.ODMWizardPageTwo_ODMFolderToolTip);

			odmPath = defaultProps.getProperty("folderODM");
			
			final DirectoryDialog dlg = new DirectoryDialog(parent.getShell());
			dlg.setText(Messages.ODMWizardPageTwo_ODMFolder); 
			dlg.setFilterPath(defaultProps.getProperty("folderODM")); 

			folderODMText = new Text(container, SWT.FILL);
			folderODMText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					false, 1, 1));
			folderODMText.setText(odmPath);
			folderODMText.setEditable(false);
			folderODMText.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
					if (!folderODMText.getText().isEmpty()) {
						ODMWizardPage2.this.setPageComplete(true);
					}
				}
			});
			Button button = new Button(container, SWT.PUSH);
			button.setText("..."); 
			button.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					odmPath = dlg.open().replaceAll("\\\\", "/");  
					odmPath += "/"; 
					folderODMText.setText(odmPath);
				}
			});
			
			Label includeCodelists = new Label(container, SWT.NONE);
			includeCodelists.setText(Messages.ODMWizardPageTwo_ImportCodelists);
			
			completeCodelist = defaultProps.getProperty("importCodelist","false");
			
			checkCompleteCodelists = new Button(container, SWT.CHECK);
			checkCompleteCodelists.setSelection(Boolean.parseBoolean(completeCodelist));
			
			new Label(container, SWT.NONE);

			Label labelCheck = new Label(container, SWT.NONE);
			labelCheck.setText(Messages.ODMWizardPageTwo_IncludePid);
			checkIncludePids = new Button(container, SWT.CHECK);
			checkIncludePids.setSelection(false);

			new Label(container, SWT.NONE);

			Label labelImportTerms = new Label(container, SWT.NONE);
			labelImportTerms.setText(Messages.ODMWizardPageTwo_ImportSTandMap);
			labelImportTerms
					.setToolTipText(Messages.ODMWizardPageTwo_ImportSTandMapToolTip);
			checkTerms = new Button(container, SWT.CHECK);
			checkTerms.setSelection(false);
			new Label(container, SWT.NONE);
			Label labelSaveContext = new Label(container, SWT.NONE);
			labelSaveContext.setText(Messages.ODMWizardPageTwo_SaveSettings);
			checkContext = new Button(container, SWT.CHECK);
			checkContext.setSelection(false);
			setControl(container);
			new Label(container, SWT.NONE);
			setPageComplete(true);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}