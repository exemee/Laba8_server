package Collection;

import BasicClasses.StudyGroup;
import Interfaces.CollectionManager;
import Interfaces.CollectionUtils;
import com.google.inject.Inject;

/**
 * Класс, содержащий утилиты для работы с коллекцией.
 */
public class CollectionUtilsImp implements CollectionUtils {
    private final CollectionManager collectionManager;

    @Inject
    public CollectionUtilsImp(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean checkExist(Integer ID) {
        for (StudyGroup studyGroup:collectionManager.getLinkedList()) {
            if (studyGroup.getId().equals(ID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String display(StudyGroup studyGroup) {
        String info = "";
        info = String.format("ID элемента коллекции – %s\n" +
                "Название группы – %s\n" +
                "Координата X – %s\n" +
                "Координата Y – %s\n" +
                "Дата и время создания элемента – %s\n" +
                "Количество студентов в группе – %s\n" +
                "Форма обучения –  %s\n" +
                "Номер семестра – %s\n" +
                "Имя админа группы – %s\n" +
                "Рост админа группы – %s\n" +
                "Цвет глаз админа группы – %s\n" +
                "Цвет волос админа группы – %s\n" +
                "Национальность админа группы – %s\n" +
                "_________________________________________________________\n",  studyGroup.getId(), studyGroup.getName(), studyGroup.getCoordinates().getX(),
                studyGroup.getCoordinates().getY(), studyGroup.getCreationDate(), studyGroup.getStudentsCount(),
                studyGroup.getFormOfEducation(), studyGroup.getSemesterEnum(), studyGroup.getGroupAdmin().getName(),
                studyGroup.getGroupAdmin().getHeight(), studyGroup.getGroupAdmin().getEyeColor(), studyGroup.getGroupAdmin().getHairColor(),
                studyGroup.getGroupAdmin().getNationality());

        return info;
    }
}
