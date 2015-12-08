
package com.linkage.mobile72.sh.utils;

import com.linkage.mobile72.sh.Consts;

public class AvatarUrlUtils {

    public static String getAvatarUrl(long userId) {
        return Consts.HOST_AVATAR + userId;
    }
    
    public static String getGroupUrl(long groupId) {
        return Consts.HOST_GROUP_AVATAR + groupId;
    }

}
