package com.mulesoft.connector.einsteinai.internal.proxy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultHttpProxyConfigTest {

  @Test
  void defaultValues_portHasDefaultMaxIntAndOtherFieldsNull() {
    DefaultHttpProxyConfig config = new DefaultHttpProxyConfig();

    assertNull(config.getHost());
    assertEquals(Integer.MAX_VALUE, config.getPort());
    assertNull(config.getUsername());
    assertNull(config.getPassword());
    assertNull(config.getNonProxyHosts());
  }

  @Test
  void equals_and_hashCode_reflectAllFields() {
    DefaultHttpProxyConfig config1 = createConfig(
                                                  "proxy.example.com",
                                                  8080,
                                                  "user",
                                                  "pass",
                                                  "localhost|127.*");
    DefaultHttpProxyConfig config2 = createConfig(
                                                  "proxy.example.com",
                                                  8080,
                                                  "user",
                                                  "pass",
                                                  "localhost|127.*");
    DefaultHttpProxyConfig differentHost = createConfig(
                                                        "other.example.com",
                                                        8080,
                                                        "user",
                                                        "pass",
                                                        "localhost|127.*");
    DefaultHttpProxyConfig differentPort = createConfig(
                                                        "proxy.example.com",
                                                        3128,
                                                        "user",
                                                        "pass",
                                                        "localhost|127.*");
    DefaultHttpProxyConfig differentUser = createConfig(
                                                        "proxy.example.com",
                                                        8080,
                                                        "otherUser",
                                                        "pass",
                                                        "localhost|127.*");
    DefaultHttpProxyConfig differentPassword = createConfig(
                                                            "proxy.example.com",
                                                            8080,
                                                            "user",
                                                            "otherPass",
                                                            "localhost|127.*");
    DefaultHttpProxyConfig differentNonProxyHosts = createConfig(
                                                                 "proxy.example.com",
                                                                 8080,
                                                                 "user",
                                                                 "pass",
                                                                 "example.com");

    // same values -> equals and same hashCode
    assertEquals(config1, config2);
    assertEquals(config1.hashCode(), config2.hashCode());

    // self equality and non-equality with null / different type
    assertEquals(config1, config1);
    assertNotEquals(config1, null);
    assertNotEquals(config1, "someString");

    // each field difference breaks equality
    assertNotEquals(config1, differentHost);
    assertNotEquals(config1, differentPort);
    assertNotEquals(config1, differentUser);
    assertNotEquals(config1, differentPassword);
    assertNotEquals(config1, differentNonProxyHosts);
  }

  @Test
  void equals_handlesNullFieldsCorrectly() {
    DefaultHttpProxyConfig configWithNulls1 = createConfig(null, 8080, null, null, null);
    DefaultHttpProxyConfig configWithNulls2 = createConfig(null, 8080, null, null, null);
    DefaultHttpProxyConfig configWithHostOnly = createConfig("proxy.example.com", 8080, null, null, null);

    assertEquals(configWithNulls1, configWithNulls2);
    assertEquals(configWithNulls1.hashCode(), configWithNulls2.hashCode());
    assertNotEquals(configWithNulls1, configWithHostOnly);
  }

  private DefaultHttpProxyConfig createConfig(String host,
                                              int port,
                                              String username,
                                              String password,
                                              String nonProxyHosts) {
    DefaultHttpProxyConfig config = new DefaultHttpProxyConfig();

    // Use reflection since fields are private and there are no setters
    try {
      java.lang.reflect.Field hostField = DefaultHttpProxyConfig.class.getDeclaredField("host");
      hostField.setAccessible(true);
      hostField.set(config, host);

      java.lang.reflect.Field portField = DefaultHttpProxyConfig.class.getDeclaredField("port");
      portField.setAccessible(true);
      portField.setInt(config, port);

      java.lang.reflect.Field usernameField = DefaultHttpProxyConfig.class.getDeclaredField("username");
      usernameField.setAccessible(true);
      usernameField.set(config, username);

      java.lang.reflect.Field passwordField = DefaultHttpProxyConfig.class.getDeclaredField("password");
      passwordField.setAccessible(true);
      passwordField.set(config, password);

      java.lang.reflect.Field nonProxyHostsField = DefaultHttpProxyConfig.class.getDeclaredField("nonProxyHosts");
      nonProxyHostsField.setAccessible(true);
      nonProxyHostsField.set(config, nonProxyHosts);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      fail("Reflection setup for DefaultHttpProxyConfig failed: " + e.getMessage());
    }

    return config;
  }
}
