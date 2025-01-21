package com.infy.RewardPointCalculator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
		info=@Info(
				contact=@Contact(
						name="Rifat",
						email="rifat.bano@infosys.com"
						),
				description="Api documentation for Reward Calculation",
				title="OpenApi specification - RewardCalci",
				version="1.0",
				termsOfService="terms of service"
				)
		)
@SecurityScheme(
		name="bearerAuth",
		description="JWT auth description",
		scheme="bearer",
		type=SecuritySchemeType.HTTP,
		bearerFormat="JWT",
		in=SecuritySchemeIn.HEADER
		)
public class OpenApiConfig {

}
