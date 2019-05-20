package com.revolut.transfermanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.transfermanager.config.GuiceJerseyBridge;
import com.revolut.transfermanager.db.util.DBUtil;
import com.revolut.transfermanager.dto.AccountModel;
import com.revolut.transfermanager.ioc.TransferManagerModule;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class TrasnferControllerTest extends JerseyTest {

    @Override
    protected Application configure() {
        DBUtil.init("db.properties", "schema.db");
        Injector injector = Guice.createInjector(Collections.singletonList(new TransferManagerModule()));
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new GuiceJerseyBridge(injector));
        resourceConfig.packages(true, "com.revolut.transfermanager");
        return resourceConfig;
    }

    @Test
    public void testTransferSuccessFlow() throws IOException {
        String id1 = createTestAccount("Test1", 100L);
        String id2 = createTestAccount("Test2", 200L);

        String transferJson = String.format("{\"accountFrom\":\"%s\",\"accountTo\":\"%s\",\"amount\":%s}", id1, id2, 50);
        Response response = target("transfer").request().post(Entity.json(transferJson));
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        Response account1 = target("account/" + id1).request().get();
        Response account2 = target("account/" + id2).request().get();
        AccountModel model1 = mapper.readValue((InputStream) account1.getEntity(), AccountModel.class);
        AccountModel model2 = mapper.readValue((InputStream) account2.getEntity(), AccountModel.class);
        Assert.assertEquals(Long.valueOf(50L), model1.getBalance());
        Assert.assertEquals(Long.valueOf(250L), model2.getBalance());

    }

    @Test
    public void testTransferWithMultipleThreadsIsConsistent() throws IOException, InterruptedException {
        Thread[] ops = new Thread[10];
        String id1 = createTestAccount("Test1", 200L);
        String id2 = createTestAccount("Test2", 200L);

        for (int i = 0; i < 10; i++) {
            int tmp = i;
            ops[i] = new Thread(() -> {
                String transferJson = String.format("{\"accountFrom\":\"%s\",\"accountTo\":\"%s\",\"amount\":%s}", tmp % 2 == 0 ? id1 : id2, tmp % 2 == 0 ? id2 : id1, 10);
                target("transfer").request().post(Entity.json(transferJson));
            });
            ops[i].start();
        }

        for(Thread t : ops) {
            t.join();
        }

        ObjectMapper mapper = new ObjectMapper();
        Response account1 = target("account/" + id1).request().get();
        Response account2 = target("account/" + id2).request().get();
        AccountModel model1 = mapper.readValue((InputStream) account1.getEntity(), AccountModel.class);
        AccountModel model2 = mapper.readValue((InputStream) account2.getEntity(), AccountModel.class);
        Assert.assertEquals(Long.valueOf(400L), Long.valueOf(model1.getBalance() + model2.getBalance()));

    }

    @Test
    public void testTransferInsufficientFunds() throws IOException {
        String id1 = createTestAccount("Test1", 100L);
        String id2 = createTestAccount("Test2", 200L);

        String transferJson = String.format("{\"accountFrom\":\"%s\",\"accountTo\":\"%s\",\"amount\":%s}", id1, id2, 150);
        Response response = target("transfer").request().post(Entity.json(transferJson));
        Assert.assertNotNull(response);
        Assert.assertEquals(400, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        Response account1 = target("account/" + id1).request().get();
        Response account2 = target("account/" + id2).request().get();
        AccountModel model1 = mapper.readValue((InputStream) account1.getEntity(), AccountModel.class);
        AccountModel model2 = mapper.readValue((InputStream) account2.getEntity(), AccountModel.class);
        Assert.assertEquals(Long.valueOf(100L), model1.getBalance());
        Assert.assertEquals(Long.valueOf(200L), model2.getBalance());

    }

    private String createTestAccount(String name, long balance) throws IOException {
        String jsonString = String.format("{\"name\":\"%s\",\"balance\":%s}", name, balance);
        Response response = target("account").request().post(Entity.json(jsonString));
        ObjectMapper mapper = new ObjectMapper();
        AccountModel model = mapper.readValue((InputStream) response.getEntity(), AccountModel.class);
        return model.getId();
    }
}
