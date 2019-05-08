package game.gui;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ScoreboardTest {
    @Test
    public void testSendingScore() throws IOException {
        String addUrl = "http://dreamlo.com/lb/eAVMHLIhu0esxJWTsbeXzQRUQwgGsID0yZo7Vi8UhL3w/add/test_name/99999";
        URL post = new URL(addUrl);
        URLConnection yc = post.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
        }

        in.close();
    }

    @Test
    public void testRetrievingScore() throws IOException {
        URL get = new URL("http://dreamlo.com/lb/5ad43e72d6024519e0c8051c/quote/10");
        URLConnection yc = get.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String[] firstLine = in.readLine().split(",");
        Assert.assertTrue(firstLine[0].substring(1, firstLine[0].length()-1).equals("test_name"));
        Assert.assertTrue(firstLine[1].substring(1, firstLine[1].length()-1).equals("99999"));
        in.close();
    }
}
