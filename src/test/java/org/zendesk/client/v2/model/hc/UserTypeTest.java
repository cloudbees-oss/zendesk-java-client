package org.zendesk.client.v2.model.hc;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testing UserType
 *
 * @author Maxim Savenko (maxim.savenko@gmail.com)
 */
public class UserTypeTest {
  @Test
  public void testToString() {
    assertEquals("signed_in_users", UserType.SIGNED_IN_USERS.toString());
    assertEquals("staff", UserType.STAFF.toString());
  }
}
