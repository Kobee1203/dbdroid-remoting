package org.nds.dbdroid.remoting;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.dbdroid.remoting.front.ServerFactoryBean;
import org.nds.dbdroid.remoting.front.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerRunner {

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring/applicationContext.xml");

            IContactService contactService = applicationContext.getBean(IContactService.class);

            ServerFactoryBean sfb = new ServerFactoryBean();
            sfb.setUrlPattern("/dbdroid-remoting/*");
            sfb.registerService(contactService);
            Server server = sfb.create();
            server.start();

            String serverUrl = "http://" + server.getServerHostName() + ":" + server.getServerPort();
            System.out.println("Server available at " + serverUrl);

            String message = null;
            while (!"quit".equals(message)) {
                // Defines the standard input stream
                BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

                System.out.print("type 'quit' to kill server : ");
                System.out.flush(); // empties buffer, before you input text
                message = stdin.readLine();
            }
            Thread.sleep(3000);

            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
