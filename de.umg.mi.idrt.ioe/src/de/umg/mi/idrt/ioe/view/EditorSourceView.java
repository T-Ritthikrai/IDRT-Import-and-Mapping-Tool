package de.umg.mi.idrt.ioe.view;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.umg.mi.idrt.ioe.Activator;
import de.umg.mi.idrt.ioe.Application;
import de.umg.mi.idrt.ioe.I2B2ImportTool;
import de.umg.mi.idrt.ioe.Resource;
import de.umg.mi.idrt.ioe.SystemMessage;
import de.umg.mi.idrt.ioe.OntologyTree.NodeDragListener;
import de.umg.mi.idrt.ioe.OntologyTree.OntologyTreeNode;
import de.umg.mi.idrt.ioe.OntologyTree.TreeContentProvider;

public class EditorSourceView extends ViewPart {

	private I2B2ImportTool _i2b2ImportTool;
	private Composite _composite;

	public EditorSourceView() {
		// do nothing
	}

	@Override
	public void createPartControl(Composite parent) {
		_composite = parent;
		Text text = new Text(this._composite, SWT.BORDER);
		text.setText("EditorSourceView");
	}

	@Override
	public void setFocus() {

	}

	public I2B2ImportTool setI2B2ImportTool(I2B2ImportTool i2b2ImportTool) {
		return _i2b2ImportTool = i2b2ImportTool;
	}

	public I2B2ImportTool getI2B2ImportTool() {
		return _i2b2ImportTool;
	}

	public void setComposite() {
		System.out.println("setCompostieSource!!");

		Control[] children = this._composite.getChildren();

		for (int x = 0; x < children.length; x++) {
			children[0].dispose();
		}

		TreeViewer viewer = new TreeViewer(_composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);

		int operations = DND.DROP_COPY | DND.DROP_MOVE;

		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		viewer.addDragSupport(operations, transferTypes, new NodeDragListener(
				viewer));
		TreeContentProvider treeContentProvider = new TreeContentProvider();

		treeContentProvider.setOT(this._i2b2ImportTool.getMyOntologyTrees()
				.getOntologyTreeSource());
		//treeContentProvider.setViewTree(this._i2b2ImportTool
		//		.getMyOntologyTrees().getViewTree());

		if (treeContentProvider.getRoot() == null) {
			System.out.println("!!!!rootNull");
			_composite.getChildren()[0].dispose();
			Label label = new Label(_composite, SWT.SHADOW_NONE);
			String message = "Error while reading or creating source tree. Please visit the LOG-File-World of despair.";
			label.setText(message);
			_composite.pack();
			Application.getStatusView().addErrorMessage(message);
			return;
		}

		viewer.setContentProvider(treeContentProvider);
		viewer.setLabelProvider(new ViewTableLabelProvider(viewer));

		OTtoTreeContentProvider oTreeContent = new OTtoTreeContentProvider();

		viewer.setInput(oTreeContent.getModel());
		viewer.expandToLevel( Resource.Options.EDITOR_SOURCE_TREE_OPENING_LEVEL );

		this._i2b2ImportTool.getMyOntologyTrees().getOntologyTreeSource()
				.setTreeViewer(viewer);

		viewer.getTree().addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				// ViewTableNode viewNode = (ViewTableNode) e.item.item;
				if (e.item == null || e.item.getData() == null) {
					System.out
							.println("WidgetSelected but no known node found!");
					return;
				}

				/*
				 * ViewTreeNode viewNode = (ViewTreeNode) e.item.getData();
				 * OTNode node = viewNode.getOTNode();
				 */

				OntologyTreeNode node = (OntologyTreeNode) e.item.getData();

				if (node != null) {

					/*
					 * if (!node.getNodeType().equals( NodeType.ITEM )){
					 * 
					 * // //Activator.getDefault().getResource().
					 * getOntologyAnswersPreviewView().setItemNode(null); }
					 */

					Activator.getDefault().getResource()
							.getEditorSourceInfoView().setNode(node);

					Application.getStatusView().addMessage(
							new SystemMessage("Selection changed to \'"
									+ node.getName() + "\'.",
									SystemMessage.MessageType.SUCCESS,
									SystemMessage.MessageLocation.MAIN));
				} else {
					Application
							.getStatusView()
							.addMessage(
									new SystemMessage(
											"Selection changed but new selection isn't any know kind of node.",
											SystemMessage.MessageType.ERROR,
											SystemMessage.MessageLocation.MAIN));
				}
			}
		});

		this._composite.layout();
	}

	@Override
	public void dispose() {
		super.dispose();

	}

}