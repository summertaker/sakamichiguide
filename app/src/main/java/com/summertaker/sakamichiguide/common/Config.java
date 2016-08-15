package com.summertaker.sakamichiguide.common;

public class Config {
    public final static String PLATFORM = "android-phone";
    public final static String VERSION = "3.5";

    public final static String PACKAGE_NAME = "com.summertaker.sakamichiguide";
    public final static String USER_PREFERENCE_KEY = PACKAGE_NAME;

    public final static String URL_GOOGLE_PLAY = "market://details?id=" + PACKAGE_NAME;

    public final static String USER_AGENT_WEB = "Mozilla/5.0 (Windows NT x.y; Win64; x64; rv:10.0) Gecko/20100101 Firefox/10.0";
    public final static String USER_AGENT_MOBILE = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";

    public final static int CACHE_EXPIRE_TIME = 1; // 5분, 60 * 24; // 1일
    public final static String CACHE_IMAGE_SAVE_PATH = "/sakamichiguide/"; // 캐쉬 이미지 저장 폴더

    public final static String CACHE_ID_VOTES = "votes";
    public final static String CACHE_ID_BLOG_LIST_CHANGED = "_CHANGED";
    public final static String CACHE_ID_BLOG_CHECK_SUFFIX = "_CHECK";

    public final static String SETTING_DISPLAY_OFFICIAL_PHOTO = "SETTING_DISPLAY_OFFICIAL_PHOTO";
    public final static String SETTING_DISPLAY_OFFICIAL_PHOTO_YES = "2";
    public final static String SETTING_DISPLAY_OFFICIAL_PHOTO_NO = "1";

    public final static String GROUP_ID_NOGIZAKA46 = "NOGIZAKA46";
    public final static String GROUP_ID_KEYAKIZAKA46 = "KEYAKIZAKA46";

    public final static int BLOG_UPDATING_CHECK_INTERVAL = 10; // 10 Min
    public final static String BLOG_ID_NOGIZAKA46_MEMBER = "NOGIZAKA46_MEMBER";
    public final static String BLOG_ID_KEYAKIZAKA46_MEMBER = "KEYAKIZAKA46_MEMBER";

    public final static String MAIN_ACTION_MEMBER = "MEMBER";
    public final static String MAIN_ACTION_BLOG = "BLOG";
    public final static String MAIN_ACTION_ELECTION = "ELECTION";
    public final static String MAIN_ACTION_BIRTHDAY = "BIRTHDAY";
    public final static String MAIN_ACTION_SLIDE = "SLIDE";
    public final static String MAIN_ACTION_MEMORY = "MEMORY";
    public final static String MAIN_ACTION_QUIZ = "QUIZ";
    public final static String MAIN_ACTION_RAW_PHOTO = "RAW_PHOTO";
    public final static String MAIN_ACTION_PUZZLE = "PUZZLE";
    public final static String MAIN_ACTION_OSHIMEN = "OSHIMEN";

    public final static String TOOLBAR_ICON_LOGO = "logo";
    public final static String TOOLBAR_ICON_BACK = "back";

    public final static String SITE_ID_OFFICIAL_PROFILE = "profile";
    public final static String SITE_ID_BLOG = "blog";
    public final static String SITE_ID_GOOGLE_PLUS = "googlePlus";
    public final static String SITE_ID_FACEBOOK = "facebook";
    public final static String SITE_ID_TWITTER = "twitter";
    public final static String SITE_ID_INSTAGRAM = "instagram";
    public final static String SITE_ID_NANAGOGO = "nanagogo";
    public final static String SITE_ID_WEIBO = "weibo";
    public final static String SITE_ID_QQ = "qq";
    public final static String SITE_ID_BAIDU = "baidu";
    public final static String SITE_ID_NAMUWIKI = "namuwkik";
    public final static String SITE_ID_PEDIA48 = "48pedia";
    public final static String SITE_ID_STAGE48 = "stage48";
    public final static String SITE_ID_GOOGLE_IMAGE_SEARCH = "googleImageSearch";
    public final static String SITE_ID_YAHOO_IMAGE_SEARCH = "yahooImageSearch";

    public final static String PUZZLE_LEVEL_EASY = "easy";
    public final static String PUZZLE_LEVEL_NORMAL = "normal";
    public final static String PUZZLE_LEVEL_HARD = "hard";

    /*public final static int IMAGE_FULL_WIDTH = 480;
    public final static int IMAGE_FULL_HEIGHT = 480;
    public final static int IMAGE_GRID2_WIDTH = 180;
    public final static int IMAGE_GRID2_HEIGHT = 220;
    public final static int IMAGE_GRID3_WIDTH = 120;
    public final static int IMAGE_GRID3_HEIGHT = 147;
    public final static int IMAGE_GRID4_WIDTH = 100;
    public final static int IMAGE_GRID4_HEIGHT = 100;*/

    public final static int PROGRESS_BAR_COLOR_WHITE = 0xffffffff;
    public final static int PROGRESS_BAR_COLOR_NORMAL = 0x669c27b0;
    public final static int PROGRESS_BAR_COLOR_LIGHT = 0x669c27b0;

    public final static int RESULT_CODE_FINISH = 900;
    public final static int REQUEST_CODE_DATA_UPDATE = 700;
    public final static int RESULT_CODE_GO_UPDATE = 720;
}
