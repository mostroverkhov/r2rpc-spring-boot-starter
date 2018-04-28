# R2RPC Spring Boot Starter

[![Build Status](https://travis-ci.org/mostroverkhov/r2rpc-spring-boot-starter.svg?branch=master)](https://travis-ci.org/mostroverkhov/r2rpc-spring-boot-starter)

Spring Boot 2 starter for [R2RPC](https://github.com/mostroverkhov/r2) - [RSocket](https://github.com/rsocket/rsocket) based RPC framework with pluggable serialization.

 ```yaml
 r2rpc:
 server:
    defaults:
      transport: tcp
      codecs:
        - json
    endpoints:
      - name: foo
        port: 8083
        responders:
          - bar
          - baz
        requesters:
          - baz
 client:
    endpoints:
      - name: foo
        transport: tcp
        codecs:
          - json
        requesters:
          - bar
          - baz
        responders:
          - baz
 ```
 
Simplifies creation of `R2RPC` Clients and Servers by providing:
##### Server
* sensible auto-configuration (simple `ServerRSocketFactory`, `JSON` serialization and `TCP` transport)
* servers lifecycle management
* declarative definition of  Requesters and Responders

Endpoints lifecycle is available with `ServerControls.endpoint(name)` API, which allows to listen for endpoint Start and Stop events (success or error), e.g.

```java
            Mono<Endpoint> endpointStart = serverControls
                .endpoint("foo")
                .started()
```
##### Client
* sensible auto-configuration (simple `ClientRSocketFactory`, `JSON` serialization and `TCP` transport)
* auto-configuration of Client Connectors `R2ClientConnector`
* declarative definition of  Requesters and Responders

For each endpoint auto-configuration creates `R2ClientConnector`, which connects to peer with `connect(address,port)`, returns `ApiRequesterFactory`. Individual Connectors are obtained with `ClientConnectors.name(endpoint-name)`.

### Interactions
 RSocket protocol itself supports symmetric interactions - each side of connection (Client and Server) have `Requester` for initiating requests (Request-response, Request-channel etc.), and `Responder` to accept requests from peer.  Sample configuration:

 ##### Requester
Each `Requester` of endpoint references `R2 API` - collection of [R2RPC](https://github.com/mostroverkhov/r2) service definitions, annotated with `R2Api(name)`. R2 APIs  allow to group related `R2` service definitions in one namespace so they can be shared more conveniently.

Requesters are created with `ApiRequesterFactory.create(R2Api.class)`

R2 APIs are registered by providing bean `RequestersProvider<ApiToken>`, where `APiToken` - any class, its package is scanned by auto-configuration for APIs (`@R2API` annotated interfaces)

Single API , referred in configuration as `requesters:[baz]`, can be as follows

```java
@R2Api("baz")
public interface ExampleApi {

  BazContract bazContract();
  
  BarContract barContract();
}
```
`BazContract` and `BarContract` are R2RPC service definitions, e.g.

```java
@Service("baz")
public interface BazContract {

  @RequestStream("bazStream")
  Flux<Baz> baz(Baz baz);
}
```
##### Responder
Each `Responder` is an  implementation of `R2API`, and acts as Handler for incoming requests from peer. Responders should be wrapped into peer-specific factory (`[Peer]HandlersProvider`), and registered in Spring.

For server, it's `ServerHandlersProvider<ApiImpl>`, where `ApiImpl` is `R2API` implementation.
Registered name is determined by `@R2Api(name)` on corresponding R2 API definition.
```java
public class BarHandlersProvider implements ServerHandlersProvider<BarApiImpl> {

  @Override
  public BarApiImpl apply(ConnectionContext connectionContext,
                          ApiRequesterFactory requesterFactory) {
    return new BarApiImpl(connectionContext, requesterFactory);
  }
}
```

```java
public class BarApiImpl implements BarApi {

  private ConnectionContext connectionContext;
  private ApiRequesterFactory apiRequesterFactory;

  public BazApiImpl(
      ConnectionContext connectionContext,
	  ApiRequesterFactory apiRequesterFactory) {
    this.connectionContext = connectionContext;
	this.apiRequesterFactory = apiRequesterFactory;
  }

   @Override
  public BazContract bazContract() {
    return new BazHandler();
  }
}
```
For Client, it's `ClientHandlersProvider<ApiImpl>`
```java
public class BazClientHandlersProvider implements ClientHandlersProvider<BazApiImpl> {
  @Override
  public BazApiImpl apply(ApiRequesterFactory requesterFactory) {
    return new BazApiImpl(requesterFactory);
  }
}
```
##### R2 API rules
R2 API definitions are constrained by following rules
* All definitions must have unique names. If several implementations of same API are necessary, 
override name on implementation itself by annotating It with `@R2API(newName)`
* API implementation (handler) must implement at most 1 (one) API definition
* API implementations per endpoint must contribute API definitions of unique type 
  (in other words, no more than one implementation of particular API per endpoint)

### Configuration

##### Defaults
`r2rpc.server.defaults` can contain `Transport` and `Codecs` common for all endpoints.
##### Transports
`tcp` transport is autoconfigured. Custom transports can be provided as implementations of `R2ServerTransport`, annotated with `@Name(value)`.
##### Codecs
 `json` codec is provided by default by `JacksonJsonDataCodec`. To configure custom codecs (e.g. `cbor`, `protobuf` from `R2`)  expose `@Name(value)` annotated `R2DataCodec` implementations as Spring beans.

### Examples

Example module contains simple self-contained runnable application - `R2rpcStarterExampleApplicaton`.

### LICENSE

Copyright 2018 Maksym Ostroverkhov

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
