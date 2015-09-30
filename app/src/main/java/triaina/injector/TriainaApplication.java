package triaina.injector;

import android.app.Application;
import android.util.Log;

import com.google.inject.Stage;

public class TriainaApplication extends Application {
	private static final String TAG = TriainaApplication.class.getSimpleName();
	
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			TriainaInjectorFactory.setBaseApplicationInjector(this, Stage.PRODUCTION);
		} catch (Exception exp) {
			Log.e(TAG, exp.getMessage() + "", exp);
		}
	}
}
