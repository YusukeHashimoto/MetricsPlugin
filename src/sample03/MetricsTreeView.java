package sample03;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import codeanalyzer.CodeAnalyzer;
import util.ProjectUtil;
import warning.Warning;
import warning.suggestion.SplitMethodSuggestion;
import warning.suggestion.Suggestion;

public class MetricsTreeView extends ViewPart {
	private TreeViewer viewer;
	private List<Warning> warnings = new ArrayList<>();

	public MetricsTreeView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getAdapter(Class arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		// viewer.setContentProvider(new ExplororContentProvider());
		viewer.setContentProvider(new WarningContentProvider());
		viewer.setLabelProvider(new ExplororLabelProvider());
		calc();
		// viewer.setInput(warnings.toArray());
		// viewer.setInput(MetricsCategory.SIMPLE_METRICS.getWarnings().toArray());
		viewer.setInput(MetricsCategory.values());
		viewer.addDoubleClickListener(event -> {

		});

		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private List<String> calc() {
		try {
			CodeAnalyzer ca = new CodeAnalyzer();
			ca.analyzeCodes(null, ProjectUtil.pathToPackage(), ProjectUtil.currentProject());
			warnings = ca.getWarnings();
			MetricsCategory.SIMPLE_METRICS.setWarnings(warnings);

			List<String> warnings = ca.getWarnings().stream().map(Warning::getMessage).collect(Collectors.toList());
			if (warnings.isEmpty())
				warnings.add("問題のあるメトリクスは見つかりませんでした");
			return warnings;
		} catch (Exception e) {
			List<String> list = new ArrayList<>();
			list.add("メトリクスを計算できませんでした" + e.toString());
			return list;
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
		/*
		 * if (parentElement instanceof Warning) { return ((Warning)
		 * parentElement).suggestions().toArray(); } return null;
		 */
		return ((Node) parentElement).getChildren().toArray();
	}

	@Override
	public Object getParent(Object element) {
		// return ((Suggestion) element).parentWarning();
		return ((Node) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		/*
		 * if (element instanceof Warning) { return true; } return false;
		 */
		return ((Node) element).hasChildren();
	}

}

class ExplororLabelProvider extends LabelProvider {
	/*
	 * public Image getImage(Object element) { if (((File)
	 * element).isDirectory()) { return
	 * ViewPlugin.getImageDescriptor("icons/folder.gif").createImage(); } else {
	 * return ViewPlugin.getImageDescriptor("icons/file.gif").createImage(); } }
	 */
	@Override
	public String getText(Object element) {
		/*
		 * if (element instanceof Warning) { return ((Warning)
		 * element).getMessage(); } else if (element instanceof Suggestion) {
		 * return ((Suggestion) element).message(); } return element.toString();
		 */
		return ((Node) element).getLabel();
	}
}

class DummyWarning extends Warning {
	private List<Suggestion> suggestions = new ArrayList<>();

	public DummyWarning() {
		super(null, null, null);
		suggestions.add(new SplitMethodSuggestion(this));
	}

	@Override
	public String getMessage() {
		return "test";
	}

	@Override
	public List<Suggestion> suggestions() {
		return suggestions;
	}

	@Override
	public List<Suggestion> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetricsCategory getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

}