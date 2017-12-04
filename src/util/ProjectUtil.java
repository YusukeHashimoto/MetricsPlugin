package util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class ProjectUtil {

	private ProjectUtil() {}

	public static IProject currentProject() {
		IFileEditorInput editorInput = (IFileEditorInput)activeEditor().getEditorInput();
		IFile file = editorInput.getFile();
		return file.getProject();
	}

	/**
	 * Return AbstractTextEditor currently opened.<br>
	 * Return null if no editor has opened or editor is not text editor.
	 * @return
	 */
	public static AbstractTextEditor activeEditor() {
		IEditorPart e = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(e instanceof AbstractTextEditor) {
			return (AbstractTextEditor)e;
		} else {
			return null;
		}
		//return (AbstractTextEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	public static IFile editingFile() {
		return ((IFileEditorInput)activeEditor().getEditorInput()).getFile();
	}

	/**
	 * Return path to package of file currently editor shows.<br>
	 * @return
	 */
	public static String pathToPackage() {
		String pathToFile =  editingFile().getLocationURI().getPath();
		if(pathToFile.charAt(0) == '/' && pathToFile.charAt(1) != 'U') pathToFile = pathToFile.substring(1);
		return parentDirOf(pathToFile);
	}

	private static String parentDirOf(String filePath) {
		for(int i = filePath.length()-1; i > 1; i--) {
			if(filePath.charAt(i) == '/') {
				return filePath.substring(0, i+1);
			}
		}
		Log.fatal("Input URI does not contain '/', it is not directory URI.");
		return null;
	}

	public static String packageOf(String qualifiedName) {
		int i = qualifiedName.lastIndexOf('.');
		return qualifiedName.substring(0, i);
	}

	public static void openInEditor(String filename) {
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
	public static void markLine(int line) {
		if(line < 1) line = 1;
		
		IEditorInput editorInput = activeEditor().getEditorInput();
		IResource resource = (IResource)editorInput.getAdapter(IResource.class);

		Map<String, Integer> attributes = new HashMap<>();
		attributes.put(IMarker.LINE_NUMBER, new Integer(line));
		//attributes.put(IMarker.CHAR_START, line);
		//attributes.put(IMarker.CHAR_END, line);

		try {
			IMarker marker = resource.createMarker(IMarker.TEXT);
			marker.setAttributes(attributes);
			IDE.gotoMarker(activeEditor(), marker);
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public static void markPosition(int position) {
		IEditorInput editorInput = activeEditor().getEditorInput();
		IResource resource = (IResource)editorInput.getAdapter(IResource.class);

		Map<String, Integer> attributes = new HashMap<>();
		attributes.put(IMarker.CHAR_START, position);
		attributes.put(IMarker.CHAR_END, position);

		try {
			IMarker marker = resource.createMarker(IMarker.TEXT);
			marker.setAttributes(attributes);
			IDE.gotoMarker(activeEditor(), marker);
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
