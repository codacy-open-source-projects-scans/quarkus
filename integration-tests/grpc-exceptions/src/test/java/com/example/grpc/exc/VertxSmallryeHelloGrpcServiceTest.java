package com.example.grpc.exc;

import io.quarkus.grpc.test.utils.VertxGRPCTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(VertxGRPCTestProfile.class)
public class VertxSmallryeHelloGrpcServiceTest extends SmallryeHelloGrpcServiceTestBase {
}
