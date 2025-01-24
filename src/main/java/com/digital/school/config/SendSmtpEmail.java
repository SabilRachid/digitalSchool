package com.digital.school.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SendSmtpEmail {
    private String sender;
    private List<EmailTo> to;
    private String subject;
    private String htmlContent;
    private String textContent;
    private List<String> tags;
    private Map<String, String> headers;
    
    @Data
    public static class EmailTo {
        private String email;
        private String name;
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public EmailTo(String email, String name) {
			super();
			this.email = email;
			this.name = name;
		}
		public EmailTo() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		
        
        
    }
    
    
    

	public SendSmtpEmail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public List<EmailTo> getTo() {
		return to;
	}

	public void setTo(List<EmailTo> to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
    
    
    
}
