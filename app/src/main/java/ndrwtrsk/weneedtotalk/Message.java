package ndrwtrsk.weneedtotalk;

/**
 * Created by Andrew on 20.06.2016.
 */
public class Message {
    public String userId;
    public String content;

    public Message() {
        super();
    }

    public Message(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }
}
