package com.infy.RewardPointCalculator.Util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserUtil {

	public static String getLoggedInUsername()
	{
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
		if(authentication !=null && authentication.isAuthenticated()) {
			Object principal=authentication.getPrincipal();
			
			if(principal instanceof UserDetails)
			{
				return ((UserDetails) principal).getUsername();
			}else {
				return principal.toString();
			}
		}
		return null;
	}
}
