package metricsplugin.views;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class MyActivator extends AbstractUIPlugin {
	//public static final String IMG_VERTICAL = "vertical";
	public static final String IMG_REFRESH = "refresh";
	//ほかにもキー値を作っておく
	
	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		//registerImage(registry, IMG_VERTICAL, "th_vertical.gif");
		registerImage(registry, IMG_REFRESH, "refresh.gif");
		// ..他にも登録
	}

	private void registerImage(ImageRegistry registry, String key,String fileName){
		try {
			IPath path = new Path("icons/" + fileName);
			URL url = find(path);
			if (url != null) {
				ImageDescriptor desc = ImageDescriptor.createFromURL(url);
				registry.put(key, desc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public ImageRegistry getImageRegistry() {
		return super.getImageRegistry();
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin("sample03", path);
	}
}