package util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class ProjectUtil {
	
	private ProjectUtil() {}
	
	public static IProject currentProject() {
		IFileEditorInput editorInput = (IFileEditorInput)activeEditor().getEditorInput();
		IFile file = editorInput.getFile();
		return file.getProject();
	}
	
	public static AbstractTextEditor activeEditor() {
		return (AbstractTextEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}
	
	public static IFile editingFile() {
		return ((IFileEditorInput)activeEditor().getEditorInput()).getFile();
	}
	
	public static String pathToPackage() {
		String pathToFile =  editingFile().getLocationURI().getPath();
		if(pathToFile.charAt(0) == '/') pathToFile = pathToFile.substring(1);
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
}
