package com.cmput.feelsbook.post;

import androidx.annotation.NonNull;

public enum SocialSituation {
    ALONE,
    ONEPERSON,
    SEVERAL,
    CROWD;

    @NonNull
    @Override
    public String toString() {
        String temp = this.name();
        return temp.substring(0,1) + temp.substring(1).toLowerCase();
    }
}