package com.tekpub.mappers;

import android.database.Cursor;

/**
 * Contract for cursor mapping. This is used when we need to map 
 * from a Database Cursor into a POJO. 
 * @author donnfelker
 *
 * @param <TOutput>
 */
public interface ICursorMapper<TOutput> extends IMapper<Cursor, TOutput> {

}
