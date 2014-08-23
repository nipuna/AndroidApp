package com.tekpub.http;

import java.io.IOException;
import java.util.List;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;

import com.tekpub.exception.TekPubApiException;
import com.tekpub.exception.TekPubFileNotFoundException;
import com.tekpub.exception.TekPubFreeSpaceException;
import com.tekpub.exception.TekPubLoginException;
import com.tekpub.messaging.IProgressNotifier;
import com.tekpub.models.LoginResult;
import com.tekpub.models.Production;
import com.tekpub.models.VideoResult;

public interface ITekPubApi {

	public abstract List<Production> getProductions() throws TekPubApiException, IllegalStateException, IOException, OperationCanceledException, AuthenticatorException;
	public abstract LoginResult getAuthToken(String email, String password) throws IOException, TekPubLoginException; 
	public abstract void downloadEpisode(String episodeId, Context context, IProgressNotifier progress) throws TekPubFreeSpaceException, TekPubFileNotFoundException, IOException; 
	public abstract VideoResult getVideo(String slug, int number) throws TekPubApiException, OperationCanceledException, AuthenticatorException, IOException ;
}