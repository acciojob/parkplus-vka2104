package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //get user by id and throws exception if user not available
        Optional<User> userOpt = userRepository3.findById(userId);
        if(!userOpt.isPresent()) throw new Exception("Cannot make reservation");
        User user = userOpt.get();

        //check parkinglot by id and throws exception if parkinglot not available
        Optional<ParkingLot> parkingLotOpt = parkingLotRepository3.findById(parkingLotId);
        if(!parkingLotOpt.isPresent()) throw new Exception("Cannot make reservation");
        ParkingLot parkingLot = parkingLotOpt.get();

        //get spot from parkinglot and if there is not spot exist throw exception
        List<Spot> spotList = parkingLot.getSpotList();
        if(spotList.isEmpty()) throw new Exception("Cannot make reservation");

        //get the perfect matching spot from the spot list based on vehicle wheels and price
        Spot availableSpot = null;
        for(Spot spot: spotList) {
            int acceptedWheels = 0;
            if(spot.getSpotType() == SpotType.TWO_WHEELER) {
                acceptedWheels = 2;
            } else if(spot.getSpotType() == SpotType.FOUR_WHEELER) {
                acceptedWheels = 4;
            } else {
                acceptedWheels = Integer.MAX_VALUE;
            }
            if(!spot.isOccupied() && acceptedWheels >= numberOfWheels && (availableSpot == null || spot.getPricePerHour() < availableSpot.getPricePerHour())) {
                availableSpot = spot;
            }
        }
        //If the spot is not available throw exception
        if(availableSpot == null) throw new Exception("Cannot make reservation");

        //set the spot to occupied
        availableSpot.setOccupied(true);

        // creating a reservation object to set value to save reservation
        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(availableSpot);
        reservation.setUser(user);
        return reservationRepository3.save(reservation);
    }
}
