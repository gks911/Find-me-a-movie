/**
 * 
 */
package com.ai.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author gaurav
 * 
 */
public class FileUtils {

	/**
	 * Reads in the Freebase generated file
	 * @param path
	 * @return Array of Object, with index 0 having the list of Movie objects,
	 *         and index 1 having unique set of genres
	 * @throws IOException
	 */
	public static Object[] readFreebaseFile(String path) throws IOException {
		List<MovieDAO> movies = new ArrayList<MovieDAO>();
		Set<String> genres = new TreeSet<String>();
		File inputFile = new File(path);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found!" + e.getLocalizedMessage());
		}

		String line = null;
		while ((line = br.readLine()) != null) {
			try {
				String[] tokens = line.split("\\|");
				if (tokens.length == 4) {
					movies.add(new MovieDAO(tokens[0], tokens[1], tokens[2],
							tokens[3]));
					for (String g : tokens[1].split(","))
						genres.add(g);
				} else {
					// TODO: What can we do about the movies having more than 4 pipes?
				}
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				// Ignore, because its a movie with no data
			}
		}
		br.close();
		return new Object[] { movies, genres };
	}

	/**
	 * Reads the GroupLens movie dataset
	 * @param path
	 * @return	Map of movie_name and genres
	 * @throws IOException
	 */
	public static Map<String, String> readGrouplensTfIdfFile(String path)
			throws IOException {
		Map<String, String> movies = new HashMap<String, String>();
		Set<String> genres = new TreeSet<String>();
		File inputFile = new File(path);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found!" + e.getLocalizedMessage());
		}

		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("::");
			String[] genreTokens = tokens[2].split("\\|");
			StringBuilder sb = new StringBuilder();
			for (String s : genreTokens) {
				sb.append(s + ",");
				genres.add(s);
			}
			movies.put(tokens[1],
					sb.toString().substring(0, sb.toString().length() - 1));
		}

		for (String s : genres)
			System.out.println(s);
		System.out.println(genres.size());
		br.close();
		return movies;
	}

	/**
	 * Writes the feature vectors to a file, in a tab separated format having
	 * <vector> <movieTitle> <starring> <director>
	 * 
	 * @param outputFile
	 * @param mapOfGenres
	 * @param movies
	 * @throws IOException
	 */
	public static void writeFeatureVectors(String outputFile,
			Map<String, Integer> mapOfGenres, List<MovieDAO> movies)
			throws IOException {
		FileWriter fw = new FileWriter(outputFile, true);
		BufferedWriter bw = new BufferedWriter(fw);

		for (MovieDAO movie : movies) {
			int[] vector = new int[mapOfGenres.size()];
			for (String genre : movie.getGenres().split(",")) {
				if (mapOfGenres.containsKey(genre))
					vector[mapOfGenres.get(genre)] = 1;
			}

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < vector.length; i++)
				sb.append(vector[i] + ",");

			bw.write(sb.toString().substring(0, sb.toString().length() - 1));
			bw.append("\t" + movie.getTitle() + "\t" + movie.getStarring()
					+ "\t" + movie.getDirector() + "\n");
		}
		bw.close();
		System.out.println("Finished writing output to " + outputFile);
	}

	/**
	 * Generates a Map of <movie_title, feature_vector> for each of the movies
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Double[]> getMapOfFeatures(String file) throws IOException{
		Map<String, Double[]> map = new HashMap<String, Double[]>();
		File inputFile = new File(file);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found!" + e.getLocalizedMessage());
		}

		String line = null;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			String[] vectorStr = tokens[0].split(",");
			Double[] vectors = new Double[vectorStr.length];
			for(int i=0; i<vectorStr.length;i++)
				vectors[i] = Double.parseDouble(vectorStr[i]);
			map.put(tokens[1].toLowerCase(), vectors);
		}
		br.close();
//		System.out.println(map.get("toy story"));
		System.out.println("Map.size = "+map.size());
		return map;
	}
	
	/**
	 * Gets all the genres corresponding to a movie
	 * @param movieName
	 * @return
	 * @throws IOException 
	 */
	public static Map<String,MovieDAO> getGenresFromMovie(String fileName) throws IOException{
		Map<String,MovieDAO> movieMap = new HashMap<String, MovieDAO>();
		File inputFile = new File(fileName);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found!" + e.getLocalizedMessage());
		}

		String line = null;
		while ((line = br.readLine()) != null) {
			try {
				String[] tokens = line.split("\\|");
				if (tokens.length == 4) {
					movieMap.put(tokens[0],new MovieDAO(tokens[0], tokens[1], tokens[2],
							tokens[3]));
				} else{
					// TODO: What can we do about the movies having more than 4 pipes?
				}
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				// Ignore, because its a movie with no data
			}
		}
		br.close();
		return movieMap;
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		 readFreebaseFile("moviesdata.txt");
		// Map<String, String> movies = fu.readGrouplensFile("movies.dat");

		// String str =
		// "Toy Story|Animation,Buddy film,Adventure Film,Family,Comedy,Family,Fantasy|Tom Hanks,Don Rickles,Jim Varney,Wallace Shawn,Tim Allen,Annie Potts,John Ratzenberger,Laurie Metcalf,Erik von Detten,John Morris,R. Lee Ermey,Penn Jillette,Sarah Freeman,Scott McAfee,Jack Angel,Jack Angel,Spencer Aste,Greg Berg,Lisa Bradley,Kendall Cunningham,Debi Derryberry,Debi Derryberry,Debi Derryberry,Cody Dorkin,Bill Farmer,Craig Good,Gregory Grudt,Danielle Judovits,Sam Lasseter,Brittany Levenbrown,Sherry Lynn,Mickie McGowan,Ryan O'Donohue,Jeff Pidgeon,Jeff Pidgeon,Jeff Pidgeon,Patrick Pinney,Philip Proctor,Philip Proctor,Jan Rabson,Joe Ranft,Andrew Stanton,Shane Sweet|John Lasseter";
		// System.out.println(str.split("\\|").length);

	}

}
