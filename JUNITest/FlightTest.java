import backEnd.Flight;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Chris on 2017-04-06.
 */
public class FlightTest {
    private Flight flightTest = new Flight(109230,"Calgary","Edmonton","2017-04-07",
            "03:45","01:00",45,40,200.59);

    @Test
    public void getNum() throws Exception {
        Assert.assertEquals(109230, flightTest.getNum());
    }

    @Test
    public void getSrc() throws Exception {
        Assert.assertEquals("Calgary", flightTest.getSrc());
    }

    @Test
    public void getDest() throws Exception {
        Assert.assertEquals("Edmonton", flightTest.getDest());
    }

    @Test
    public void getDate() throws Exception {
        Assert.assertEquals("2017-04-07", flightTest.getDate());
    }

    @Test
    public void getTime() throws Exception {
        Assert.assertEquals("03:45", flightTest.getTime());
    }

    @Test
    public void getDur() throws Exception {
        Assert.assertEquals("01:00", flightTest.getDur());
    }

    @Test
    public void getTotalSeats() throws Exception {
        Assert.assertEquals(45, flightTest.getTotalSeats());
    }

    @Test
    public void getAvailSeats() throws Exception {
        Assert.assertEquals(40,flightTest.getAvailSeats());
    }

    @Test
    public void getPrice() throws Exception {
        Assert.assertEquals(200.59, flightTest.getPrice(),0.001);
    }

}
