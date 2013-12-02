/**
 * 
 */
package com.ai.project;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaurav
 *
 */
public class AIUtil {

	/**
	 * Calculates Dot Product of two vectors
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Double getDotProduct(Double[] x, Double[] y) {
		List<Double> dotProduct = new ArrayList<Double>();
		for (int i = 0; i < x.length && i < y.length; i++) {
			dotProduct.add(x[i] * y[i]);
		}

		Double sumOfDotProduct = 0.0;
		for (Double val : dotProduct)
			sumOfDotProduct += val;
		return sumOfDotProduct;
	}

	/**
	 * Calculates Magnitude of a vector
	 * 
	 * @param x
	 * @return
	 */
	public static Double getVectorMagnitude(Double[] x) {
		Double sumOfSquare = 0.0;
		for (int i = 0; i < x.length; i++) {
			sumOfSquare += x[i] * x[i];
		}
		return Math.sqrt(sumOfSquare);
	}

	
	/**
	 * Calculates Cosine Similarity between two vectors
	 * 
	 * @param dotProduct
	 * @param magnitudeOne
	 * @param magnitudeTwo
	 * @return
	 */
	public static Double getCosineSimilarity(Double[] Doubles, Double[] vector) {
		Double cosineSim = getDotProduct(Doubles, vector)
				/ (getVectorMagnitude(Doubles) * getVectorMagnitude(vector));
		if (Double.isNaN(cosineSim))
			return 0.0;
		else
			return cosineSim;
	}

	/**
	 * Euclidian Distance
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Double getEuclidianDistance(Double[] x, Double[] y) {
		assert (x.length == y.length);
		int min = (x.length > y.length) ? y.length : x.length;
		Double sum = 0.0;
		for (int i = 0; i < min; i++)
			sum += ((x[i] - y[i]) * (x[i] - y[i]));
		return Math.sqrt(sum);
	}
	
	public static Double getManhattanDistance(Double[] x, Double[] y){
		double sum = 0.0;
		for (int i = 0; i < x.length; i++)
			sum += Math.abs((x[i] - y[i]));
		return sum;
	}

	/**
	 * Converts the document feature vector into an array of Double
	 * @param line
	 * @return
	 */
	public static Double[] getVectorFromString(String line){
		
		String csv = line.split("\t")[1];
		String[] vectorStr = csv.split(",");
		
		Double[] vector = new Double[vectorStr.length];
		int i=0;
		for(String s : vectorStr){
			vector[i++] = Double.parseDouble(s);
		}
		return vector;
	}
	
}
