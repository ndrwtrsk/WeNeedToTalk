package ndrwtrsk.weneedtotalk;

import java.util.List;

/**
 * Created by Andrew on 20.06.2016.
 */
public class Chat {
    public String title;
    public List<String> users;

    public Chat(){}

    public Chat(String title, List<String> users) {
        this.title = title;
        this.users = users;
    }
}
