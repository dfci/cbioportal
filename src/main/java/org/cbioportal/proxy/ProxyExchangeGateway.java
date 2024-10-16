package org.cbioportal.proxy;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/proxy")
@ConfigurationProperties(prefix = "proxy")
@ConfigurationPropertiesScan 
public class ProxyExchangeGateway {
	private Map<String, String> routes;
	
	@Value("${oncokb.token:}")
    private String oncokbToken;
	
	@Value("${server.servlet.context-path:}")
    private String contextPath;
	
	@Value("${oncokb.public_api.url:https://public.api.oncokb.org/api/v1}")
    private String oncokbApiUrl;
	
	@Value("${show.oncokb:false}")
    private Boolean showOncokb;
	
	
	@RequestMapping("/**")
	public ResponseEntity<?> proxy(ProxyExchange<byte[]> proxy, @RequestBody(required = false) String body, HttpServletRequest request, HttpMethod method) throws Exception {
		String service = proxy.path("/proxy/").toString();
		
		
		int endPosition = service.indexOf("/");
		if(endPosition >= 0) {
			service = service.substring(0, endPosition);
		}
		
		if(!routes.containsKey(service)) {
			throw new UnknownServiceException();
		}
		
		String updatedPath = proxy.path("/proxy/" + service);
		if(method == HttpMethod.DELETE) {
			return proxy.uri(routes.get(service) + updatedPath).body(body).delete();
		}
		else if(method == HttpMethod.GET) {
			return proxy.uri(routes.get(service) + updatedPath).body(body).get();
		}
		else if(method == HttpMethod.PATCH) {
			return proxy.uri(routes.get(service) + updatedPath).body(body).get();
		}
		else if(method == HttpMethod.POST) {
			return proxy.uri(routes.get(service) + updatedPath).body(body).post();
		}
		else if(method == HttpMethod.PUT) {
			return proxy.uri(routes.get(service) + updatedPath).body(body).put();
		}
		
		throw new UnknownServiceException();
	}
	
	// OnkoKB Specific Proxy
	@RequestMapping("/A8F74CD7851BDEE8DCD2E86AB4E2A711/**")
    public String proxyEncodedOncokb(
        @RequestBody(required = false) String body,
        HttpMethod method, 
        HttpServletRequest request
    ) throws URISyntaxException, UnsupportedEncodingException {
        // make sure that the custom Proxy User Agreement header exists
        String proxyUserAgreement = request.getHeader("X-Proxy-User-Agreement");
        if (proxyUserAgreement == null || !proxyUserAgreement.equals(
            "I/We do NOT use this obfuscated proxy to programmatically obtain private OncoKB data. " +
            "I/We know that I/we should get a valid data access token by registering at https://www.oncokb.org/account/register."
        )) {
            throw new OncoKBProxyUserAgreementException();
        }
        
        String decodedBody = body == null ? null: Monkifier.decodeBase64(body);
        String encodedPath = request.getRequestURI().replaceFirst(contextPath + "/proxy/A8F74CD7851BDEE8DCD2E86AB4E2A711/", "");
        String decodedPath = Monkifier.decodeBase64(encodedPath);
        String decodedQueryString = Monkifier.decodeQueryString(request);
        
        String response = exchangeOncokbData(
            decodedBody,
            decodedPath,
            decodedQueryString,
            method,
            getOncokbHeaders(request)
        );
        
        return "\"" + Monkifier.encodeBase64(response) + "\"";
    }
	
	private String exchangeOncokbData(
	        String body,
	        String pathInfo,
	        String queryString,
	        HttpMethod method,
	        HttpHeaders httpHeaders
	    ) throws URISyntaxException {
	        return exchangeData(
	            body,
	            buildUri(this.oncokbApiUrl + pathInfo, queryString),
	            method,
	            httpHeaders,
	            String.class
	        ).getBody();
	    }
	
	private <T> ResponseEntity<T> exchangeData(String body, URI uri, HttpMethod method, HttpHeaders httpHeaders, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate.exchange(uri, method, new HttpEntity<>(body, httpHeaders), responseType);
    }
	
	private HttpHeaders getOncokbHeaders(HttpServletRequest request, String token) {
        String oncokbToken = token == null ? this.oncokbToken : token;

        if (!this.showOncokb) {
            throw new OncoKBServiceIsDisabledException();
        }

        HttpHeaders httpHeaders = initHeaders(request);
        
        if (!StringUtils.isEmpty(oncokbToken)) {
            httpHeaders.add("Authorization", "Bearer " + oncokbToken);
        }
        
        return httpHeaders;
    }
	
	private HttpHeaders initHeaders(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        
        String contentType = request.getHeader("Content-Type");
        if (contentType != null) {
            httpHeaders.setContentType(MediaType.valueOf(contentType));
        }
        
        return httpHeaders;
    }
	
	@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "OncoKB service is disabled")
    public class OncoKBServiceIsDisabledException extends RuntimeException {
    }
	
	private HttpHeaders getOncokbHeaders(HttpServletRequest request) {
        return this.getOncokbHeaders(request, null);
    }
	
	private URI buildUri(String path, String queryString) throws URISyntaxException {
        return new URI(path + (queryString == null ? "" : "?" + queryString));
    }
	
	public Map<String, String> getRoutes() {
		return routes;
	}

	public void setRoutes(Map<String, String> routes) {
		this.routes = routes;
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Fair Usage Agreement is missing")
    public class OncoKBProxyUserAgreementException extends RuntimeException {
    }
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unknown Service")
	public class UnknownServiceException extends RuntimeException {
	}
}
