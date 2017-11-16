package util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.*;
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
}
