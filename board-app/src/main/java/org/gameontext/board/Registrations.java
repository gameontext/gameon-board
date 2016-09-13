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
package org.gameontext.board;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gameontext.board.clients.IoTBoardClient;
import org.gameontext.board.kafka.GameOnEvent;
import org.gameontext.board.kafka.KafkaRxJavaObservable;
import org.gameontext.board.models.devices.Registration;
import org.gameontext.board.models.devices.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

import rx.Subscription;

/**
 * Registration management
 *
 */

@ApplicationScoped
public class Registrations {
    @Inject
    KafkaRxJavaObservable kafka;
    
    @Inject
    IoTBoardClient iotboard;
    
    private final ConcurrentMap<String, Registration> registrations = new ConcurrentHashMap<>();
    private Subscription subscription = null;
    
    public Collection<Registration> getRegistrations() {
        return registrations.values();
    }
    
    private void processEvent(GameOnEvent event) {
        System.out.println("Processing event : " + event.getKey());
        ObjectMapper mapper = new ObjectMapper();
        Value value;
        try {
            value = mapper.readValue(event.getValue(), Value.class);
            switch(value.getType()) {
                case "CREATE":
                    System.out.println("Creating new registration");
                    addRegistration(value);
                    iotboard.control(value.getSite().getOwner(), value.getSite().getId(), true);
                    break;
                case "DELETE":
                    System.out.println("Deleting registration");
                    String playerId = deleteRegistration(value);
                    if(playerId != null) {
                        iotboard.control(playerId, value.getSite().getId(), true);
                    }
                    break; 
                default:
                    System.out.println("WARN : unknown event type : " + value.getType());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    //TODO make thread safe and lock access to the sites under this registration
    private String deleteRegistration(Value value) {
        String playerId = value.getSite().getOwner();
        if(playerId == null) {
            System.out.println("WARN : missing player ID for delete operation, searching by site.");
            playerId = findPlayerBySite(value.getSite().getId());
            if(playerId == null) {
                System.out.println("WARN : Unable to find player ID using the site ID");
            }
            System.out.println("Deleting room registration for " + playerId);
        }
        Registration reg = registrations.get(playerId);
        if(reg != null) {
            reg.getSites().remove(value.getSite().getId());
            if(reg.getSites().isEmpty()) {
                registrations.remove(playerId);
                return playerId;
            }
        }
        return null;
    }
    
    private String findPlayerBySite(String siteId) {
        if(siteId == null) {
            return null;
        }
        for(Entry<String, Registration> entry : registrations.entrySet()) {
            for(String site : entry.getValue().getSites()) {
                if(site.equals(siteId)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
    
    //add a new site registration or update an existing one
    private Registration addRegistration(Value value) {
        Registration reg = new Registration();
        String playerId = value.getSite().getOwner();
        reg.setPlayer(playerId);
        Registration existing = registrations.putIfAbsent(playerId, reg);
        if(existing == null) {
            existing = reg;     //this was a new entry
        }
        existing.addSite(value.getSite().getId());
        return existing;
    }
    
    @PostConstruct
    public void init() {
        System.out.println("Rx Endpoint [" + this.hashCode() + "] Initializing rxjava based event monitor.");
        subscription = kafka.consume()
                // .filter(gameOnEvent -> gameOnEvent.getKey().equals("coffee"))
                // //test filter.. =)
                .subscribe(event -> processEvent(event));
        System.out.println("Rx Endpoint [" + this.hashCode() + "] RxJava observable init complete.");
    }
    
    @PreDestroy
    public void destroy() {
        if (this.subscription != null) {
            Subscription subscription = this.subscription;
            this.subscription = null;
            subscription.unsubscribe();
        }
    }
}
