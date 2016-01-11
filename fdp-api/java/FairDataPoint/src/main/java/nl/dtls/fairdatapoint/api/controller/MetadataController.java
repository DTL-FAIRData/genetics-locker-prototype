/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller;



import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.dtls.fairdatapoint.api.controller.utils.HandleHttpHeadersUtils;
import nl.dtls.fairdatapoint.utils.MediaType;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.rio.RDFFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-18
 * @version 0.2
 */

@RestController
@Api(description = "FDP metadata")
@RequestMapping(value = "/")
public class MetadataController {  
    private final static Logger LOGGER 
            = LogManager.getLogger(MetadataController.class);
    @Autowired
    private FairMetaDataService fairMetaDataService;
    @ApiOperation(value = "FDP metadata")
    @RequestMapping(method = RequestMethod.GET,
            produces = {MediaType.TEXT_TURTLE, MediaType.APPLICATION_JSONLD}
    )
    public String getFDAMetaData(HttpServletRequest request,
                    HttpServletResponse response) { 
        String responseBody;
        LOGGER.info("Request to get FDP metadata");
        LOGGER.info("GET : " + request.getRequestURL());
        String contentType = request.getHeader(HttpHeaders.ACCEPT);
        RDFFormat requesetedContentType = HandleHttpHeadersUtils.
                requestedContentType(contentType); 
        HandleHttpHeadersUtils.setMandatoryResponseHeader(response);
        if (requesetedContentType == null) {
            responseBody = HandleHttpHeadersUtils.
                    setNotAcceptedResponseHeader(response, contentType);               
        }        
        else {
            try {
            responseBody = fairMetaDataService.retrieveFDPMetaData(
                    requesetedContentType);
            HandleHttpHeadersUtils.setSuccessResponseHeader(
                        responseBody, response, contentType);        
            } catch (FairMetadataServiceException ex) {            
                responseBody = HandleHttpHeadersUtils.setErrorResponseHeader(
                    response, ex);        
            }
        }        
        return responseBody;
    }
        
    @ApiOperation(value = "Catalog metadata")
    @RequestMapping(value = "/{catalogID:[^.]+}", method = RequestMethod.GET,
            produces = {MediaType.TEXT_TURTLE, MediaType.APPLICATION_JSONLD}
    )
    public String getCatalogMetaData(
            @PathVariable final String catalogID, HttpServletRequest request,
                    HttpServletResponse response) {
        LOGGER.info("Request to get CATALOG metadata {}", catalogID);
        LOGGER.info("GET : " + request.getRequestURL());
        String responseBody;
        String contentType = request.getHeader(HttpHeaders.ACCEPT);
        RDFFormat requesetedContentType = HandleHttpHeadersUtils.
                requestedContentType(contentType);         
        HandleHttpHeadersUtils.setMandatoryResponseHeader(response);
        if (requesetedContentType == null) {
            responseBody = HandleHttpHeadersUtils.
                    setNotAcceptedResponseHeader(response, contentType);               
        }        
        else {      
            try {
                responseBody = fairMetaDataService.
                        retrieveCatalogMetaData(catalogID, 
                                requesetedContentType);
                HandleHttpHeadersUtils.setSuccessResponseHeader(
                        responseBody, response, contentType);
            } catch (FairMetadataServiceException ex) {
                responseBody = HandleHttpHeadersUtils.setErrorResponseHeader(
                        response, ex);
            }
        }
        return responseBody;
    }
    
    @ApiOperation(value = "Dataset metadata")
    @RequestMapping(value = "/{catalogID}/{datasetID}", 
            method = RequestMethod.GET,
            produces = {MediaType.TEXT_TURTLE, MediaType.APPLICATION_JSONLD}
    )
    public String getDatasetMetaData(@PathVariable final String catalogID,
            @PathVariable final String datasetID, HttpServletRequest request,
                    HttpServletResponse response) {  
        LOGGER.info("Request to get DATASET metadata {}", catalogID);
        LOGGER.info("GET : " + request.getRequestURL());
        String responseBody;
        String contentType = request.getHeader(HttpHeaders.ACCEPT);
        RDFFormat requesetedContentType = HandleHttpHeadersUtils.
                requestedContentType(contentType);        
        HandleHttpHeadersUtils.setMandatoryResponseHeader(response);
        if (requesetedContentType == null) {
            responseBody = HandleHttpHeadersUtils.
                    setNotAcceptedResponseHeader(response, contentType);               
        }        
        else {      
            try {
                responseBody = fairMetaDataService.
                        retrieveDatasetMetaData(catalogID, datasetID, 
                                requesetedContentType);
                HandleHttpHeadersUtils.setSuccessResponseHeader(
                        responseBody, response, contentType);
            } catch (FairMetadataServiceException ex) {
                responseBody = HandleHttpHeadersUtils.setErrorResponseHeader(
                        response, ex);
            }
        }
        return responseBody;
    }
    
}
