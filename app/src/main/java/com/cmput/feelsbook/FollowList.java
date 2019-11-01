package com.cmput.feelsbook;

import java.io.Serializable;
import java.util.List;

public class FollowList implements Serializable {
    private List<User> following;
    private List<User> followingRequests;
    private List<User> followers;
}
