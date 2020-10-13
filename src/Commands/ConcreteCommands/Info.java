package Commands.ConcreteCommands;

import Commands.Command;
import Commands.SerializedCommand;
import Exceptions.DatabaseException;
import Interfaces.CommandReceiver;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * Конкретная команда получения информации о коллекции.
 */
public class Info extends Command implements Serializable {
    private static final long serialVersionUID = 32L;

    @Override
    public void execute(Object argObject, Socket socket, CommandReceiver commandReceiver) throws IOException, DatabaseException {
        SerializedCommand serializedCommand = (SerializedCommand) argObject;
        commandReceiver.info(serializedCommand, socket);
    }
}
