package Commands.ConcreteCommands;

import Commands.Command;
import Commands.SerializedCommands.SerializedCombinedCommand;
import Exceptions.DatabaseException;
import Interfaces.CommandReceiver;

import java.io.IOException;
import java.net.Socket;

/**
 * Конкретная команда обновления объекта.
 */
public class Update extends Command {
    private static final long serialVersionUID = 32L;

    @Override
    public void execute(Object argObject, Socket socket, CommandReceiver commandReceiver) throws IOException, DatabaseException {
        SerializedCombinedCommand serializedCombinedCommand = (SerializedCombinedCommand) argObject;
        commandReceiver.update(serializedCombinedCommand, socket);
    }
}
