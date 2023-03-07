package gg.match.controller.api;

import gg.match.domain.board.overwatch.dto.OverwatchResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import gg.match.domain.board.overwatch.dto.OverwatchRequestDTO;
import gg.match.domain.board.overwatch.service.OverwatchService;

import java.util.List;

@RestController
@RequestMapping("/api/overwatch")
public class OverwatchController {

    private final OverwatchService overwatchService;

    @Autowired
    public OverwatchController(OverwatchService overwatchService) {
        this.overwatchService = overwatchService;
    }

    @GetMapping
    public ResponseEntity<List<OverwatchResponseDTO>> getAllOverwatch() {
        List<OverwatchResponseDTO> overwatchList = overwatchService.getAllOverwatch();
        return new ResponseEntity<>(overwatchList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OverwatchResponseDTO> createOverwatch(@RequestBody OverwatchRequestDTO overwatchRequestDTO) {
        OverwatchResponseDTO overwatch = overwatchService.createOverwatch(overwatchRequestDTO);
        return new ResponseEntity<>(overwatch, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOverwatch(@PathVariable Long id) {
        overwatchService.deleteOverwatch(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OverwatchResponseDTO> updateOverwatch(@PathVariable Long id, @RequestBody OverwatchRequestDTO overwatchRequestDTO) throws ChangeSetPersister.NotFoundException {
        OverwatchResponseDTO updatedOverwatch = overwatchService.updateOverwatch(id, overwatchRequestDTO);
        return new ResponseEntity<>(updatedOverwatch, HttpStatus.OK);
    }
}
