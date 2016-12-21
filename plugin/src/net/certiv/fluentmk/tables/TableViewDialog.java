package net.certiv.fluentmk.tables;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.certiv.fluentmark.model.PagePart;
import net.certiv.fluentmk.tables.TableModel.Row;

public class TableViewDialog extends MessageDialog {

	public class AddColAction extends Action {

		public AddColAction() {
			super("Add Column");
		}

		@Override
		public void run() {
			ViewerCell cell = cellMgr.getFocusCell();
			int target = cell != null ? cell.getColumnIndex() : 0;
			target = target < 0 ? tableModel.numCols : target;
			tableModel.insertCol(target);
			int cnt = table.getColumnCount();
			for (int idx = 0; idx < cnt; idx++) {
				table.getColumn(0).dispose();
			}
			createColumns();
			viewer.refresh();
		}
	}

	public class RmvColAction extends Action {

		public RmvColAction() {
			super("Remove Column");
		}

		@Override
		public void run() {
			ViewerCell cell = cellMgr.getFocusCell();
			int target = cell != null ? cell.getColumnIndex() : 0;
			if (target < 0 || target >= tableModel.numCols) return;
			tableModel.removeCol(target);
			int cnt = table.getColumnCount();
			for (int idx = 0; idx < cnt; idx++) {
				table.getColumn(0).dispose();
			}
			createColumns();
			viewer.refresh();
		}
	}

	public class AddRowAction extends Action {

		public AddRowAction() {
			super("Add Row");
		}

		@Override
		public void run() {
			TableItem[] items = table.getSelection();
			if (items == null || items.length == 0) return;
			int target = table.indexOf(items[0]);
			if (target < 1) return;

			tableModel.addRow(target + 1);
			viewer.refresh();
		}
	}

	public class RmvRowAction extends Action {

		public RmvRowAction() {
			super("Remove Row");
		}

		@Override
		public void run() {
			TableItem[] items = table.getSelection();
			if (items == null || items.length == 0) return;
			int target = table.indexOf(items[0]);
			if (target < 1) return;

			tableModel.removeRow(target + 1);
			viewer.refresh();
		}
	}

	private class TableEditingSupport extends EditingSupport {

		private TextCellEditor editor;
		private int idx;

		public TableEditingSupport(ColumnViewer viewer, int idx) {
			super(viewer);
			this.editor = new TextCellEditor((Composite) viewer.getControl());
			this.idx = idx;
		}

		@Override
		protected boolean canEdit(Object element) {
			return element instanceof Row;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			return ((Row) element).data[idx];
		}

		@Override
		protected void setValue(Object element, Object value) {
			((Row) element).data[idx] = (String) value;
			viewer.refresh();
		}
	}

	private PagePart part;
	private int style;

	private TableViewer viewer;
	private Table table;
	private TableModel tableModel;
	private TableViewerFocusCellManager cellMgr;

	/**
	 * Creates a new TableViewDialog with default SWT styles.
	 */
	public TableViewDialog(PagePart part) {
		this(part, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
	}

	/**
	 * Creates a new TableViewDialog with the given style.
	 * 
	 * @param editor
	 * @param part
	 */
	public TableViewDialog(PagePart part, int style) {
		super(Display.getCurrent().getActiveShell(), "Table Editor", null, null, MessageDialog.NONE,
				new String[] { "Apply", "Cancel" }, 1);
		this.part = part;
		this.style = style;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public String build() {
		return tableModel.build();
	}

	@Override
	protected Control createCustomArea(final Composite parent) {
		viewer = new TableViewer(parent, style);
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(viewer.getControl());

		table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		cellMgr = new TableViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));

		ColumnViewerEditorActivationStrategy activator = new ColumnViewerEditorActivationStrategy(viewer) {

			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		int features = ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION;

		TableViewerEditor.create(viewer, cellMgr, activator, features);

		MenuManager mgr = new MenuManager();
		mgr.add(new AddColAction());
		mgr.add(new RmvColAction());
		mgr.add(new Separator());
		mgr.add(new AddRowAction());
		mgr.add(new RmvRowAction());
		viewer.getControl().setMenu(mgr.createContextMenu(viewer.getControl()));

		tableModel = new TableModel();
		setInput(part);
		return viewer.getControl();
	}

	private void setInput(PagePart part) {
		if (tableModel.load(part)) {
			createColumns();
			TableContentProvider provider = new TableContentProvider(tableModel);
			viewer.setContentProvider(provider);
			viewer.setInput(part);
		}
	}

	private void createColumns() {
		for (int idx = 0; idx < tableModel.numCols; idx++) {
			int align = tableModel.aligns[idx];
			createColumn(idx, align);
		}
	}

	private void createColumn(int idx, int align) {
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, align, idx);
		viewerColumn.setEditingSupport(new TableEditingSupport(viewer, idx));
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {

			private int col = idx;

			public String getText(Object element) {
				if (element instanceof Row) {
					return ((Row) element).data[col];
				}
				return super.getText(element);
			}
		});

		TableColumn column = viewerColumn.getColumn();
		column.setResizable(true);
		column.setMoveable(true);

		column.setText(tableModel.headers[idx]);

		int width = convertWidthInCharsToPixels(tableModel.colWidths[idx]);
		width = Math.max(width, 50);
		column.setWidth(width);
	}

	public TableViewer getTableViewer() {
		return viewer;
	}

	/**
	 * Returns the SWT Table.
	 */
	public Table getTable() {
		return table;
	}
}