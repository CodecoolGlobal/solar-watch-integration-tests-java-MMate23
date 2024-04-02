package com.code.solarwatch.controller;

import com.code.solarwatch.dataModel.City;
import com.code.solarwatch.model.CityReport;
import com.code.solarwatch.model.SunsetReport;
import com.code.solarwatch.service.SunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;

@RestController
public class SunController {

    private final SunService sunService;

    @Autowired
    public SunController(SunService service) {
        this.sunService = service;
    }

    @GetMapping("/city2/{date}/{city}")
    public ResponseEntity<?> getCityByDate(@PathVariable LocalDate date,
                                           @PathVariable String city) {

        double lat = 0;
        double lon = 0;
        SunsetReport report = null;

        City targetCity = sunService.findCityByNameInDb(city);

        if (targetCity != null) {

            var reportTest = sunService.getSunsetFromDbByCoordinatesAndDate(date, targetCity);
            if (reportTest != null) {
                report = reportTest;
            } else {
                CityReport targetCityLatAndLon = sunService.getCityCoordinates(city);
                report = sunService.getLocationDate(date, targetCityLatAndLon.lat(),
                        targetCityLatAndLon.lon());
            }
        } else {
            CityReport cityReport = sunService.getCityCoordinates(city);
            sunService.saveCityAtDb(city, cityReport);

            if (cityReport == null) {
                return ResponseEntity.notFound().build();
            }
            lat = cityReport.lat();
            lon = cityReport.lon();

            if (validateCoordinates(lat, lon)) {
                return ResponseEntity.badRequest().body("Not valid coordinate");
            }

            report = sunService.getLocationDate(date, lat, lon);

        }
        sunService.saveSunsetOfCityAtDb(
                sunService.findCityByNameInDb(city), date, report);

        return ResponseEntity.ok(report);


    }


    @GetMapping("/city")
    public ResponseEntity<?> getCityDate(@RequestParam LocalDate date,
                                         @RequestParam String city) {

        CityReport cityReport = sunService.getCityCoordinates(city);
        double lon = cityReport.lon();
        double lat = cityReport.lat();

        if (validateCoordinates(lat, lon)) {
            return ResponseEntity.badRequest().body("Not valid coordinate");
        }
        var report = sunService.getLocationDate(date, lat, lon);

        return ResponseEntity.ok(report);
    }


    @GetMapping("/bpsunsetToday")
    public SunsetReport getCurrent() {
        return sunService.getCurrentSunsetForBudapest();
    }

    @GetMapping("/bpdate")
    public SunsetReport getBpDate(@RequestParam LocalDate date) {
        return sunService.getSunsetByDateForBudapest(date);
    }

    @GetMapping("/locationanddate")
    public ResponseEntity<?> getLocationDate(@RequestParam LocalDate date,
                                             @RequestParam double lat,
                                             @RequestParam double lon) {

        if (validateCoordinates(lat, lon)) {
            return ResponseEntity.badRequest().body("Not valid coordinate");
        }
        var report = sunService.getLocationDate(date, lat, lon);

        return ResponseEntity.ok(report);
    }


    private boolean validateCoordinates(double lat, double lon) {
        return lat > 90 || lat < -90 || lon > 180 || lon < -180;
    }

}
