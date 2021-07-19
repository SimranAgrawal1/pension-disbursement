package com.pms.pensionDisbursement.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pms.pensionDisbursement.dao.PensionDisbursementServiceDao;
import com.pms.pensionDisbursement.model.Pensioner;
import com.pms.pensionDisbursement.model.ProcessPensionInput;
import com.pms.pensionDisbursement.restClients.AuthClient;
import com.pms.pensionDisbursement.restClients.PensionerDetailClient;

@Service
public class PensionDisbursementServiceDaoImpl implements PensionDisbursementServiceDao {
	private static Logger logger = LoggerFactory.getLogger(PensionDisbursementServiceDaoImpl.class);

	@Autowired
	PensionerDetailClient pensionerDetailClient;

	@Autowired
	AuthClient authClient;
	final int succesStatus=10;
	final int wrongStatus=21;
	
	private static final Map<String, Double> banks = createMap();

	/**
	 * This method initializes a list of banks with their service charges
	 * 
	 * @return List og banks with service charge
	 */
	private static Map<String, Double> createMap() {
		logger.info("START");

		Map<String, Double> tempBanks = new HashMap<>();
		tempBanks.put("SBI", 500.0);
		tempBanks.put("IOB", 500.0);
		tempBanks.put("BYTECARD", 550.0);
		tempBanks.put("PANNIER", 550.0);
		logger.info("END");

		return tempBanks;
	}

	/**
	 * This method retrieves service charge for a bank
	 * 
	 * @param bank name
	 * @return service charge
	 */

	@Override
	public double getBankServiceCharge(String bankName) {
		if (banks.containsKey(bankName.toUpperCase()))
			return banks.get(bankName.toUpperCase());
		else
			return 0;
	}

	/**
	 * This method verifies and initiates pension disbursement
	 * 
	 * @param String Token, ProcessPensionInput
	 * @return process code
	 */

	@Override
	public int processPension(String token, ProcessPensionInput processPensionInput) {
		logger.info("START");

		Pensioner pensioner = pensionerDetailClient.getPensionerDetailByAadhaar(token,
				processPensionInput.getAadhaarNumber());
		if (pensioner == null) {
			logger.info("END");

			return wrongStatus;
		}

		double bankServiceCharge = banks.get(pensioner.getBankName().toUpperCase());
		if ((processPensionInput.getPensionAmount().equals(pensioner.getPensionAmount()))
				&& (processPensionInput.getBankServiceCharge().equals(bankServiceCharge))) {
			pensioner.setBankServiceCharge(bankServiceCharge);
			if (pensionerDetailClient.logTransaction(token, pensioner)
					&& pensionerDetailClient.updatePensioner(token, pensioner)) {
				logger.info("END");

				return succesStatus;
			}
		}
		logger.info("END");

		return wrongStatus;
	}

	/**
	 * This method checks token validity
	 * 
	 * @param String token
	 * @return Boolean
	 */
	@Override
	public Boolean isSessionValid(String token) {
		logger.info("START");

		try {
			authClient.getValidity(token);
		} catch (Exception e) {
			logger.info("EXCEPTION");

			return false;
		}
		logger.info("END");

		return true;
	}

}
