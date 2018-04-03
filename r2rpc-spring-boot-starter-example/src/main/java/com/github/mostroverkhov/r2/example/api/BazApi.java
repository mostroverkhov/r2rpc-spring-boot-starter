package com.github.mostroverkhov.r2.example.api;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.example.api.baz.BazContract;

@R2Api("baz")
public interface BazApi {

  BazContract baz();
}
