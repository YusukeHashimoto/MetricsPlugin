package sample03;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import codeanalyzer.CodeAnalyzer;
import util.ProjectUtil;
import warning.Suggestion;
import warning.Warning;

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
		warnings.add(new DummyWarning());
		warnings.add(new DummyWarning());
		warnings.add(new DummyWarning());

		// viewer.setInput(File.listRoots());
		viewer.setInput(warnings.toArray());
		/*
		 * viewer.setSorter(new ViewerSorter() {
		 * 
		 * @Override public int category(Object element) { if (((File)
		 * element).isDirectory()) { return 0; } return 1; } });
		 */
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
		if (parentElement instanceof DummyWarning) {
			return ((DummyWarning) parentElement).suggestions().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof DummyWarning) {
			return true;
		}
		return false;
	}

}

class ExplororContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		File[] children = ((File) parentElement).listFiles();
		return children == null ? new Object[0] : children;
	}

	@Override
	public Object getParent(Object element) {
		return ((File) element).getParentFile();
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length == 0 ? false : true;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return (File[]) inputElement;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
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
		 * File file = (File) element; String name = file.getName(); if
		 * (name.equals("")) { name = file.getPath(); } return name;
		 */
		if (element instanceof Suggestion) {
			return ((Suggestion) element).message();
		}
		return element.toString();
	}
}

class DummyWarning extends Warning {
	private List<Suggestion> suggestions = new ArrayList<>();

	public DummyWarning() {
		super(null, null, null);
		suggestions.add(new Suggestion(Suggestion.SPLIT_METHOD));
	}

	@Override
	public String getMessage() {
		return "test";
	}

	@Override
	public List<Suggestion> suggestions() {
		return suggestions;
	}

}