package triaina.webview.entity.device;

import android.os.Parcel;
import android.os.Parcelable;
import triaina.commons.json.annotation.Exclude;
import triaina.webview.entity.Params;

public class NewWindowParams implements Params {
	private String mUrl;
	
	public NewWindowParams() {}
	
	public NewWindowParams(Parcel source) {
		mUrl = source.readString();
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public void setUrl(String url) {
		mUrl = url;
	}
	
	@Exclude
	public static final Creator<NewWindowParams> CREATOR = new Creator<NewWindowParams>() {
		@Override
		public NewWindowParams createFromParcel(Parcel source) {
			return new NewWindowParams(source);
		}

		@Override
		public NewWindowParams[] newArray(int size) {
			return new NewWindowParams[size];
		}
	};
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mUrl);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
