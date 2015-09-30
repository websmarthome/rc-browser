package triaina.webview.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.google.inject.Inject;

import java.io.File;
import java.lang.reflect.Method;

import triaina.commons.exception.InvalidConfigurationRuntimeException;
import triaina.commons.utils.ClassUtils;
import triaina.webview.Callback;
import triaina.webview.InjectorHelper;
import triaina.webview.WebViewBridge;
import triaina.webview.annotation.Bridge;
import triaina.webview.annotation.Domain;
import triaina.webview.annotation.Layout;
import triaina.webview.annotation.NoPause;
import triaina.webview.bridge.AccelerometerBridge;
import triaina.webview.bridge.NotificationBridge;
import triaina.webview.bridge.ToastBridge;
import triaina.webview.bridge.VibratorBridge;
import triaina.webview.bridge.WebStatusBridge;
import triaina.webview.bridge.WiFiBridge;
import triaina.webview.bridge.WiFiP2PBridge;
import triaina.webview.entity.Params;

public class WebViewBridgeAnnotationConfigurator implements
		WebViewBridgeConfigurator {
	private static final String TAG = WebViewBridgeAnnotationConfigurator.class.getSimpleName();

	@Inject
	private ConfigCache mConfigCache;

	@Inject
	private InjectorHelper mInjectorHelper;

	@Inject
	private Context mContext;

	@Override
	public WebViewBridge loadWebViewBridge(Activity activity) {
		Class<?> clazz = activity.getClass();
		LayoutConfig config = createLayoutConfig(clazz);

		activity.setContentView(config.getLayoutId());
		WebViewBridge webViewBridge = (WebViewBridge) activity
				.findViewById(config.getWebViewBridgeId());
		webViewBridge.setDomainConfig(createDomainConfig(clazz));
		webViewBridge.setNoPause(hasNoPause(clazz));

		return webViewBridge;
	}

	@Override
	public View loadInflatedView(Fragment fragment, LayoutInflater inflater,
			ViewGroup container) {
		Class<?> clazz = fragment.getClass();
		LayoutConfig config = createLayoutConfig(clazz);
		return inflater.inflate(config.getLayoutId(), container, false);
	}

	@Override
	public WebViewBridge loadWebViewBridge(Fragment fragment, View inflatedView) {
		Class<?> clazz = fragment.getClass();
		LayoutConfig config = createLayoutConfig(clazz);

		WebViewBridge webViewBridge = (WebViewBridge) inflatedView.findViewById(config.getWebViewBridgeId());
		webViewBridge.setDomainConfig(createDomainConfig(clazz));
		webViewBridge.setNoPause(hasNoPause(clazz));

		return webViewBridge;
	}

	private LayoutConfig createLayoutConfig(Class<?> clazz) {
		LayoutConfig config = mConfigCache.getLayoutConfig(clazz);

		if (config == null) {
			Log.d(TAG, "Miss cache layout config");

			Layout layoutAnn = (Layout) clazz.getAnnotation(Layout.class);
			if (layoutAnn == null) {
				throw new InvalidConfigurationRuntimeException(
						"Must be defined as use layout");
			}

			triaina.webview.annotation.WebViewBridgeResouce bridgeAnn = (triaina.webview.annotation.WebViewBridgeResouce) clazz
					.getAnnotation(triaina.webview.annotation.WebViewBridgeResouce.class);
			if (bridgeAnn == null)
				throw new InvalidConfigurationRuntimeException(
						"Must be defined as use layout");

			config = new LayoutConfig(layoutAnn.value(), bridgeAnn.value());
			mConfigCache.putLayoutConfig(clazz, config);
		} else
			Log.d(TAG, "Layout config from cache");

		return config;
	}

	private DomainConfig createDomainConfig(Class<?> clazz) {
		DomainConfig config = mConfigCache.getDomainConfig(clazz);

		if (config == null) {
			Domain domainAnn = (Domain) clazz.getAnnotation(Domain.class);
			if (domainAnn == null)
				throw new InvalidConfigurationRuntimeException(
						"Must be defined as access domain");

			config = new DomainConfig(domainAnn.value());
		} else
			Log.d(TAG, "Domain config from cache");

		return config;
	}
	
	private boolean hasNoPause(Class<?> clazz) {
	    return clazz.getAnnotation(NoPause.class) != null;
	}

	@Override
	public void configure(WebViewBridge bridge) {
		registerBridge(bridge, mContext);
		registerBridge(bridge, new WebStatusBridge(bridge));
		//registerBridge(bridge, new NetHttpSendBridge(bridge));
		registerBridge(bridge, new WiFiBridge(bridge));
		registerBridge(bridge, new VibratorBridge());
		registerBridge(bridge, new ToastBridge());
		registerBridge(bridge, new AccelerometerBridge(bridge));
		registerBridge(bridge, new NotificationBridge(bridge));
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			registerBridge(bridge, new WiFiP2PBridge(bridge));
	}

	@Override
	public void registerBridge(WebViewBridge webViewBridge, Object bridgeObject) {
		bridgeObject = mInjectorHelper.inject(mContext, bridgeObject);

		Class<?> clazz = bridgeObject.getClass();
		BridgeObjectConfig config = mConfigCache.getBridgeObjectConfig(clazz);

		if (config == null) {
			Log.d(TAG, "Miss cache bridge config");

			config = createBridgeConfig(bridgeObject);
			mConfigCache.putBridgeObjectConfig(clazz, config);
		} else
			Log.d(TAG, "Bridge config from cache");

		webViewBridge.addBridgeObjectConfig(bridgeObject, config);
	}

	private BridgeObjectConfig createBridgeConfig(Object bridgeObject) {
		Class<?> clazz = bridgeObject.getClass();

		Method[] methods = clazz.getMethods();
		BridgeObjectConfig configSet = new BridgeObjectConfig();

		for (Method method : methods) {
			Bridge chAnn = method.getAnnotation(Bridge.class);
			if (chAnn != null) {
				if (validDestinationMethod(method)) {
					Log.d(TAG, "dest:" + chAnn.value());
					Log.d(TAG, "method:" + method.getName());
					
					BridgeMethodConfig config = new BridgeMethodConfig(
							chAnn.value(), method);
					configSet.add(config);
				}
			}
		}

		return configSet;
	}

	private boolean validDestinationMethod(Method method) {
		Class<?>[] argTypes = method.getParameterTypes();

		switch (argTypes.length) {
		case 2:
			if (!ClassUtils.isImplement(argTypes[1], Callback.class))
				return false;
		case 1:
			if (!ClassUtils.isImplement(argTypes[0], Params.class)
					&& !ClassUtils.isImplement(argTypes[0], Callback.class))
				return false;
		case 0:
			break;
		default:
			Log.w(TAG, "dest:" + method.getName() + " has illegal arguments");
			return false;
		}

		return true;
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void configureSetting(WebViewBridge webViewBridge) {
		WebSettings settings = webViewBridge.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setGeolocationEnabled(true);
		settings.setDomStorageEnabled(true);
		File databasePath = new File(mContext.getCacheDir(), "webstorage");
		settings.setDatabasePath(databasePath.toString());
		settings.setAppCacheEnabled(true);
		settings.setAppCacheMaxSize(1024 * 1024 * 4);
	}

	// for test
	public void setInjectorHelper(InjectorHelper injectorHelper) {
		mInjectorHelper = injectorHelper;
	}

	// for test
	public void setContext(Context context) {
		mContext = context;
	}

	// for test
	public void setConfigCache(ConfigCache configCache) {
		mConfigCache = configCache;
	}
}
