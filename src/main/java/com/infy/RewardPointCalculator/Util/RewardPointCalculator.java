package com.infy.RewardPointCalculator.Util;

public class RewardPointCalculator {

    public static int calculatePoints(double amount) {
        int points = 0;

        if (amount > 100) {
            points += (int)((amount - 100) * 2);  // 2 points per dollar for amount > 100
            amount = 100;
        }
        if (amount > 50) {
            points += (int)((amount - 50) * 1);  // 1 point per dollar for amount between 50 and 100
        }

        return points;
    }
}
