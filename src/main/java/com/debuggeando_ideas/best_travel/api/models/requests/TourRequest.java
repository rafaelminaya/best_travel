package com.debuggeando_ideas.best_travel.api.models.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TourRequest implements Serializable {
    @Size(min = 18, max = 20, message = "The size have to a length between 18 and 20 characters.")
    @NotBlank(message = "Id client is mandatory")
    private String customerId;
    @Size(min = 1, message = "Min 1 flight tour per tour")
    @NotNull(message = "flights is mandatory")
    private Set<TourFlyRequest> flights;
    @Size(min = 1, message = "Min 1 hotel tour per tour")
    @NotNull(message = "hotels is mandatory")
    private Set<TourHotelRequest> hotels;
    @Email(message = "Invalid email")
    private String email;
}
