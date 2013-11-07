package de.umg.mi.idrt.ioe.commands.OntologyEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;

import de.umg.mi.idrt.ioe.Resource.I2B2.NODE.TYPE;
import de.umg.mi.idrt.ioe.OntologyTree.Dimension;
import de.umg.mi.idrt.ioe.OntologyTree.MyOntologyTrees;
import de.umg.mi.idrt.ioe.OntologyTree.NodeType;
import de.umg.mi.idrt.ioe.OntologyTree.OntologyTreeNode;
import de.umg.mi.idrt.ioe.view.OntologyEditorView;

public class AddItemCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Adding Item");
		OntologyTreeNode currentNode = OntologyEditorView.getCurrentTargetNode();

		
		OntologyTreeNode subRootNode = new OntologyTreeNode("New Item");

		subRootNode.setID("new Item");
		subRootNode.setTreePath("\\i2b2\\newItem\\");
		subRootNode.setTreePathLevel(1);
		subRootNode.setType(TYPE.ONTOLOGY_TARGET);
//		subRootNode.getTargetNodeAttributes().addStagingPath("");
		subRootNode.getTargetNodeAttributes().setDimension(Dimension.CONCEPT_DIMENSION);
		subRootNode.getTargetNodeAttributes().setVisualattributes("LAE");
		subRootNode.setName("New Item");
		currentNode.add(subRootNode);

		//MyOntologyTree myOT = OntologyEditorView.getSelf().getMyOntologyTree();
		TreeViewer targetTreeViewer = OntologyEditorView.getTargetTreeViewer();
//		targetTreeViewer.expandToLevel(subRootNode, 10);
		targetTreeViewer.setSelection(new StructuredSelection(subRootNode), true);
		targetTreeViewer.editElement(subRootNode, 0);
		OntologyEditorView.getOntologyTargetTree().getNodeLists().add(subRootNode);
		targetTreeViewer.refresh();
		
		OntologyTreeNode test = (OntologyTreeNode) OntologyEditorView.getOntologyTargetTree().getTreeRoot().getNextNode();
		TreeViewerColumn column = OntologyEditorView.getTargetTreeViewerColumn();
		column.getViewer().editElement(subRootNode, 0);
		System.out.println(test.getName());
		
		return null;
	}
}