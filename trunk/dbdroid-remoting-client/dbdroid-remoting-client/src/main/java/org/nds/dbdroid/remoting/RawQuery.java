package org.nds.dbdroid.remoting;

public class RawQuery {

    private String rawQueryString;

    public RawQuery(String rawQueryString) {
        this.rawQueryString = rawQueryString;
    }

    public String getRawQueryString() {
        return rawQueryString;
    }

    public void setRawQueryString(String rawQueryString) {
        this.rawQueryString = rawQueryString;
    }

}
