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
package org.gameontext.board.models.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(Include.NON_EMPTY)
public class Exit {

    /** Room id */
    private String id;

    /** Terse/Short room name */
    private String name;

    /** full name */
    private String fullName;

    /** description of target room's door */
    private String door = null;

    /** target room connection details */
    private ConnectionDetails connectionDetails = null;

    public Exit() {}

    public Exit(Site targetSite, String direction) {
        this.id = targetSite.getId();

        if ( targetSite.getInfo() != null ) {
            this.name = targetSite.getInfo().getName();
            this.fullName = targetSite.getInfo().getFullName();
            this.connectionDetails = targetSite.getInfo().getConnectionDetails();

            setDoorNameFromTargetSite(targetSite, direction);

            // This won't be the prettiest. ew.
            if ( this.fullName == null )
                this.fullName = this.name;

        } else {
            // Empty/placeholder room. Still navigable if very unclear.
            this.name = "Nether space";
            this.fullName = "Nether space";
            this.door = "Tenuous doorway filled with gray fog";
        }
    }

    private void setDoorNameFromTargetSite(Site targetSite, String direction) {
        RoomInfo targetSiteInfo = targetSite.getInfo();
        Doors doors = targetSiteInfo != null ? targetSiteInfo.getDoors() : null;
        if (doors != null) {
            switch(direction.toLowerCase()) {
                case "n" :
                    this.door = doors.getN();
                    break;
                case "s" :
                    this.door = doors.getS();
                    break;
                case "e" :
                    this.door = doors.getE();
                    break;
                case "w" :
                    this.door = doors.getW();
                    break;
            }
        }

        // Really generic. They gave us nothing interesting.
        if ( this.door == null )
            this.door = "A door";
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDoor() {
        return door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }
    public void setConnectionDetails(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }


    @Override
    public String toString()  {
      StringBuilder sb = new StringBuilder();
      sb.append("class Exit {\n");
      sb.append("  id: ").append(id).append("\n");
      sb.append("  name: ").append(name).append("\n");
      sb.append("  door: ").append(door).append("\n");
      sb.append("  connDetails: ").append(connectionDetails).append("\n");
      sb.append("}\n");
      return sb.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionDetails == null) ? 0 : connectionDetails.hashCode());
        result = prime * result + ((door == null) ? 0 : door.hashCode());
        result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Exit other = (Exit) obj;
        if (connectionDetails == null) {
            if (other.connectionDetails != null) {
                return false;
            }
        } else if (!connectionDetails.equals(other.connectionDetails)) {
            return false;
        }
        if (door == null) {
            if (other.door != null) {
                return false;
            }
        } else if (!door.equals(other.door)) {
            return false;
        }
        if (fullName == null) {
            if (other.fullName != null) {
                return false;
            }
        } else if (!fullName.equals(other.fullName)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
