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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
                    break;
                case "DELETE":
                    System.out.println("Deleting registration");
                    deleteRegistration(value);
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
    private void deleteRegistration(Value value) {
        String playerId = value.getSite().getOwner();
        if(playerId == null) {
            System.out.println("WARN : missing player ID for delete operation.");
            return;
        }
        Registration reg = registrations.get(playerId);
        if(reg != null) {
            reg.getSites().remove(value.getSite());
            if(reg.getSites().isEmpty()) {
                registrations.remove(playerId);
            }
        }
    }
    
    //add a new site registration or update an existing one
    private void addRegistration(Value value) {
        Registration reg = new Registration();
        String playerId = value.getSite().getOwner();
        reg.setPlayer(playerId);
        Registration existing = registrations.putIfAbsent(playerId, reg);
        if(existing == null) {
            existing = reg;     //this was a new entry
        }
        existing.addSite(value.getSite().getId());
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
