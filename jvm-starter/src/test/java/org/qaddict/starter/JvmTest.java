package org.qaddict.starter;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class JvmTest {

    @Test
    public void testSimpleClassPath() throws URISyntaxException, IOException, InterruptedException {
        Assert.assertEquals(Jvm.ofCurrent().classPathOf(Paths.get(getClass().getResource("/").toURI()).toString()).mainClass(TestMainClass.class.getCanonicalName()).startAndWaitFor(), 0);
    }

}