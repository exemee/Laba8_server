package Commands.ConcreteCommands;

import Commands.Command;
import Commands.SerializedCommands.SerializedArgumentCommand;
import Exceptions.DatabaseException;
import Interfaces.CommandReceiver;

import java.io.IOException;
import java.net.Socket;

/**
 * Конкретная команда удаления по ID.
 */
public class RemoveByID extends Command {
    private static final long serialVersionUID = 32L;

    @Override
    public void execute(Object object, Socket socket, CommandReceiver commandReceiver) throws IOException, DatabaseException {
        SerializedArgumentCommand serializedArgumentCommand = (SerializedArgumentCommand) object;
        commandReceiver.removeById(serializedArgumentCommand, socket);
    }
}
