package com.debuggeando_ideas.best_travel.api.models.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TourHotelRequest implements Serializable {
    @Positive(message = "Id hotel must be greater than 0")
    @NotNull(message = "Id hotel is mandatory")
    private Long id;
    @Positive
    @Min(value = 1, message = "Min 1 day to make reservation")
    @Max(value = 30, message = "Max 30 days to make reservation")
    @NotNull(message = "Total days is mandatory")
    private Integer totalDays;
}
