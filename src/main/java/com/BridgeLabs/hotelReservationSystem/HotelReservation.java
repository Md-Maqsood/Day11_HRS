package com.BridgeLabs.hotelReservationSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HotelReservation {
	private static final Logger logger=LogManager.getLogger(HotelReservation.class);
	static Scanner sc=new Scanner(System.in);
	public List<Hotel> hotels;
	private static final Pattern DAY_PATTERN = Pattern.compile("\\([a-z]{3,4}\\)");
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
	
	/**
	 * uc2
	 */
	public void getCheapestHotel() {
		logger.debug("Enter the date range in format <date1>, <date2>, <date3>\nEg.:  16Mar2020(mon), 17Mar2020(tues), 18Mar2020(wed)");
		String customerInput=sc.nextLine();
		Matcher dayMatcher=DAY_PATTERN.matcher(customerInput);
		List<String> daysList= new ArrayList<String>();
		while(dayMatcher.find()) {
			daysList.add(dayMatcher.group());
		}
		Map<Hotel, Integer> hotelToTotalRateMap=hotels.stream().collect(Collectors.toMap(h->h, h->h.getRegularRate()*daysList.size()));
		Hotel cheapestHotel=hotelToTotalRateMap.keySet().stream().min((n1,n2)->hotelToTotalRateMap.get(n1)-hotelToTotalRateMap.get(n2)).orElse(null);
		logger.debug(cheapestHotel.getName()+", Total Rates: $"+hotelToTotalRateMap.get(cheapestHotel));
	}
	
	public static void main(String[] args) {
		HotelReservation hotelReservation=new HotelReservation();
		hotelReservation.addHotels();
		hotelReservation.getCheapestHotel();
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