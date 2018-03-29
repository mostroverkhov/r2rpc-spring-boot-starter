# R2RPC Spring Boot Starter

Spring Boot 2 starter for [R2RPC](https://github.com/mostroverkhov/r2) - [RSocket](https://github.com/rsocket/rsocket) based RPC framework with pluggable serialization.

Simplifies creation of `R2RPC` servers by providing:

* sensible auto-configuration (simple `ServerRSocketFactory`, `JSON` serialization and `TCP` transport)
* declarative definition of server endpoints (YAML is preferred)  

```yaml
r2rpc:
 server:
  defaults:
    codecs:
      - jackson
    transport: tcp
  endpoints:
    - name: foo
      port: 8083
      api: baz
```

`api` refers to API interface annotated with `R2Api(name)`, which aggregates [R2RPC](https://github.com/mostroverkhov/r2) service definitions. Its implementation serves as Endpoint handler, and must be provided to Spring with 
`R2ServerApiHandlers` bean (`BazServerApiHandlers` in library example).   

Additional transports can be declared as `@R2ServerTransport` annotated Spring beans, codecs - `@R2DataCodec`.

Example module contains simple self-contained runnable application - `R2rpcStarterExampleApplicaton`.

### LICENSE

Copyright 2018 Maksym Ostroverkhov

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.