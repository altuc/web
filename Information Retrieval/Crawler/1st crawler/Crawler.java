/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Scanner;
import java.io.IOException;

/**
 *
 * @author AltuC
 */
public class Crawler {

    public static void main(String[] args) throws IOException {
        try {
            HttpClient c = new HttpClient();
            Scanner scanner = new Scanner(System.in);
            String[] form = scanner.nextLine().split(" ");
            String name = form[0];
            String pwd = form[1];
            String Fakebook = "cs5700f12.ccs.neu.edu";
            String strPage = "/fakebook/";
            String[] csrftoken = new String[2];
            String sessionid = "";
            c.broadTraverse(strPage, Fakebook, name, pwd, csrftoken, sessionid);
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}
