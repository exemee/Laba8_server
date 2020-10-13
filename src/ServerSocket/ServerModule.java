package ServerSocket;

import Collection.CollectionManagerImp;
import Collection.CollectionUtilsImp;
import Commands.CommandReceiverImp;
import Utils.CommandHandler.DecryptingImp;
import Utils.Database.DatabaseManagerImp;
import Utils.HashEncrypterImp;
import Utils.ValidatorImp;
import com.google.inject.AbstractModule;
import Interfaces.*;

/**
 * Серверный модуль для Guice Dependency Injection.
 */
public class ServerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Controller.class).to(ControllerImp.class);
        bind(CollectionManager.class).to(CollectionManagerImp.class);
        bind(CollectionUtils.class).to(CollectionUtilsImp.class);
        bind(DatabaseManager.class).to(DatabaseManagerImp.class);
        bind(Validator.class).to(ValidatorImp.class);
        bind(HashEncrypter.class).to(HashEncrypterImp.class);
        bind(CommandReceiver.class).to(CommandReceiverImp.class);
        bind(Decrypting.class).to(DecryptingImp.class);
    }
}