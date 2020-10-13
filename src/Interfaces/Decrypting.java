package Interfaces;

import Exceptions.DatabaseException;

import java.io.IOException;
import java.net.Socket;

public interface Decrypting {
    void decrypt(Object o, Socket socket) throws IOException, InterruptedException, ClassNotFoundException, DatabaseException;
}
