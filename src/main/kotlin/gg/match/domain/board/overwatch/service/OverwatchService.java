package gg.match.domain.board.overwatch.service;

import gg.match.domain.board.overwatch.dto.OverwatchRequestDTO;
import gg.match.domain.board.overwatch.dto.OverwatchResponseDTO;
import gg.match.domain.board.overwatch.entity.Overwatch;
import gg.match.domain.board.overwatch.entity.Position;
import gg.match.domain.board.overwatch.entity.Tier;
import gg.match.domain.board.overwatch.entity.Type;
import gg.match.controller.common.entity.Expire;
import gg.match.domain.board.overwatch.repository.OverwatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) //DB조회만 변경X, 데이터베이스 부하방지 성능최적화
public class OverwatchService {
    private final OverwatchRepository overwatchRepository;

    @Autowired
    public OverwatchService(OverwatchRepository overwatchRepository) {
        this.overwatchRepository = overwatchRepository;
    }

    //글 등록기능
    public OverwatchResponseDTO createOverwatch(OverwatchRequestDTO overwatchRequestDTO) {
        Overwatch overwatch = new Overwatch();
        overwatch.setName(overwatchRequestDTO.getName());
        overwatch.setTier(Tier.valueOf(overwatchRequestDTO.getTier()));
        overwatch.setPosition(Position.valueOf(overwatchRequestDTO.getPosition()));
        overwatch.setVoice(overwatchRequestDTO.getVoice());
        overwatch.setContent(overwatchRequestDTO.getContent());
        overwatch.setExpire(Expire.valueOf(overwatch.getExpire().name()));;
        overwatch.setRegdate(overwatchRequestDTO.getRegdate());
        overwatchRepository.save(overwatch);

        return toOverwatchResponseDTO(overwatch);
    }

    public List<OverwatchResponseDTO> getAllOverwatch() {
        List<Overwatch> overwatchList = overwatchRepository.findAll();
        return overwatchList.stream().map(this::toOverwatchResponseDTO).collect(Collectors.toList());
    }

    private OverwatchResponseDTO toOverwatchResponseDTO(Overwatch overwatch) {
        OverwatchResponseDTO overwatchResponseDTO = new OverwatchResponseDTO();
        overwatchResponseDTO.setId(overwatch.getId());
        overwatchResponseDTO.setName(overwatch.getName());
        overwatchResponseDTO.setTier(overwatch.getTier().name());
        overwatchResponseDTO.setPosition(overwatch.getPosition().name());
        overwatchResponseDTO.setVoice(overwatch.getVoice());
        overwatchResponseDTO.setContent(overwatch.getContent());
        overwatchResponseDTO.setExpire(Expire.valueOf(overwatch.getExpire().name()));
        overwatchResponseDTO.setRegdate(overwatch.getRegdate());
        return overwatchResponseDTO;
    }

    //글 삭제기능
    @Transactional
    public void deleteOverwatch(Long id) {
        overwatchRepository.deleteById(id);
    }

    //글 수정기능
    @Transactional
    public OverwatchResponseDTO updateOverwatch(Long id, OverwatchRequestDTO overwatchRequestDTO) throws ChangeSetPersister.NotFoundException {
        Overwatch overwatch = overwatchRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        overwatch.setName(overwatchRequestDTO.getName());
        overwatch.setTier(Tier.valueOf(overwatchRequestDTO.getTier()));
        overwatch.setPosition(Position.valueOf(overwatchRequestDTO.getPosition()));
        overwatch.setVoice(overwatchRequestDTO.getVoice());
        overwatch.setContent(overwatchRequestDTO.getContent());
        overwatch.setExpire(Expire.valueOf(overwatch.getExpire().name()));
        overwatch.setRegdate(overwatchRequestDTO.getRegdate());

        Overwatch updatedOverwatch = overwatchRepository.save(overwatch);

        return toOverwatchResponseDTO(updatedOverwatch);
    }
}
