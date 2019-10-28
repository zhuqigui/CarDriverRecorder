package com.softwinner.bionrecorder.common;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class C {

    // 录像质量
    public static class RecordQuality {
        public static final int RECORD_QUALITY_720P = 720;
        public static final int RECORD_QUALITY_1080P = 1080;
    }

    // 拍照质量
    public static class PictureQuality {
        public static final int PICTURE_QUALITY_720P = 720;
        public static final int PICTURE_QUALITY_1080P = 1080;

    }

    // 录像时间
    public static class RecordDuraton {
        public static final int ONE_MINUTE_DURATION = 1;
        public static final int TWO_MINUTE_DURATION = 2;
        public static final int THREE_MINUTE_DURATION = 3;

    }

//    public static class SIZE {
//        public static final int SIZE_1080P_1MIN = (120 * 1024 * 1024).toLong()
//        public static final int SIZE_720P_1MIN = (110 * 1024 * 1024).toLong()
//    }
}
