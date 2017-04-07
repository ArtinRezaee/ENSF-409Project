import backEnd.UserInfo;
import org.junit.Test;
import org.junit.Assert;

/**
 * Created by crist on 2017-04-06.
 */
public class UserInfoTest {

    private UserInfo userInfo = new UserInfo("first","last", "email","password","type");
    @Test
    public void getFirst() throws Exception {
        Assert.assertEquals("first", userInfo.getFirst());
    }

    @Test
    public void getLast() throws Exception {
        Assert.assertEquals("last",userInfo.getLast());
    }

    @Test
    public void getMail() throws Exception {
        Assert.assertEquals("email",userInfo.getMail());
    }

    @Test
    public void getPass() throws Exception {
        Assert.assertEquals("password", userInfo.getPass());
    }

    @Test
    public void getType() throws Exception {
        Assert.assertEquals("type", userInfo.getType());
    }

}
