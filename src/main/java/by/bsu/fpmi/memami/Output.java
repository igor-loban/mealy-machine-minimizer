package by.bsu.fpmi.memami;

import java.io.Serializable;

public class Output implements Serializable {
    private String value;

    public Output(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
