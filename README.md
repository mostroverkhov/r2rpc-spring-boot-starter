# R2RPC Spring Boot Starter

Spring Boot 2 starter for [R2RPC](https://github.com/mostroverkhov/r2) - [RSocket](https://github.com/rsocket/rsocket) based RPC framework with pluggable serialization.

Simplifies creation of `R2RPC` servers by providing:

* sensible auto-configuration (simple `ServerRSocketFactory`, `JSON` serialization and `TCP` transport)
* declarative definition of server endpoints (YAML is preferred)  

```yaml
r2rpc:
 server:
  defaults:
    codecs: [jackson]
    transport: tcp
  endpoints:
    - name: foo
      port: 8083
      api: [baz, bar]
```
Custom transports can be provided as `@R2ServerTransport` annotated Spring beans, and custom codecs - with `@R2DataCodec`.

`api` is set of API interfaces annotated with `R2Api(name)`, each aggregates [R2RPC](https://github.com/mostroverkhov/r2) service definitions. Its implementation serves as Endpoint handler, and must be provided to Spring with 
`ServerApiProvider` bean for each API implementation.   

API definitions allow to group related R2RPC services in one namespace so they can be shared more conveniently.

Single API definition, referred in configuration as `api:[example]`, can be as follows

```
@R2Api("example")
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

API definition is exposed to Spring with `ServerApiProvider<ExampleApiImpl>` implementation,
where `ExampleApiImpl` is implementation of `ExampleApi` providing handlers for incoming 
requests.
```java
public class ExampleApiProvider implements ServerApiProvider<ExampleApiImpl> {

  @Override
  public ExampleApiImpl apply(ConnectionContext connectionContext) {
    return new BarApiImpl();
  }
}
```
API definitions are constrained by following rules
* All definitions must have unique names. If several implementations of same API are necessary, 
override name on implementation itself by annotating It with `@R2API(newName)`
* API implementation (handler) must implement at most 1 (one) API definition
* API implementations per endpoint must contribute API definitions of unique type 
  (in other words, no more than one implementation of particular API per endpoint)

Endpoints lifecycle is available with `ServerControls.endpoint(name)` API, which allows to listen for 
endpoint Start and Stop events (success or error), e.g.

```java
            Mono<Endpoint> endpointStart = serverControls
                .endpoint("foo")
                .started()
```  

### Examples

Example module contains simple self-contained runnable application - `R2rpcStarterExampleApplicaton`.

### LICENSE

Copyright 2018 Maksym Ostroverkhov

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.