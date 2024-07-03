/*
 * Copyright (c) 2002-2024, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.verifybackurl.modules.rest.rs;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.plugins.verifybackurl.service.AuthorizedUrlService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

/**
 * 
 * ServiceNameRest
 *
 */
@Path( RestConstants.BASE_PATH + Constants.API_PATH + Constants.VERSION_PATH + Constants.API_PATH_SERVICE_NAME )
public class ServiceNameRest
{
    private static final int VERSION_1 = 1;

    /**
     * Gets service name by url
     * 
     * @param strUrl
     * @return json response
     */
    @GET
    @Path ( StringUtils.EMPTY )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getServicenameByUrl( @PathParam( Constants.VERSION ) Integer nVersion , @QueryParam( Constants.API_QUERY_PARAM_URL ) String strUrl)
    {
        if ( nVersion == VERSION_1 )
        {
            return getServicenameV1( strUrl );
        }
        
        AppLogService.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }

    private Response getServicenameV1( String strUrl )
    {
        if( StringUtils.isEmpty( strUrl ) )
        {
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( getResponse( Response.Status.BAD_REQUEST.getReasonPhrase( ), Constants.API_EMPTY_URL_MESSAGE ) )
                    .build( );
        }
        
        try
        {
            String urlDecode = URLDecoder.decode( strUrl, "UTF-8" );
            String strServicename = AuthorizedUrlService.getInstance( ).getName( urlDecode );
   
            if ( StringUtils.isNotEmpty( strServicename ) )
            {
                return Response.ok( getResponse( Response.Status.OK.getReasonPhrase( ), strServicename ) )
                        .build( );
            }
   
        } catch ( UnsupportedEncodingException e )
        {
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( getResponse( Response.Status.BAD_REQUEST.getReasonPhrase( ), e.getMessage( ) ) )
                    .build( );
        }
   
        return Response.status( Response.Status.NOT_FOUND )
                .entity( getResponse( Response.Status.NOT_FOUND.getReasonPhrase( ), Response.Status.NOT_FOUND.getReasonPhrase( ) ) )
                .build( );
    }

    @SuppressWarnings( "unchecked" )
    private JSONObject getResponse( String strStatus, String strMessage )
    {
        JSONObject json = new JSONObject( );
        json.put( Constants.API_JSON_STATUS, strStatus );
        json.put( Constants.API_JSON_MESSAGE, strMessage );

        return json;
    }
}
