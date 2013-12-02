/**
 * 
 */
package com.ai.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author gaurav
 * 
 */

class Results implements Comparable<Results> {
	String title;
	Double distance;

	public Results(String title, Double distance) {
		this.title = title;
		this.distance = distance;
	}

	@Override
	public int compareTo(Results o) {
		return Double.compare(this.distance, o.distance);
	}

}

public class KNearestNeighbor {
	private static final String CLUSTER_FILE_PREFIX = "cluster_";
	private Map<String, Double[]> mapOfMovieAndFeatures;
	private Map<String, Integer> mapOfGenres;
	private static final int K = 5; 

	@SuppressWarnings("unchecked")
	public KNearestNeighbor() throws IOException {
		Object[] obj = FileUtils.readFreebaseFile("moviesdata.txt");
//		movies = (ArrayList<MovieDAO>) obj[0];
		Set<String> genres = (TreeSet<String>) obj[1];
		mapOfGenres = new HashMap<String, Integer>();
		mapOfMovieAndFeatures = FileUtils.getMapOfFeatures("data");
		
		Integer id = 0;
		for (String s : genres)
			mapOfGenres.put(s.toLowerCase().trim(), id++);
	}

	public Double[] convertInputToFeatureVector(String input) {
		String[] tokens = input.split("(?=\\+)|(?=\\-)");

		// TODO: Identify the entities in these tokens, into
		// <actor/movie/genre/director>

		Double[] curVector = null;
		if (mapOfMovieAndFeatures.containsKey(tokens[0].trim().toLowerCase())) {
			curVector = mapOfMovieAndFeatures.get(tokens[0].trim()
					.toLowerCase());
		} else {
			System.err.println("Cannot find the input movie!");
			System.exit(1);
		}
		
		for (int i = 1; i < tokens.length; i++) {
			if (tokens[i].contains("+")) {
				String curGenre = tokens[i].replace("+", "");
				try{
					curVector[mapOfGenres.get(curGenre.toLowerCase().trim())] = 5.0;
				}catch(NullPointerException e){
					System.err.println("Invalid Genre!");
					System.exit(1);
				}
			} else if (tokens[i].contains("-")) {
				String curGenre = tokens[i].replace("-", "");
				try{
					curVector[mapOfGenres.get(curGenre.toLowerCase().trim())] = 0.0;
				}catch(NullPointerException e){
					System.err.println("Invalid Genre!");
					System.exit(1);
				}
			}
		}
		return curVector;
	}

	/**
	 * Get 'k' nearest neighbors
	 * @param vector
	 * @throws IOException 
	 */
	public void getNearestNeighbors(Double[] vector, boolean requireClustering) throws IOException {
		if(requireClustering){
			int closestCluster = getNearestCluster(vector,"data", 8);
			Map<String, Double[]> mapOfClusteredMovieAndFeatures = FileUtils.getMapOfFeatures(CLUSTER_FILE_PREFIX+closestCluster);
			_getNearestNeighbors(vector, mapOfClusteredMovieAndFeatures);
		}else
			_getNearestNeighbors(vector, mapOfMovieAndFeatures);
	}

	/**
	 * Helper method to get the nearest neighbors
	 * @param vector
	 * @param mapOfMovieAndFeatures
	 */
	private void _getNearestNeighbors(Double[] vector, Map<String, Double[]> mapOfMovieAndFeatures){
		List<Results> results = new ArrayList<Results>();
		for (Map.Entry<String, Double[]> kv : mapOfMovieAndFeatures.entrySet()) {
			Double cosineSimilarity = AIUtil.getEuclidianDistance(kv.getValue(),
					vector);
			results.add(new Results(kv.getKey(), cosineSimilarity));
//			Comparator<Results> c = Collections.reverseOrder();
			Collections.sort(results);
			if (results.size() > K+1)
				results = results.subList(0, K+1);
		}

		for (int i = 0; i < results.size(); i++)
			System.out.println(results.get(i).title + " , "
					+ results.get(i).distance);
	}

	/**
	 * 
	 * @param input String entered by the user
	 * @param inputFilePath Path to feature vector file
	 * @param k Number of clusters to generate
	 * @return id of the closest cluster
	 * @throws IOException
	 */
	public int getNearestCluster(Double[] inputVector, String inputFilePath, int k) throws IOException{
		return getClusterWithMinimumDistance(inputVector, inputFilePath, 8);
	}
	
	/**
	 * Helper method to get the closest cluster. Select one from the three distance metrics
	 * @param curVector User input converted to a feature vector
	 * @param featureVectorFile (eg. "data")
	 * @param k (eg. 8, for 8-means clustering)
	 * @return cluster with minimum distance
	 * @throws IOException
	 */
	private int getClusterWithMinimumDistance(Double[] curVector, String featureVectorFile, int k) throws IOException{
		Double[][] means = KMeans.kMeansDriver(featureVectorFile, k);
		Double[] dist = new Double[means.length];
		
		Double min = Double.MAX_VALUE;
		int cluster = 0;
		for (int i = 0; i < means.length; i++){
			dist[i] = AIUtil.getEuclidianDistance(curVector, means[i]);
			// dist[i] = MiningUtil.getCosineSimilarity(point, means[i]);
			// dist[i] = MiningUtil.getManhattanDistance(point, means[i]);
			if(dist[i] < min){
				min = dist[i];
				cluster = i;
			}
		}
		System.out.println("Minimum distance from cluster #"+cluster+", distance = "+min);
		return cluster;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		KNearestNeighbor knn = new KNearestNeighbor();
		String input = "Every Other Weekend+comedy+action Film";//"Jumanji+Comedy-Thriller-Fantasy";
		Double[] vector = knn.convertInputToFeatureVector(input);
		knn.getNearestNeighbors(vector, false);

		// String[] tokens = input.split("[+-]");
		// System.out.println(tokens[0]+" , "+tokens[1]+" , "+tokens[2]);
		// String[] tokens2 = input.split("(?=\\+)|(?=\\-)");
		// System.out.println(tokens2[0].trim()+" , "+tokens2[1].trim()+" , "+tokens2[2].trim());
	}
}
