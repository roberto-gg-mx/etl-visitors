//import java.util.Properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

//import com.vinkos.visitas.io.Configuration;
 
public class ConfigurationTest {
    //private static Properties cnf;
 
    @BeforeClass
    public static void initcnf() {
    	//cnf = Configuration.loadParams("default.properties");
    }
 
    @Before
    public void beforeEachTest() {
        System.out.println("This is executed before each Test");
    }
 
    @After
    public void afterEachTest() {
        System.out.println("This is exceuted after each Test");
    }
 
    @Test
    public void testSum() {
        Object result = Integer.parseInt("7");
 
    	assertEquals(7, result);
    }
 
    @Test
    public void testDivison() {
        try {
            int result = Integer.parseInt("5");
 
            assertEquals(5, result);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
 
    @Test(expected = Exception.class)
    public void testDivisionException() throws Exception {
    	int result = Integer.parseInt("4aasfd");
    	System.out.println(result);
    }
 
    @Ignore
    @Test
    public void testEqual() {
        boolean result = Boolean.parseBoolean("true");
 
        assertFalse(result);
    }
 
    @Ignore
    @Test
    public void testSubstraction() {
        int result = 10 - 1;
 
        assertTrue(result == 9);
    }
}
