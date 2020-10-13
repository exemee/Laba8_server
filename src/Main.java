import Interfaces.Controller;
import ServerSocket.ServerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ServerModule());
        Controller controller = injector.getInstance(Controller.class);
        try {
            controller.run(args[0]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Введено некорректное количество аргументов.\n" +
                    "Требуются 1 аргумент: порт");
        }
    }
}
