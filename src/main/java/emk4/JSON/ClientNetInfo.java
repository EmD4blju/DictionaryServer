package emk4.JSON;

import com.google.gson.GsonBuilder;

public class ClientNetInfo extends NetInfo{

    public String word;
    public String langCode;
    public String ipAddress;
    public int port;

    public ClientNetInfo(String word, String langCode, String ipAddress, int port) {
        super("Translate request");
        this.word = word;
        this.langCode = langCode;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
