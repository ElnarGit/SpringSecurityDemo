package net.elnar.springsecuritydemo.rest;

import net.elnar.springsecuritydemo.model.Developer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/developers")
public class DeveloperRestControllerV1 {
	
	private List<Developer> DEVELOPERS = Stream.of(
			new Developer(1L, "Meiram", "Saparov"),
			new Developer(2L, "Elnar", "Saparov"),
			new Developer(3L, "Eugene", "Suleimanov")
	).toList();
	
	@GetMapping
	public List<Developer> getAll(){
		return DEVELOPERS;
	}
	
	@GetMapping("/{id}")
	public Developer getById(@PathVariable("id") Long id){
		return DEVELOPERS.stream()
				.filter(developer -> developer.getId().equals(id))
				.findFirst()
				.orElse(null);
	}
}
