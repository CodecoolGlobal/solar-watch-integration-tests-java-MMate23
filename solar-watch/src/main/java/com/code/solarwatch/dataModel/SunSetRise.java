package com.code.solarwatch.dataModel;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class SunSetRise {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sunrise")
    private String sunrise;

    @Column(name = "sunset")
    private String sunset;

    @Column(name = "date")
    private Date date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
    private City city;

    public SunSetRise(String sunrise, String sunset, Date date, City city) {
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.date = date;
        this.city = city;
    }

    public SunSetRise() {

    }


    public String getSunset() {
        return sunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    public Date getDate() {
        return date;
    }
}
