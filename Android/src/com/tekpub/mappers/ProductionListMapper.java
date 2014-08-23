package com.tekpub.mappers;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tekpub.models.Production;

/**
 * Maps from a large chunk of JSON into a list of productions. 
 * @author Donn Felker
 *
 */
public class ProductionListMapper implements IProductionListMapper {

	@Override
	public List<Production> MapFrom(String input) {
		Type typeOfDest = new TypeToken<List<Production>>(){}.getType();
		
		Gson gson = new Gson(); 
		return gson.fromJson(input, typeOfDest); 
	}

}
