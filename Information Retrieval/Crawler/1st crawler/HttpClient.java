/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author AltuC
 */
public class HttpClient {

    public int port = 80;
    Map map = new HashMap();
    Queue<String> q = new LinkedList<String>();
    private String currentSessionId = "";

    public void get(String strPage, String strServer, String name, String pwd, String[] csrftoken, String sessionid) throws IOException {
        try {
            InetAddress addr = InetAddress.getByName(strServer);
            Socket s = new Socket(addr, port);
            s.setSoTimeout(30000);
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
            wr.write("GET " + strPage + " HTTP/1.1\r\n");
            wr.write("HOST: " + strServer + "\r\n");
            wr.write("Accept: */*\r\n");
            wr.write("Connection: Keep-Alive\r\n");
            wr.write("Cookie: " + csrftoken[0] + "\r\n");
            wr.write("Cookie: " + sessionid + "\r\n");
            wr.write("\r\n");
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.startsWith("HTTP/1.1 302") || line.startsWith("HTTP/1.1 301")) {
                    while ((line = rd.readLine()) != null) {
                        int start = line.indexOf("Location");
                        if (start == 0) {
                            String temp = line.substring(start, line.length() - start);
                            String[] strArray = temp.split("/", 4);
                            strPage = "/" + strArray[3];
                            if (strPage.equals("/accounts/login/") || strPage.equals("/accounts/login/?next=/fakebook/")) {
                                post(strPage, strServer, name, pwd, csrftoken, sessionid);  //302: HTTP redirect
                            } else {
                                get(strPage, strServer, name, pwd, csrftoken, currentSessionId);  //302: HTTP redirect
                            }
                        }
                    }
                }
                if (line.startsWith("HTTP/1.1 404")) {
                }
                if (line.startsWith("HTTP/1.1 501")) {
                    get(strPage, strServer, name, pwd, csrftoken, currentSessionId);
                } else {
                    Pattern p = Pattern.compile("<a\\s.*?href=\"/f([^\"]+)\"[^>]*>");
                    Matcher m = p.matcher(line);
                    while (m.find()) {           //search URL
                        String[] strArray = m.group().split("\"");
                        String str = strArray[1];
                        if (!map.containsKey(str)) {
                            map.put(str, true);
                            q.add(str);
                            System.out.println(str);
                        }
                    }
                }
            }
            wr.close();
            rd.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void post(String strPage, String strServer, String name, String pwd, String[] csrftoken, String sessionid) throws IOException {
        try {
            InetAddress addr = InetAddress.getByName(strServer);
            Socket s = new Socket(addr, port);
            s.setSoTimeout(30000);
            String postStr = "csrfmiddlewaretoken=" + csrftoken[1] + "&username=" + name + "&password=" + pwd;
            int postStrLen = postStr.length();
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF8"));
            wr.write("POST " + strPage + " HTTP/1.1\r\n");
            wr.write("HOST: " + strServer + "\r\n");
            wr.write("Accept: */*\r\n");
            wr.write("Connection: Keep-Alive\r\n");
            wr.write("Content-Length: " + postStrLen + "\r\n");
            wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
            wr.write("Cookie: " + csrftoken[0] + "\r\n");
            wr.write("\r\n");
            wr.write(postStr);
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.startsWith("HTTP/1.1 403")) {
                    csrftoken = gettoken(strPage, strServer, name, pwd, csrftoken);
                    post(strPage, strServer, name, pwd, csrftoken, sessionid);  //403 Forbidden
                } else if (line.startsWith("HTTP/1.1 302")) {
                    while ((line = rd.readLine()) != null) {
                        int start1 = line.indexOf("Location");
                        if (start1 == 0) {
                            String temp1 = line.substring(start1, line.length() - start1);
                            String[] strArray1 = temp1.split("/", 4);
                            strPage = "/" + strArray1[3];
                        }
                        int start2 = line.indexOf("Set-Cookie: s");
                        if (start2 == 0) {
                            String temp2 = line.substring(start2, line.length() - start2);
                            String[] strArray2 = temp2.split(";");
                            String temp3 = strArray2[0];
                            sessionid = temp3.substring(12);
                            currentSessionId = sessionid;
                        }
                    }
                    get(strPage, strServer, name, pwd, csrftoken, currentSessionId);  //302: HTTP redirect 
                }
            }
            wr.close();
            rd.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] gettoken(String strPage, String strServer, String name, String pwd, String[] csrftoken) throws IOException {
        try {
            InetAddress addr = InetAddress.getByName(strServer);
            Socket s = new Socket(addr, port);
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
            wr.write("GET " + strPage + " HTTP/1.1\r\n");
            wr.write("HOST: " + strServer + "\r\n");
            wr.write("Accept: */*\r\n");
            wr.write("Connection: Keep-Alive\r\n");
            wr.write("\r\n");
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            int i = 0;
            while ((line = rd.readLine()) != null) {
                int start = line.indexOf("Set-Cookie: c");
                if (start == 0) {
                    String temp = line.substring(start, line.length() - start);
                    String[] strArray = temp.split(";");
                    String temp1 = strArray[0];
                    csrftoken[i] = temp1.substring(12);
                    i += 1;
                }
                Pattern p = Pattern.compile("csrfmiddlewaretoken\'\\s+value=\'[^\']+");
                Matcher m = p.matcher(line);
                if (m.find()) {
                    String[] strArray = m.group().split("\'");
                    csrftoken[i] = strArray[2];
                }
            }
            wr.close();
            rd.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return csrftoken;
    }

    public void broadTraverse(String strPage, String strServer, String name, String pwd, String[] csrftoken, String sessionid) throws IOException {  //BFS
        q.add(strPage);
        while (!q.isEmpty()) {
            get(q.remove(), strServer, name, pwd, csrftoken, currentSessionId);
        }
        System.out.println(map.size());
    }
}
