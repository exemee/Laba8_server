package Commands.ConcreteCommands;

import Commands.Command;
import Commands.SerializedCommands.SerializedObjectCommand;
import Exceptions.DatabaseException;
import Interfaces.CommandReceiver;

import java.io.IOException;
import java.net.Socket;

/**
 * Конкретная команда удаления объектов, превышающих заданный.
 */
public class RemoveGreater extends Command {
    private static final long serialVersionUID = 32L;

    @Override
    public void execute(Object argObject, Socket socket, CommandReceiver commandReceiver) throws IOException, DatabaseException {
        SerializedObjectCommand serializedObjectCommand = (SerializedObjectCommand) argObject;
        commandReceiver.removeGreater(serializedObjectCommand, socket);
    }
}
