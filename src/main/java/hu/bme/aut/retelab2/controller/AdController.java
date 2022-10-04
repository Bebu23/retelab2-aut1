package hu.bme.aut.retelab2.controller;

import hu.bme.aut.retelab2.domain.Ad;
import hu.bme.aut.retelab2.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    @Autowired
    private AdRepository adRepository;

    @PostMapping
    public Ad create(@RequestBody Ad ad) {
        ad.setId(null);
        return adRepository.save(ad);
    }

    @GetMapping("/search")
    public List<Ad> searchByPrice(@RequestParam(required = false) Integer minPrice, @RequestParam(required = false) Integer maxPrice) {
        if (minPrice == null) minPrice = 0;
        if (maxPrice == null) maxPrice = 10000000;

        return adRepository.searchByPrice(minPrice, maxPrice);
    }

    @PutMapping
    public ResponseEntity<Ad> update(@RequestBody Ad updated) {
        updated = adRepository.updateAd(updated);
        if (updated == null)
            return ResponseEntity.status(403).build();
        else
            return ResponseEntity.ok(updated);
    }
}
