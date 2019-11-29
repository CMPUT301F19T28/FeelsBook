package com.cmput.feelsbook.post;

import androidx.annotation.NonNull;

/**
 * Contains the social situations used for displaying a Post object.
 */
public enum SocialSituation {

    SELECT_A_SOCIAL_SITUATION,
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

    public static SocialSituation getSocialSituation(String social){

        if(social.toLowerCase().equals("alone"))
            return SocialSituation.ALONE;

        else if(social.toLowerCase().equals("oneperson"))
            return SocialSituation.ONEPERSON;

        else if(social.toLowerCase().equals("several"))
            return SocialSituation.SEVERAL;

        else
            return SocialSituation.CROWD;
    }
}
