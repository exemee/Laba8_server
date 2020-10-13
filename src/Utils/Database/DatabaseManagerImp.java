package Utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import BasicClasses.*;
import Exceptions.DatabaseException;

import Interfaces.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DatabaseManagerImp implements DatabaseManager {
    private final String url = "jdbc:postgresql://localhost:5432/ITMO";
    private final String user = "postgres";
    private final String password = "qwerty12345";
    private final String salty = "Primite Labu";
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManagerImp.class);
    private final HashEncrypter hashEncrypter;

    @Inject
    public DatabaseManagerImp(HashEncrypter hashEncrypter) {
        this.hashEncrypter = hashEncrypter;
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("Драйвер подключён");
        } catch (ClassNotFoundException e) {
            logger.error("Драйвер не подключён");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Добавляет элемент в базу данных.
     */
    @Override
    public Integer addElement(StudyGroup studyGroup, String username) throws DatabaseException {
        return this.<Integer>handleQuery((Connection connection) -> {

            Person groupAdmin = studyGroup.getGroupAdmin();
            String addPersonSql = "INSERT INTO person (person_name, height, eye_color, hair_color, nationality)" +
                    "VALUES (?, ?, ?::color, ?::color, ?::country)";

            PreparedStatement groupAdminStatement = connection.prepareStatement(addPersonSql, Statement.RETURN_GENERATED_KEYS);
            groupAdminStatement.setString(1, groupAdmin.getName());
            groupAdminStatement.setInt(2, groupAdmin.getHeight());
            groupAdminStatement.setString(3, groupAdmin.getEyeColor().name());
            groupAdminStatement.setString(4, groupAdmin.getHairColor().name());
            groupAdminStatement.setString(5, groupAdmin.getNationality().name());
            groupAdminStatement.executeUpdate();

            ResultSet rs = groupAdminStatement.getGeneratedKeys();
            if (rs.next()) {
                String addElementSql = "INSERT INTO studyGroup (studyGroup_name, coordinateX, coordinateY, creationDate, " +
                        "studentsCount, formOfEducation, semester, person_id, user_id)" +
                        " SELECT ?, ?, ?, ?, ?, ?::formOfEducation, ?::semester, ?, id" +
                        " FROM \"user\"" +
                        " WHERE \"user\".login = ?";

                PreparedStatement elementStatement = connection.prepareStatement(addElementSql, Statement.RETURN_GENERATED_KEYS);
                Coordinates coordinates = studyGroup.getCoordinates();
                elementStatement.setString(1, studyGroup.getName());
                elementStatement.setInt(2, coordinates.getX());
                elementStatement.setFloat(3, coordinates.getY());
                elementStatement.setTimestamp(4, Timestamp.valueOf(studyGroup.getCreationDate().toLocalDateTime()));
                elementStatement.setInt(5, studyGroup.getStudentsCount());
                if (studyGroup.getFormOfEducation() == null) elementStatement.setString(6, null);
                else elementStatement.setString(6, studyGroup.getFormOfEducation().name());
                elementStatement.setString(7, studyGroup.getSemesterEnum().name());
                elementStatement.setInt(8, rs.getInt(1));
                elementStatement.setString(9, username);

                elementStatement.executeUpdate();
                ResultSet result = elementStatement.getGeneratedKeys();
                result.next();

                logger.info("В коллекцию добавлен элемент");
                return result.getInt(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        });
    }

    /**
     * Обработка запроса без возврата значения.
     */
    @Override
    public void handleQuery(SqlConsumer<Connection> queryBody) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            queryBody.accept(connection);
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при работе с базой данных: " + e.getMessage());
        }
    }

    /**
     * Обработка запроса с возвратом значения.
     */
    @Override
    public <T> T handleQuery(SqlFunction<Connection, T> queryBody) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            return queryBody.apply(connection);
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при работе с базой данных: " + e.getMessage());
        }
    }

    /**
     * Удаляет элемент из базы данных по id.
     */
    @Override
    public boolean removeById(int id, String username) throws DatabaseException {
        return handleQuery((Connection connection) -> {
            String query =
                    "DELETE from person" +
                            " USING \"user\", studyGroup" +
                            " WHERE person.person_id = studyGroup.person_id AND studyGroup.studyGroup_id = ? " +
                            "AND studyGroup.user_id = \"user\".id AND \"user\".login = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, username);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        });
    }

    /**
     * Обновляет элемент базы данных с указанным id.
     */
    @Override
    public boolean updateById(StudyGroup studyGroup, int id, String username) throws DatabaseException {
        return handleQuery((Connection connection) -> {
            connection.createStatement().execute("BEGIN TRANSACTION;");
            String query =
                    "UPDATE studyGroup" +
                            " SET studyGroup_name = ?," +
                            "coordinateX = ?," +
                            "coordinateY = ?," +
                            "creationDate = ?," +
                            "studentsCount = ?," +
                            "formOfEducation = ?::formOfEducation," +
                            "semester = ?::semester" +
                            " FROM \"user\"" +
                            " WHERE studyGroup.studyGroup_id = ? AND studyGroup.user_id = \"user\".id AND \"user\".login = ?;" +
                            " UPDATE person" +
                            " SET person_name = ?," +
                            "height = ?," +
                            "eye_color = ?::color," +
                            "hair_color = ?::color," +
                            "nationality = ?::country" +
                            " FROM studyGroup, \"user\"" +
                            " WHERE studyGroup.person_id = person.person_id and studyGroup.studyGroup_id = ? AND studyGroup.user_id = \"user\".id AND \"user\".login = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, studyGroup.getName());
            statement.setInt(2, studyGroup.getCoordinates().getX());
            statement.setFloat(3, studyGroup.getCoordinates().getY());
            statement.setTimestamp(4, Timestamp.valueOf(studyGroup.getCreationDate().toLocalDateTime()));
            statement.setInt(5, studyGroup.getStudentsCount());
            if (studyGroup.getFormOfEducation() == null) statement.setString(6, null);
            else statement.setString(6, studyGroup.getFormOfEducation().name());
            statement.setString(7, studyGroup.getSemesterEnum().name());
            statement.setInt(8, id);
            statement.setString(9, username);

            Person groupAdmin = studyGroup.getGroupAdmin();
            statement.setString(10, groupAdmin.getName());
            statement.setInt(11, groupAdmin.getHeight());
            statement.setString(12, groupAdmin.getEyeColor().name());
            statement.setString(13, groupAdmin.getHairColor().name());
            statement.setString(14, groupAdmin.getNationality().name());

            statement.setInt(15, id);

            statement.setString(16, username);

            int result = statement.executeUpdate();

            connection.createStatement().execute("COMMIT;");

            return result > 0; // Если true, значит результат не пустой и записи обновлены
        });
    }

    /**
     * Загружает коллекцию из базы данных.
     */
    @Override
    public void loadCollectionFromDatabase(CollectionManager collectionManager) throws DatabaseException {
        handleQuery((connection -> {
            collectionManager.initList();
            String query = "SELECT * FROM studyGroup" +
                    " INNER JOIN person ON studyGroup.person_id = person.person_id";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            StudyGroup studyGroup;
            while (rs.next()) {
                FormOfEducation formOfEducation = null;
                if (rs.getString("formOfEducation") != null) {
                    formOfEducation = FormOfEducation.valueOf(rs.getString("formOfEducation"));
                }

                studyGroup = new StudyGroup(
                        rs.getString("studyGroup_name"),
                        new Coordinates(
                                rs.getInt("coordinatex"),
                                rs.getFloat("coordinatey")
                        ),
                        rs.getInt("studentsCount"),
                        formOfEducation,
                        Semester.valueOf(rs.getString("semester")),
                        new Person(
                                rs.getString("person_name"),
                                rs.getInt("height"),
                                Color.valueOf(rs.getString("eye_color")),
                                Color.valueOf(rs.getString("hair_color")),
                                Country.valueOf(rs.getString("nationality"))
                        )
                );
                studyGroup.setId(rs.getInt("studyGroup_id"));
                collectionManager.add(studyGroup);
            }
            logger.info("Коллекция загружена из базы данных");
        }));
    }

    /**
     * Получает пароль по имени пользователя.
     */
    @Override
    public String getPassword(String username) throws DatabaseException {
        return this.handleQuery((Connection connection) -> {
            String query = "SELECT (\"password\")" +
                    " FROM \"user\"" +
                    " WHERE \"user\".login = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getString("password");
            }
            return null;
        });
    }

    /**
     * Проверяет, существует ли пользователь.
     */
    @Override
    public boolean doesUserExist(String username) throws DatabaseException {
        return this.<Boolean>handleQuery((Connection connection) -> {
            String query = "SELECT COUNT(*)" +
                    " FROM \"user\"" +
                    " WHERE \"user\".login = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();

            result.next();

            return result.getInt("count") > 0;
        });
    }

    /**
     * Добавляет пользователя в базу данных.
     */
    @Override
    public void addUser(String username, String password) throws DatabaseException {
        handleQuery((Connection connection) -> {
            String query = "INSERT INTO \"user\" (\"login\", \"password\")" +
                    "VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hashEncrypter.encryptString(password + salty));

            statement.executeUpdate();
        });
    }

    /**
     * Удаляет все элементы, владельцем которых является пользователь.
     */
    @Override
    public List<Integer> clear(String username) throws DatabaseException {
        return handleQuery((Connection connection) -> {
            String query = "DELETE FROM person" +
                    " USING studyGroup, \"user\"" +
                    "WHERE person.person_id = studyGroup.person_id AND studyGroup.user_id = \"user\".id AND \"user\".login = ?" +
                    " RETURNING studyGroup.studyGroup_id;";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();

            ArrayList<Integer> ids = new ArrayList<>();

            while (result.next()) {
                ids.add(result.getInt("studyGroup_id"));
            }
            return ids;
        });
    }

    @Override
    public List<Integer> getIdOfUserElements(String username) throws DatabaseException {
        return handleQuery((Connection connection) -> {
            String query = "SELECT studyGroup_id FROM studyGroup, \"user\"" +
                    " WHERE studyGroup.user_id = \"user\".id AND \"user\".login = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();

            ArrayList<Integer> ids = new ArrayList<>();

            while (result.next()) {
                ids.add(result.getInt("studyGroup_id"));
            }
            return ids;
        });
    }


    @Override
    public List<List<Integer>> getIdElementsAllUsers() throws DatabaseException {
        return handleQuery((Connection connection) -> {
            String query = "SELECT login FROM \"user\"";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            ArrayList<List<Integer>> res = new ArrayList<>();

            while (result.next()) {
                res.add(getIdOfUserElements(result.getString("login")));
            }
            return res;
        });
    }

    @Override
    public boolean validateUserData(String login, String password) throws DatabaseException {
        String realPassword = getPassword(login);

        return hashEncrypter.encryptString(password + salty).equals(realPassword);
    }
}
