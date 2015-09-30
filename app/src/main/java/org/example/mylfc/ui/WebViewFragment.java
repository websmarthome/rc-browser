package org.example.mylfc.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.example.mylfc.R;
import org.example.mylfc.app.Constants;
import org.example.mylfc.models.RCConnectRequestParams;
import org.example.mylfc.models.RCSendRequestParams;
import org.example.mylfc.models.RCStateParams;
import org.example.mylfc.models.RCUpdateParams;

import mouthpiece.central.Destination;
import mouthpiece.central.MouthPieceCentral;
import triaina.webview.AbstractWebViewBridgeFragment;
import triaina.webview.TriainaWebChromeClient;
import triaina.webview.TriainaWebViewClient;
import triaina.webview.annotation.Bridge;
import triaina.webview.annotation.Domain;
import triaina.webview.annotation.Layout;
import triaina.webview.annotation.WebViewBridgeResouce;
import triaina.webview.exception.SkipDomainCheckRuntimeException;

@Domain({Constants.WEB_VIEW_DOMAIN})
@Layout(R.layout.fragment_web_view)
@WebViewBridgeResouce(R.id.my_webview)
public class WebViewFragment extends AbstractWebViewBridgeFragment {

    public static final String TAG = WebViewFragment.class.getSimpleName();

    public interface Callback {
        public void onRemoteControllerStartRequest(Destination destination);
        public void onRemoteControllerStopRequest();
        public void onRemoteControllerWriteRequest(String uuid, int value);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getWebViewBridge().setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    @Bridge("ble.connect")
    public void onBLEConnectRequest(RCConnectRequestParams params) {
        ((Callback)getActivity()).onRemoteControllerStartRequest(params.toDestination());
    }

    private String mCurrentUuid = "";

    @Bridge("ble.send")
    public void onBLESendRequest(RCSendRequestParams params) {
        String uuid = params.getUUID();
        String value = params.getValue();
        ((Callback)getActivity()).onRemoteControllerWriteRequest(uuid, Integer.parseInt(value));
    }

    @Override
    protected void configureClients() {
        getWebViewBridge().setWebChromeClient(createWebChromeClient());
        getWebViewBridge().setWebViewClient(createWebViewClient());
    }

    protected TriainaWebChromeClient createWebChromeClient() {
        return new TriainaWebChromeClient(getActivity());
    }

    protected TriainaWebViewClient createWebViewClient() {
        return new TriainaWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, url);
                throw new SkipDomainCheckRuntimeException("ignore domain check");
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                onWebViewPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (onWebViewShouldOverrideUrlLoading(view, url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                onWebViewReceivedError(view, errorCode, description, failingUrl);
            }
        };
    }

    protected void onWebViewReceivedError(WebView view, int errorCode, String description, String failingUrl) {

    }

    protected void onWebViewPageFinished(WebView view, String url) {
        stopRemoteController();
    }

    protected boolean onWebViewShouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    public void onCharacteristicUpdated(String serviceUUID, String characteristicUUID, int value) {
        getWebViewBridge().call("ble.update", new RCUpdateParams(characteristicUUID, String.valueOf(value)));
    }

    public void onRemoteControllerStateChanged(String serviceUUID, int stateCode) {
        String state = "";
        switch (stateCode) {
            case MouthPieceCentral.STATE_SCANNING:
                state = "SCANNING";
                break;
            case MouthPieceCentral.STATE_CONNECTED:
                state = "CONNECTED";
                break;
            case MouthPieceCentral.STATE_IDLE:
                state = "IDLE";
                break;
            case MouthPieceCentral.STATE_ERROR:
                state = "ERROR";
                break;
            default:
                // do nothing
        }

        getWebViewBridge().call("ble.connection.state", new RCStateParams(serviceUUID, state));

    }

    public void stopRemoteController() {
        ((Callback)getActivity()).onRemoteControllerStopRequest();
    }

    public void reload() {
        getWebViewBridge().reload();
        //testBLECall();
    }
    public void loadUrl(String url) {
        getWebViewBridge().loadUrl(url);
    }

}
