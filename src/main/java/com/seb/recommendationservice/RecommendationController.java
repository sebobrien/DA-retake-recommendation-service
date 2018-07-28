package com.seb.recommendationservice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/recommendation-service")
public class RecommendationController {

	@Autowired
	private Environment env;

	private HashMap<String, Integer> genres = new HashMap<>();

	@PostConstruct
	private void loadGenres() {
		RestTemplate restTemplate = new RestTemplate();
		String uri = String.format("https://api.themoviedb.org/3/genre/movie/list?api_key=%s&laguage=en-US",
				env.getProperty("themoviedb.api-key"));
		for(Genre g:  restTemplate.getForObject(uri, GenreResponseEntity.class).genres) {
			genres.put(g.name.toLowerCase(), g.id);
		}
		
	}
	
	
	@GetMapping("/genre/{genres}")
    public String GetRecomendationsByGenre(@PathVariable(value="genres") String genres ){
		
		List<String> genresSplit = Arrays.asList(genres.split("&"));
		String ids = "";
		
		for (String g : genresSplit) {
			
			if (this.genres.containsKey(g.toLowerCase())) {ids += this.genres.get(g.toLowerCase()) + ",";}
			
		}
		System.out.println(ids);
        RestTemplate restTemplate = new RestTemplate();
        String uri = String.format("https://api.themoviedb.org/3/discover/movie?api_key=%s&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&with_genres=%s", env.getProperty("themoviedb.api-key"), ids );
        return restTemplate.getForObject(uri,String.class);

    }
	

	static class GenreResponseEntity {

		List<Genre> genres;

		public GenreResponseEntity() {
		}

		public List<Genre> getGenres() {
			return genres;
		}

		public void setGenres(List<Genre> genres) {
			this.genres = genres;
		}

	}

	static class Genre {

		int id;
		String name;

		public Genre() {
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
