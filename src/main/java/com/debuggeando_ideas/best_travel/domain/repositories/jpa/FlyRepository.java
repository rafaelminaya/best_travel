package com.debuggeando_ideas.best_travel.domain.repositories.jpa;

import com.debuggeando_ideas.best_travel.domain.entities.jpa.FlyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FlyRepository extends JpaRepository<FlyEntity, Long> {
    // "fly" es el alias de "FlyEntity", determinado en la anotación "@Entity".
    // select * from fly where price < 20;
    @Query("SELECT f FROM fly AS f WHERE f.price < :price")
    Set<FlyEntity> selectLessPrice(BigDecimal price);

    // select * from fly where price between 10 and 15;
    @Query("SELECT f FROM fly AS f WHERE f.price BETWEEN :min AND :max")
    Set<FlyEntity> selectBetweenPrice(BigDecimal min, BigDecimal max);

    // select * from fly where origin_name = 'Grecia' and destiny_name ='Mexico';
    @Query("SELECT f FROM fly AS f WHERE f.originName = :origin AND f.destinyName = :destiny")
    Set<FlyEntity> selectOriginDestiny(String origin, String destiny);

    // select * from fly as f join ticket as t on f.id = t.fly_id where t.id  = '12345678-1234-5678-2236-567812345678'
    @Query("SELECT f FROM fly AS f JOIN FETCH f.tickets AS t WHERE t.id = :id")
    Optional<FlyEntity> findByTicketId(UUID id);
}
