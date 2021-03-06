/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.HashMap;
import java.util.Map;
import nl.dtls.fairdatapoint.service.MyconsentServiceException;
import org.apache.logging.log4j.LogManager;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer for communicating with myconsent api
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2017-08-28
 * @version 0.1
 */
@Service("myconsentService")
public class MyconsentService {
    
    private final static org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(MyconsentService.class);
    
    @Autowired
    @Qualifier("myconsentApiUrl")
    private String apiUrl;     
    @Autowired
    @Qualifier("myconsentResearcherToken")
    private String researcherToken;     
    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();
    
    /**
     * Get status of data access request status
     * 
     * @param url  Data access request URL (Required) 
     * @return  Returns status of data access status
     * 
     * @throws MyconsentServiceException    Exception is thrown when GET request is invalid 
     * @throws IllegalArgumentException Exception is thrown when response status is not 200 
     */
    
    public boolean getRequestStatus(String url) throws MyconsentServiceException, 
            IllegalArgumentException {
        boolean status = false;
        try {
            HttpResponse<String> response = Unirest.get(url)
                    .header("accept", "application/json")
                    .header("Authorization", ("Bearer " + researcherToken))
                    .asString();
            if(response.getStatus() != 200) {
                throw (new IllegalArgumentException("Not valid request url"));
            }
            
            Gson gson = new Gson(); 
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            status = jsonObject.get("request").getAsJsonObject().get("status").getAsBoolean();
        } catch (UnirestException ex) {
            String msg = "Error querying myconsent API. " + ex.getMessage();
            LOGGER.error(msg);
            throw (new MyconsentServiceException(msg));
        }
        return status;
    } 
    
    /**
     * Create data access request token on myconsent system and return request token
     * 
     * @param dsid  Data source unique token (Required)
     * @param description   Data access request description
     * @param foreignKey    Local reference of data record 
     * @param studyId Study token (Required) 
     * @return  Returns data access request token
     * 
     * @throws MyconsentServiceException    Exception is thrown when GET request is invalid 
     * @throws IllegalArgumentException Exception is thrown when response status is not 200 
     */
    public IRI createDataAccessRequest(String dsid, String studyId, String foreignKey, 
            String description, String reqTemplateId) throws MyconsentServiceException, IllegalArgumentException {
        IRI requestUrl = null;
        Map<String, String> data = new HashMap<>();
        data.put("study_id", studyId);
        data.put("record_id", foreignKey);
        data.put("data_source_id", dsid);
        data.put("request_body", description);
        data.put("request_template_id", reqTemplateId);
        Gson gson = new Gson(); 
        String jsonBody = gson.toJson(data); 
        String url = apiUrl + "request";
        try {
            HttpResponse<String> response = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("Authorization", ("Bearer " + researcherToken))
                    .body(jsonBody)
                    .asString();
            if(response.getStatus() != 200) {
                throw (new IllegalArgumentException("Not valid request"));
            }
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            String requestId = jsonObject.get("request_id").getAsString();
            requestUrl = valueFactory.createIRI(url + "/" + requestId);
        } catch (UnirestException ex) {
            String msg = "Error querying myconsent API. " + ex.getMessage();
            LOGGER.error(msg);
            throw (new MyconsentServiceException(msg));
        }
        return requestUrl;
    }
    
    /**
     * Create data-source entry on myconsent system and return dsid
     * 
     * @param name  Data source name (Required)
     * @param description   Data source description
     * @param email Data source email (Required) 
     * @return  Returns data source token (dsid)
     * 
     * @throws MyconsentServiceException    Exception is thrown when POST request is invalid 
     * @throws IllegalArgumentException Exception is thrown when response status is not 200 
     */
    public IRI createDataSource(String name, String description, String email) 
            throws MyconsentServiceException, IllegalArgumentException {
        IRI dsUri = null;
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("researcher_email", email);
        Gson gson = new Gson(); 
        String jsonBody = gson.toJson(data); 
        String url = apiUrl + "data-source";
        try {
            HttpResponse<String> response = Unirest.post(url)
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("Authorization", ("Bearer " + researcherToken))
                    .body(jsonBody)
                    .asString();
            if(response.getStatus() != 200) {
                throw (new IllegalArgumentException("Not valid request"));
            }
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            String dsid = jsonObject.get("data").getAsJsonObject().get("id").getAsString();
            dsUri = valueFactory.createIRI(url + "/" + dsid);
        } catch (UnirestException ex) {
            String msg = "Error querying myconsent API. " + ex.getMessage();
            LOGGER.error(msg);
            throw (new MyconsentServiceException(msg));
        }
        return dsUri;
    } 
    
    /**
     * Create data record entry on myconsent system and return token which use to link data record
     * to user account
     * 
     * @param dsid  Data source unique token (Required)
     * @param foreignKey   Local reference of data record 
     * @return  Returns token
     * 
     * @throws MyconsentServiceException    Exception is thrown when POST request is invalid 
     * @throws IllegalArgumentException Exception is thrown when response status is not 200 
     */
    public String createDataRecord(String dsid, String foreignKey) 
            throws MyconsentServiceException, IllegalArgumentException {
        String token = null;
        Map<String, String> data = new HashMap<>();
        data.put("record_id", foreignKey);
        Gson gson = new Gson(); 
        String jsonBody = gson.toJson(data); 
        try {
            HttpResponse<String> response = Unirest.post(apiUrl + "data-source/" + dsid + "/record")
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("Authorization", ("Bearer " + researcherToken))
                    .body(jsonBody)
                    .asString();
            if(response.getStatus() != 200) {
                throw (new IllegalArgumentException("Not valid request"));
            }
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            token = jsonObject.get("token").getAsString();
        } catch (UnirestException ex) {
            String msg = "Error querying myconsent API. " + ex.getMessage();
            LOGGER.error(msg);
            throw (new MyconsentServiceException(msg));
        }
        return token;
    }
    
}
