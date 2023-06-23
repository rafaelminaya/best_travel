package com.debuggeando_ideas.best_travel.api.models.requests;

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
public class TourFlyRequest implements Serializable {
    @Positive(message = "Id fly must be greater than 0")
    @NotNull(message = "Id fly is mandatory")
    private Long id;
}
