package simon.proyecto1.restcommunicationclient;

import java.io.Serializable;

/**
 * Created by simon on 8/26/13.
 */
public class Post implements Comparable<Post>, Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -9181705812125433738L;
	private int rating;
    private String author;
    private String content;
    private String title;
    private Integer id;

    public Post( Integer id, int rating, String author, String content, String title ) {
    	this.id = id;
        this.rating = rating;
        this.author = author;
        this.content = content;
        this.title = title;
    }

    public int getRating() {
        return rating;
    }

    public void setRating( int rating ) {
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor( String author ) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent( String content ) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }
    
    public Integer getId() {
    	return id;
    }
    
    public void setId(Integer id) {
    	this.id = id;
    }

	@Override
	public int compareTo(Post another) {
		return this.id.compareTo(another.id);
	}
	
	@Override
	public String toString() {
		 return String.format("Post: %s\n Author: %s\n Content: %s", this.title, this.author, this.content);
	}
}
