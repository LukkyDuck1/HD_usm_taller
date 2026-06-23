package cl.usm.sansaweigh.entities;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EspecificacionBalanza implements Serializable {

    private String id;
    private String name;
    private String brand;
    private double maxCapacity;
    private double precision;
    private double lastCalibrationOffset;
}
