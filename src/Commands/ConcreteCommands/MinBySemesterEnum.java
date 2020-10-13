package Commands.ConcreteCommands;

import Commands.Command;
import Commands.SerializedCommand;
import Exceptions.DatabaseException;
import Interfaces.CommandReceiver;

import java.io.IOException;
import java.net.Socket;

/**
 * Конкретная команда подсчета миниммального семестра.
 */
public class MinBySemesterEnum extends Command {
    private static final long serialVersionUID = 32L;

    @Override
    public void execute(Object argObject, Socket socket, CommandReceiver commandReceiver) throws IOException, DatabaseException {
        SerializedCommand serializedCommand = (SerializedCommand) argObject;
        commandReceiver.minBySemesterEnum(serializedCommand, socket);
    }
}
