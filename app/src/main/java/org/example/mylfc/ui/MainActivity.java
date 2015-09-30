package org.example.mylfc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.example.mylfc.R;

import mouthpiece.central.Characteristic;
import mouthpiece.central.MouthPieceCentral;

import mouthpiece.central.Destination;
import triaina.injector.activity.TriainaFragmentActivity;


public class MainActivity extends TriainaFragmentActivity implements WebViewFragment.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MouthPieceCentral mRemoteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRemoteController();

        if (Intent.ACTION_VIEW.equals(getIntent().getAction()) ){
            loadUrl(getIntent().getDataString());
        } else {
            loadUrl("http://litelite.net/");
        }

    }

    public void setupRemoteController() {
        mRemoteController = new MouthPieceCentral(this, new MouthPieceCentral.Listener() {
            @Override
            public void onCharacteristicReceived(Characteristic characteristic) {
                String uuid = characteristic.getUuid();
                String serviceUUID = characteristic.getServiceUuid();
                int value = characteristic.getIntValue();
                Log.d(TAG, "found characteristic:" + uuid + ":" + String.valueOf(value));
                notifyCharacteristicValueToBrowser(serviceUUID, uuid, value);
            }

            @Override
            public void onStateChanged(String serviceUUID, int state) {

                switch (state) {
                    case MouthPieceCentral.STATE_CONNECTED:
                        Log.d(TAG, "state changed:" + "connected");
                        notifyStateToBrowser(serviceUUID, state);
                        break;
                    case MouthPieceCentral.STATE_IDLE:
                        Log.d(TAG, "state changed:" + "idle");
                        notifyStateToBrowser(serviceUUID, state);
                        break;
                    case MouthPieceCentral.STATE_SCANNING:
                        Log.d(TAG, "state changed:" + "scanning");
                        notifyStateToBrowser(serviceUUID, state);
                        break;
                    case MouthPieceCentral.STATE_ERROR:
                        Log.d(TAG, "state changed:" + "error");
                        notifyStateToBrowser(serviceUUID, state);
                        break;
                    default:
                        // do nothing
                }
            }
        });
        mRemoteController.initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mRemoteController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRemoteController.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRemoteController.pause();
    }

    @Override
    public void onDestroy() {
        mRemoteController.destroy();
        super.onDestroy();
    }

    private Destination mPageSettings;


    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyStateToBrowser(final String serviceUUID, final int state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWebViewFragment().onRemoteControllerStateChanged(serviceUUID, state);
            }
        });
    }

    private void notifyCharacteristicValueToBrowser(final String serviceUUID,
        final String characteristicUUID, final int value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWebViewFragment().onCharacteristicUpdated(serviceUUID, characteristicUUID, value);
            }
        });

    }

    public void loadUrl(String url) {
        getWebViewFragment().loadUrl(url);
    }

    public WebViewFragment getWebViewFragment() {
        return (WebViewFragment)getSupportFragmentManager().findFragmentById(R.id.lfc_webview_fragment);
    }

    @Override
    public void onRemoteControllerStartRequest(Destination destination) {
        if (mRemoteController.canStart()) {
            mRemoteController.start(destination);
        } else {
            Log.d(TAG, "remote controller cannot start");
        }
    }

    @Override
    public void onRemoteControllerWriteRequest(String uuid, int value) {
        mRemoteController.writeInt(uuid, value);
    }

    @Override
    public void onRemoteControllerStopRequest() {
        mRemoteController.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_realod) {
            getWebViewFragment().reload();
            return true;
        }
        if (id == R.id.action_stop_ble) {
            mRemoteController.stop();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
