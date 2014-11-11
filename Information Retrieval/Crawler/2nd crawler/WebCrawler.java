package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

	private String domainUrl = "http://en.wikipedia.org/wiki";
	private String mainPageUrl = "http://en.wikipedia.org/wiki/Main_Page";
	Queue<String> q = new LinkedList<String>();
	HashMap<String, Integer> m = new HashMap<String, Integer>();

	/**
	 * 
	 * @param url
	 *            crawling url
	 * @throws IOException
	 */
	public void get(String url) throws IOException {
		// set the connection
		Connection conn = Jsoup.connect(url);
		// set the connection timeout
		conn.timeout(20000);
		// get the document from the connection
		Document doc = conn.get();
		// print the crawling link
		System.out.println(url);
		// get all the links from the page
		Elements hrefs = doc.select("a[href]");
		for (Element e : hrefs) {
			// not follow links to non-English articles or to non-Wikipedia
			// pages
			if (e.attr("abs:href").contains(domainUrl)
			// not follow links with a colon (:) in the rest of the URL
					&& !e.attr("abs:href").substring(28).contains(":")
					// not follow links to the main page
					&& !e.attr("abs:href").equals(mainPageUrl)
					// not follow links with (#) mark
					&& !e.attr("abs:href").contains("#")
					// not follow links already crawled and only follow links at
					// most depth 3
					&& !m.containsKey(e.attr("abs:href")) && m.get(url) < 3) {
				Connection connection = Jsoup.connect(e.attr("abs:href"));
				connection.timeout(20000);
				Document document = connection.get();
				// get the canonical link from the page source
				Elements redirectUrl = document.select("link[rel=canonical]");
				/*
				 * check if the canonical link is different from original link
				 * and if the canonical link is already in the hashmap
				 */
				if (!redirectUrl.attr("abs:href").equals(e.attr("abs:href"))
						&& !m.containsKey(redirectUrl.attr("abs:href"))) {
					m.put(redirectUrl.attr("abs:href"), m.get(url) + 1);
					q.add(redirectUrl.attr("abs:href"));
				}
				if (redirectUrl.attr("abs:href").equals(e.attr("abs:href"))) {
					m.put(e.attr("abs:href"), m.get(url) + 1);
					q.add(e.attr("abs:href"));
				}
			}
		}
	}

	/**
	 * 
	 * @param seedUrl
	 *            starting url
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void crawl(String seedUrl) throws IOException, InterruptedException {
		q.add(seedUrl);
		m.put(seedUrl, 1);
		while (!q.isEmpty()) {
			get(q.remove());
			// delay one second between requests
			// Thread.sleep(1000);
		}
		// print the total links crawled
		System.out.println("Total links: " + m.size());
	}

}
