package org.example.mylfc.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

import triaina.commons.json.annotation.Exclude;
import triaina.webview.entity.Params;

/**
 * Created by lyokato on 15/09/14.
 */
public class RCStateParams implements Params {

    private String mUuid;
    private String mState;

    public RCStateParams(String uuid, String state) {
        mUuid = uuid;
        mState = state;
    }

    public RCStateParams(Parcel source) {
        mUuid = source.readString();
        mState = source.readString();
    }

    public String getUUID() {
        return mUuid;
    }
    public String getState() {
        return mState;
    }

    @Override
    public void writeToParcel(Parcel out, int flats) {
        out.writeString(mUuid);
        out.writeString(mState);
    }

    @Exclude
    public static final Parcelable.Creator<RCStateParams> CREATOR =
            ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<RCStateParams>() {
                @Override
                public RCStateParams createFromParcel(Parcel in, ClassLoader loader) {
                    return new RCStateParams(in);
                }

                @Override
                public RCStateParams[] newArray(int size) {
                    return new RCStateParams[size];
                }
            });

    @Override
    public int describeContents() {
        return 0;
    }
}
