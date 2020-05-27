package com.example.top.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UsuarioService {

    @GET("/api/users/{id}")
    public Call<UsuarioApiResponse> obtenerUsuario(@Path("id") long id);
}
