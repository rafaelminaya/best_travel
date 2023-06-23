package com.debuggeando_ideas.best_travel.api.models.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReservationRequest implements Serializable {

    @Size(min = 18, max = 20, message = "The size have to a length between 18 and 20 characters.")
    @NotBlank(message = "Id client is mandatory")
    private String idClient;
    @Positive(message = "Id hotel must be greater than 0")
    @NotNull(message = "Id hotel is mandatory")
    private Long idHotel;
    @Min(value = 1, message = "Min 1 day to make reservation")
    @Max(value = 30, message = "Max 30 days to make reservation")
    @NotNull(message = "Total days is mandatory")
    private Integer totalDays;
    // 1° Opción validación - expresión regular
    //@Pattern(regexp = "(.+)@(.+)$")
    // 2° Opción validación - anotación @Email
    @Email(message = "Invalid email")
    private String email;
}
