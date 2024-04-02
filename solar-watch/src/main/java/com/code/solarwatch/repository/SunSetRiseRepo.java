package com.code.solarwatch.repository;

import com.code.solarwatch.dataModel.City;
import com.code.solarwatch.dataModel.SunSetRise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;

@Repository
public interface SunSetRiseRepo extends JpaRepository<SunSetRise, Long> {

    public SunSetRise findByDateAndCity(Date date,City city);

}
