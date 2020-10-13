package Commands;

import java.io.Serializable;

public class SerializedResAuth implements Serializable {
    private boolean res;
    private String type;

    SerializedResAuth(boolean res, String type) {
        this.res = res;
        this.type = type;
    }

    public boolean getRes() {
        return res;
    }

    public String getType() {
        return type;
    }
}
