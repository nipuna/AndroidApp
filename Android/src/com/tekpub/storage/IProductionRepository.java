package com.tekpub.storage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import com.tekpub.exception.TekPubApiException;
import com.tekpub.models.Production;

public interface IProductionRepository {
	List<Production> getProductions() throws TekPubApiException, IllegalStateException, IOException, URISyntaxException; 
	Production getProduction(String slug);
	Production getProduction(long mProductionRowId);
	void deleteAllProductions(); 
}
