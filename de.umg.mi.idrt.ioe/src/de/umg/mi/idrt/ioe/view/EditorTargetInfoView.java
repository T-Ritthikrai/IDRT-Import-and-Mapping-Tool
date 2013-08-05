package de.umg.mi.idrt.ioe.view;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.core.resources.mapping.ModelProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.umg.mi.idrt.ioe.Activator;
import de.umg.mi.idrt.ioe.Application;
import de.umg.mi.idrt.ioe.Debug;
import de.umg.mi.idrt.ioe.GUITools;
import de.umg.mi.idrt.ioe.I2B2ImportTool;
import de.umg.mi.idrt.ioe.Resource;
import de.umg.mi.idrt.ioe.OntologyTree.OntologyCellAttributes;
import de.umg.mi.idrt.ioe.OntologyTree.OntologyTreeNode;
import de.umg.mi.idrt.ioe.OntologyTree.TargetNodeAttributes;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class EditorTargetInfoView extends ViewPart {

	private I2B2ImportTool _i2b2ImportTool = null;
	private Resource _resource = null;
	private String _text = ""; 
	private Composite parentPane;

	OntologyTreeNode _node = null;
	private Composite _editorComposite;
	private Composite _parent;
	private Table _infoTable;
	private TableColumn infoTableDBColumn;
	private TableColumn infoTableValue;
	private TableItem tableItem;
	private TableCursor tableCursor;
	private TableViewer viewer;
	
	public EditorTargetInfoView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {

		Debug.f("createPartControl",this);
		_parent = parent;
		parent.setLayout(new GridLayout(1, false));

		//createInfoGroup();

		Label lblNodeInfosDeluxe = new Label(parent, SWT.NONE);
		lblNodeInfosDeluxe.setText("node infos deluxe");

		_editorComposite = new Composite(parent, SWT.NONE);
		_editorComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		_editorComposite.setLayout(new FillLayout(SWT.VERTICAL));
		

		createTable();
		
		//item.setImage (image);

		parentPane = parent.getParent();
	}
	
	private TableItem addColumItem( String text ){
		TableItem item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] { text, "" });
		return item;
	}

	private void createTable() {
		
		if (_infoTable != null)
			return;
		
		 int[] bounds = { 100, 100 };
		
		_infoTable = new Table( _parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL );
		_infoTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_infoTable.setHeaderVisible(true);
		_infoTable.setLinesVisible(true);
		
		
		
		//_infoTable.getColumnCount().get
		
		
		// editor as mouse listener
		
		final TableEditor editor = new TableEditor(_infoTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		_infoTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent event) {
				
				System.out.println("InfoTableSelection: mouseDown!");
				
				Control old = editor.getEditor();
				if (old != null) {
					old.dispose();
				}
				Point pt = new Point(event.x, event.y);
				
				System.out.println("position0: " + event.x + "/" + event.y + "!");
				
				
				
				final TableItem item = _infoTable.getItem(pt);
				
				
				
				
				System.out.println("Item " +  ( item != null ? "!=" : "==" ) +  " NULL");
				
				if (item != null) {
					int column = -1;
					int row = -1;
					
					for (int i = 0, n = _infoTable.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						System.out.println("position1: " + rect.height + "/" + rect.width + "!");
						
						
						if (rect.contains(pt)) {
							column = i;
							break;
						}
					}
					
					// detect the row where the mouse was clicked
					for (int i = 0, n = _infoTable.getItemCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							row = i;
							break;
						}
					}
					
					System.out.println("position2: " + column + "/" + row + "("+ _infoTable.getSelectionIndex() +")!");
					
					row = _infoTable.getSelectionIndex();
					final int col = 1;
					
					// Source Path
					if ( row == 0 ) {
						System.out.println("Editor: Source Path");
						final Text text = new Text(_infoTable, SWT.NONE);
						
						text.setForeground(item.getForeground());
						text.setText("Hallo Test");//item.getText(0));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();
						editor.minimumWidth = text.getBounds().width;
						editor.setEditor(text, item, col);
						
						
						text.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event) {
								item.setText(col, text.getText());
							}
						});
						text.addFocusListener(new FocusListener() {
							@Override
							public void focusGained(FocusEvent e) {
							}

							@Override
							public void focusLost(FocusEvent e) {
								item.setText(col, text.getText());
								text.dispose();
							}
						});
						
					}
					
					// The "nice name" of the column.
					if (column == 111) {
						final Text text = new Text(_infoTable, SWT.NONE);
						text.setForeground(item.getForeground());
						text.setText(item.getText(col));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();
						editor.minimumWidth = text.getBounds().width;
						editor.setEditor(text, item, col);
						
						text.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event) {
								item.setText(col, text.getText());
							}
						});
						text.addFocusListener(new FocusListener() {
							@Override
							public void focusGained(FocusEvent e) {
							}

							@Override
							public void focusLost(FocusEvent e) {
								item.setText(col, text.getText());
								text.dispose();
							}
						});
						// Datatype of the column
					} else if (column == 112) {
						final CCombo combo = new CCombo(_infoTable,
								SWT.READ_ONLY);
						
						
						/*
						for (String element : ConfigMetaData.optionsData) {
							combo.add(element);
						}
						combo.setText(item.getText(column));
						combo.select(combo.indexOf(item.getText(column)));
						editor.minimumWidth = combo.computeSize(
								SWT.DEFAULT, SWT.DEFAULT).x;
						combo.setFocus();
						editor.setEditor(combo, item, column);
						*/
						/*
						final int col = column;
						combo.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent event) {
								item.setText(col, combo.getText());
								combo.dispose();
							}
						});
						*/
						// 	i2b2 Metadata of the column (e.g. patient id)
					} else if (column == 113) {
						/*
						String[] optionMetas = ConfigMetaData.getMetaCombo(
								table.getItems(),
								item.getText(column));
						final CCombo combo = new CCombo(table,
								SWT.READ_ONLY);
						for (String optionMeta : optionMetas) {
							combo.add(optionMeta);
						}
						combo.select(combo.indexOf(item.getText(column)));
						editor.minimumWidth = combo.computeSize(
								SWT.DEFAULT, SWT.DEFAULT).x;
						combo.setFocus();
						editor.setEditor(combo, item, column);
						final int col = column;
						combo.addSelectionListener(new SelectionAdapter() {

							@Override
							public void widgetSelected(SelectionEvent event) {
								item.setText(col, combo.getText());
								combo.dispose();
								// checkTable();
							}
						});
						*/
						// PID-Generator specific Metadata (e.g. bday, name, lastname...)
					/*
					} else if ((column == 4)
							&& CSVWizardPageTwo.getBtnRADIOCsvfile()
							&& CSVWizardPageTwo.getUsePid()) {
						final CCombo combo = new CCombo(table,
								SWT.READ_ONLY);
						String[] optionMetas = ConfigMetaData
								.getMetaComboPIDGen(
										table.getItems(),
										item.getText(column));
						for (String optionMeta : optionMetas) {
							combo.add(optionMeta);
						}
						combo.select(combo.indexOf(item.getText(column)));
						editor.minimumWidth = combo.computeSize(
								SWT.DEFAULT, SWT.DEFAULT).x;
						combo.setFocus();
						editor.setEditor(combo, item, column);

						final int col = column;
						combo.addSelectionListener(new SelectionAdapter() {

							@Override
							public void widgetSelected(SelectionEvent event) {
								item.setText(col, combo.getText());
								if (!combo.getText().isEmpty()) {
									item.setText(col - 1, "ignore"); 
								} else {
									item.setText(col - 1, ""); 
								}
								combo.dispose();
							}
						});
					*/
					}
					
					
				}
			}
		});
		

		infoTableDBColumn = new TableColumn(_infoTable, SWT.NONE);
		infoTableDBColumn.setWidth(170);
		infoTableDBColumn.setText("column");

		
		
		infoTableValue = new TableColumn(_infoTable, SWT.NONE);
		infoTableValue.setWidth(600);
		infoTableValue.setText("value");
		//infoTableValue.

		addColumItem(Resource.I2B2.NODE.TARGET.SOURCE_PATH);
		addColumItem(Resource.I2B2.NODE.TARGET.NAME);
		addColumItem(Resource.I2B2.NODE.TARGET.CHANGED);
		addColumItem(Resource.I2B2.NODE.TARGET.STARTDATE_SOURCE_PATH);
		addColumItem(Resource.I2B2.NODE.TARGET.ENDDATE_SOURCE_PATH);
		addColumItem(Resource.I2B2.NODE.TARGET.VISUALATTRIBUTE);
		
		
		
		//TableViewerColumn col = createTableViewerColumn(Resource.I2B2.NODE.TARGET.SOURCE_PATH, bounds[0], 0);
		
		tableItem = new TableItem(_infoTable, SWT.NONE);
		tableItem.setText("New TableItem");
		
		tableCursor = new TableCursor(_infoTable, SWT.NONE);

	}
	
	private void createViewer(Composite parent) {
	    viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
	        | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
	    createColumns(parent, viewer);
	    final Table table = viewer.getTable();
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);

	    viewer.setContentProvider(new ArrayContentProvider());
	    // Get the content for the viewer, setInput will call getElements in the
	    // contentProvider
	    //viewer.setInput(ModelProvider.INSTANCE.getPersons());
	    // Make the selection available to other views
	    getSite().setSelectionProvider(viewer);

	    // Layout the viewer
	    GridData gridData = new GridData();
	    gridData.verticalAlignment = GridData.FILL;
	    gridData.horizontalSpan = 2;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    viewer.getControl().setLayoutData(gridData);
	  }
	
	  private TableViewerColumn createTableViewerColumn(String title, int bound,
		      final int colNumber) {
		    final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
		        SWT.NONE);
		    final TableColumn column = viewerColumn.getColumn();
		    column.setText(title);
		    column.setWidth(bound);
		    column.setResizable(true);
		    column.setMoveable(true);
		    //column.addSelectionListener(getSelectionAdapter(column, colNumber));
		    return viewerColumn;
		  }

	  
	  private void createColumns(final Composite parent, final TableViewer viewer) {
		    String[] titles = { "First name", "Last name", "Gender", "Married" };
		    int[] bounds = { 100, 100, 100, 100 };

		    // First column is for the first name
		    TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		    col.setLabelProvider(new ColumnLabelProvider() {
		      @Override
		      public String getText(Object element) {
		        //Person p = (Person) element;
		        //return p.getFirstName();
		    	return "firstname";
		      }
		    });
		    
		    /*
		     @Override
		      public Image getImage(Object element) {
		        if (((Person) element).isMarried()) {
		          return CHECKED;
		        } else {
		          return UNCHECKED;
		        }
		      }
		      */
		    //});

		  }

	  public TableViewer getViewer() {
		    return viewer;
		  }
	  
	private boolean hasNode() {
		if (_node != null)
			return true;
		else
			return false;
	}

	@Override
	public void setFocus() {

	}
	
	public void setComposite(String text){
		Debug.f("setComposite",this);
		
		this._text = text;
		refresh();
	}
	
	public void setComposite(Composite pane){
		Debug.f("setComposite",this);
		
		refresh();
	}
	
	
	public void setNode(OntologyTreeNode node){//, List<String> answersList, MyOntologyTreeItemLists itemLists){
		//Debug.f("setNode",this);
		//Console.info("setting node");
		System.out.println("setting node ("+node.getName()+")");
		_node = node;
		refresh();
	}
	
	public void setI2B2ImportTool(I2B2ImportTool i2b2ImportTool){
		this._i2b2ImportTool = i2b2ImportTool;
	}
	
	public I2B2ImportTool getI2B2ImportTool(){
		return this._i2b2ImportTool;
	}
	
	public void setResource(Resource resource){
		this._resource = resource;
	}
	
	public Resource getResource(){
		return this._resource;
	}
	
	public void refresh(){
		Debug.f("refresh",this);
		
		
		//Editor von Benjamin aus CSVWizard klauen
		
		Display display = Display.getCurrent();
		
		if(display == null) {
		    // Bad, no display for this thread => we are not in (a) UI thread
		    //display.syncExec(new Runnable() {void run() { gc = new GC(display);}});
			Debug.e("no display");

			if (PlatformUI.getWorkbench().getDisplay() != null){
				Debug.d("display by PlatformUI");
				PlatformUI.getWorkbench().getDisplay().syncExec(
					  new Runnable() {
					    public void run(){
					    	executeRefresh();
					    }
					  });
				
			} else if (Application.getDisplay() != null){
				Debug.d("display by Acitvator");
				Application.getDisplay().syncExec(
						  new Runnable() {
						    public void run(){
						    	executeRefresh();
						    }
						  });
			} else {
				Debug.e("no Display (final)");
			}
		} else {
			  new Runnable() {
				    public void run(){
				    	executeRefresh();
				    }
				  };
			executeRefresh();
		}
	}
	
	public TableItem addValueItem( TableItem[] items, int row, String value ){
		if (items[row] == null){
			Debug.e("Could not add an item to a table in EditorSourceInfoView, because there was no row #"+row+".");
			return null;
		}
		TableItem item = items[row];
		item.setText (1, value != null && !value.equals("null") ? value : "" );
		return item;
	}
	
	public void executeRefresh(){
		System.out.println("executeRefresh for text:\""+ this._node.getName() +"\"");
		
		if(parentPane == null){
			Debug.e("no pane avaible @OntologyNodeEditorView");
			return;
		}

		createTable();
		
		TargetNodeAttributes attributes = _node.getTargetNodeAttributes();
		
		TableItem[] items = _infoTable.getItems();

		int row = 0;

		addValueItem(items, row++, String.valueOf( attributes.getSourcePath() ) );
		addValueItem(items, row++, String.valueOf( _node.getName() ) );
		addValueItem(items, row++, String.valueOf( attributes.isChanged() == true ? "true" : false ) );
		addValueItem(items, row++, String.valueOf( attributes.getStartDateSource() ) );
		addValueItem(items, row++, String.valueOf( attributes.getEndDateSource() ) );
		addValueItem(items, row++, String.valueOf( attributes.getVisualattribute() ) );

		
		/*
		TableItem item = new TableItem (_infoTable, SWT.NONE);
		item.setText (2, String.valueOf( attributes.getC_HLEVEL() ) );
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_FULLNAME", attributes.getC_FULLNAME()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_NAME", attributes.getC_NAME()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_SYNONYM_CD", attributes.getC_SYNONYM_CD()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_VISUALATTRIBUTES", attributes.getC_VISUALATTRIBUTES()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_TOTALNUM", String.valueOf( attributes.getC_TOTALNUM() )});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_BASECODE", attributes.getC_BASECODE()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_METADATAXML", String.valueOf( attributes.getC_METADATAXML() )});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_FACTTABLECOLUMN", attributes.getC_FACTTABLECOLUMN()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_TABLENAME", attributes.getC_TABLENAME()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_COLUMNNAME", attributes.getC_COLUMNNAME()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_COLUMNDATATYPE", attributes.getC_COLUMNDATATYPE()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_OPERATOR", attributes.getC_OPERATOR()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_DIMCODE", attributes.getC_DIMCODE()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_COMMENT", String.valueOf( attributes.getC_COMMENT() )});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_TOOLTIP", attributes.getC_TOOLTIP()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"M_APPLIED_PATH", attributes.getM_APPLIED_PATH()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"UPDATE_DATE", String.valueOf( attributes.getUPDATE_DATE() )});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"DOWNLOAD_DATE", String.valueOf( attributes.getDOWNLOAD_DATE() )});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"IMPORT_DATE", String.valueOf( attributes.getIMPORT_DATE() )});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"SOURCESYSTEM_CD", attributes.getSOURCESYSTEM_CD()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"VALUETYPE_CD", attributes.getVALUETYPE_CD()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"M_EXCLUSIVE_CD", attributes.getM_EXCLUSION_CD()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_PATH", attributes.getC_PATH()});
		item = new TableItem (_infoTable, SWT.NONE);
		item.setText (new String[] {"C_SYMBOL", attributes.getC_SYMBOL()});
		*/
		
		//_infoTable.getColumn(1)
		
		
		/*
		
		// recreate bottom-label
		if ( _itemNode == null ){
			
			disposeChildren(_editorComposite);
			
			Group grpLabel = new Group(_editorComposite, SWT.NONE);
			grpLabel.setText("label");
			grpLabel.setLayout(new GridLayout(2, false));
			
			//_bottomLabel = new Label(grpLabel, SWT.NONE);
			//_bottomLabel.setText("New Label");
		}

		if ( _itemNode != null ){
			
			path = _itemNode.getTreePath();
			name = _itemNode.getName();
			nodeType = _itemNode.getNodeType().toString();
			
			

			bottomLabel = "ItemNode";

			disposeChildren(_editorComposite);

			_editorComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			_editorComposite.setLayout(new FillLayout(SWT.VERTICAL));
			
			NodeEditorCompositeInteger bottomComposite = new NodeEditorCompositeInteger(this._editorComposite, SWT.NONE);
			
		} 
		
		if ( _node != null ) {
			
			//if ( _node.getNodeType().equals(NodeType.ANSWER) ){
			//	_node = (OTNode)_node.getParent();
			//	if ( _node.getNodeType().equals(NodeType.ANSWER) ){
			//		_node = (OTNode)_node.getParent();
			//		if ( _node.getNodeType().equals(NodeType.ANSWER) ){
			//			_node = (OTNode)_node.getParent();
			//		}
			//	}
			//}
			
			path = _node.getTreePath();
			name = _node.getName();
			nodeType = _node.getNodeType().toString();
			importPath = _node.getImportPath();
			treePath = _node.getTreePath();
			i2b2Path = _node.getI2B2Path();
			i2b2Level = String.valueOf(_node.getLevel());
			if ( _node.isAdditionalData() ){
				OTNode additionalDataParent = _node.getAdditionalDataParentNode();
				
				if ( _node.isAdditionDataParent() ) {
					additionalDataParentNodePath = "*";
				} else if ( additionalDataParent != null ){
					additionalDataParentNodePath = additionalDataParent.getImportPath();
				}
			}
			additionalDataParentNodePath = ( _node != null && _node.hastAdditionalDataParentNode() ) ? _node.getAdditionalDataParentNode().getImportPath() : "-";
			
			bottomLabel = "Node";
			
		} else {
			path = ">";
			name = "Name";
			nodeType = "Type";
			
			bottomLabel = "nothing";
		}
		
		if (_node != null){
			i2b2Info = _node.getLevel() + "|" + _node.getI2B2Path();
			
			//Console.info("checkID2");
			//_checkboxIsVisible.setSelection(_node.isVisable());
			//_checkboxIsVisible.update();
		}
		
		*/
		
		/*
		_infoLabelPath.setText(path);
		_infoLabelPath.update();
		_infoLabelName.setText(name);
		_infoLabelName.update();
		//_infoLabelI2B2InfoData.setText(i2b2Info);
		//_infoLabelI2B2InfoData.update();
		if (nodeType != null)
			_infoLabelNodeType.setText(nodeType + ( ( ( (nodeType.equals(NodeType.ITEM.toString() ) && _itemNode != null )  ) ?  " (" + _itemNode.getDataType() + ")" : "") ));
		
		_infoLabelNodeType.update();
		_infoLabelImportPathData.setText(importPath);
		_infoLabelImportPathData.update();
		_infoLabelTreePathData.setText(treePath);
		_infoLabelTreePathData.update();
		_infoLabelI2B2PathData.setText(i2b2Path);
		_infoLabelI2B2PathData.update();
		_infoLabelI2B2LevelData.setText(i2b2Level);
		_infoLabelI2B2LevelData.update();
		_infoLabelAdditionalDataParentPathData.setText(additionalDataParentNodePath);
		_infoLabelAdditionalDataParentPathData.update();
		
		_infoLabelNodeType.getParent().layout();
		
		// only edit if there is a active instance
		if (!_bottomLabel.isDisposed()){
			_bottomLabel.setText(bottomLabel);
			_bottomLabel.update();
			
		}
		
		*/
		
		
		//_editorComposite.dispose();
		//_editorComposite = new NodeEditorCompositeInteger(this._editorComposite, SWT.NONE);
		
		/*
		Label _bottomLabel2 = new Label(_editorComposite, SWT.NONE);
		_bottomLabel2.setText("New Label2 (XX)");
		_bottomLabel2.update();
		*/
		
		//_editorComposite.update();
		//_editorComposite.layout();
		
		
		
		_editorComposite.update();
		_editorComposite.layout();
		
		_parent.layout();
		
	}
	
	
	private JPanel createItemPanel() {

		JPanel itemPanel = new JPanel();
		itemPanel.setBackground(Color.LIGHT_GRAY);
		//itemPanel.setLayout(getStandardGridBagLayout());
		itemPanel.setSize(new Dimension(1000, 500));
		// itemPanel.setPreferredSize( new Dimension ( 900, 200 ) );

		return itemPanel;
	}
	
	private JLabel createJLabel(String text) {

		return new JLabel(text);
	}
	
	private JButton createButton(String label, String command) {

		JButton button = new JButton();
		if (!label.isEmpty())
			button.setText(label);
		button.setVerticalAlignment(AbstractButton.BOTTOM);
		button.setActionCommand(command);
		//button.addActionListener(this.myOntologyTree);
		// button.setLabel( label );

		// button.setPreferredSize( button.getPreferredSize() );
		// button.setMaximumSize( new Dimension(10,3) );
		button.setVisible(true);

		return button;
	}
	
	private JComboBox createJComboBox(String name, String value, int type) {

		String[] pattern = null;
		String selected = "";
		int size = 0;

		if (type == 1) // month
			size = 12;
		else if (type == 2) // days
			size = 31;
		else
			size = 1;

		if (type < 3) {
			pattern = new String[size];
			for (int x = 0; x < size; x++) {
				pattern[x] = String.valueOf(x + 1);
			}
		} else if (type == 3) {
			// groups

			// add value to pattern
			if (value != null) {
				if (value != "10" && value != "50" && value != "100"
						&& value != "1000" && value != "10" && value != "10"
						&& value != "10") {
					pattern = new String[] { value, "10", "50", "100", "1000" };
				}

			}

			// set selected value to 10
			if (value == null)
				value = "10";
		}

		JComboBox jComboBox = new JComboBox(pattern);
		jComboBox.setName(name);
		jComboBox.setSelectedItem(value);
		jComboBox.setPrototypeDisplayValue("XXXX");
		jComboBox.setMaximumSize(jComboBox.getPreferredSize());

		if (type == 3)
			jComboBox.setEditable(true);

		// add this object to the session array
		//((Object) this.editorFields).put(name, jComboBox);

		return jComboBox;
	}
	
	private JTextField createJTextField(String name, String value, int size) {

		JTextField jTextField = new JTextField(size);
		jTextField.setName(name);
		jTextField.setText(value);

		// add this object to the session array
		//this.editorFields.put(name, jTextField);

		return jTextField;
	}
	
	private JButton createIconButton(String label, String command, String icon) {

		JButton button = createButton(label, command);
		button.setIcon(GUITools.createImageIcon(icon));
		button.setBorderPainted(false);
		button.setIconTextGap(0);

		return button;
	}
	
	private void disposeChildren(Composite composite){
		Control[] children = composite.getChildren();
		
		for(int x = 0; x < children.length; x++){
			children[x].dispose();
		}
		
	}
	
	public Composite getComposite(){
		return _editorComposite;
	}
}