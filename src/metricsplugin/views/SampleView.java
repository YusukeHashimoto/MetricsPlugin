package metricsplugin.views;


import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
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
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

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

				Map<String, Integer> attributes = new HashMap<>();
				attributes.put(IMarker.LINE_NUMBER, new Integer(line));

				try {
					IMarker marker = resource.createMarker(IMarker.TEXT);
					marker.setAttributes(attributes);
					IDE.gotoMarker(ProjectUtil.activeEditor(), marker);
					marker.delete();
				} catch (CoreException e) {
					//e.printStackTrace();
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
		CodeAnalyzer ca = new CodeAnalyzer();
		//try {
		ca.run(ProjectUtil.pathToPackage());
		//} catch(Exception e) {
		//ca.run(ProjectUtil.pathToPackage().substring(1));
		//}
		//analyze();

		warnings = ca.getWarnings();
		return ca.getWarnings().stream().map(Warning::getMessage).collect(Collectors.toList());
	}

	private void refresh() {
		viewer.setInput(calc());
		//viewer.setInput(analyze());
	}

	private List<String> analyze() {
		CodeAnalyzer ca = new CodeAnalyzer();
		ca.analyzeCodes(null, ProjectUtil.pathToPackage());

		warnings = ca.getWarnings();
		return ca.getWarnings().stream().map(Warning::getMessage).collect(Collectors.toList());
	}

	@Override
	public Object getAdapter(Class arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
