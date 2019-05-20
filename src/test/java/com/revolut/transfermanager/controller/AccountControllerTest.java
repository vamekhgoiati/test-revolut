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

public class AccountControllerTest extends JerseyTest {

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
    public void testAccountCreate() {
        String jsonString = "{\"name\":\"Test\",\"balance\":100}";
        Response response = target("account").request().post(Entity.json(jsonString));
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testAccountCreateInvalid() {
        String jsonString = "{\"nam\":\"Test\"}";
        Response response = target("account").request().post(Entity.json(jsonString));
        Assert.assertNotNull(response);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testAccountGet() throws IOException {
        String jsonString = "{\"name\":\"Test\",\"balance\":100}";
        Response response = target("account").request().post(Entity.json(jsonString));
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        AccountModel model = mapper.readValue((InputStream) response.getEntity(), AccountModel.class);
        Assert.assertNotNull(model.getId());

        Response getByIdResponse = target("account/" + model.getId()).request().get();
        Assert.assertNotNull(getByIdResponse);
        Assert.assertEquals(200, getByIdResponse.getStatus());

    }

    @Test
    public void testAccountUpdate() throws IOException {
        String jsonString = "{\"name\":\"Test\",\"balance\":100}";
        Response response = target("account").request().post(Entity.json(jsonString));
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        AccountModel model = mapper.readValue((InputStream) response.getEntity(), AccountModel.class);
        Assert.assertNotNull(model.getId());

        String updatedJson = "{\"name\":\"Test\",\"balance\":200,\"version\":1}";
        Response updateResponse = target("account/" + model.getId()).request().put(Entity.json(updatedJson));
        Assert.assertNotNull(updateResponse);
        Assert.assertEquals(200, updateResponse.getStatus());

        Response getByIdResponse = target("account/" + model.getId()).request().get();
        Assert.assertNotNull(getByIdResponse);
        Assert.assertEquals(200, getByIdResponse.getStatus());
        AccountModel updatedModel = mapper.readValue((InputStream) getByIdResponse.getEntity(), AccountModel.class);
        Assert.assertEquals(Long.valueOf(200L), updatedModel.getBalance());
        Assert.assertEquals(Long.valueOf(2L), updatedModel.getVersion());
    }

    @Test
    public void testAccountUpdateWithInvalidVersion() throws IOException {
        String jsonString = "{\"name\":\"Test\",\"balance\":100}";
        Response response = target("account").request().post(Entity.json(jsonString));
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        AccountModel model = mapper.readValue((InputStream) response.getEntity(), AccountModel.class);
        Assert.assertNotNull(model.getId());

        String updatedJson = "{\"name\":\"Test\",\"balance\":200,\"version\":3}";
        Response updateResponse = target("account/" + model.getId()).request().put(Entity.json(updatedJson));
        Assert.assertNotNull(updateResponse);
        Assert.assertEquals(400, updateResponse.getStatus());
    }

    @Test
    public void testAccountDelete() throws IOException {
        String jsonString = "{\"name\":\"Test\",\"balance\":100}";
        Response response = target("account").request().post(Entity.json(jsonString));
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        AccountModel model = mapper.readValue((InputStream) response.getEntity(), AccountModel.class);
        Assert.assertNotNull(model.getId());

        Response deleteResponse = target("account/" + model.getId()).request().delete();
        Assert.assertNotNull(deleteResponse);
        Assert.assertEquals(200, deleteResponse.getStatus());

        Response getByIdResponse = target("account/" + model.getId()).request().get();
        Assert.assertNotNull(getByIdResponse);
        Assert.assertEquals(404, getByIdResponse.getStatus());
    }
}
