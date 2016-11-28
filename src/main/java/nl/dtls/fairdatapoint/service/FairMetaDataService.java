/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-23
 * @version 0.2
 */
public interface FairMetaDataService {   
    /**
     * Get FDP server metadata
     * @param format RDFFormat serialization formats
     * @return  String object  
     * @throws FairMetadataServiceException  
     */
    String retrieveFDPMetaData(RDFFormat format) 
            throws FairMetadataServiceException;  

    /**
     * Get catalog metadata
     * @param catalogID Unique catalog ID
     * @param format RDFFormat serialization formats
     * @return  String object
     * @throws FairMetadataServiceException
     */
    String retrieveCatalogMetaData
        (String catalogID, RDFFormat format) throws FairMetadataServiceException;

    /**
     * Get dataset metadata
     * @param catalogID
     * @param datasetID Unique dataset ID
     * @param format RDFFormat serialization formats
     * @return  String object
     * @throws FairMetadataServiceException
     */
    String retrieveDatasetMetaData
        (String catalogID, String datasetID, RDFFormat format) 
                throws FairMetadataServiceException;
        
    /**
     * Get datarecord metadata
     * @param dataRecordID Unique dataRecordID ID
     * @param format RDFFormat serialization formats
     * @return  String object
     * @throws FairMetadataServiceException
     */
    String retrieveDataRecordMetaData
        (String dataRecordID, RDFFormat format) 
                throws FairMetadataServiceException;
        
    /**
     * Get dataset distribution
     * 
     * @param catalogID
     * @param datasetID
     * @param distributionID
     * @param format
     * @return  String object  
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException  
     */
    String retrieveDatasetDistribution(String catalogID, 
            String datasetID, String distributionID, RDFFormat format) 
            throws FairMetadataServiceException;      
        
}
