package emk4.Interfaces;

import java.io.IOException;

public interface Handleable {

    void handleRequest() throws InterruptedException;
    void close() throws IOException;

}
