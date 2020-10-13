package Interfaces;

import BasicClasses.StudyGroup;
import Exceptions.DatabaseException;

import java.sql.Connection;
import java.util.List;

public interface DatabaseManager {

    Integer addElement(StudyGroup studyGroup, String username) throws DatabaseException;

    void handleQuery(SqlConsumer<Connection> queryBody) throws DatabaseException;

    <T> T handleQuery(SqlFunction<Connection, T> queryBody) throws DatabaseException;

    boolean removeById(int id, String username) throws DatabaseException;

    boolean updateById(StudyGroup studyGroup, int id, String username) throws DatabaseException;

    void loadCollectionFromDatabase(CollectionManager collectionManager) throws DatabaseException;

    String getPassword(String username) throws DatabaseException;

    boolean doesUserExist(String username) throws DatabaseException;

    void addUser(String username, String password) throws DatabaseException;

    List<Integer> clear(String username) throws DatabaseException;

    List<Integer> getIdOfUserElements(String username) throws DatabaseException;

    List<List<Integer>> getIdElementsAllUsers() throws DatabaseException;

    boolean validateUserData(String login, String password) throws DatabaseException;
}
