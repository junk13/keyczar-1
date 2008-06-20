/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.keyczar;

import com.google.gson.annotations.Expose;
import com.google.keyczar.enums.KeyPurpose;
import com.google.keyczar.enums.KeyType;
import com.google.keyczar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encodes metadata for a set of keys which consists of the following:
 * <ul>  
 *   <li>a string-valued name,
 *   <li>a KeyPurpose,
 *   <li>a KeyType, and
 *   <li>a set of KeyVersion values.
 * </ul>
 * 
 * <p>JSON Representation consists of the following fields:
 * <ul>  
 *   <li>"name": a String name, 
 *   <li>"purpose": JSON representation of KeyPurpose value, 
 *   <li>"type": JSON representation of KeyType value,
 *   <li>"versions": JSON representation of an array of KeyVersion values.
 * </ul>
 *            
 * @author steveweis@gmail.com (Steve Weis)
 *
 */
class KeyMetadata {
  @Expose private String name = "";
  @Expose private KeyPurpose purpose = KeyPurpose.TEST;
  @Expose private KeyType type = KeyType.TEST;
  @Expose private ArrayList<KeyVersion> versions = new ArrayList<KeyVersion>();
  private Map<Integer, KeyVersion> versionMap = 
      new HashMap<Integer, KeyVersion>(); // link version number to version
  

  private KeyMetadata() {
    // For GSON
  }
  
  KeyMetadata(String n, KeyPurpose p, KeyType t) {
    name = n;
    purpose = p;
    type = t;
  }

  @Override
  public String toString() {
    return Util.gson().toJson(this);
  }

  /**
   * Adds given key version to key set.
   * 
   * @param version KeyVersion of key to be added
   * @return true if add was successful, false if version number collides
   */
  boolean addVersion(KeyVersion version) {
    int versionNumber = version.getVersionNumber();
    if (!versionMap.containsKey(versionNumber)) {
      versionMap.put(versionNumber, version);
      versions.add(version);
      return true;
    }
    return false;
  }
  
  /**
   * Removes given key version from key set.
   * 
   * @param versionNumber integer version number of key to be removed
   * @return true if remove was successful
   */
  boolean removeVersion(int versionNumber) {
    if (versionMap.containsKey(versionNumber)) {
      KeyVersion version = versionMap.get(versionNumber);
      versions.remove(version);
      versionMap.remove(versionNumber);
      return true;
    }
    return false;
  }

  String getName() {
    return name;
  }

  KeyPurpose getPurpose() {
    return purpose;
  }

  KeyType getType() {
    return type;
  }
  
  /**
   * Returns the version corresponding to the version number.
   * 
   * @param versionNumber
   * @return KeyVersion corresponding to given number, or null if nonexistent
   */
  KeyVersion getVersion(int versionNumber) {
    return versionMap.get(versionNumber);
  }

  List<KeyVersion> getVersions() {
    return versions;
  }

  static KeyMetadata read(String jsonString) {
    KeyMetadata kmd = Util.gson().fromJson(jsonString, KeyMetadata.class);
    for (KeyVersion version : kmd.getVersions()) {
      kmd.versionMap.put(version.getVersionNumber(), version);
    } //FIXME: can we initialize the version map from JSON too?
    return kmd;
  }
}
