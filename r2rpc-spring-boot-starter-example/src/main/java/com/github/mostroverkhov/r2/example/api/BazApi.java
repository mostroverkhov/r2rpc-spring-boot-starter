package com.github.mostroverkhov.r2.example.api;

import com.github.mostroverkhov.r2.contract.R2Api;
import com.github.mostroverkhov.r2.example.svc.contract.BazContract;

@R2Api("baz")
public interface BazApi {

  BazContract bazContract();
}
