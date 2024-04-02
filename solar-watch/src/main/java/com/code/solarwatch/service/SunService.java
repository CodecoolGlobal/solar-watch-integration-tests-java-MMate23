package com.code.solarwatch.service;

import com.code.solarwatch.config.SunServiceConfig;
import com.code.solarwatch.dataModel.City;
import com.code.solarwatch.dataModel.SunSetRise;
import com.code.solarwatch.model.*;
import com.code.solarwatch.repository.CityRepo;
import com.code.solarwatch.repository.SunSetRiseRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;


import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Date.valueOf;


@Service
public class SunService {

    private static final Logger logger = LoggerFactory.getLogger(SunService.class);

    private final RestTemplate restTemplate;
    private SunSetRiseRepo sunSetRiseRepo;

    private CityRepo cityRepo;

    @Autowired
    private SunServiceConfig sunServiceConfig;


    @Autowired
    public SunService(RestTemplate restTemplate, SunSetRiseRepo sunSetRiseRepo, CityRepo cityRepo) {
        this.restTemplate = restTemplate;
        this.sunSetRiseRepo = sunSetRiseRepo;
        this.cityRepo = cityRepo;
    }

    public SunsetReport getCurrentSunsetForBudapest() {
        //Budapest lat & lon
        return getLocationDate(LocalDate.now(), sunServiceConfig.latitude(),
                sunServiceConfig.longitude());

    }

    public SunsetReport getSunsetByDateForBudapest(LocalDate date) {
        //Budapest lat & lon
        return getLocationDate(date, sunServiceConfig.latitude(),
                sunServiceConfig.longitude());
    }


    public SunsetReport getLocationDate(LocalDate date, double lat, double lon) {

        String url = String.format(sunServiceConfig.SunriseSunsetApi() +
                "?lat=%s&lng=%s&date=%s", lat, lon, date.toString());

        System.out.println(url);
        RawSunsetReport response = restTemplate.getForObject(url, RawSunsetReport.class);
        logger.info("Response from Open Weather API: {}", response);

        assert response != null;
        return new SunsetReport(response.results().sunrise(), response.results().sunset());
    }


    public CityReport getCityCoordinates(String city) {

        String url = String.format(sunServiceConfig.OpenWeatherApi() +
                "?q=%s&limit=1&appid=%s", city, sunServiceConfig.OpenWeatherApiKey());

        CoordinateByCity[] dataList = restTemplate.getForObject(url, CoordinateByCity[].class);

        assert dataList != null;
        if (dataList.length > 0) {
            return new CityReport(Double.parseDouble(dataList[0].getLat()),
                    Double.parseDouble(dataList[0].getLon()));
        }

        return null;

    }


    public City findCityByNameInDb(String name) {

        City targetCity = cityRepo.findByName(name);

        if (targetCity != null) {
            return targetCity;
        }
        return null;

    }

    public SunsetReport getSunsetFromDbByCoordinatesAndDate(LocalDate date, City targetCity) {

        SunSetRise sunSetRise = sunSetRiseRepo.findByDateAndCity(Date.valueOf(date), targetCity);

        if (sunSetRise != null) {
            return new SunsetReport(sunSetRise.getSunset(), sunSetRise.getSunrise());
        }
        return null;
    }

    public boolean saveCityAtDb(String name, CityReport cityReport) {

        City city = new City(name, cityReport.lon(), cityReport.lat());

        cityRepo.save(city);

        return true;
    }

    public boolean saveSunsetOfCityAtDb(City city, LocalDate date, SunsetReport sunsetReport) {

        SunSetRise sunSetRiseToDb = new SunSetRise(
                sunsetReport.sunset(), sunsetReport.sunrise(), Date.valueOf(date), city);

        sunSetRiseRepo.save(sunSetRiseToDb);

        return true;
    }

}
