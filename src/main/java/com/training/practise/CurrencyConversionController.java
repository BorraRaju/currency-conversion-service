package com.training.practise;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.training.practise.bean.CurrencyConversionBean;

@RestController
public class CurrencyConversionController {

	private Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);
	
	//@Autowired
	//Environment environment;
	
	@Autowired
	private CurrencyExchangeServiceProxy proxy;
	
	
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {
		
		
		Map<String , String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		
		ResponseEntity<CurrencyConversionBean> forEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
				CurrencyConversionBean.class, uriVariables);
		CurrencyConversionBean response = forEntity.getBody();
		
		logger.info("CurrencyConversionController response{} : ",response);
		
		return new CurrencyConversionBean(response.getId(), response.getFrom(), 
				response.getTo(), response.getConversionMultiple(), quantity, 
				quantity.multiply(response.getConversionMultiple()),
				response.getPort());
		//Integer.parseInt(environment.getProperty("local.server.port"))
		
		//return new CurrencyConversionBean(1000L, from, to, BigDecimal.valueOf(65), BigDecimal.TEN, BigDecimal.valueOf(6500),8100);
		
	}
	
	
	
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {
		
		CurrencyConversionBean response = proxy.retriveExchangeValues(from, to);
		
		logger.info("CurrencyConversionController response{} : ",response);
		
		
		return new CurrencyConversionBean(response.getId(), response.getFrom(), 
				response.getTo(), response.getConversionMultiple(), quantity, 
				quantity.multiply(response.getConversionMultiple()),
				response.getPort());
	}
	
	
}
