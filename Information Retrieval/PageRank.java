package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.lang.Math;

public class PageRank {

	HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
	HashMap<String, Integer> outLinkMap = new HashMap<String, Integer>();
	static HashMap<String, Double> pageRankMap = new HashMap<String, Double>();
	HashMap<String, Boolean> sinkNodeMap = new HashMap<String, Boolean>();
	HashMap<String, Integer> inLinkMap = new HashMap<String, Integer>();
	double d = 0.85;

	private void readFile(String fileDir) throws IOException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(fileDir));
			String inLine = null;
			while ((inLine = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(inLine);
				String node = st.nextToken();
				ArrayList<String> inLinks = new ArrayList<String>();
				while (st.hasMoreTokens()) {
					String linkNode = st.nextToken();
					if (!inLinks.contains(linkNode)) {
						inLinks.add(linkNode);
					}
				}
				for (String o : inLinks) {
					if (outLinkMap.containsKey(o)) {
						outLinkMap.put(o, outLinkMap.get(o) + 1);
					} else {
						outLinkMap.put(o, 1);
					}
				}
				map.put(node, inLinks);
				inLinkMap.put(node, inLinks.size());
			}
			for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
				if (!outLinkMap.containsKey(entry.getKey())) {
					sinkNodeMap.put(entry.getKey(), true);
				}
			}
			in.close();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void initializePageRank() {
		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			double pr = 1.0 / map.size();
			pageRankMap.put(entry.getKey(), pr);
		}
	}

	private void updatePageRank() {
		double sinkPR = 0.0;
		for (Map.Entry<String, Boolean> entry : sinkNodeMap.entrySet()) {
			sinkPR += pageRankMap.get(entry.getKey());
		}
		HashMap<String, Double> tempMap = new HashMap<String, Double>();
		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			double newPR = (1.0 - d) / map.size();
			newPR += d * sinkPR / map.size();
			for (String p : entry.getValue()) {
				newPR += d * pageRankMap.get(p) / outLinkMap.get(p);
			}
			tempMap.put(entry.getKey(), newPR);
		}
		for (Map.Entry<String, Double> entry : tempMap.entrySet()) {
			pageRankMap.put(entry.getKey(), entry.getValue());
		}
	}

	private double calculatePerplexity() {
		double sum = 0.0;
		for (Map.Entry<String, Double> entry : pageRankMap.entrySet()) {
			sum += entry.getValue() * Math.log(entry.getValue()) / Math.log(2);
		}
		double perplexity = Math.pow(2, -sum);
		return perplexity;
	}

	private void sortByPageRank() {
		List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
				pageRankMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				Double diff = new Double((o2.getValue() - o1.getValue())
						* Math.pow(10, 10));
				return diff.intValue();
			}
		});
		System.out.println("Top 50 pages sorted by PageRank:");
		for (int i = 0; i < 50; i++) {
			String id = list.get(i).getKey();
			double pr = list.get(i).getValue();
			System.out.println("TOP" + (i + 1) + " ID: " + id + " PageRank: "
					+ pr);
		}
	}

	private void sortByinLinks() {
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
				inLinkMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				int diff = o2.getValue() - o1.getValue();
				return diff;
			}
		});
		System.out.println("Top 50 pages sorted by in-link count:");
		for (int i = 0; i < 50; i++) {
			String id = list.get(i).getKey();
			int count = list.get(i).getValue();
			System.out.println("TOP" + (i + 1) + " ID: " + id
					+ " In-link count: " + count);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 2 && args.length != 1) {
			System.err.println("Usage: <fileDir> <iterations>");
			System.exit(0);
		}
		PageRank pr = new PageRank();
		pr.readFile(args[0]);
		pr.initializePageRank();
		if (args.length == 2) {
			for (int i = 0; i < Integer.parseInt(args[1]); i++) {
				pr.updatePageRank();
			}
			for (Map.Entry<String, Double> entry : pageRankMap.entrySet()) {
				System.out.println("PR(" + entry.getKey() + ") = "
						+ entry.getValue());
			}
		}
		if (args.length == 1) {
			int round = 1;
			int count = 0;
			while (true) {
				double d1 = pr.calculatePerplexity();
				System.out.println("Round " + round + ": " + d1);
				pr.updatePageRank();
				double d2 = pr.calculatePerplexity();
				round++;
				if (Math.abs(d2 - d1) < 1.0) {
					count++;
				} else {
					count = 0;
				}
				if (count == 4) {
					System.out.println("Round " + round + ": " + d2);
					pr.sortByPageRank();
					pr.sortByinLinks();
					break;
				}
			}
		}
	}

}
