package com.cmput.feelsbook.post;

import androidx.annotation.NonNull;

public enum SocialSituation {

    ALONE,
    ONEPERSON,
    SEVERAL,
    CROWD;


    /**
     * @return
     * The type of situation with the first letter uppercase and the rest lowercase
     */
    @NonNull
    @Override
    public String toString() {
        String temp = this.name();
        return temp.substring(0,1) + temp.substring(1).toLowerCase();
    }
}
