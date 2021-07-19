package com.pms.pensionDisbursement.restClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.pms.pensionDisbursement.model.AuthResponse;

/**
 * To access Authorization Microservice
 */
@FeignClient(name = "authorization-service", url = "http://pmsauthservice-env.eba-2fp2sbyw.ap-south-1.elasticbeanstalk.com")	//9095
public interface AuthClient {

	@GetMapping("/validate")
	public AuthResponse getValidity(@RequestHeader("Authorization") String token);

}
