package triaina.webview.entity.device;

import android.os.Parcel;
import android.os.Parcelable;
import triaina.commons.json.annotation.Exclude;
import triaina.webview.entity.Params;

public class NetBrowserOpenParams implements Params {
	private String mUrl;
	
	public NetBrowserOpenParams() {}
	
	public NetBrowserOpenParams(Parcel parcel) {
		mUrl = parcel.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mUrl);
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public void setUrl(String url) {
		mUrl = url;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Exclude
	public static final Creator<NetBrowserOpenParams> CREATOR = new Creator<NetBrowserOpenParams>() {
		@Override
		public NetBrowserOpenParams createFromParcel(Parcel source) {
			return new NetBrowserOpenParams(source);
		}
        
		@Override
		public NetBrowserOpenParams[] newArray(int size) {
			return new NetBrowserOpenParams[size];
        }
    };
}
