import bgu.spl.mics.Future;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


/**
 * This is a Unit Test for the {@link Future} public class.
 *
 * @author Joshua Shay Kricheli
 *
 */

@SuppressWarnings("unchecked") // suppress unchecked assignment warnings

public class FutureTest {

    /**
     * OUT (Object Under Test)
     */
    private Future testFutureInstance;

    /**
     * Set up for a test
     */
    @Before
    public void setUp() {

        testFutureInstance = new Future<Integer>(); // instantiate a new Future test instance with an Integer object
    }

    /**
     * Test method for {@link Future#get()}:
     * Retrieves the result the Future object holds if it has been resolved.
     * returns the result of type T if it is available, if not waits until it is available.
     */
    @Test
    public void get() {

        Assert.assertNotNull("The future instance is null", testFutureInstance);
        // check that the instance is not null

        // the test for this function is in the next section
    }

    /**
     * Test method for {@link Future#resolve(Object)}:
     * Resolves the result of this Future object
     */
    @Test
    public void resolve() {

        testFutureInstance.resolve(19);
        Assert.assertEquals ("The Integer result is not correct",
                testFutureInstance.get(), 19);

        testFutureInstance = new Future<String>();
        testFutureInstance.resolve("The future is coming on");
        Assert.assertEquals ("The string result is not correct",
                testFutureInstance.get(), "The future is coming on");

    }

    /**
     * Test method for {@link Future#isDone()}:
     * returns true if this object has been resolved, false otherwise
     */
    @Test
    public void isDone() {

        Assert.assertFalse ("The future should be not done",
                testFutureInstance.isDone());

    }

    /**
     * Test method for {@link Future#get(long, TimeUnit)}:
     * Resolves the result of this Future object
     */
    @Test
    public void get1() {

        testFutureInstance.resolve(19);
        Assert.assertNull("The string result is not correct",
                testFutureInstance.get(1, TimeUnit.MINUTES));
        
    }
}