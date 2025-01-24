package com.digital.school.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "cmi")
@Data
public class CMIConfig {
    private String merchantId;
    private String storeKey;
    private String apiUrl;
    private String successUrl;
    private String failureUrl;
    private String currency = "504"; // Code ISO pour MAD
    private String language = "fr"; // ou "ar" pour l'arabe
    private boolean testMode = false;
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getStoreKey() {
		return storeKey;
	}
	public void setStoreKey(String storeKey) {
		this.storeKey = storeKey;
	}
	public String getApiUrl() {
		return apiUrl;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	public String getSuccessUrl() {
		return successUrl;
	}
	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}
	public String getFailureUrl() {
		return failureUrl;
	}
	public void setFailureUrl(String failureUrl) {
		this.failureUrl = failureUrl;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public boolean isTestMode() {
		return testMode;
	}
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
    
    
    
}
