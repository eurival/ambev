package com.ambev.order_viewer.web.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

/**
* Classe utilitária para lidar com paginação.
*
* <p>
* A paginação usa os mesmos princípios da <a href="https://docs.github.com/rest/guides/using-pagination-in-the-rest-api">API do GitHub</a>,
* e segue <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (cabeçalho do link)</a>.
*/

public final class PaginationUtil {

    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";

    private static LinkHeaderUtil linkHeaderUtil = new LinkHeaderUtil();

    private PaginationUtil() {}

    /**
     * Gera cabeçalhos de paginação para um objeto {@link org.springframework.data.domain.Page} do Spring Data.
     *
     * @param uriBuilder O construtor de URI.
     * @param page A página.
     * @param <T> O tipo de objeto.
     * @return cabeçalho http.
     */
    public static <T> HttpHeaders generatePaginationHttpHeaders(UriComponentsBuilder uriBuilder, Page<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_X_TOTAL_COUNT, Long.toString(page.getTotalElements()));
        headers.add(HttpHeaders.LINK, linkHeaderUtil.prepareLinkHeaders(uriBuilder, page));
        return headers;
    }
}
