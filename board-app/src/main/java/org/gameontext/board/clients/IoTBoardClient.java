/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.gameontext.board.clients;

import java.io.IOException;
import java.util.logging.Level;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gameontext.board.Log;
import org.gameontext.board.models.devices.BoardControl;
import org.gameontext.board.models.devices.DeviceData;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A wrapped/encapsulation of outbound REST requests to the player service.
 * <p>
 * The URL for the player service is injected via CDI: {@code <jndiEntry />}
 * elements defined in server.xml maps the environment variable to the JNDI
 * value.
 * </p>
 * <p>
 * CDI will create this (the {@code IoTBoardClient} as an application scoped bean.
 * This bean will be created when the application starts, and can be injected
 * into other CDI-managed beans for as long as the application is valid.
 * </p>
 *
 * @see ApplicationScoped
 */
@ApplicationScoped
public class IoTBoardClient {
    /**
     * The player URL injected from JNDI via CDI.
     *
     * @see {@code playerUrl} in
     *      {@code /iotboard-wlpcfg/servers/iotboard/server.xml}
     */
    @Resource(lookup = "iotBoardUrl")
    String iotBoardLocation;
    
    /**
     * Register a room with the IoT board
     * 
     * @param registration room/site registration to process
     */
    public void register(String gameonId, String siteId) {
        String endpoint = iotBoardLocation + "/v1/control";
        System.out.println("Sending data to : " + endpoint);
        
        BoardControl control = new BoardControl(gameonId, siteId);
        DeviceData devdata = new DeviceData("reg", true);
        control.setData(devdata);
        System.out.println("Data : " + control);
        
        try {
            HttpClient client = HttpClientBuilder.create().build();
    
            HttpPost hg = new HttpPost(endpoint);
            
            ObjectMapper om = new ObjectMapper();
            String data = om.writeValueAsString(control);
            StringEntity entity = new StringEntity(data);
            entity.setContentType("application/json");
            hg.setEntity(entity);
            
    
            Log.log(Level.FINEST, this, "Building web target: {0}", hg.getURI().toString());
    
            // Make GET request using the specified target, get result as a
            // string containing JSON
            HttpResponse r = client.execute(hg);
            r.getStatusLine().getStatusCode();
    
            Log.log(Level.FINER, this, "Response from IoT board", r.getStatusLine());


        } catch (HttpResponseException hre) {
            Log.log(Level.FINEST, this, "Error communicating with IoT board service: {0} {1}", hre.getStatusCode(), hre.getMessage());
            throw new WebApplicationException("Error communicating with IoT board service", Response.Status.INTERNAL_SERVER_ERROR);
        } catch (WebApplicationException wae) {
            Log.log(Level.FINEST, this, "Error processing response: {0}", wae.getResponse());
            throw wae;
        } catch ( IOException e ) {
            Log.log(Level.FINEST, this, "Unexpected exception from IoT board: {0}", e);
            e.printStackTrace();
            throw new WebApplicationException("Error communicating with IoT board service", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
