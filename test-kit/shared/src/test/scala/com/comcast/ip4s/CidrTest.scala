/*
 * Copyright 2018 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.comcast.ip4s

import org.scalacheck.Prop.forAll
import Arbitraries._

class CidrTest extends BaseTestSuite {
  property("roundtrip through string") {
    forAll { (cidr: Cidr[IpAddress]) => assertEquals(Cidr.fromString(cidr.toString), Some(cidr)) }
  }

  property("fromIpAndMask") {
    forAll { (ip: IpAddress, prefixBits0: Int) =>
      val max = ip.fold(_ => 32, _ => 128)
      val prefixBits = ((prefixBits0 % max).abs + 1)
      val maskInt = BigInt(-1) << (max - prefixBits)
      val mask = ip.fold(_ => Ipv4Address.fromLong(maskInt.toLong & 0xffffffff), _ => Ipv6Address.fromBigInt(maskInt))
      assertEquals(Cidr.fromIpAndMask(ip, mask), Cidr(ip, prefixBits))
    }
  }
}
