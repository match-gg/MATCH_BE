package gg.match.controller.api

import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.service.LoLService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lol")
class LoLController(
    private val loLService: LoLService
) {
    @PostMapping("/hi")
    fun hi(@RequestBody loLRequestDTO: LoLRequestDTO): ResponseEntity<Any> {
        try{
            return ResponseEntity.ok().body(loLService.saveContent(loLRequestDTO))
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    //@GetMapping("/test")
    //public ResponseEntity<?> test(){
    //    try{
    //        return ResponseEntity.ok().body(null);
    //    } catch (Exception e) {
    //        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    //    }
    @GetMapping("/test")
    fun test(): ResponseEntity<Any> {
        return try{
            ResponseEntity.ok().body("null")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }
}