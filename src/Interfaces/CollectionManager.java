package Interfaces;

import BasicClasses.Person;
import BasicClasses.StudyGroup;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface CollectionManager {
    void initList();

    ConcurrentLinkedQueue<StudyGroup> getLinkedList();

    void add(StudyGroup studyGroup);

    String getInfo();

    LinkedList<StudyGroup> show();

    void update(StudyGroup groupToUpdate, Integer elementId);

    void removeById(Integer groupId);

    void clear();

    LinkedList<StudyGroup> head();

    List<Integer> removeGreater(StudyGroup studyGroup, List<Integer> ids);

    List<Integer> removeLower(StudyGroup studyGroup, List<Integer> ids);

    LinkedList<StudyGroup> minBySemesterEnum();

    LinkedList<StudyGroup> maxByGroupAdmin();

    String countByGroupAdmin(Person groupAdmin);

    void appendToList(Object o);

}
