## Dictionary server ##
Client-server programming using sockets and network protocols (multi-threaded server, without channels and selectors)

Design an application that allows using a dictionary service in different languages. The application structure should consist of at least:

* main server,
* client with a simple GUI,
* servers responsible for dictionaries in various languages.

Each dictionary server stores data in one language, for simplification, it can be assumed that they are pairs {Polish word, translation}. Languages are identified by short codes (e.g. "PL", "EN", "FR", ...). The application should allow easy addition of support/servers for new languages.

The client sends a query to the main server in the form {"Polish word to translate", "target language code", port}. In case the client provides an incorrect/non-existent language code, the main server should return an appropriate message to the client. The port on which the client expects the translation will be closed after receiving the information from the dictionary server.

The main server sends a message to a specific dictionary server in the form {"Polish word to translate", client address, port on which the client is waiting for the result}. A connection is made from the dictionary server to the client, and the translation result is forwarded, after which the connection is closed.
