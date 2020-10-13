package Commands;

import java.io.Serializable;

public class SerializedAuthOrReg implements Serializable {
    private String login;
    private String password;
    private String type;

    SerializedAuthOrReg(String login, String password, String type) {
        this.login = login;
        this.password = password;
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public String getType() {
        return type;
    }
}
