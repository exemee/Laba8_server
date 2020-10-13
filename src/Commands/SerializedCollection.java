package Commands;

import BasicClasses.StudyGroup;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SerializedCollection implements Serializable {
    private LinkedList<StudyGroup> linkedList;
    private List<List<Integer>> idElementsAllUsers;
    private String requireType;

    public SerializedCollection(LinkedList<StudyGroup> linkedList, List<List<Integer>> idElementsAllUsers, String requireType) {
        this.linkedList = linkedList;
        this.idElementsAllUsers = idElementsAllUsers;
        this.requireType = requireType;
    }

    public List<List<Integer>> getIdElementsAllUsers() {
        return idElementsAllUsers;
    }
    public LinkedList<StudyGroup> getLinkedList() {
        return linkedList;
    }
    public String getRequireType() {
        return requireType;
    }
}
