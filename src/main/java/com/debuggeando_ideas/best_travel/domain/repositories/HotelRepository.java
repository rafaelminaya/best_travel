package com.debuggeando_ideas.best_travel.domain.repositories;

import com.debuggeando_ideas.best_travel.domain.entities.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
    // select * from hotel where price < 100 ;
    Set<HotelEntity> findByPriceLessThan(BigDecimal price);

    // select * from hotel where price between 100 and 150 ;
    Set<HotelEntity> findByPriceBetween(BigDecimal min, BigDecimal max);

    // select * from hotel where rating > 3
    Set<HotelEntity> findByRatingGreaterThan(Integer rating);

    // select * from hotel as h join reservation as r on h.id = r.hotel_id where r.id = '12345678-1234-5678-1234-567812345678';
    Optional<HotelEntity> findByReservationId(UUID reservationId);
}
