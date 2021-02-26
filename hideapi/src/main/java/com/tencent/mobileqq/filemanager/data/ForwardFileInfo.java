package com.tencent.mobileqq.filemanager.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ForwardFileInfo implements Parcelable {
    protected ForwardFileInfo(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ForwardFileInfo> CREATOR = new Creator<ForwardFileInfo>() {
        @Override
        public ForwardFileInfo createFromParcel(Parcel in) {
            return new ForwardFileInfo(in);
        }

        @Override
        public ForwardFileInfo[] newArray(int size) {
            return new ForwardFileInfo[size];
        }
    };

    public String getFileName() {
        throw new IllegalArgumentException("Stub!");
    }
    public long getFileSize() {
        throw new IllegalArgumentException("Stub!");
    }
    public String getLocalPath() {
        throw new IllegalArgumentException("Stub!");
    }
    public int getType() {
        throw new IllegalArgumentException("Stub!");
    }
    public void setType(int type) {
        throw new IllegalArgumentException("Stub!");
    }
}
