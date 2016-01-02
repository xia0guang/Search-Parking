package com.xiaoguang.searchparking.yelp;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Code sample for accessing the Yelp API V2.
 * <p/>
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 * <p/>
 * <p/>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 */
public class YelpAPI {

    private static final String API_HOST = "api.yelp.com";
    private static final String DEFAULT_TERM = "parking lot";
    private static final String DEFAULT_LOCATION = "San Francisco, CA";
    private static final int SEARCH_LIMIT = 20;
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    /*
     * Update OAuth credentials below from the Yelp Developers API site:
     * http://www.yelp.com/developers/getting_started/api_access
     */
    private static final String CONSUMER_KEY = "LO13tP9jzDY6A1Ehhmb8WA";
    private static final String CONSUMER_SECRET = "vokmIqJZhgyKS497cC0GuOhb7kQ";
    private static final String TOKEN = "A3jXyRZ41gS1NyXFPf_3IRUjdD6AqK9C";
    private static final String TOKEN_SECRET = "tNcm7T7z33HK_Ob_tXGB0VG21yc";
    private static final String TAG = "YelpAPI";

    OAuthService service;
    Token accessToken;

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * @param consumerKey    Consumer key
     * @param consumerSecret Consumer secret
     * @param token          Token
     * @param tokenSecret    Token secret
     */
    public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey)
                        .apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    public YelpAPI() {
        this(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
    }

    /**
     * Creates and sends a request to the Search API by term and location.
     * <p/>
     * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
     * for more info.
     *
     * @param term     <tt>String</tt> of the search term to be queried
     * @param location <tt>String</tt> of the location
     * @return <tt>String</tt> JSON Response
     */
    public String searchForBusinessesByLocation(String term, String location) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        return sendRequestAndGetResponse(request);
    }

    public String searchForBusinessesByLatLng(String term, LatLng latLng, String location, int radius) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("radius_filter", String.valueOf(radius));
        request.addQuerystringParameter("cll", latLng.latitude + "," + latLng.longitude);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and sends a request to the Business API by business ID.
     * <p/>
     * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
     * for more info.
     *
     * @param businessID <tt>String</tt> business ID of the requested business
     * @return <tt>String</tt> JSON Response
     */
    public String searchByBusinessId(String businessID) {
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://" + API_HOST + path);
        return request;
    }

    /**
     * Sends an {@link OAuthRequest} and returns the {@link Response} body.
     *
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    public JSONArray queryAPI(LatLng latLng, String city) {
        String searchResponseJSON = searchForBusinessesByLatLng(DEFAULT_TERM, latLng, city, 16);
        try {
            JSONObject response = new JSONObject(searchResponseJSON);
            return response.getJSONArray("businesses");
        } catch (JSONException e) {
            Log.d(TAG, "Error: could not parse JSON response:");
            Log.d(TAG,searchResponseJSON);
            e.printStackTrace();
        }
        return null;
    }

}
