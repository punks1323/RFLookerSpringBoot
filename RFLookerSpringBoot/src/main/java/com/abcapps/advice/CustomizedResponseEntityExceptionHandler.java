package com.abcapps.advice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.abcapps.dto.ErrorDetails;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
		List<String> errorList = new ArrayList<String>();
		for (ObjectError e : allErrors) {
			errorList.add(e.getDefaultMessage());
		}
		ErrorDetails errorDetails = new ErrorDetails(new Date(), "Validation Failed", errorList);
		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}