package com.BridgeLabs.hotelReservationSystem;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.BridgeLabs.hotelReservationSystem.HotelReservation.CustomerType;

public class HotelReservation {
	
	public enum CustomerType{
		REGULAR, REWARDS
	}
	
	private static final Logger logger = LogManager.getLogger(HotelReservation.class);
	static Scanner sc = new Scanner(System.in);
	public List<Hotel> hotels;
	private static final Pattern DAY_PATTERN = Pattern.compile("[0-9]{2}[A-Z][a-z]{2}[0-9]{4}");
	private static final Pattern CUSTOMER_TYPE_PATTERN=Pattern.compile("Re[gw][ua][lr][ad][rs]");
	private static final List<DayOfWeek> WEEKENDS = Arrays.asList(new DayOfWeek[] { DayOfWeek.SUNDAY, DayOfWeek.SATURDAY });

	public HotelReservation() {
		this.hotels = new ArrayList<Hotel>();
	}

	public void addHotels() {
		do {
			logger.debug("Enter the hotel details in given order -\nName:\nWeekday Rate for Regular Customer:\nWeekend Rate for Regular Customer:\nWeekday Rate for Rewards Customer:\nWeekend Rate for Rewards Customer:\nRating:");
			hotels.add(new Hotel(sc.nextLine(), Integer.parseInt(sc.nextLine()), Integer.parseInt(sc.nextLine()),
					Integer.parseInt(sc.nextLine()), Integer.parseInt(sc.nextLine()), Integer.parseInt(sc.nextLine())));
			logger.debug("Enter 1 to add another hotel, else enter 0: ");
		} while (sc.nextLine().equals("1"));
	}

	public int getTotalRate(Hotel hotel,Customer customer) throws NullPointerException{
		int numWeekdays=customer.getNumWeekdays();
		int numWeekends=customer.getNumWeekends();
		int weekdayRate = customer.getType() == CustomerType.REWARDS ? hotel.getRewardsWeekdayRate()
				: hotel.getRegularWeekdayRate();
		int weekendRate = customer.getType() == CustomerType.REWARDS ? hotel.getRewardsWeekendRate()
				: hotel.getRegularWeekendRate();
		int totalRate = weekdayRate * numWeekdays + weekendRate * numWeekends;
		return totalRate;
	}
	
	public void getCheapestBestRatedHotel() {
		Customer customer=getCustomerInput();
		Map<Hotel, Integer> hotelToTotalRateMap = hotels.stream().collect(Collectors.toMap(hotel -> hotel,
				hotel -> getTotalRate(hotel, customer)));
		Hotel cheapestBestRatedHotel = hotelToTotalRateMap.keySet().stream().min((hotel1, hotel2) -> {
			int rateDifference = hotelToTotalRateMap.get(hotel1) - hotelToTotalRateMap.get(hotel2);
			int ratingDifference = hotel1.getRating() - hotel2.getRating();
			return rateDifference == 0 ? -(ratingDifference) : rateDifference;
		}).orElse(null);
		try {
			logger.debug(cheapestBestRatedHotel.getName() + ", Rating: " + cheapestBestRatedHotel.getRating() + " and Total Rates: $"
					+ hotelToTotalRateMap.get(cheapestBestRatedHotel));
		} catch (NullPointerException e) {
			logger.debug("No hotel found");
		}
	}

	public Customer getCustomerInput() {
		logger.debug("Enter the customer_type and date range in format <customer_type>:<date1>, <date2>, <date3>\nEg.:  Regular: 09Mar2020, 10Mar2020, 11Mar2020");
		String customerInput = sc.nextLine();
		Matcher customerTypeMatcher=CUSTOMER_TYPE_PATTERN.matcher(customerInput);
		CustomerType customerType=CustomerType.REGULAR;
		if(customerTypeMatcher.find()) {
			if(customerTypeMatcher.group().equals("Rewards")) {
				customerType=CustomerType.REWARDS;
			}
		}
		Matcher dateMatcher = DAY_PATTERN.matcher(customerInput);
		DateTimeFormatter formatter=DateTimeFormatter.ofPattern("ddMMMuuuu");
		List<DayOfWeek> daysList = new ArrayList<DayOfWeek>();
		while (dateMatcher.find()) {
			daysList.add(LocalDate.parse(dateMatcher.group(),formatter).getDayOfWeek());
		}
		int numWeekends = (int) daysList.stream().filter(day -> WEEKENDS.contains(day)).count();
		return new Customer(daysList.size()-numWeekends, numWeekends, customerType);
	}
	
	public void getBestRatedHotel() {
		Customer customer=getCustomerInput();
		Hotel bestRatedHotel = hotels.stream().max((hotel1, hotel2) -> hotel1.getRating() - hotel2.getRating())
				.orElse(null);
		try {
			int totalRate=getTotalRate(bestRatedHotel, customer);
			logger.debug(bestRatedHotel.getName() + " & Total Rates $" + totalRate);
		} catch (NullPointerException e) {
			logger.debug("No hotel found");
		}
	}
	
	public static void main(String[] args) {
		HotelReservation hotelReservation = new HotelReservation();
		hotelReservation.addHotels();
		hotelReservation.getCheapestBestRatedHotel();
	}
}

class Customer{
	private int numWeekdays;
	private int numWeekends;
	private CustomerType type;
	
	public Customer(int numWeekdays, int numWeekends, CustomerType type) {
		super();
		this.numWeekdays = numWeekdays;
		this.numWeekends = numWeekends;
		this.type = type;
	}
	public CustomerType getType() {
		return type;
	}
	public void setType(CustomerType type) {
		this.type = type;
	}
	public int getNumWeekdays() {
		return numWeekdays;
	}
	public void setNumWeekdays(int numWeekdays) {
		this.numWeekdays = numWeekdays;
	}
	public int getNumWeekends() {
		return numWeekends;
	}
	public void setNumWeekends(int numWeekends) {
		this.numWeekends = numWeekends;
	}
	
}

class Hotel {
	private String name;
	private int regularWeekdayRate;
	private int regularWeekendRate;
	private int rewardsWeekdayRate;
	private int rewardsWeekendRate;
	private int rating;

	public Hotel(String name, int regularWeekdayRate, int regularWeekendRate, int rewardsWeekdayRate,
			int rewardsWeekendRate, int rating) {
		super();
		this.name = name;
		this.regularWeekdayRate = regularWeekdayRate;
		this.regularWeekendRate = regularWeekendRate;
		this.rewardsWeekdayRate = rewardsWeekdayRate;
		this.rewardsWeekendRate = rewardsWeekendRate;
		this.rating = rating;
	}
	
	public int getRewardsWeekdayRate() {
		return rewardsWeekdayRate;
	}

	public void setRewardsWeekdayRate(int rewardsWeekdayRate) {
		this.rewardsWeekdayRate = rewardsWeekdayRate;
	}

	public int getRewardsWeekendRate() {
		return rewardsWeekendRate;
	}

	public void setRewardsWeekendRate(int rewardsWeekendRate) {
		this.rewardsWeekendRate = rewardsWeekendRate;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRegularWeekdayRate() {
		return regularWeekdayRate;
	}

	public void setRegularWeekdayRate(int regularWeekdayRate) {
		this.regularWeekdayRate = regularWeekdayRate;
	}

	public int getRegularWeekendRate() {
		return regularWeekendRate;
	}

	public void setRegularWeekendRate(int regularWeekendRate) {
		this.regularWeekendRate = regularWeekendRate;
	}
	
	@Override
	public String toString() {
		return "Hotel [name=" + name + ", regularWeekdayRate=" + regularWeekdayRate + ", regularWeekendRate="
				+ regularWeekendRate + ", rewardsWeekdayRate=" + rewardsWeekdayRate + ", rewardsWeekendRate="
				+ rewardsWeekendRate + ", rating=" + rating + "]";
	}
}