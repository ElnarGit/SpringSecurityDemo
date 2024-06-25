package net.elnar.springsecuritydemo.rest;

import net.elnar.springsecuritydemo.model.Developer;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/developers")
public class DeveloperRestControllerV1 {
	
	private List<Developer> DEVELOPERS = new ArrayList<>(Stream.of(
			new Developer(1L, "Meiram", "Saparov"),
			new Developer(2L, "Elnar", "Saparov"),
			new Developer(3L, "Eugene", "Suleimanov")
	).toList());
	
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
	
	@PostMapping
	public Developer create(@RequestBody Developer developer){
		this.DEVELOPERS.add(developer);
		return developer;
	}
	
	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable("id") Long id){
		this.DEVELOPERS.removeIf(developer -> developer.getId().equals(id));
	}
}
