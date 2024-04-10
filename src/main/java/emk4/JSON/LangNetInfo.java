package emk4.JSON;

import com.google.gson.GsonBuilder;

public class LangNetInfo extends NetInfo{
    public String langCode;
    public String ipAddress;
    public int port;

    public LangNetInfo(String langCode, String ipAddress, int port) {
        super("Register request");
        this.langCode = langCode;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
