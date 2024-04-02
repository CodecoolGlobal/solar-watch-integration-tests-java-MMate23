package com.code.solarwatch.repository;

import com.code.solarwatch.dataModel.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CityRepo extends JpaRepository<City,Long> {

    public City findByName(String name);


}
