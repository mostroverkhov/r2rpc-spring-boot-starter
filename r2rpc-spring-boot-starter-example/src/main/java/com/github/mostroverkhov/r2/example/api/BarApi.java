package com.github.mostroverkhov.r2.example.api;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.example.svc.contract.BarContract;

@R2Api("bar")
public interface BarApi {

  BarContract barContract();
}
