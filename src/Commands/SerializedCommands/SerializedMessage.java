package Commands.SerializedCommands;

import BasicClasses.StudyGroup;

import java.io.Serializable;
import java.util.LinkedList;

public class SerializedMessage implements Serializable {
    private String message;
    private LinkedList<StudyGroup> linkedList;

    public SerializedMessage(String message) {
        this.message = message;
        this.linkedList = null;
    }

    public SerializedMessage(LinkedList<StudyGroup> linkedList) {
        this.message = null;
        this.linkedList = linkedList;
    }

    public String getMessage() {
        return message;
    }

    public LinkedList<StudyGroup> getLinkedList() {
        return linkedList;
    }
}
