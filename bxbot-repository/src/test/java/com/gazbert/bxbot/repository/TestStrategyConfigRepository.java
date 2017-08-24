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

package com.gazbert.bxbot.repository;

import com.gazbert.bxbot.datastore.ConfigurationManager;
import com.gazbert.bxbot.datastore.strategy.generated.ConfigItemType;
import com.gazbert.bxbot.datastore.strategy.generated.OptionalConfigType;
import com.gazbert.bxbot.datastore.strategy.generated.StrategyType;
import com.gazbert.bxbot.datastore.strategy.generated.TradingStrategiesType;
import com.gazbert.bxbot.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.repository.impl.StrategyConfigRepositoryXmlImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gazbert.bxbot.datastore.FileLocations.STRATEGIES_CONFIG_XML_FILENAME;
import static com.gazbert.bxbot.datastore.FileLocations.STRATEGIES_CONFIG_XSD_FILENAME;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.easymock.EasyMock.*;

/**
 * Tests Strategy configuration repository behaves as expected.
 *
 * @author gazbert
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationManager.class})
public class TestStrategyConfigRepository {

    private static final String UNKNOWN_STRAT_ID = "unknown-or-new-strat-id";

    private static final String STRAT_ID_1 = "macd-long-position";
    private static final String STRAT_NAME_1 = "MACD Long Position Algo";
    private static final String STRAT_DESCRIPTION_1 = "Uses MACD as indicator and takes long position in base currency.";
    private static final String STRAT_CLASSNAME_1 = "com.gazbert.nova.algos.MacdLongBase";

    private static final String STRAT_ID_2 = "long-scalper";
    private static final String STRAT_NAME_2 = "Long Position Scalper Algo";
    private static final String STRAT_DESCRIPTION_2 = "Scalps and goes long...";
    private static final String STRAT_CLASSNAME_2 = "com.gazbert.nova.algos.LongScalper";

    private static final String BUY_PRICE_CONFIG_ITEM_KEY = "buy-price";
    private static final String BUY_PRICE_CONFIG_ITEM_VALUE = "671.15";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_KEY = "buy-amount";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_VALUE = "0.5";


    @Before
    public void setup() throws Exception {
        PowerMock.mockStatic(ConfigurationManager.class);
    }

    @Test
    public void whenFindAllStrategiesCalledThenExpectServiceToReturnThemAll() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        PowerMock.replayAll();

        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
        final List<StrategyConfig> strategyConfigItems = strategyConfigRepository.findAllStrategies();

        assertThat(strategyConfigItems.size()).isEqualTo(2);

        assertThat(strategyConfigItems.get(0).getId()).isEqualTo(STRAT_ID_1);
        assertThat(strategyConfigItems.get(0).getName()).isEqualTo(STRAT_NAME_1);
        assertThat(strategyConfigItems.get(0).getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
        assertThat(strategyConfigItems.get(0).getClassName()).isEqualTo(STRAT_CLASSNAME_1);
        assertThat(strategyConfigItems.get(0).getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
        assertThat(strategyConfigItems.get(0).getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
        assertThat(strategyConfigItems.get(0).getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
        assertThat(strategyConfigItems.get(0).getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));

        assertThat(strategyConfigItems.get(1).getId()).isEqualTo(STRAT_ID_2);
        assertThat(strategyConfigItems.get(1).getName()).isEqualTo(STRAT_NAME_2);
        assertThat(strategyConfigItems.get(1).getDescription()).isEqualTo(STRAT_DESCRIPTION_2);
        assertThat(strategyConfigItems.get(1).getClassName()).isEqualTo(STRAT_CLASSNAME_2);
        assertThat(strategyConfigItems.get(1).getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
        assertThat(strategyConfigItems.get(1).getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
        assertThat(strategyConfigItems.get(1).getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
        assertThat(strategyConfigItems.get(1).getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));

        PowerMock.verifyAll();
    }

    @Test
    public void whenFindByIdCalledWithRecognizedIdThenReturnMatchingStrategy() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        PowerMock.replayAll();

        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
        final StrategyConfig strategyConfig = strategyConfigRepository.findById(STRAT_ID_1);

        assertThat(strategyConfig.getId()).isEqualTo(STRAT_ID_1);
        assertThat(strategyConfig.getName()).isEqualTo(STRAT_NAME_1);
        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
        assertThat(strategyConfig.getClassName()).isEqualTo(STRAT_CLASSNAME_1);
        assertThat(strategyConfig.getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
        assertThat(strategyConfig.getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
        assertThat(strategyConfig.getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
        assertThat(strategyConfig.getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));

        PowerMock.verifyAll();
    }

    @Test
    public void whenFindByIdCalledWithUnrecognizedIdThenReturnEmptyStrategy() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        PowerMock.replayAll();

        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
        final StrategyConfig strategyConfig = strategyConfigRepository.findById("unknown-id");

        assertThat(strategyConfig.getId()).isEqualTo(null);
        assertThat(strategyConfig.getName()).isEqualTo(null);
        assertThat(strategyConfig.getDescription()).isEqualTo(null);
        assertThat(strategyConfig.getClassName()).isEqualTo(null);
        assertThat(strategyConfig.getConfigItems().isEmpty());

        PowerMock.verifyAll();
    }

    @Test
    public void whenUpdateStrategyCalledWithKnownIdThenExpectServiceToReturnUpdatedStrategy() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        ConfigurationManager.saveConfig(
                eq(TradingStrategiesType.class),
                anyObject(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME));

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        PowerMock.replayAll();

        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
        final StrategyConfig strategyConfig = strategyConfigRepository.updateStrategy(someExternalStrategyConfig());

        assertThat(strategyConfig.getId()).isEqualTo(STRAT_ID_1);
        assertThat(strategyConfig.getName()).isEqualTo(STRAT_NAME_1);
        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
        assertThat(strategyConfig.getClassName()).isEqualTo(STRAT_CLASSNAME_1);
        assertThat(strategyConfig.getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
        assertThat(strategyConfig.getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
        assertThat(strategyConfig.getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
        assertThat(strategyConfig.getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));

        PowerMock.verifyAll();
    }

    @Test
    public void whenUpdateStrategyConfigCalledWithUnrecognizedIdThenReturnEmptyStrategy() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        PowerMock.replayAll();

        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
        final StrategyConfig strategyConfig = strategyConfigRepository.updateStrategy(someExternalStrategyConfigWithUnknownId());

        assertThat(strategyConfig.getId()).isEqualTo(null);
        assertThat(strategyConfig.getName()).isEqualTo(null);
        assertThat(strategyConfig.getDescription()).isEqualTo(null);
        assertThat(strategyConfig.getClassName()).isEqualTo(null);
        assertThat(strategyConfig.getConfigItems().isEmpty());

        PowerMock.verifyAll();
    }

    @Test
    public void whenDeleteByIdCalledWithRecognizedIdThenReturnMatchingStrategy() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        ConfigurationManager.saveConfig(
                eq(TradingStrategiesType.class),
                anyObject(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME));

        PowerMock.replayAll();

        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
        final StrategyConfig strategyConfig = strategyConfigRepository.deleteStrategyById(STRAT_ID_1);

        assertThat(strategyConfig.getId()).isEqualTo(STRAT_ID_1);
        assertThat(strategyConfig.getName()).isEqualTo(STRAT_NAME_1);
        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
        assertThat(strategyConfig.getClassName()).isEqualTo(STRAT_CLASSNAME_1);
        assertThat(strategyConfig.getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
        assertThat(strategyConfig.getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
        assertThat(strategyConfig.getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
        assertThat(strategyConfig.getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));

        PowerMock.verifyAll();
    }

    @Test
    public void whenDeleteByIdCalledWithUnrecognizedIdThenReturnEmptyStrategy() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        PowerMock.replayAll();

        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
        final StrategyConfig strategyConfig = strategyConfigRepository.deleteStrategyById("unknown-id");

        assertThat(strategyConfig.getId()).isEqualTo(null);
        assertThat(strategyConfig.getName()).isEqualTo(null);
        assertThat(strategyConfig.getDescription()).isEqualTo(null);
        assertThat(strategyConfig.getClassName()).isEqualTo(null);
        assertThat(strategyConfig.getConfigItems().isEmpty());

        PowerMock.verifyAll();
    }

    @Test
    public void whenCreateStrategyCalledWithUnknownThenExpectServiceToReturnCreatedStrategy() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        ConfigurationManager.saveConfig(
                eq(TradingStrategiesType.class),
                anyObject(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME));

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfigPlusNewOne());

        PowerMock.replayAll();

        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
        final StrategyConfig strategyConfig = strategyConfigRepository.createStrategy(someExternalStrategyConfigWithUnknownId());

        assertThat(strategyConfig.getId()).isEqualTo(UNKNOWN_STRAT_ID);
        assertThat(strategyConfig.getName()).isEqualTo(STRAT_NAME_1);
        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
        assertThat(strategyConfig.getClassName()).isEqualTo(STRAT_CLASSNAME_1);
        assertThat(strategyConfig.getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
        assertThat(strategyConfig.getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
        assertThat(strategyConfig.getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
        assertThat(strategyConfig.getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));

        PowerMock.verifyAll();
    }

    @Test
    public void whenCreateStrategyConfigCalledWithExistingIdThenReturnEmptyStrategy() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(TradingStrategiesType.class),
                eq(STRATEGIES_CONFIG_XML_FILENAME),
                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalStrategiesConfig());

        PowerMock.replayAll();

        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
        final StrategyConfig strategyConfig = strategyConfigRepository.createStrategy(someExternalStrategyConfig());

        assertThat(strategyConfig.getId()).isEqualTo(null);
        assertThat(strategyConfig.getName()).isEqualTo(null);
        assertThat(strategyConfig.getDescription()).isEqualTo(null);
        assertThat(strategyConfig.getClassName()).isEqualTo(null);
        assertThat(strategyConfig.getConfigItems().isEmpty());

        PowerMock.verifyAll();
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    /*
     * Very, very painful... muh.
     */
    private static TradingStrategiesType allTheInternalStrategiesConfig() {

        final ConfigItemType buyPriceConfigItem = new ConfigItemType();
        buyPriceConfigItem.setName(BUY_PRICE_CONFIG_ITEM_KEY);
        buyPriceConfigItem.setValue(BUY_PRICE_CONFIG_ITEM_VALUE);

        final ConfigItemType amountToBuyConfigItem = new ConfigItemType();
        amountToBuyConfigItem.setName(AMOUNT_TO_BUY_CONFIG_ITEM_KEY);
        amountToBuyConfigItem.setValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);

        final OptionalConfigType configurationType = new OptionalConfigType();
        configurationType.getConfigItem().add(buyPriceConfigItem);
        configurationType.getConfigItem().add(amountToBuyConfigItem);

        final StrategyType strategyType1 = new StrategyType();
        strategyType1.setId(STRAT_ID_1);
        strategyType1.setName(STRAT_NAME_1);
        strategyType1.setDescription(STRAT_DESCRIPTION_1);
        strategyType1.setClassName(STRAT_CLASSNAME_1);
        strategyType1.setOptionalConfig(configurationType);

        final StrategyType strategyType2 = new StrategyType();
        strategyType2.setId(STRAT_ID_2);
        strategyType2.setName(STRAT_NAME_2);
        strategyType2.setDescription(STRAT_DESCRIPTION_2);
        strategyType2.setClassName(STRAT_CLASSNAME_2);
        strategyType2.setOptionalConfig(configurationType);

        final TradingStrategiesType tradingStrategiesType = new TradingStrategiesType();
        tradingStrategiesType.getStrategies().add(strategyType1);
        tradingStrategiesType.getStrategies().add(strategyType2);

        return tradingStrategiesType;
    }

    private static TradingStrategiesType allTheInternalStrategiesConfigPlusNewOne() {

        final ConfigItemType buyPriceConfigItem = new ConfigItemType();
        buyPriceConfigItem.setName(BUY_PRICE_CONFIG_ITEM_KEY);
        buyPriceConfigItem.setValue(BUY_PRICE_CONFIG_ITEM_VALUE);

        final ConfigItemType amountToBuyConfigItem = new ConfigItemType();
        amountToBuyConfigItem.setName(AMOUNT_TO_BUY_CONFIG_ITEM_KEY);
        amountToBuyConfigItem.setValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);

        final OptionalConfigType configurationType = new OptionalConfigType();
        configurationType.getConfigItem().add(buyPriceConfigItem);
        configurationType.getConfigItem().add(amountToBuyConfigItem);

        final StrategyType newStrat = new StrategyType();
        newStrat.setId(UNKNOWN_STRAT_ID);
        newStrat.setName(STRAT_NAME_1);
        newStrat.setDescription(STRAT_DESCRIPTION_1);
        newStrat.setClassName(STRAT_CLASSNAME_1);
        newStrat.setOptionalConfig(configurationType);

        final TradingStrategiesType existingStatsPlusNewOne = allTheInternalStrategiesConfig();
        existingStatsPlusNewOne.getStrategies().add(newStrat);
        return existingStatsPlusNewOne;
    }

    private static StrategyConfig someExternalStrategyConfig() {
        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return new StrategyConfig(STRAT_ID_1, STRAT_NAME_1, STRAT_DESCRIPTION_1, STRAT_CLASSNAME_1, configItems);
    }

    private static StrategyConfig someExternalStrategyConfigWithUnknownId() {
        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return new StrategyConfig(UNKNOWN_STRAT_ID, STRAT_NAME_1, STRAT_DESCRIPTION_1, STRAT_CLASSNAME_1, configItems);
    }
}
