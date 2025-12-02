Protobuf Example — Java & Spring Boot

This repository is a complete, hands-on learning and testing environment for Protocol Buffers (protobuf) in Java and Spring Boot.
It demonstrates every important operational scenario you will encounter when using protobuf inside modern microservice architectures — from message construction all the way to compatibility evolution.

The project includes:

Full protobuf setup with Maven & protoc

Advanced .proto schemas (enums, repeated, optional, oneof, nested)

Spring Boot endpoints using protobuf over HTTP

Extensive JUnit tests covering all runtime behaviors

Compatibility tests simulating microservice evolution (new fields, removed fields, enum changes)

This repository exists as a reference implementation, laboratory, and study project for mastering Protocol Buffers.

🎯 Project Goals

This repo provides a structured environment to help you understand:

How protobuf messages behave at runtime

How to serialize/deserialize messages safely

How to integrate protobuf into Spring Boot controllers

How to evolve protobuf schemas without breaking clients

How JSON and protobuf interact (JsonFormat)

How to validate protobuf behavior with automated tests

All code is real, runnable, and designed for step-by-step exploration.


📁 Project Structure
protobuf-example/
├── src/
│   ├── main/
│   │   ├── java/                # Spring Boot application + controllers
│   │   └── proto/               # user_api.proto + advanced_user.proto + versioning.proto
│   └── test/
│       ├── AdvancedUserProtoTest.java     # 12 protobuf runtime scenarios
│       ├── AdvancedUserHttpTest.java      # HTTP (Spring MVC) protobuf tests
│       └── VersioningProtoTest.java       # Forward/backward compatibility tests
├── pom.xml
└── README.md

🔧 Technologies Used

Java 21

Spring Boot

Protocol Buffers (protoc + Java codegen)

protobuf-java & protobuf-java-util

JUnit 5 / Spring MVC Test

Maven + protobuf-maven-plugin

🚀 How to Run

1. Install protoc (Ubuntu example)
sudo apt install -y protobuf-compiler
protoc --version
2. Build & generate protobuf code
mvn clean compile
3. Run tests
mvn test
4. Run the Spring Boot application
mvn spring-boot:run

🧪 What This Repository Demonstrates
✔ 1. Full protobuf operational scenarios

Located in: AdvancedUserProtoTest.java

Basic message creation & immutability

Serialization & parsing (valid + corrupted bytes)

Optional fields + default values

Enum handling + unknown enums

Repeated fields (lists)

oneof behavior

Nested messages

JSON ↔ protobuf (JsonFormat)

Simulated persistence (Redis-style byte storage)

Versioning basics (adding/removing fields, clearing fields)

✔ 2. Spring Boot + Protobuf over HTTP

Located in: AdvancedUserHttpTest.java

Returning protobuf (application/x-protobuf)

Receiving protobuf POST body

Mixed-mode API: internal protobuf + external JSON response

Error handling for invalid protobuf payloads

✔ 3. Versioning, evolution & compatibility

Located in: VersioningProtoTest.java

New fields added — forward compatibility

Removed fields — backward compatibility

Enum evolution (unknown enum values)

Realistic round-trip simulation:
new client → old service → new client

Preservation of unknown fields in proto3

📚 Protobuf Files Included
user_api.proto

Simple examples used for initial tests.

advanced_user.proto

A comprehensive schema including:

enum Gender

repeated fields

optional fields

oneof structures

nested messages (ContactInfo, PhoneNumber)

versioning.proto

Two message versions (UserV1, UserV2) to test:

forward/backward compatibility

breaking & nonbreaking schema changes

enum evolution

🌐 Example HTTP Endpoints

Get protobuf response
GET /advanced/users/{id}
Accept: application/x-protobuf

Send protobuf request
POST /advanced/users/parse
Content-Type: application/x-protobuf

Mixed JSON mode
GET /advanced/users/{id}/json
Accept: application/json

Why This Repo Is Valuable

This codebase explicitly demonstrates:

How protobuf behaves beyond “hello world”

Real-life schema evolution problems

Practical Spring Boot integration patterns

Compatibility and versioning — the hardest part of protobuf

How to build correct HTTP APIs using binary payloads

How to write robust tests for microservice message contracts

If you want to learn protobuf properly with 100% real-world coverage, this repository gives you everything you need.

📜 License

This project is created and maintained by Mahmood Saneian.
Feel free to fork, study, copy, or adapt for your own systems.
