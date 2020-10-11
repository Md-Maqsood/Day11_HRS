package com.BridgeLabs.hotelReservationSystem;

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

public class HotelReservation {
	private static final Logger logger = LogManager.getLogger(HotelReservation.class);
	static Scanner sc = new Scanner(System.in);
	public List<Hotel> hotels;
	private static final Pattern DAY_PATTERN = Pattern.compile("\\([a-z]{3,4}\\)");
	private static final List<String> WEEKENDS = Arrays.asList(new String[] { "(sat)", "(sun)" });

	public HotelReservation() {
		this.hotels = new ArrayList<Hotel>();
	}

	/**
	 * uc9
	 */
	public void addHotels() {
		do {
			logger.debug("Enter the hotel details in given order -\nName:\nWeekday Rate for Regular Customer:\nWeekend Rate for Regular Customer:\nWeekday Rate for Rewards Customer:\nWeekend Rate for Rewards Customer:\nRating:");
			hotels.add(new Hotel(sc.nextLine(), Integer.parseInt(sc.nextLine()), Integer.parseInt(sc.nextLine()),
					Integer.parseInt(sc.nextLine()), Integer.parseInt(sc.nextLine()), Integer.parseInt(sc.nextLine())));
			logger.debug("Enter 1 to add another hotel, else enter 0: ");
		} while (sc.nextLine().equals("1"));
	}

	/**
	 * uc6
	 */
	public void getCheapestBestRatedHotel() {
		Customer customer=getCustomerInput();
		int numWeekdays=customer.getNumWeekdays();
		int numWeekends=customer.getNumWeekends();
		Map<Hotel, Integer> hotelToTotalRateMap = hotels.stream().collect(Collectors.toMap(hotel -> hotel,
				hotel -> hotel.getRegularWeekendRate() * numWeekends + hotel.getRegularWeekdayRate() * numWeekdays));
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
	
	/**
	 * uc10
	 */
	public void getCheapestBestRatedHotelForRewards() {
		Customer customer=getCustomerInput();
		int numWeekdays=customer.getNumWeekdays();
		int numWeekends=customer.getNumWeekends();
		Hotel cheapestBestRatedHotel=null;
		Integer minTotalRate=null;
		for(Hotel hotel: hotels) {
			Integer totalRate = hotel.getRewardsWeekdayRate() * numWeekdays
					+ hotel.getRewardsWeekendRate() * numWeekends;
			try {
				if (minTotalRate.compareTo(totalRate) > 0) {
					cheapestBestRatedHotel = hotel;
					minTotalRate = totalRate;
				} else if (minTotalRate.compareTo(totalRate) == 0) {
					if (hotel.getRating() > cheapestBestRatedHotel.getRating()) {
						cheapestBestRatedHotel = hotel;
						minTotalRate = totalRate;
					}
				}
			}catch (NullPointerException e) {
				cheapestBestRatedHotel=hotel;
				minTotalRate=totalRate;
			}
		}
		try {
			logger.debug(cheapestBestRatedHotel.getName() + ", Rating: "+cheapestBestRatedHotel.getRating()+" and Total Rates: $" + minTotalRate);
		} catch (NullPointerException e) {
			logger.debug("No hotel found");
		}
	}
	
	/**
	 * uc10
	 * Method takes date range input from customer and returns Customer object
	 * @return
	 */
	public Customer getCustomerInput() {
		logger.debug("Enter the date range in format <date1>, <date2>, <date3>\nEg.:  16Mar2020(mon), 17Mar2020(tues), 18Mar2020(wed)");
		String customerInput = sc.nextLine();
		Matcher dayMatcher = DAY_PATTERN.matcher(customerInput);
		List<String> daysList = new ArrayList<String>();
		while (dayMatcher.find()) {
			daysList.add(dayMatcher.group());
		}
		int numWeekends = (int) daysList.stream().filter(day -> WEEKENDS.contains(day)).count();
		return new Customer(daysList.size()-numWeekends, numWeekends);
	}
	
	/**
	 * uc7
	 */
	public void getBestRatedHotel() {
		Customer customer=getCustomerInput();
		int numWeekdays=customer.getNumWeekdays();
		int numWeekends=customer.getNumWeekends();
		Hotel bestRatedHotel = hotels.stream().max((hotel1, hotel2) -> hotel1.getRating() - hotel2.getRating())
				.orElse(null);
		try {
			int totalRate = bestRatedHotel.getRegularWeekdayRate() * numWeekdays
					+ bestRatedHotel.getRegularWeekendRate() * numWeekends;
			logger.debug(bestRatedHotel.getName() + " & Total Rates $" + totalRate);
		} catch (NullPointerException e) {
			logger.debug("No hotel found");
		}
	}
	
	public static void main(String[] args) {
		HotelReservation hotelReservation = new HotelReservation();
		hotelReservation.addHotels();
		hotelReservation.getCheapestBestRatedHotelForRewards();
	}
}

class Customer{
	private int numWeekdays;
	private int numWeekends;
	public Customer(int numWeekdays, int numWeekends) {
		super();
		this.numWeekdays = numWeekdays;
		this.numWeekends = numWeekends;
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