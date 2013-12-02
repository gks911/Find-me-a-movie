package com.ai.project;

/**
 * @author gaurav
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author gaurav
 * 
 */

class Point {
	Double point[];
	Double[] distances;
	String title;
	// int clusterId;

	Map<Integer, Point> clusterMap;

	public Point(Double[] point, int k, String title) {
		this.point = point;
		this.title = title;
		distances = new Double[k];
		this.clusterMap = new HashMap<Integer, Point>();
	}
}

public class KMeans {

	// List<Double> data;
	List<Point> points;
	List<Point> samples;
	Double[][] means;
	int k;
	public int MAX_SIZE = 0;
	Map<Integer, List<Point>> clusterMap;
	Map<String, Integer> titleMap;
	int numOfLabeledDocs = 0;
	
	private static final String FREEBASE_FILE_PATH = "moviesdata.txt";

	public KMeans(String path, int k) throws IOException {
		this.k = k;
		this.clusterMap = new HashMap<Integer, List<Point>>();
		this.titleMap = new HashMap<String, Integer>();
		points = new ArrayList<Point>();
		List<String> data = readData(path);

		for (String s : data) {
			String title = s.split("\t")[1];
			titleMap.put(title, 1);
			points.add(new Point(getFeaturesAsDoubleArray(s), k,
					s.split("\t")[1]));

		}
		means = new Double[k][MAX_SIZE];
	}

	/**
	 * Read the data
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public List<String> readData(String path) throws IOException {
		List<String> trainData = new ArrayList<String>();
		File inputFile = new File(path);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found!" + e.getLocalizedMessage());
		}

		String line = null;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty()) {
				trainData.add(line);
			}
		}
		br.close();
		return trainData;
	}

	/**
	 * Converts a string of feature vector from the file into an array of Double
	 * @param line
	 * @return
	 */
	public Double[] getFeaturesAsDoubleArray(String line) {
		String[] strFeature = line.split("\t")[0].split(",");
		Double[] DoubleFeature = new Double[strFeature.length];

		for (int i = 0; i < strFeature.length; i++)
			DoubleFeature[i] = Double.parseDouble(strFeature[i]);

		if (MAX_SIZE < DoubleFeature.length)
			this.MAX_SIZE = DoubleFeature.length;

		return DoubleFeature;
	}

	public void kMeansCluster(List<Point> points) {
		boolean hasConverged = false;
		long iterations = 0;

		do {
			int[] oldSize = new int[k];
			for (int i = 0; i < k; i++)
				if (iterations == 0)
					oldSize[i] = 0;
				else if (clusterMap.containsKey(i))
					oldSize[i] = clusterMap.get(i).size();

			clusterMap.clear();
			Double[][] oldMeans = new Double[k][MAX_SIZE];
			for (int i = 0; i < means.length; i++)
				oldMeans[i] = means[i];

			for (int i = 0; i < points.size(); i++) {
				Point x = points.get(i);
				Double[] distFromMean = getDistancesFromMean(x.point, means);
				for (int j = 0; j < distFromMean.length; j++)
					x.distances[j] = distFromMean[j];
				int id = getClusterWithMinDist(x.distances);
				// for Cosine Similarity
				// int id = getClusterWithMinSimilarity(x.distances);
				// x.clusterId = getClusterWithMinDist(x.distances);
				if (clusterMap.containsKey(id)) {
					clusterMap.get(id).add(x);
				} else {
					List<Point> pts = new ArrayList<Point>();
					pts.add(x);
					clusterMap.put(id, pts);
				}
			}
			means = calculateNewMeans();

			int count = 0;
			for (int i = 0; i < k; i++) {
				if (clusterMap.get(i) == null)
					count++;
				else if (oldSize[i] == clusterMap.get(i).size())
					count++;
			}
			if (count == k)
				hasConverged = true;
			iterations++;
		} while (!hasConverged);

		System.out.println("Number Of iterations = " + iterations);
	}

	private Double[][] calculateNewMeans() {
		Double[][] newMeans = new Double[k][this.MAX_SIZE];
		for (int i = 0; i < newMeans.length; i++) {
			if (clusterMap.containsKey(i)) {
				Double[] tempMean = new Double[this.MAX_SIZE];
				 for(int k=0; k<this.MAX_SIZE; k++)
				 tempMean[k] = 0.0;
				List<Point> pts = clusterMap.get(i);
				for (Point p : pts) {
					for (int j = 0; j < p.point.length; j++){
						tempMean[j] += p.point[j];
					}
				}
				for (int j = 0; j < tempMean.length; j++)
					tempMean[j] = (Double) tempMean[j] / pts.size();
				newMeans[i] = tempMean;
			}
		}
		return newMeans;
	}

	private int getClusterWithMinDist(Double[] distances) {
		Double min = 999999.99;
		int clusterId = 0;
		for (int i = 0; i < distances.length; i++) {
			if (distances[i] < min) {
				min = distances[i];
				clusterId = i;
			}
		}
		return clusterId;
	}

	/**
	 * Used in case of Cosine Similarity
	 * @param distances
	 * @return
	 */
	private int getClusterWithMinSimilarity(Double[] distances) {
		Double min = -1.0;
		int clusterId = 0;
		for (int i = 0; i < distances.length; i++) {
			if (distances[i] > min) {
				min = distances[i];
				clusterId = i;
			}
		}
		return clusterId;
	}

	/**
	 * Implement either Cosine Similarity or Euclidian Distance
	 * 
	 * @param point
	 * @param means
	 * @return
	 */
	private Double[] getDistancesFromMean(Double[] point, Double[][] means) {
		Double[] dist = new Double[k];

		for (int i = 0; i < k; i++)
			dist[i] = AIUtil.getEuclidianDistance(point, means[i]);
		// dist[i] = MiningUtil.getCosineSimilarity(point, means[i]);
		// dist[i] = MiningUtil.getManhattanDistance(point, means[i]);
		return dist;
	}

	@SuppressWarnings("unchecked")
	public void printCluster() throws IOException {
		Map<Integer,Set<String>> uniqueGenresInCluster = new HashMap<Integer, Set<String>>();  
		Map<String,MovieDAO> movieWithGenres = FileUtils.getGenresFromMovie(FREEBASE_FILE_PATH);
		Set<String> genres = (TreeSet<String>)FileUtils.readFreebaseFile(FREEBASE_FILE_PATH)[1];
		
		Map<String,Integer> mapOfGenres  = new HashMap<String,Integer>();
		int id=-1;
		for(String genre : genres){
			mapOfGenres.put(genre, ++id);
		}
		
		
		for (int i = 0; i < means.length; i++) {
			if (clusterMap.containsKey(i)) {
				System.out.println("Cluster #" + (i + 1) + " size = "
						+ clusterMap.get(i).size());
				
				List<MovieDAO> movies = new ArrayList<MovieDAO>();
				Set<String> clusterGenres = new TreeSet<String>();
				for(Point p : clusterMap.get(i)){
					String[] genreTokens = movieWithGenres.get(p.title).getGenres().split(",");
					for(String s : genreTokens)
						clusterGenres.add(s);
					
					movies.add(movieWithGenres.get(p.title));
				}
				
				//write the clusters to a file
				FileUtils.writeFeatureVectors("cluster_"+i, mapOfGenres, movies);
				
				uniqueGenresInCluster.put(i, clusterGenres);
				
				System.out.println("Genres in Cluster #" + (i + 1)+" \n" );
				for(String s : clusterGenres)
					System.out.print(s+",");
				System.out.println();
			} else {
				System.out.println("Cluster #" + (i + 1) + " is Empty!");
			}
		}
	}

	private double getGenreEntropy() {
		int overallDenom = 0;
		for (Map.Entry<String, Integer> kv : titleMap.entrySet()) {
			overallDenom += kv.getValue();
		}

		double entropy = 0.0;
		for (Map.Entry<Integer, List<Point>> kv : clusterMap.entrySet()) {
			Map<String, Integer> clusterTopicCount = new HashMap<String, Integer>();
			for (Point p : kv.getValue()) {
				if (clusterTopicCount.containsKey(p.title)) {
					int count = clusterTopicCount.get(p.title) + 1;
					clusterTopicCount.put(p.title, count);
				} else
					clusterTopicCount.put(p.title, 1);
			}

			int denom = 0;
			for (Map.Entry<String, Integer> ctc : clusterTopicCount.entrySet()) {
				if (ctc.getKey() != null)
					denom += ctc.getValue();
			}

			for (Map.Entry<String, Integer> kv2 : clusterTopicCount.entrySet()) {
				if (denom != 0)
					entropy += -(double) kv2.getValue()
							/ denom
							* (Math.log((double) kv2.getValue() / denom) / Math
									.log(2));
			}
			entropy *= (double) denom / overallDenom;
		}

		// int totalTopics = topicMap.size();
		// double entropy = 0.0;
		// for(Map.Entry<String, Integer> kv : topicMap.entrySet()){
		// entropy += -(double)kv.getValue()/totalTopics *
		// (Math.log((double)kv.getValue()/totalTopics)/Math.log(2));
		// }
		return entropy;
	}
	
	public Double getSquaredDifference(Double[] vector, Double[] mean) {
		Double sum = 0.0;
		for (int i = 0; i < vector.length; i++) {
			sum += ((mean[i] - vector[i]) * (mean[i] - vector[i]));
		}
		return sum;
	}

	public Double[] getSkew(Double[][] means) {
		Double[] variances = new Double[k];
		for (Map.Entry<Integer, List<Point>> kv : this.clusterMap.entrySet()) {
			Double sum = 0.0;
			for (Point p : kv.getValue()) {
				sum += getSquaredDifference(p.point, means[kv.getKey()]);
			}
			variances[kv.getKey()] = (Double) sum / kv.getValue().size();
		}
		return variances;
	}
	
	/**
	 * Driver method to run KMeans Algorithm
	 * @param featureVectorFile
	 * @param k
	 * @throws IOException
	 */
	public static Double[][] kMeansDriver(String featureVectorFile, int k) throws IOException{
		Date start = new Date();
		KMeans km = new KMeans(featureVectorFile, k);

		int[] initialMean = new int[k];
		Random rnd = new Random();
		for (int i = 0; i < k; i++) {
			int r = rnd.nextInt(km.points.size());
			initialMean[i] = r;
			km.means[i] = km.points.get(r).point; // initialMean + (Double)i/k;
		}
		System.out.println("Starting Means = " + Arrays.toString(initialMean));
		km.kMeansCluster(km.points);

		km.printCluster();
		Date end = new Date();

		System.out.println("Time taken : "
				+ (double) (end.getTime() - start.getTime()) / 1000);
		
		return km.means;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Date start = new Date();
		// String path = args[0];
		// int k = Integer.parseInt(args[1]);
		String path = "data";
		int k = 8;

		KMeans km = new KMeans(path, k);

		// Double[] initialMean = new Double[km.MAX_SIZE];
		//
		// for (Double p : km.data)
		// initialMean += p;
		// initialMean = initialMean / km.data.size();

		int[] initialMean = new int[k];
		Random rnd = new Random();
		for (int i = 0; i < k; i++) {
			int r = rnd.nextInt(km.points.size());
			initialMean[i] = r;
			km.means[i] = km.points.get(r).point; // initialMean + (Double)i/k;
		}
		System.out.println("Starting Means = " + Arrays.toString(initialMean));
		km.kMeansCluster(km.points);

		km.printCluster();
		Date end = new Date();

		System.out.println("Time taken : "
				+ (double) (end.getTime() - start.getTime()) / 1000);

		for(int i=0; i<k; i++)
			System.out.println(Arrays.toString(km.means[i]));
		
		//
		// Double[] variances = km.getSkew(km.means);
		// for(int i=0; i < variances.length; i++)
		// System.out.println(variances[i]);
		// for(int i=0; i<k;i++){
		// System.out.println("["+km.means[i].length);
		// for(int j=0; j<km.means[i].length;j++){
		// System.out.print(km.means[i][j]+",");
		// }
		// System.out.println("]");
		// }
	}
}