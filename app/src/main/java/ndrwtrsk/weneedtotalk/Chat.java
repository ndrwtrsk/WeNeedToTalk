package ndrwtrsk.weneedtotalk;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by Andrew on 20.06.2016.
 */
@IgnoreExtraProperties
public class Chat {

    @Exclude
    public String key;
    public String title;
    public List<String> users;

    public Chat(){}

    public Chat(String title, List<String> users) {
        this.title = title;
        this.users = users;
    }
}
