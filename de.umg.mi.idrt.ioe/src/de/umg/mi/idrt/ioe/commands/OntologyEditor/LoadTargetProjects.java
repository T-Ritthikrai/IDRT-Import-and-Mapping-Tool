package de.umg.mi.idrt.ioe.commands.OntologyEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.umg.mi.idrt.ioe.ActionCommand;
import de.umg.mi.idrt.ioe.Application;
import de.umg.mi.idrt.ioe.Console;
import de.umg.mi.idrt.ioe.Resource;
import de.umg.mi.idrt.ioe.OntologyTree.OntologyTreeTargetRootNode;
import de.umg.mi.idrt.ioe.OntologyTree.TOSConnector;
import de.umg.mi.idrt.ioe.OntologyTree.Target;
import de.umg.mi.idrt.ioe.OntologyTree.TargetInstance;
import de.umg.mi.idrt.ioe.OntologyTree.TargetInstances;
import de.umg.mi.idrt.ioe.view.OntologyEditorView;
import de.umg.mi.idrt.ioe.view.StatusView;


public class LoadTargetProjects extends AbstractHandler {

	
	public LoadTargetProjects(){
		super();
	}



	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		// deleting old entries
		OntologyEditorView.getTargetInstance().clear();
		
		Console.info("Command: LoadTargetProjects");
		
		TOSConnector tos = new TOSConnector();


		try {

			tos.setContextVariable("Job", "LoadTargetProjects");
			//tos.setContextVariable("SQLTable", "I2B2");
	
			tos.runJob();
			
			// create TargetProject and TargetVersion if nothing was found
			
			boolean newData = false;
			
			TargetInstances targetProjects = OntologyEditorView.getTargetInstance();

			if ( targetProjects.getSelectedTargetInstance() == null ){
				System.out.println("Loading: no TargetProjec -> creating new one");
				TargetInstance targetProject = new TargetInstance();
				targetProject.setName( "new target instance" );
				targetProject.setDescription( "description" );
				targetProjects.add(targetProject);
				targetProjects.setSelectedTargetProject(targetProject);
				newData = true;
				
			}

			if ( targetProjects.getSelectedTarget() == null ){
				System.out.println("Loading: no Target -> creating new one");
				Target target = new Target();
				target.setTargetProjectID(targetProjects.getSelectedTargetInstance().getTargetInstanceID());
				target.setTargetDBSchema("");
				targetProjects.addTarget(target);
		
				targetProjects.setSelectedTarget(target);
				
				newData = true;
				
			}
			
			if ( newData == true ){
				Console.info("Saving new create TargetInstance/TargetVersion.");
				StatusView.addMessage(
						"While loading the i2b2 staging project a new target instance has been created.");
				ActionCommand command  = new ActionCommand(Resource.ID.Command.IOE.SAVETARGETPROJECT);
				Application.executeCommand(command);
				
				
			}
			
			//updateing the gui
//			OntologyEditorView.refreshVersionCombo();
			//targetProjects.getSelectedTarget()
			OntologyEditorView.refreshTargetVersionGUI();

		} catch (Exception e) {
			String message = "Error while using a TOS-plugin for job \"LoadTargetProjects\": "
					+ e.getMessage();
			Console.error(message);
			StatusView.addErrorMessage(message);


		}
		
		
		
		return null;
	}
}
