package com.tekpub.mappers;

/**
 * Generic mapper interface. 
 * @author Donn Felker
 *
 * @param <TInput>
 * @param <TOutput>
 */
public interface IMapper<TInput, TOutput> {
	
	/**
	 * Maps from a provided to input to a given output type.
	 * @param input
	 * @return
	 */
	TOutput MapFrom(TInput input); 
}
