package com.BridgeLabs.hotelReservationSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HotelReservation {
	private static final Logger logger=LogManager.getLogger(HotelReservation.class);
	static Scanner sc=new Scanner(System.in);
	public List<Hotel> hotels;
	
	public HotelReservation() {
		this.hotels=new ArrayList<Hotel>();
	}
	
	/**
	 * uc1
	 */
	public void addHotels() {
		do {
			logger.debug("Enter the hotel details in given order -\nName:\nRate for Regular Customer:");
			hotels.add(new Hotel(sc.nextLine(), Integer.parseInt(sc.nextLine())));
			logger.debug("Enter 1 to add another hotel, else enter 0: ");
		}while(sc.nextLine().equals("1"));
	}
	
	public static void main(String[] args) {
		HotelReservation hotelReservation=new HotelReservation();
		hotelReservation.addHotels();
		hotelReservation.hotels.forEach(n->logger.debug(n));
	}
}

class Hotel{
	private String name;
	private int regularRate;
	public Hotel(String name, int regularRate) {
		super();
		this.name = name;
		this.regularRate = regularRate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRegularRate() {
		return regularRate;
	}
	public void setRegularRate(int regularRate) {
		this.regularRate = regularRate;
	}
	@Override
	public String toString() {
		return "Hotel [\nName=" + name + "\nRegular Rate=$" + regularRate + "\n]";
	}
}