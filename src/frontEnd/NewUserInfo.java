package frontEnd;
// Created by satyaki on Apr 3 2017.
import java.io.Serializable;

public class NewUserInfo implements Serializable
{
    public String first;
    public String last;
    public String email;
    public String password;
    public String type;

    static final long serialVersionUID = 1;

    public NewUserInfo(String a, String b, String c, String d, String e)
    {
        first = a;
        last = b;
        email = c;
        password = d;
        type = e;
    }
}
