import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

import controllers.API;
import org.junit.Test;

import play.Logger;
import play.mvc.Result;

public class ControllerTest {

    @Test
    public void testIndex() {
        Result result = new API().index();
        assertEquals(OK, result.status());
        assertEquals("application/json", result.contentType().get());
        assertTrue(contentAsString(result).contains("ok"));
    }

}