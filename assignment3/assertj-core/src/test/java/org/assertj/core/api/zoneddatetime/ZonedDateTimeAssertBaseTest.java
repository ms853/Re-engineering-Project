/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2017 the original author or authors.
 */
package org.assertj.core.api.zoneddatetime;

import static org.junit.Assume.assumeTrue;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.assertj.core.api.BaseTest;
import org.junit.experimental.theories.DataPoint;


/**
 * Base test class for {@link org.assertj.core.api.ZonedDateTimeAssert} tests.
 * 
 * @author Paweł Stawicki
 * @author Joel Costigliola
 * @author Marcin Zajączkowski
 */
public class ZonedDateTimeAssertBaseTest extends BaseTest {

  @DataPoint
  public static ZonedDateTime dateTime1 = ZonedDateTime.of(2000, 12, 14, 0, 0, 0, 0, ZoneOffset.UTC);
  @DataPoint
  public static ZonedDateTime dateTime2 = ZonedDateTime.of(2000, 12, 13, 23, 59, 59, 999, ZoneOffset.UTC);
  @DataPoint
  public static ZonedDateTime dateTime3 = ZonedDateTime.of(2000, 12, 14, 0, 0, 0, 1, ZoneOffset.UTC);
  @DataPoint
  public static ZonedDateTime dateTime4 = ZonedDateTime.of(2000, 12, 14, 22, 15, 15, 875, ZoneOffset.UTC);
  @DataPoint
  public static ZonedDateTime dateTime5 = ZonedDateTime.of(2000, 12, 14, 22, 15, 15, 874, ZoneOffset.UTC);
  @DataPoint
  public static ZonedDateTime dateTime6 = ZonedDateTime.of(2000, 12, 14, 22, 15, 15, 876, ZoneOffset.UTC);

  protected static void testAssumptions(ZonedDateTime reference, ZonedDateTime dateBefore, ZonedDateTime dateAfter) {
    assumeTrue(dateBefore.isBefore(reference));
    assumeTrue(dateAfter.isAfter(reference));
  }

}