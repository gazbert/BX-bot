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

package com.gazbert.bxbot.rest.api.markets;

import com.gazbert.bxbot.domain.market.MarketConfig;
import org.springframework.security.core.userdetails.User;
import com.gazbert.bxbot.services.MarketConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * Controller for directing Market config requests.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api/config")
class MarketConfigController {

    private static final Logger LOG = LogManager.getLogger();
    private final MarketConfigService marketConfigService;

    @Autowired
    public MarketConfigController(MarketConfigService marketConfigService) {
        Assert.notNull(marketConfigService, "marketConfigService dependency cannot be null!");
        this.marketConfigService = marketConfigService;
    }

    /**
     * Returns all of the Market configuration for the bot.
     *
     * @param user the authenticated user.
     * @return a list of Market configurations.
     */
    @RequestMapping(value = "/market", method = RequestMethod.GET)
    public List<MarketConfig> getAllMarkets(@AuthenticationPrincipal User user) {

        LOG.info("GET /market - getAllMarkets - caller: " + user.getUsername());

        final List<MarketConfig> marketConfigs = marketConfigService.findAllMarkets();
        LOG.info("Response: " + marketConfigs);

        return marketConfigs;
    }

    /**
     * Returns the Market configuration for a given id.
     *
     * @param user the authenticated user.
     * @param marketId the id of the Market to fetch.
     * @return the Market configuration.
     */
    @RequestMapping(value = "/market/{marketId}", method = RequestMethod.GET)
    public ResponseEntity<?> getMarket(@AuthenticationPrincipal User user, @PathVariable String marketId) {

        LOG.info("GET /market/" + marketId + " - getMarket - caller: " + user.getUsername());

        final MarketConfig marketConfig = marketConfigService.findById(marketId);
        LOG.info("Response: " + marketConfig);

        return marketConfig.getId() != null
                ? new ResponseEntity<>(marketConfig, null, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Updates a given Market configuration.
     *
     * @param user the authenticated user.
     * @param marketId id of the Market config to update.
     * @param config the updated Market config.
     * @return 204 'No Content' HTTP status code if update successful, 404 'Not Found' HTTP status code if
     * Market config not found.
     */
    @RequestMapping(value = "/market/{marketId}", method = RequestMethod.PUT)
    ResponseEntity<?> updateMarket(@AuthenticationPrincipal User user, @PathVariable String marketId,
                                   @RequestBody MarketConfig config) {

        LOG.info("PUT /market/" + marketId + " - updateMarket - caller: " + user.getUsername());
        LOG.info("Request: " + config);

        if (config == null || config.getId() == null || !marketId.equals(config.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final MarketConfig updatedConfig = marketConfigService.updateMarket(config);
        return updatedConfig.getId() != null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a new Market configuration.
     *
     * @param user the authenticated user.
     * @param marketId id of the Market config to create.
     * @param config the new Market config.
     * @return 201 'Created' HTTP status code if create successful, 409 'Conflict' HTTP status code if
     * Market config already exists.
     */
    @RequestMapping(value = "/market/{marketId}", method = RequestMethod.POST)
    ResponseEntity<?> createMarket(@AuthenticationPrincipal User user, @PathVariable String marketId,
                                   @RequestBody MarketConfig config) {

        LOG.info("POST /market/" + marketId + " - createMarket - caller: " + user.getUsername());
        LOG.info("Request: " + config);

        if (config == null || config.getId() == null || !marketId.equals(config.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final MarketConfig createdConfig = marketConfigService.createMarket(config);
        if (createdConfig.getId() != null) {
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{marketId}")
                    .buildAndExpand(createdConfig.getId()).toUri());
            return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Deletes a Market configuration for a given id.
     *
     * @param user the authenticated user.
     * @param marketId the id of the Market configuration to delete.
     * @return 204 'No Content' HTTP status code if update successful, 404 'Not Found' HTTP status code if
     * Market config not found.
     */
    @RequestMapping(value = "/market/{marketId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMarket(@AuthenticationPrincipal User user, @PathVariable String marketId) {

        LOG.info("DELETE /market/" + marketId + " - deleteMarket - caller: " + user.getUsername());

        final MarketConfig deletedConfig = marketConfigService.deleteMarketById(marketId);
        return deletedConfig.getId() != null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

