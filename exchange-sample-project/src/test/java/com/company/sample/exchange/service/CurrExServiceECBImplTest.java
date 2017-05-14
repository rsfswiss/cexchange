package com.company.sample.exchange.service;

import com.company.sample.exchange.CurrExApplication;
import com.company.sample.exchange.domain.ICurrExRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.ZonedDateTime;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CurrExApplication.class)
@AutoConfigureMockMvc
public class CurrExServiceECBImplTest {

    @Value("${currex.service.ecb.scheduler.cron}")
    private String cronExpression;

    @MockBean
    private ICurrExRepository currExRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ICurrExService currExService;


    private MockMvc mockMvc;


    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testFetchAndStoreExchangeRateInformation() throws Exception {
    }

    @Test
    public void testGetExchangeRateBasedOnEuroForCurrencyAtDate() throws Exception {
        given(currExRepository.findByCurrencyCodeAndDate("USD","20170511")).
                willReturn("1.086");
        given(currExRepository.getMaxAvailableDateStr()).
                willReturn("20170511");
        given(currExRepository.getMinAvailableDateStr()).
                willReturn("20170411");
        CurrExRateResource resource =
                currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD", "20170511");
        Assert.assertTrue(resource.getCurrencyCode() == "USD");
        Assert.assertTrue(resource.getExchangeRate() == "1.086");
        Assert.assertTrue(resource.getExchangeRateDate() == "20170511");
    }

    @Test
    public void testGetExchangeRateBasedOnEuroForCurrencyAtDateNotFound() {
        given(currExRepository.findByCurrencyCodeAndDate("USD","20170511")).
                willReturn(null);
        given(currExRepository.getMaxAvailableDateStr()).
                willReturn("20170511");
        given(currExRepository.getMinAvailableDateStr()).
                willReturn("20170411");
        try {
            currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD", "20170511");
        } catch(Exception ex) {
            Assert.assertTrue(ex instanceof CurrExServiceDataNotFoundException);
        }
    }

    @Test
    public void testGetExchangeRateBasedOnEuroForCurrencyAtDateCurrencyIncorrect() {
        given(currExRepository.getMaxAvailableDateStr()).
                willReturn("20170511");
        given(currExRepository.getMinAvailableDateStr()).
                willReturn("20170411");
        try {
            currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("#", "20170511");
        } catch(Exception ex) {
            Assert.assertTrue(ex instanceof CurrExServiceCurrencyIncorrectException);
        }
    }

    @Test
    public void testGetExchangeRateBasedOnEuroForCurrencyAtDateCurrencyNotAvailable() {
        given(currExRepository.findByCurrencyCodeAndDate("USD","20170511")).
                willReturn("NA");
        given(currExRepository.getMaxAvailableDateStr()).
                willReturn("20170511");
        given(currExRepository.getMinAvailableDateStr()).
                willReturn("20170411");
        try {
            currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD", "20170511");
        } catch(Exception ex) {
            Assert.assertTrue(ex instanceof CurrExServiceCurrencyNotAvailableException);
        }
    }

    @Test
    public void testGetExchangeRateBasedOnEuroForCurrencyAtDateNotRecognized() {
        given(currExRepository.getMaxAvailableDateStr()).
                willReturn("20170511");
        given(currExRepository.getMinAvailableDateStr()).
                willReturn("20170411");
        try {
            currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD", "01-01-2017");
        } catch(Exception ex) {
            Assert.assertTrue(ex instanceof CurrExServiceDateNotRecognizedException);
        }
    }

    @Test
    public void testGetExchangeRateBasedOnEuroForCurrencyAtDateTooNew() {
        given(currExRepository.getMaxAvailableDateStr()).
                willReturn("20170511");
        given(currExRepository.getMinAvailableDateStr()).
                willReturn("20170411");
        try {
            currExService.getExchangeRateBasedOnEuroForCurrencyAtDate("USD", "30000101");
        } catch(Exception ex) {
            Assert.assertTrue(ex instanceof CurrExServiceDateTooNewException);
        }
    }

    @Test
    //to verify the scheduler cron expression from configuration
    public void testSchedulerWorkingDay(){
        //12/05/2017 was a Friday, Zurich is in CET
        ZonedDateTime fakeWorkDayCurrentDate = ZonedDateTime.parse("2017-05-12T15:30:00+01:00[Europe/Zurich]");
        ZonedDateTime expectedExecutionDate = ZonedDateTime.parse("2017-05-12T16:05:00+01:00[Europe/Zurich]");
        //must execute at 16:05
        java.util.Date mockedTriggerNextExecutionDate = getMockedTriggerNextExecutionTime(fakeWorkDayCurrentDate);
        Assert.assertTrue(mockedTriggerNextExecutionDate.toInstant().equals(expectedExecutionDate.toInstant()));
    }

    @Test
    //to verify the scheduler cron expression from configuration at weekends
    public void testSchedulerWeekendDay(){
        //13/05/2017 was a Saturday
        ZonedDateTime fakeWeekendDayCurrentDate = ZonedDateTime.parse("2017-05-13T15:30:00+01:00[Europe/Zurich]");
        ZonedDateTime expectedExecutionDate = ZonedDateTime.parse("2017-05-15T16:05:00+01:00[Europe/Zurich]");
        //must execute on Monday
        java.util.Date mockedTriggerNextExecutionDate = getMockedTriggerNextExecutionTime(fakeWeekendDayCurrentDate);
        Assert.assertTrue(mockedTriggerNextExecutionDate.toInstant().equals(expectedExecutionDate.toInstant()));
    }

    //mocks Spring's scheduler and delivers the next execution date given the current date
    private Date getMockedTriggerNextExecutionTime(ZonedDateTime currentMockDate) {
        org.springframework.scheduling.support.CronTrigger trigger =
                new CronTrigger(cronExpression);

        return trigger.nextExecutionTime(
                new TriggerContext() {

                    @Override
                    public Date lastScheduledExecutionTime() {
                        return java.util.Date.from(currentMockDate.toInstant());
                    }

                    @Override
                    public Date lastActualExecutionTime() {
                        return java.util.Date.from(currentMockDate.toInstant());
                    }

                    @Override
                    public Date lastCompletionTime() {
                        return java.util.Date.from(currentMockDate.toInstant());
                    }
                });
    }

}