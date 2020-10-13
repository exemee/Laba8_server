package Collection;

import BasicClasses.*;
import Interfaces.CollectionManager;
import Interfaces.CollectionUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Менеджер коллекцией. Описывает логику команд, выполняющих работу с коллекцией.
 */
@Singleton
public class CollectionManagerImp implements CollectionManager {
    private ConcurrentLinkedQueue<StudyGroup> linkedList;
    private ZonedDateTime creationDate;
    private List res = new ArrayList();
    private final CollectionUtils collectionUtils;

    @Inject
    public CollectionManagerImp(CollectionUtils collectionUtils) {
        this.collectionUtils = collectionUtils;
    }

    @Override
    public void initList() {
        if (linkedList == null) {
            linkedList = new ConcurrentLinkedQueue<>();
            creationDate = ZonedDateTime.now();
        } else linkedList.clear();

    }

    @Override
    public ConcurrentLinkedQueue<StudyGroup> getLinkedList() {
        return linkedList;
    }

    @Override
    public void add(StudyGroup studyGroup) {
        linkedList.add(studyGroup);
    }

    @Override
    public String getInfo() {
        String info = "";
        info += "%type " + linkedList.getClass().getName() + "\n";
        info += "%data " + creationDate + "\n";
        info += "%size " + linkedList.size() + "\n";

        return info;
    }

    @Override
    public LinkedList<StudyGroup> show() {
        return new LinkedList<>(linkedList);
    }

    @Override
    public void update(StudyGroup groupToUpdate, Integer elementId) {
        linkedList.forEach(studyGroup -> {
            if (studyGroup.getId().equals(elementId)) {
                studyGroup.setName(groupToUpdate.getName());
                studyGroup.setCoordinates(groupToUpdate.getCoordinates());
                studyGroup.setStudentsCount(groupToUpdate.getStudentsCount());
                studyGroup.setFormOfEducation(groupToUpdate.getFormOfEducation());
                studyGroup.setSemesterEnum(groupToUpdate.getSemesterEnum());
                studyGroup.setGroupAdmin(groupToUpdate.getGroupAdmin());
            }
        });
    }

    @Override
    public void removeById(Integer groupId) {
        linkedList.forEach(studyGroup -> {
            if (studyGroup.getId().equals(groupId)) { linkedList.remove(studyGroup); }
        });
    }

    @Override
    public void clear() {
        linkedList.clear();
    }

    @Override
    public LinkedList<StudyGroup> head() {
        LinkedList<StudyGroup> res = new LinkedList<>();
        res.add(linkedList.peek());
        return res;
    }

    @Override
    public List<Integer> removeGreater(StudyGroup studyGroup, List<Integer> ids) {
        res.clear();
        Iterator<StudyGroup> iterator = linkedList.iterator();
        while (iterator.hasNext()){
            StudyGroup listStudyGroup = iterator.next();
            if (ids.contains(listStudyGroup.getId()) && listStudyGroup.compareTo(studyGroup) > 0) {
                appendToList(listStudyGroup.getId());
                iterator.remove();
            }
        }

        return res;
    }

    @Override
    public List<Integer> removeLower(StudyGroup studyGroup, List<Integer> ids) {
        res.clear();
        Iterator<StudyGroup> iterator = linkedList.iterator();
        while (iterator.hasNext()){
            StudyGroup listStudyGroup = iterator.next();
            if (ids.contains(listStudyGroup.getId()) && listStudyGroup.compareTo(studyGroup) < 0) {
                appendToList(listStudyGroup.getId());
                iterator.remove();
            }
        }

        return res;
    }

    @Override
    public LinkedList<StudyGroup> minBySemesterEnum() {
        LinkedList<StudyGroup> res = new LinkedList<>();
        res.add(Collections.min(linkedList,
                Comparator.comparingInt(studyGroup -> studyGroup.getSemesterEnum().getValue())));
        return res;
    }

    @Override
    public LinkedList<StudyGroup> maxByGroupAdmin() {
        LinkedList<StudyGroup> res = new LinkedList<>();
        res.add(Collections.max(linkedList,
                Comparator.comparingInt(studyGroup -> studyGroup.getGroupAdmin().compareValue())));
        return res;
    }

    @Override
    public String countByGroupAdmin(Person groupAdmin) {
        return Long.toString(linkedList.stream().filter(studyGroup -> studyGroup.getGroupAdmin().equals(groupAdmin)).count());
    }

    @Override
    public void appendToList(Object o){
        res.add(o);
    }
}
