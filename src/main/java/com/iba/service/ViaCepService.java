package com.iba.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class ViaCepService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> buscarEnderecoPorCep(String cep) {
        try {
            String cepLimpo = cep.replaceAll("\\D", "");
            if (cepLimpo.length() != 8) {
                throw new RuntimeException("CEP inválido - deve conter 8 dígitos");
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://viacep.com.br/ws/" + cepLimpo + "/json/"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            JsonNode json = objectMapper.readTree(response.body());
            
            if (json.has("erro")) {
                throw new RuntimeException("CEP não encontrado");
            }
            
            Map<String, String> endereco = new HashMap<>();
            endereco.put("cep", cepLimpo);
            endereco.put("logradouro", json.has("logradouro") && !json.get("logradouro").isNull() ? json.get("logradouro").asText() : "");
            endereco.put("bairro", json.has("bairro") && !json.get("bairro").isNull() ? json.get("bairro").asText() : "");
            endereco.put("cidade", json.has("localidade") && !json.get("localidade").isNull() ? json.get("localidade").asText() : "");
            endereco.put("estado", json.has("uf") && !json.get("uf").isNull() ? json.get("uf").asText() : "");
            endereco.put("complemento", json.has("complemento") && !json.get("complemento").isNull() ? json.get("complemento").asText() : "");
            
            return endereco;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar CEP: " + e.getMessage());
        }
    }
}