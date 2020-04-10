package de.delphi.phi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Polyfill {

    @FunctionalInterface
    public interface ThrowingFunction {
        void run() throws Throwable;
    }

    public static <T extends Throwable> void assertThrows(String message, Class<T> expectedType, ThrowingFunction code){
        boolean throwsFailed = false;
        try {
            code.run();
            throwsFailed = true;
        }
        catch(Throwable e){
            assertEquals(expectedType, e.getClass());
        }
        if(throwsFailed)
            fail(message);
    }
}
