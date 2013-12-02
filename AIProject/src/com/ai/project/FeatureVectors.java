/**
 * 
 */
package com.ai.project;

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
public class FeatureVectors {

	@SuppressWarnings("unchecked")
	public static void prepareFeatureVectorsFromFreebase(String fileName) throws IOException{
		Object[]  obj = FileUtils.readFreebaseFile(fileName);
		List<MovieDAO> movies = (ArrayList<MovieDAO>)obj[0]; 
		Set<String> genres = (TreeSet<String>)obj[1]; 
		Map<String,Integer> mapOfGenres  = new HashMap<String,Integer>();
		int id=-1;
		for(String genre : genres){
			mapOfGenres.put(genre, ++id);
		}
		FileUtils.writeFeatureVectors("data", mapOfGenres, movies);
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		prepareFeatureVectorsFromFreebase("moviesdata.txt");
	}

}
