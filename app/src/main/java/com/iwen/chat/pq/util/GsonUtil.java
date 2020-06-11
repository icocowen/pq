package com.iwen.chat.pq.util;

import com.google.gson.Gson;

public class GsonUtil {
    private GsonUtil(){};
    private static class GsonUtilInner {
        final static Gson gsonUtil = new Gson();
    }

    public static Gson getInstance() {
        return GsonUtilInner.gsonUtil;
    }


}
