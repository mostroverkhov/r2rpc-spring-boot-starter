package com.github.mostroverkhov.r2.example.api;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;

@R2Api(value = "baz")
public interface BazApi {

  BazContract baz();
}
