package com.debuggeando_ideas.best_travel.domain.entities;

import com.debuggeando_ideas.best_travel.util.enums.AeroLine;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

// Serializable :
// Indica que toda objeto que implemente esta interfaz será serlializado a través de la red.
// Quiere decir que el objeto será convertido en bytes para ser transmitido a través de http o hacia una BD.
// En versiones más recientes ya no será necesario, ya que en vesiones más recientes de "Spring" y "JPA" se hará internamente
@Entity(name = "fly")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder // patrón de diseño builder que evita tener una sobre carga de constructores, siendo muy útil. Necesita tener la anotación  @AllArgsConstructor
public class FlyEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double originLat;
    private Double originLng;
    private Double destinyLat;
    private Double destinyLng;
    @Column(length = 20)
    private String originName;
    @Column(length = 20)
    private String destinyName;
    private BigDecimal price; // double precision
    @Enumerated(EnumType.STRING)
    private AeroLine aeroLine;

    // Exclusión, en el método ".toString()" de los atributos anotados con "@OneToMany" para evitar la recursividad infinita
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            mappedBy = "fly" // relación bidireccional con el atributo "fly" de la clase "TicketEntity"
    )
    private Set<TicketEntity> tickets;
}
