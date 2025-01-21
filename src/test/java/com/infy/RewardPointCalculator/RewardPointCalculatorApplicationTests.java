package com.infy.RewardPointCalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.RewardPointCalculator.Util.RewardPointCalculator;

@SpringBootTest
class RewardPointCalculatorApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
    public void testCalculatePoints_AmountLessThan50() {
        double amount = 30;
        int expectedPoints = 0;  // No points for amounts <= 50
        int actualPoints = RewardPointCalculator.calculatePoints(amount);
        assertEquals(expectedPoints, actualPoints, "Points should be 0 for amounts less than or equal to 50");
    }

    @Test
    public void testCalculatePoints_AmountBetween51And100() {
        double amount = 75;
        int expectedPoints = 25;  
        int actualPoints = RewardPointCalculator.calculatePoints(amount);
        assertEquals(expectedPoints, actualPoints, "Points should be calculated as (amount - 50) for amounts between 51 and 100");
    }

    @Test
    public void testCalculatePoints_AmountEqualTo50() {
        double amount = 50;
        int expectedPoints = 0;  // No points for exactly 50
        int actualPoints = RewardPointCalculator.calculatePoints(amount);
        assertEquals(expectedPoints, actualPoints, "Points should be 0 for amounts exactly equal to 50");
    }

    @Test
    public void testCalculatePoints_AmountBetween101And200() {
        double amount = 150;
        int expectedPoints = 150;  
        int actualPoints = RewardPointCalculator.calculatePoints(amount);
        assertEquals(expectedPoints, actualPoints, "Points should be calculated as (amount - 100) * 2 for amounts greater than 100");
    }


    @Test
    public void testCalculatePoints_AmountGreaterThan200() {
        double amount = 250;
        int expectedPoints = 350;  
        int actualPoints = RewardPointCalculator.calculatePoints(amount);
        assertEquals(expectedPoints, actualPoints, "Points should be calculated as (amount - 100) * 2 for amounts greater than 100");
    }

    @Test
    public void testCalculatePoints_AmountEqualTo100() {
        double amount = 100;
        int expectedPoints = 50;
        int actualPoints = RewardPointCalculator.calculatePoints(amount);
        assertEquals(expectedPoints, actualPoints, "Points should be 0 for exactly 100 amount");
    }
}
