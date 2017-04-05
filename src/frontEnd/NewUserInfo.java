package frontEnd;
// Created by satyaki on Apr 3 2017.
import java.io.Serializable;

public class NewUserInfo implements Serializable
{
    private String first;
    private String last;
    private String email;
    private String password;
    private String type;

    static final long serialVersionUID = 11111110;

    public NewUserInfo(String a, String b, String c, String d, String e)
    {
        first = a;
        last = b;
        email = c;
        password = d;
        type = e;
    }
    
    public String getFirst(){ return first; }
    public String getLast(){ return last; }
    public String getMail(){ return email; }
    public String getPass(){ return password; }
    public String getType(){ return type; }
}
