package com.pms.pensionDisbursement.dao;

import com.pms.pensionDisbursement.model.ProcessPensionInput;

public interface PensionDisbursementServiceDao {

	public double getBankServiceCharge(String bankName);

	public int processPension(String token, ProcessPensionInput processPensionInput);

	public Boolean isSessionValid(String token);
}
