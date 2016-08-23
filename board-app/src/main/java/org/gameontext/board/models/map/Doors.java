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

/**
 * Descriptions for the doors in the room. These may be used by other rooms to describe the appearance of the door.
 * Keys should be relative to this room (e, w, n, s, u, d). Values are simple string descriptions for the outside
 * of the door. If the string value references a direction at all, it should be the opposite direction:for the
 * East door, the direction mentioned should be West. Descriptions are optional, and will be generated if absent.
 */
@JsonInclude(Include.NON_EMPTY)
public class Doors {

    private String n = null;
    private String w = null;
    private String s = null;
    private String e = null;
    private String u = null;
    private String d = null;

    public Doors() {};

    public Doors(String prefix) {
        n = prefix + " North door";
        s = prefix + " South door";
        e = prefix + " East door";
        w = prefix + " West door";
        u = prefix + " Hatch door";
        d = prefix + " Trap door";
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    @Override
    public String toString()  {
      StringBuilder sb = new StringBuilder();
      sb.append("class Doors {\n");
      sb.append("  n: ").append(n).append("\n");
      sb.append("  w: ").append(w).append("\n");
      sb.append("  s: ").append(s).append("\n");
      sb.append("  e: ").append(e).append("\n");
      sb.append("  u: ").append(u).append("\n");
      sb.append("  d: ").append(d).append("\n");
      sb.append("}\n");
      return sb.toString();
    }
}
