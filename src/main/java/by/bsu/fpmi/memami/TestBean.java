package by.bsu.fpmi.memami;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class TestBean {
    public String getText() {
        return "Hello, world!";
    }
}
