/**
 * 
 */
package com.ai.project;

/**
 * @author gaurav
 *
 */
public class MovieDAO {

	private String title;
	private String genres;
	private String starring;
	private String director;
	
	public MovieDAO(String title, String genres, String starring, String director){
		this.setTitle(title);
		this.setGenres(genres);
		this.setStarring(starring);
		this.setDirector(director);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGenres() {
		return genres;
	}

	public void setGenres(String genres) {
		this.genres = genres;
	}

	public String getStarring() {
		return starring;
	}

	public void setStarring(String starring) {
		this.starring = starring;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}
}
