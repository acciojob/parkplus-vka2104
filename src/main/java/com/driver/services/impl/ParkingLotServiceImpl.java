package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(name);
        parkingLot.setAddress(address);
        return parkingLotRepository1.save(parkingLot);
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        Spot spot = new Spot();

        //get the parking lot by id
        Optional<ParkingLot> parkingLotOpt = parkingLotRepository1.findById(parkingLotId);
        if(!parkingLotOpt.isPresent()) return null;
        ParkingLot parkingLot = parkingLotOpt.get();

        //set the details to spot object
        if(numberOfWheels > 0 && numberOfWheels <= 2) spot.setSpotType(SpotType.TWO_WHEELER);
        else if(numberOfWheels > 2 && numberOfWheels <= 4) spot.setSpotType(SpotType.FOUR_WHEELER);
        else if(numberOfWheels > 4) spot.setSpotType(SpotType.OTHERS);

        spot.setPricePerHour(pricePerHour);
        spot.setOccupied(false);
        spot.setParkingLot(parkingLot);

        //saving the spot
        Spot savedSpot = spotRepository1.save(spot);

        //adding the saved spot to the parking lot
        parkingLot.getSpotList().add(savedSpot);
        parkingLotRepository1.save(parkingLot);

        return savedSpot;
    }

    @Override
    public void deleteSpot(int spotId) {
        spotRepository1.deleteById(spotId);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        //get the parking lot by id
        Optional<ParkingLot> parkingLotOpt = parkingLotRepository1.findById(parkingLotId);
        if(!parkingLotOpt.isPresent()) return null;
        ParkingLot parkingLot = parkingLotOpt.get();

        //get the parking lot's spot by id
        Optional<Spot> spotOpt = spotRepository1.findById(spotId);
        if(!spotOpt.isPresent()) return null;
        Spot spot = spotOpt.get();

        //updating the spot price
        spot.setPricePerHour(pricePerHour);

        return spotRepository1.save(spot);
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
       List<Spot> spotList = spotRepository1.findByParkingLotId(parkingLotId);
       if(!spotList.isEmpty()) {
           for (Spot spot: spotList) {
               spotRepository1.delete(spot);
           }
       }
        parkingLotRepository1.deleteById(parkingLotId);
    }
}
