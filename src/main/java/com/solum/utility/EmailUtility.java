package com.solum.utility;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class EmailUtility {

	public boolean validateEmails(List<String> emailList) {
		AtomicBoolean result = new AtomicBoolean(true);
		emailList.forEach((e) -> {
			boolean valid = EmailValidator.getInstance().isValid(e);
			if(!valid) {
				result.getAndSet(false);
			}
		});
		return result.get();
	}
}
