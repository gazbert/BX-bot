/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Gareth Jon Lynch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gazbert.bxbot.rest.api.exchange;

import org.springframework.security.core.userdetails.User;
import com.gazbert.bxbot.domain.exchange.ExchangeConfig;
import com.gazbert.bxbot.services.ExchangeConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * <p>
 * Controller for directing Exchange config requests.
 * <p>
 * Exchange config can only be fetched and updated - there is only 1 Exchange Adapter per bot.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api/config/")
class ExchangeConfigController {

    private static final Logger LOG = LogManager.getLogger();
    private final ExchangeConfigService exchangeConfigService;

    public ExchangeConfigController(ExchangeConfigService exchangeConfigService) {
        Assert.notNull(exchangeConfigService, "exchangeConfigService dependency cannot be null!");
        this.exchangeConfigService = exchangeConfigService;
    }

    /**
     * Returns Exchange configuration for the bot.
     *
     * @return the Exchange configuration.
     */
    @RequestMapping(value = "/exchange", method = RequestMethod.GET)
    public ExchangeAdapterConfig getExchange(@AuthenticationPrincipal User user) {

        LOG.info("GET /exchange - getExchange - caller: " + user.getUsername());

        final ExchangeConfig exchangeConfig = exchangeConfigService.getConfig();
        final ExchangeAdapterConfig exchangeAdapterConfig = adaptInternalConfigToExternalPayload(exchangeConfig);

        LOG.info("Response: " + exchangeAdapterConfig);
        return exchangeAdapterConfig;
    }

    /**
     * Updates Exchange configuration for the bot.
     *
     * @return 204 'No Content' HTTP status code if exchange config was updated, some other HTTP status code otherwise.
     */
    @RequestMapping(value = "/exchange", method = RequestMethod.PUT)
    ResponseEntity<?> updateExchange(@AuthenticationPrincipal User user, @RequestBody ExchangeConfig config) {

        LOG.info("PUT /exchange - updateExchange - caller: " + user.getUsername());
        LOG.info("Request: " + config);

        exchangeConfigService.updateConfig(config);
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/").buildAndExpand().toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.NO_CONTENT);
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    private ExchangeAdapterConfig adaptInternalConfigToExternalPayload(ExchangeConfig internalConfig) {

        final NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setConnectionTimeout(internalConfig.getNetworkConfig().getConnectionTimeout());
        networkConfig.setNonFatalErrorHttpStatusCodes(internalConfig.getNetworkConfig().getNonFatalErrorCodes());
        networkConfig.setNonFatalErrorMessages(internalConfig.getNetworkConfig().getNonFatalErrorMessages());

        final OtherConfig otherConfig = new OtherConfig();
        otherConfig.setItems(internalConfig.getOtherConfig().getItems());

        final ExchangeAdapterConfig exchangeAdapterConfig = new ExchangeAdapterConfig();
        exchangeAdapterConfig.setName(internalConfig.getExchangeName());
        exchangeAdapterConfig.setClassName(internalConfig.getExchangeAdapter());
        exchangeAdapterConfig.setNetworkConfig(networkConfig);

        // TODO - Not exposing AuthenticationConfig for now - too risky?
        // final AuthenticationConfig authenticationConfig = new AuthenticationConfig();

        return exchangeAdapterConfig;
    }
}

