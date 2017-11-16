package metricsplugin.views;


import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.MarkerUtilities;

import codeanalyzer.CodeAnalyzer;
import util.ProjectUtil;
import warning.Warning;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "sample03.views.SampleView";

	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The constructor.
	 */
	public SampleView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(calc().toArray());
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "Sample03.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		//refresh warnings when code has changed
		if(ProjectUtil.activeEditor() != null) 
			ProjectUtil.activeEditor().addPropertyListener((source, propId) -> refresh());

		//WebViewer.showInternalBrowser("file:///C:/Users/Hashimoto/GoogleDrive/MetricsGraph/graphsample.html?Animal=Object");
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				//showMessage("Refresh!");
				refresh();
			}
		};
		action1.setText("Refresh");
		action1.setToolTipText("Action 1 tooltip");
		//action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
		//getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		//action1.setImageDescriptor(MyActivator.getImageDescriptor("icons/refresh.gif"));
		//action1.setImageDescriptor(MyActivator.getImageRegistry());

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		doubleClickAction = new Action() {
			public void run() {
				//refresh();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();

				for(int i = 0; i < viewer.getTable().getItemCount(); i++) {
					if(obj.toString().equals(viewer.getElementAt(i))) {
						openInEditor(warnings.get(i).getFilename());
						markLine(warnings.get(i).getLine());
						break;
					}
				}
			}

			private void openInEditor(String filename) {
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				IFile openedFile = ((IFileEditorInput)ProjectUtil.activeEditor().getEditorInput()).getFile();
				IProject project = openedFile.getProject();
				String projectName = project.toString().substring(1);

				String relativePath = filename.substring(filename.indexOf(projectName));
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(relativePath));

				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
					//e.printStackTrace();
				}
			}

			private void markLine(int line) {
				IEditorInput editorInput = ProjectUtil.activeEditor().getEditorInput();
				IResource resource = (IResource)editorInput.getAdapter(IResource.class);

				Map<String, Object> attributes = new HashMap<>();
				attributes.put(IMarker.LINE_NUMBER, new Integer(line));

				attributes.put(IMarker.TRANSIENT, true);    // マーカーの永続化:true
				attributes.put(IMarker.PRIORITY, Integer.valueOf(IMarker.PRIORITY_NORMAL));      // マーカーの優先度:中
				attributes.put(IMarker.SEVERITY, Integer.valueOf(IMarker.SEVERITY_WARNING));    // マーカーの重要度:警告
				attributes.put(IMarker.LINE_NUMBER, line);  // マーカーを表示させる行番号
				attributes.put(IMarker.MESSAGE, "hogehoge"); // マーカーに表示するメッセージ

				final String ID = "metricsplugin.views" + ".mymarker";

				try {
					IMarker marker = resource.createMarker(IMarker.TEXT);
					marker.setAttributes(attributes);
					IDE.gotoMarker(ProjectUtil.activeEditor(), marker);

					MarkerUtilities.createMarker(resource, attributes, ID);  // マーカーを作成
					//marker.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(e -> doubleClickAction.run());
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
				viewer.getControl().getShell(),
				"Sample View",
				message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private List<Warning> warnings;

	private List<String> calc() {
		try {
			CodeAnalyzer ca = new CodeAnalyzer();
			//ca.run(ProjectUtil.pathToPackage());
			ca.analyzeCodes(null, ProjectUtil.pathToPackage(), ProjectUtil.currentProject());
			warnings = ca.getWarnings();
			for(Warning warning : warnings) {
				//markLine(warning);
			}
			return ca.getWarnings().stream().map(Warning::getMessage).collect(Collectors.toList());
		} catch(Exception e) {
			List<String> list = new ArrayList<>();
			list.add("メトリクスを計算できませんでした");
			return list;
		}
	}

	private void refresh() {
		viewer.setInput(calc());

		if(ProjectUtil.activeEditor() != null) 
			ProjectUtil.activeEditor().addPropertyListener((source, propId) -> refresh());
	}

	private List<String> analyze() {
		CodeAnalyzer ca = new CodeAnalyzer();
		ca.analyzeCodes(null, ProjectUtil.pathToPackage(), ProjectUtil.currentProject());

		warnings = ca.getWarnings();
		return ca.getWarnings().stream().map(Warning::getMessage).collect(Collectors.toList());
	}

	@Override
	public Object getAdapter(Class arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void markLine(Warning warning) {
		String message = warning.getMessage();
		int line = warning.getLine();
		//IEditorInput editorInput = ProjectUtil.activeEditor().getEditorInput();
		//IResource resource = (IResource)editorInput.getAdapter(IResource.class);
		IResource resource;
		try {
			//resource = warning.getCompilationUnit().getJavaElement().getCorrespondingResource();
			CompilationUnit unit = warning.getCompilationUnit();
			IJavaElement je = unit.getJavaElement();
			if(je == null) return;
			resource = je.getCorrespondingResource();
			IMarker marker = resource.createMarker(IMarker.TASK);
			Map<String, Object> attributeMap = new HashMap<>();
			attributeMap.put(IMarker.MESSAGE, message);
			attributeMap.put(IMarker.LINE_NUMBER, line);
			marker.setAttributes(attributeMap);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}/*
		IMarker marker;
		try {
			marker = resource.createMarker(IMarker.TASK);
			Map<String, Object> attributeMap = new HashMap<>();
			attributeMap.put(IMarker.MESSAGE, message);
			attributeMap.put(IMarker.LINE_NUMBER, line);
			marker.setAttributes(attributeMap);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
