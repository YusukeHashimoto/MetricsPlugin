package metricsplugin.views.metricstreeview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import codeanalyzer.CodeAnalyzer;
import util.ProjectUtil;
import warning.Warning;

public class MetricsTreeView extends ViewPart {
	private TreeViewer viewer;
	private List<Warning> warnings = new ArrayList<>();

	@Override
	public Object getAdapter(Class arg0) {
		// TODO Auto-generated method stub
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
			Object obj = ((IStructuredSelection)selection).getFirstElement();
			if(obj instanceof Warning) {
				Warning warning = (Warning)obj;
				ProjectUtil.openInEditor(warning.getFilename());
				ProjectUtil.markPosition(warning.getNode() != null ? warning.getNode().getStartPosition() : 0);
			}
		});

		getSite().setSelectionProvider(viewer);
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
		} catch (Exception e) {
			System.err.println("Failed to calcurate metrics\n" + e);
		}
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
