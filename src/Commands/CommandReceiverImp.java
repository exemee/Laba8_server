package Commands;

import BasicClasses.Person;
import BasicClasses.StudyGroup;
import Commands.SerializedCommands.*;
import Exceptions.DatabaseException;
import Interfaces.*;
import Utils.ObjectSizeFetcher;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Ресивер(получатель), отправляет серилизованные объекты на сервер.
 */
@Singleton
public class CommandReceiverImp implements CommandReceiver {
    private static final Logger logger = LoggerFactory.getLogger(CommandReceiverImp.class);
    private final CollectionManager collectionManager;
    private final CollectionUtils collectionUtils;
    private final DatabaseManager databaseManager;
    private final Validator validator;
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(2);
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    @Inject
    public CommandReceiverImp(CollectionManager collectionManager, CollectionUtils collectionUtils, DatabaseManager databaseManager, Validator validator) {
        this.collectionManager = collectionManager;
        this.collectionUtils = collectionUtils;
        this.databaseManager = databaseManager;
        this.validator = validator;
    }

    @Override
    public void sendObject(Socket socket, SerializedMessage serializedMessage) throws IOException, DatabaseException {
        executor.submit(() -> {
            try {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(serializedMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean checkUser(String login, String password, Socket socket) throws DatabaseException, IOException {
        boolean exist = databaseManager.validateUserData(login, password);

        if (exist) {
            logger.info(String.format("Пользователь %s, живущий по адресу %s:%s - прошел проверку на реального ИТМОшника", login, socket.getInetAddress(), socket.getPort()));
            return true;
        } else {
            logger.info(String.format("Товарищ %s:%s ошибся дверью, клуб кожевного ремесла два блока вниз.", socket.getInetAddress(), socket.getPort()));
        }

        return false;
    }

    @Override
    public void tryAuth(String login, String password, Socket socket) throws DatabaseException, IOException {
        boolean res = checkUser(login, password, socket);
        executor.submit(() -> {
            try {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(new SerializedResAuth(res, "auth"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void info(SerializedCommand command, Socket socket) throws IOException, DatabaseException {
        if (checkUser(command.getLogin(), command.getPassword(), socket)) {
            sendObject(socket, new SerializedMessage(collectionManager.getInfo()));

            logger.info(String.format("Клиенту %s:%s отправлен результат работы команды INFO", socket.getInetAddress(), socket.getPort()));
        }
    }

    @Override
    public void show(SerializedCommand command, Socket socket) throws IOException, DatabaseException {
        if (checkUser(command.getLogin(), command.getPassword(), socket)) {
            sendObject(socket, new SerializedMessage(collectionManager.show()));

            logger.info(String.format("Клиенту %s:%s отправлен результат работы команды SHOW", socket.getInetAddress(), socket.getPort()));
        }
    }

    @Override
    public void add(SerializedObjectCommand command, Socket socket) throws IOException, DatabaseException {
        if (checkUser(command.getLogin(), command.getPassword(), socket)) {
            try {
                StudyGroup studyGroup = (StudyGroup) command.getObject();
                studyGroup.setId(databaseManager.addElement(studyGroup, command.getLogin()));
                collectionManager.add(studyGroup);

                sendObject(socket, new SerializedMessage("элемент добавлен"));
            } catch (Exception e) {
                sendObject(socket, new SerializedMessage("элемент не добавлен"));
                e.printStackTrace();
            }

            logger.info(String.format("Клиенту %s:%s отправлен результат работы команды ADD", socket.getInetAddress(), socket.getPort()));
        }
    }

    @Override
    public void update(SerializedCombinedCommand command, Socket socket) throws IOException, DatabaseException {
        if (checkUser(command.getLogin(), command.getPassword(), socket)) {
            Integer groupId;
            try {
                groupId = Integer.parseInt(command.getArg());
                if (collectionUtils.checkExist(groupId)) {
                    try {
                        StudyGroup studyGroup = (StudyGroup) command.getObject();
                        if (databaseManager.updateById(studyGroup, groupId, command.getLogin())) {
                            databaseManager.updateById(studyGroup, groupId, command.getLogin());
                            collectionManager.update(studyGroup, groupId);

                            sendObject(socket, new SerializedMessage(String.format("элемент обновлен", command.getLogin())));
                        } else sendObject(socket, new SerializedMessage("элемент создан другим пользователем"));
                    } catch (Exception e){
                        e.printStackTrace();

                        sendObject(socket, new SerializedMessage("элемент не обновлен"));
                    }
                } else {
                    sendObject(socket, new SerializedMessage("элемента с таким ID нет в коллекции"));
                }
            } catch (NumberFormatException e) {
                sendObject(socket, new SerializedMessage("некорректный аргумент"));
            }

            logger.info(String.format("Клиенту %s:%s отправлен результат работы команды UPDATE", socket.getInetAddress(), socket.getPort()));
        }
    }

    @Override
    public void removeById(SerializedArgumentCommand command, Socket socket) throws IOException, DatabaseException {
        if (checkUser(command.getLogin(), command.getPassword(), socket)) {
            Integer groupId;
            try {
                groupId = Integer.parseInt(command.getArg());
                if (collectionUtils.checkExist(groupId)) {
                    if (databaseManager.removeById(groupId, command.getLogin())) {
                        collectionManager.removeById(groupId);
                        sendObject(socket, new SerializedMessage(String.format("элемент удален", command.getLogin(), groupId)));
                    } else sendObject(socket, new SerializedMessage("элемент создан другим пользователем"));
                } else {
                    sendObject(socket, new SerializedMessage("элемента с таким ID нет в коллекции"));
                }
            } catch (NumberFormatException e) {
                sendObject(socket, new SerializedMessage("некорректный аргумент"));
            }

            logger.info(String.format("Клиенту %s:%s отправлен результат работы команды REMOVE_BY_ID", socket.getInetAddress(), socket.getPort()));
        }
    }

    @Override
    public void clear(SerializedCommand command, Socket socket) throws IOException, DatabaseException {
        if (checkUser(command.getLogin(), command.getPassword(), socket)) {
            List<Integer> deleteID = databaseManager.clear(command.getLogin());
            deleteID.forEach(collectionManager::removeById);

            sendObject(socket, new SerializedMessage("ваши элементы коллекции удалены"));
            logger.info(String.format("Клиенту %s:%s отправлен результат работы команды CLEAR", socket.getInetAddress(), socket.getPort()));
        }
    }

    @Override
    public void head(SerializedCommand command, Socket socket) throws IOException, DatabaseException {
        if (checkUser(command.getLogin(), command.getPassword(), socket)) {
            sendObject(socket, new SerializedMessage(collectionManager.head()));
            logger.info(String.format("Клиенту %s:%s отправлен результат работы команды HEAD", socket.getInetAddress(), socket.getPort()));
        }
    }

    @Override
    public void removeGreater(SerializedObjectCommand command, Socket socket) throws IOException, DatabaseException {
        forkJoinPool.submit(() -> {
            try {
                if (checkUser(command.getLogin(), command.getPassword(), socket)) {
                    StudyGroup studyGroup = (StudyGroup) command.getObject();
                    if (validator.validateStudyGroup(studyGroup)) {
                        List<Integer> ids = collectionManager.removeGreater(studyGroup, databaseManager.getIdOfUserElements(command.getLogin()));
                        if (ids.isEmpty()) sendObject(socket, new SerializedMessage("таких элементов не найдено"));
                        else sendObject(socket, new SerializedMessage("removeElements " +
                                ids.toString().replaceAll("[\\[\\]]", "")));

                        ids.forEach(id -> {
                            try {
                                databaseManager.removeById(id, command.getLogin());
                            } catch (DatabaseException e) {
                                try {
                                    sendObject(socket, new SerializedMessage("ошибка при удалении из бд элемента с id="+ id + "\n" + e));
                                } catch (IOException | DatabaseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    } else {
                        sendObject(socket, new SerializedMessage("элемент не прошел валидацию на стороне сервера"));
                    }

                    logger.info(String.format("Клиенту %s:%s отправлен результат работы команды REMOVE_GREATER", socket.getInetAddress(), socket.getPort()));
                }
            } catch (DatabaseException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void removeLower(SerializedObjectCommand command, Socket socket) throws IOException, DatabaseException {
        forkJoinPool.submit(() -> {
            try {
                if (checkUser(command.getLogin(), command.getPassword(), socket)) {
                    StudyGroup studyGroup = (StudyGroup) command.getObject();
                    if (validator.validateStudyGroup(studyGroup)) {
                        List<Integer> ids = collectionManager.removeLower(studyGroup, databaseManager.getIdOfUserElements(command.getLogin()));
                        if (ids.isEmpty()) sendObject(socket, new SerializedMessage("таких элементов не найдено"));
                        else sendObject(socket, new SerializedMessage("removeElements " +
                                ids.toString().replaceAll("[\\[\\]]", "")));

                        ids.forEach(id -> {
                            try {
                                databaseManager.removeById(id, command.getLogin());
                            } catch (DatabaseException e) {
                                try {
                                    sendObject(socket, new SerializedMessage("ошибка при удалении из бд элемента с id=" + id + "\n" + e));
                                } catch (IOException | DatabaseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    } else {
                        sendObject(socket, new SerializedMessage("элемент не прошел валидацию на стороне сервера"));
                    }

                    logger.info(String.format("Клиенту %s:%s отправлен результат работы команды REMOVE_LOWER", socket.getInetAddress(), socket.getPort()));
                }
            } catch (DatabaseException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void minBySemesterEnum(SerializedCommand command, Socket socket) throws IOException, DatabaseException {
        forkJoinPool.submit(() -> {
            try {
                if (checkUser(command.getLogin(), command.getPassword(), socket)) {
                    sendObject(socket, new SerializedMessage(collectionManager.minBySemesterEnum()));
                    logger.info(String.format("Клиенту %s:%s отправлен результат работы команды MIN_BY_SEMESTER_ENUM", socket.getInetAddress(), socket.getPort()));
                }
            } catch (DatabaseException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void maxByGroupAdmin(SerializedCommand command, Socket socket) throws IOException, DatabaseException {
        forkJoinPool.submit(() -> {
            try {
                if (checkUser(command.getLogin(), command.getPassword(), socket)) {
                    sendObject(socket, new SerializedMessage(collectionManager.maxByGroupAdmin()));
                    logger.info(String.format("Клиенту %s:%s отправлен результат работы команды MAX_BY_GROUP_ADMIN", socket.getInetAddress(), socket.getPort()));
                }
            } catch (DatabaseException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void countByGroupAdmin(SerializedObjectCommand command, Socket socket) throws IOException, DatabaseException {
        forkJoinPool.submit(() -> {
            try {
                if (checkUser(command.getLogin(), command.getPassword(), socket)) {
                    Person groupAdmin = (Person) command.getObject();
                    if (validator.validatePerson(groupAdmin)) {
                        sendObject(socket, new SerializedMessage(collectionManager.countByGroupAdmin(groupAdmin)));
                    } else {
                        sendObject(socket, new SerializedMessage("элемент не прошел валидацию на стороне сервера"));
                    }

                    logger.info(String.format("Клиенту %s:%s отправлен результат работы команды COUNT_BY_GROUP_ADMIN", socket.getInetAddress(), socket.getPort()));
                }
            } catch (DatabaseException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void register(String login, String password, Socket socket) throws IOException, DatabaseException {
        boolean res = databaseManager.doesUserExist(login);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        if (!res) {
            databaseManager.addUser(login, password);
            out.writeObject(new SerializedResAuth(true, "reg"));
            logger.info(String.format("Пользователь %s успешно зарегистрирован!", login));
        } else { out.writeObject(new SerializedResAuth(false, "reg")); }
        logger.info(String.format("Клиенту %s:%s отправлен результат попытки регистрации", socket.getInetAddress(), socket.getPort()));
    }

    @Override
    public void sendCollection(Socket socket, String requireType) throws IOException, DatabaseException {
        SerializedCollection serializedCollection = null;
        if (requireType.equals("init")) serializedCollection = new SerializedCollection(Lists.newLinkedList(collectionManager.getLinkedList()), databaseManager.getIdElementsAllUsers(), "init");
        else serializedCollection = new SerializedCollection(Lists.newLinkedList(collectionManager.getLinkedList()), databaseManager.getIdElementsAllUsers(), "regular");
        //long size = ObjectSizeFetcher.getObjectSize(serializedCollection);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(serializedCollection);
        logger.info(String.format("Пользователю %s отправлена коллекция!", socket.getInetAddress()));
    }
}
