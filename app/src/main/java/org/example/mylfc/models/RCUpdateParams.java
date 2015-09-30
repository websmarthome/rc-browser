package org.example.mylfc.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

import triaina.commons.json.annotation.Exclude;
import triaina.webview.entity.Params;

/**
 * Created by lyokato on 15/09/08.
 */
public class RCUpdateParams implements Params {
    private String mUuid;
    private String mValue;

    public RCUpdateParams(String uuid, String value) {
        mUuid = uuid;
        mValue = value;
    }

    public RCUpdateParams(Parcel source) {

        mUuid = source.readString();
        mValue = source.readString();
    }

    public String getUUID() {
        return mUuid;
    }

    public String getValue() {return mValue; }

    @Override
    public void writeToParcel(Parcel out, int flats) {

        out.writeString(mUuid);
        out.writeString(mValue);
    }

    @Exclude
    public static final Parcelable.Creator<RCUpdateParams> CREATOR =
            ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<RCUpdateParams>() {
                @Override
                public RCUpdateParams createFromParcel(Parcel in, ClassLoader loader) {
                    return new RCUpdateParams(in);
                }

                @Override
                public RCUpdateParams[] newArray(int size) {
                    return new RCUpdateParams[size];
                }
            });

    @Override
    public int describeContents() {
        return 0;
    }
}
