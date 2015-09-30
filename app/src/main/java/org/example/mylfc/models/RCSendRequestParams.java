package org.example.mylfc.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

import triaina.commons.json.annotation.Exclude;
import triaina.webview.entity.Params;

/**
 * Created by lyokato on 15/09/07.
 */
public class RCSendRequestParams implements Params {


    private String mUuid;
    private String mValue;

    public RCSendRequestParams() {

    }

    public RCSendRequestParams(Parcel source) {

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
    public static final Parcelable.Creator<RCSendRequestParams> CREATOR =
            ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<RCSendRequestParams>() {
                @Override
                public RCSendRequestParams createFromParcel(Parcel in, ClassLoader loader) {
                    return new RCSendRequestParams(in);
                }

                @Override
                public RCSendRequestParams[] newArray(int size) {
                    return new RCSendRequestParams[size];
                }
            });

    @Override
    public int describeContents() {
        return 0;
    }
}
