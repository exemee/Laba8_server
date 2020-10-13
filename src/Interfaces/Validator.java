package Interfaces;

import BasicClasses.Person;
import BasicClasses.StudyGroup;

public interface Validator {
    boolean validateStudyGroup(StudyGroup studyGroup);

    boolean validatePerson(Person person);
}
