package Interfaces;

import Commands.SerializedCommand;
import Commands.SerializedCommands.SerializedArgumentCommand;
import Commands.SerializedCommands.SerializedCombinedCommand;
import Commands.SerializedCommands.SerializedMessage;
import Commands.SerializedCommands.SerializedObjectCommand;
import Exceptions.DatabaseException;

import java.io.IOException;
import java.net.Socket;

public interface CommandReceiver {
    boolean checkUser(String login, String password, Socket socket) throws DatabaseException, IOException;

    void tryAuth(String login, String password, Socket socket) throws DatabaseException, IOException;

    void sendObject(Socket socket, SerializedMessage serializedMessage) throws IOException, DatabaseException;

    void info(SerializedCommand command, Socket socket) throws IOException, DatabaseException;

    void show(SerializedCommand command, Socket socket) throws IOException, DatabaseException;

    void add(SerializedObjectCommand command, Socket socket) throws IOException, DatabaseException;

    void update(SerializedCombinedCommand command, Socket socket) throws IOException, DatabaseException;

    void removeById(SerializedArgumentCommand command, Socket socket) throws IOException, DatabaseException;

    void clear(SerializedCommand command, Socket socket) throws IOException, DatabaseException;

    void head(SerializedCommand command, Socket socket) throws IOException, DatabaseException;

    void removeGreater(SerializedObjectCommand command, Socket socket) throws IOException, DatabaseException;

    void removeLower(SerializedObjectCommand command, Socket socket) throws IOException, DatabaseException;

    void minBySemesterEnum(SerializedCommand command, Socket socket) throws IOException, DatabaseException;

    void maxByGroupAdmin(SerializedCommand command, Socket socket) throws IOException, DatabaseException;

    void countByGroupAdmin(SerializedObjectCommand command, Socket socket) throws IOException, DatabaseException;

    void register(String login, String password, Socket socket) throws IOException, DatabaseException;

    void sendCollection(Socket socket, String requireType) throws IOException, DatabaseException;

}
