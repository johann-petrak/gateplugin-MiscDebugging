package org.jpetrak.gate.plugins;

import gate.test.GATEPluginTests;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Using this class automatically prepares GATE and the plugin for testing.
 * 
 * This class automatically initializes GATE and loads the plugin. 
 * Any method in this class with the "@Test" annotation will then get
 * run with the plugin already properly loaded.
 * 
 */
public class TestDebugControllerCallbacks extends GATEPluginTests {

  @Test
  public void testSomething() {
    AbstractLanguageAnalyser pr = Factory.createResource("org.jpetrak.gate.plugins.DebugControllerCallbacks");
    try {
      // testing code goes here
    } finally {
      Factory.deleteResource(pr);
    }
  }
}
