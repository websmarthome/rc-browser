package triaina.webview.config;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import triaina.webview.WebViewBridge;

public interface WebViewBridgeConfigurator {
	
	public WebViewBridge loadWebViewBridge(Activity activity);
	
	public View loadInflatedView(Fragment fragment, LayoutInflater inflater, ViewGroup container);
	
	public WebViewBridge loadWebViewBridge(Fragment fragment, View inflatedView);
	
	public void configure(WebViewBridge webViewBridge);
	
	public void registerBridge(WebViewBridge webViewBridge, Object bridgeObject);
	
	public void configureSetting(WebViewBridge webViewBridge);
}
