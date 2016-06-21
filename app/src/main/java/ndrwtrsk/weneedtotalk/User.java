package ndrwtrsk.weneedtotalk;

import com.google.firebase.database.Exclude;

import java.util.List;

/**
 * Created by Andrew on 21.06.2016.
 */
public class User {
    @Exclude
    public String key;
    public String name;
    public List<String> chats;
}
