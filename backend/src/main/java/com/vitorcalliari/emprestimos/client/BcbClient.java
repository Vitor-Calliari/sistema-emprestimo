package com.vitorcalliari.emprestimos.client;

import com.vitorcalliari.emprestimos.client.dto.BcbCotacaoDTO;
import com.vitorcalliari.emprestimos.client.dto.BcbMoedaDTO;
import com.vitorcalliari.emprestimos.client.dto.BcbODataResponse;
import com.vitorcalliari.emprestimos.exception.IntegracaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BcbClient {

    private static final String BASE_URL =
            "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata";

    private static final DateTimeFormatter FORMATO_DATA_BCB =
            DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private final RestTemplate restTemplate;

    public List<BcbMoedaDTO> buscarMoedas() {
        String url = BASE_URL + "/Moedas?$format=json";

        try {
            BcbODataResponse<BcbMoedaDTO> resposta = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<BcbODataResponse<BcbMoedaDTO>>() {}
            ).getBody();

            return resposta != null ? resposta.value() : List.of();
        } catch (RestClientException ex) {
            throw new IntegracaoException(
                    "Nao foi possivel obter a lista de moedas do Banco Central. Tente novamente em instantes.");
        }
    }

    public Optional<BcbCotacaoDTO> buscarCotacao(String codigoMoeda, LocalDate data) {
        String dataFormatada = data.format(FORMATO_DATA_BCB);

        String url = UriComponentsBuilder.fromUriString(BASE_URL +
                        "/CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)")
                .queryParam("@moeda", "'" + codigoMoeda + "'")
                .queryParam("@dataCotacao", "'" + dataFormatada + "'")
                .queryParam("$format", "json")
                .encode()
                .toUriString();

        try {
            BcbODataResponse<BcbCotacaoDTO> resposta = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<BcbODataResponse<BcbCotacaoDTO>>() {}
            ).getBody();

            if (resposta == null || resposta.value().isEmpty()) {
                return Optional.empty();
            }

            List<BcbCotacaoDTO> cotacoes = resposta.value();
            return Optional.of(cotacoes.get(cotacoes.size() - 1));
        } catch (RestClientException ex) {
            throw new IntegracaoException(
                    "Nao foi possivel consultar a cotacao no Banco Central. Tente novamente em instantes.");
        }
    }

    public BcbCotacaoDTO buscarCotacaoComFallback(String codigoMoeda, LocalDate data) {
        LocalDate dataConsulta = data;
        int tentativas = 0;

        while (tentativas < 7) {
            Optional<BcbCotacaoDTO> cotacao = buscarCotacao(codigoMoeda, dataConsulta);
            if (cotacao.isPresent()) {
                return cotacao.get();
            }
            dataConsulta = dataConsulta.minusDays(1);
            tentativas++;
        }

        throw new IntegracaoException(
                "Nao foi possivel obter cotacao para " + codigoMoeda +
                        " apos " + tentativas + " tentativas");
    }
}