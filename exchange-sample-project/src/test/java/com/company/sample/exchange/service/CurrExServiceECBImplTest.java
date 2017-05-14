package com.company.sample.exchange.service;

import com.company.sample.exchange.CurrExApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CurrExApplication.class)
@AutoConfigureMockMvc
public class CurrExServiceECBImplTest {

    @Value("${currex.service.ecb.scheduler.cron}")
    private String cronExpression;

    @Test
    public void testFetchAndStoreExchangeRateInformation() throws Exception {
    }

    @Test
    public void testgetExchangeRateBasedOnEuroForCurrencyAtDate() throws Exception {
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