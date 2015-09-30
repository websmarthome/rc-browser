package org.example.mylfc.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;

import java.util.ArrayList;
import java.util.List;

import mouthpiece.central.Destination;
import triaina.commons.json.annotation.Exclude;
import triaina.webview.entity.Params;

/**
 * Created by lyokato on 15/09/07.
 */
public class RCConnectRequestParams implements Params {

    private String mUuid;
    private String[] mObserve;
    private String[] mWrite;

    public RCConnectRequestParams() {

    }

    public RCConnectRequestParams(Parcel source) {

        mUuid = source.readString();
        mObserve = source.createStringArray();
        mWrite = source.createStringArray();
    }

    public String getUUID() {
        return mUuid;
    }
    public String[] getObserveDestinations() {
        return mObserve;
    }
    public String[] getWriteDestinations() {
        return mWrite;
    }

    public Destination toDestination() {
        List<String> observe = new ArrayList<String>();
        for (String u : mObserve) {
            observe.add(u);
        }
        List<String> write = new ArrayList<String>();
        for (String u : mWrite) {
            write.add(u);
        }

        List<String> read = new ArrayList<String>();
        Destination destination = new Destination(mUuid, observe, write, read);
        return destination;
    }

    @Override
    public void writeToParcel(Parcel out, int flats) {
        out.writeString(mUuid);
        out.writeStringArray(mObserve);
        out.writeStringArray(mWrite);
    }

    @Exclude
    public static final Parcelable.Creator<RCConnectRequestParams> CREATOR =
            ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<RCConnectRequestParams>() {
                @Override
                public RCConnectRequestParams createFromParcel(Parcel in, ClassLoader loader) {
                    return new RCConnectRequestParams(in);
                }

                @Override
                public RCConnectRequestParams[] newArray(int size) {
                    return new RCConnectRequestParams[size];
                }
            });

    @Override
    public int describeContents() {
        return 0;
    }
}
