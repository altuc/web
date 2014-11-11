package main;

import java.io.IOException;

public class Go {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		if (args.length != 2 && args.length != 1) {
			System.err.println("Usage: <seedUrl> <keyphrase>");
			System.exit(0);
		}
		/*
		 * Crawl with keyphrase
		 */
		if (args.length == 2) {
			WebCrawlerWithKeyphrase wcwk = new WebCrawlerWithKeyphrase();
			wcwk.crawl(args[0], args[1].toLowerCase());
		}
		/*
		 * Crawl without keyphrase
		 */
		if (args.length == 1) {
			WebCrawler wc = new WebCrawler();
			wc.crawl(args[0]);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Running Time: " + (endTime - startTime) / 1000
				+ "s");
	}

}
