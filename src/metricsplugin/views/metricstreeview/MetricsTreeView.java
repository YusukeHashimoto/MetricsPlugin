package metricsplugin.views.metricstreeview;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import codeanalyzer.ClassInfo;
import codeanalyzer.CodeAnalyzer;
import log.LogManager;
import metricsplugin.views.WebViewer;
import util.ClassMetrics;
import util.ProjectUtil;
import warning.Warning;
import warning.suggestion.Suggestion;

public class MetricsTreeView extends ViewPart {
	private TreeViewer viewer;
	private List<Warning> warnings = new ArrayList<>();
	private Action action1;
	private Action action2;

	private LogManager lm;
	private Map<String, ClassInfo> ci;

	@Override
	public Object getAdapter(Class arg0) {
		return null;
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new WarningContentProvider());
		viewer.setLabelProvider(new ExplororLabelProvider());
		calc();
		viewer.setInput(MetricsCategory.values());
		viewer.addDoubleClickListener(event -> {
			ISelection selection = viewer.getSelection();
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof Warning) {
				Warning warning = (Warning) obj;
				ProjectUtil.openInEditor(warning.getFilename());
				ProjectUtil.markPosition(warning.getNode() != null ? warning.getNode().getStartPosition() : 0);
			} else if (obj instanceof Suggestion) {
				WebViewer.showInternalBrowser("file:///C:/Users/Hashimoto/GoogleDrive/MetricsGraph/sample.html",
						"Sample");
			}
		});

		getSite().setSelectionProvider(viewer);

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		lm = new log.LogManager(ProjectUtil.currentProject());
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	private void calc() {
		try {
			CodeAnalyzer ca = new CodeAnalyzer();
			ca.analyzeCodes(null, ProjectUtil.pathToPackage(), ProjectUtil.currentProject());
			warnings = ca.getWarnings();
			MetricsCategory.setAllWarnings(warnings);
			ci = ca.getClassInfo();
		} catch (Exception e) {
			System.err.println("Failed to calcurate metrics\n" + e);
			e.printStackTrace();
		}
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

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				MetricsTreeView.this.fillContextMenu(manager);
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

	private void makeActions() {
		action1 = new Action() {
			@Override
			public void run() {
				// showMessage("Refresh!");
				refresh();
			}
		};
		action1.setText("Refresh");
		action1.setToolTipText("Action 1 tooltip");

		action2 = new Action() {
		};
		action2.setText("2");
	}

	void refresh() {
		calc();
		viewer.setInput(MetricsCategory.values());
		printLog();
	}

	void printLog() {
		LogManager lm = new LogManager(ProjectUtil.currentProject());
		List<ClassMetrics> cmList = ci.values().stream().map(c -> new ClassMetrics(c, ci.values()))
				.collect(Collectors.toList());
		lm.write(cmList);
	}
}

class WarningContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		return (Object[]) inputElement;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return ((Node<?, ?>) parentElement).getChildren().toArray();
	}

	@Override
	public Object getParent(Object element) {
		return ((Node<?, ?>) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((Node<?, ?>) element).hasChildren();
	}
}

class ExplororLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		return ((Node<?, ?>) element).getLabel();
	}
}
