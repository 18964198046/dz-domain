package zgdx.xxaq.domain.enums;

public enum UserAgentTypeEnum {

    NONE(0, "*****"),
    BING(1, "Mozilla/5.0+(compatible;+bingbot/2.0;++http://www.bing.com/bingbot.htm)"),
    BAIDU(2,"Mozilla/5.0+(compatible;+Baiduspider/2.0;++http://www.baidu.com/search/spider.html)"),
    SOGOU(3,"Sogou+web+spider/4.0(+http://www.sogou.com/docs/help/webmasters.htm#07)"),
    GOOGLE(4,"Mozilla/5.0+(compatible;+Googlebot/2.1;++http://www.google.com/bot.html)");

    private int code;

    private String userAgent;

    UserAgentTypeEnum(int code, String userAgent) {
        this.code = code;
        this.userAgent = userAgent;
    }

    public int getCode() {
        return code;
    }

    public String getUserAgent() {
        return userAgent;
    }

}
