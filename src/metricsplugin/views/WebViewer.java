package metricsplugin.views;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class WebViewer {
	private WebViewer() {}
	
	public static void showExternalBrowser(String url) {
		IWorkbenchBrowserSupport browserSupport = PlatformUI
				.getWorkbench().getBrowserSupport();
		try {
			IWebBrowser browser = browserSupport.getExternalBrowser();

			//logger.debug(browser);
			browser.openURL(new URL(url));
		} catch (PartInitException e) {
			//logger.error("widgetSelected(SelectionEvent)", e);
			e.printStackTrace();
		} catch (MalformedURLException e) {
			//logger.error("widgetSelected(SelectionEvent)", e);
			e.printStackTrace();
		}
	}

	public static void showInternalBrowser(String url, String title) {
		//String url = "http://www.eisbahn.jp/";
		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
		if (browserSupport.isInternalWebBrowserAvailable()) {
			IWebBrowser browser;
			try {
				browser = browserSupport.createBrowser(
						IWorkbenchBrowserSupport.LOCATION_BAR,
						"id1",
						"Inheritance tree",
						"Inheritance tree"
						);
				//browser = browserSupport.createBrowser("hoge");
				browser.openURL(new URL(url));
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
