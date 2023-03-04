package gg.match.domain.board.overwatch.repository;

import java.util.*;

import gg.match.domain.board.overwatch.entity.Overwatch;
import gg.match.domain.board.overwatch.entity.Tier;
import gg.match.domain.board.overwatch.entity.Position;
import gg.match.domain.board.overwatch.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OverwatchRepository extends JpaRepository<Overwatch, Long> {

    //필터링 조회방법
    List<Overwatch> findByTier(Tier tier);
    List<Overwatch> findByType(Type type);
    List<Overwatch> findByPosition(Position position);

}
