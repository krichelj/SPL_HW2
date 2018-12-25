package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

    public static void main(String[] args) {

        // store runner variables
        Inventory currentInventory = Inventory.getInstance(); // get the Inventory singleton
        ResourcesHolder currentResourcesHolder = ResourcesHolder.getInstance(); // get the ResourcesHolder singleton
        MoneyRegister currentMoneyRegister = MoneyRegister.getInstance(); // get the MoneyRegister singleton
        HashMap<Integer,Customer> customerHashMap = new HashMap<>(); // current store customer list variable
        Gson gson = new Gson(); // gson variable to read from file

        try {

            // parse the input JSON file and construct the initial objects
            JsonReader reader = new JsonReader(new FileReader(args[0]));
            InitialBookStoreInfo initialInfo = gson.fromJson(reader, InitialBookStoreInfo.class);
            int duration = initialInfo.services.time.getDuration();
            int speed = initialInfo.services.time.getSpeed();
            currentInventory.load(initialInfo.initialInventory); // open the store's Inventory and add the input book list
            currentResourcesHolder.load(initialInfo.initialResources[0].vehicles); // open the store's ResourcesHolder and add the input vehicle list

            // add all the store services' threads to appropriate executor services
            ExecutorService sellingServicesPool = Executors.newFixedThreadPool(initialInfo.services.selling),
                    inventoryServicesPool = Executors.newFixedThreadPool(initialInfo.services.inventoryService),
                    logisticsServicesPool = Executors.newFixedThreadPool(initialInfo.services.logistics),
                    resourceServicesPool = Executors.newFixedThreadPool(initialInfo.services.resourcesService),
                    apiServicesPool = Executors.newFixedThreadPool(initialInfo.services.customers.length);

            for (int i=1 ; i<=initialInfo.services.selling; i++)
                sellingServicesPool.submit(new SellingService(i,duration,speed));
            for (int i=1 ; i<=initialInfo.services.inventoryService; i++)
                inventoryServicesPool.submit(new InventoryService(i, duration));
            for (int i=1 ; i<=initialInfo.services.logistics; i++)
                logisticsServicesPool.submit(new LogisticsService(i,duration,speed));
            for (int i=1 ; i<=initialInfo.services.resourcesService; i++)
                resourceServicesPool.submit(new ResourceService(i, duration));

            // initiate new APIServices for each customer and put them in a hash map
            for (int i=0; i<initialInfo.services.customers.length; i++) {

                Customer currentCustomer = new Customer(initialInfo.services.customers[i]);
                apiServicesPool.submit(new APIService (i+1, duration, speed, currentCustomer)); // initiate and start a new APIService thread for the customer
                /*new Thread(new APIService (i+1, currentCustomer)).start(); */
                customerHashMap.put(currentCustomer.getId(), currentCustomer); // put the customer in the hash map
            }

            // initialize the time service
            Thread timeServiceThread = new Thread(new TimeService(speed,duration));
            timeServiceThread.start();
            timeServiceThread.join(); // wait for the time service to terminate

            // shutdown the executor services
            sellingServicesPool.shutdown();
            inventoryServicesPool.shutdown();
            logisticsServicesPool.shutdown();
            resourceServicesPool.shutdown();
            apiServicesPool.shutdown();

            // wait the maximum time for all the executor services to terminate
            sellingServicesPool.awaitTermination(duration*speed, TimeUnit.MILLISECONDS);
            inventoryServicesPool.awaitTermination(duration*speed, TimeUnit.MILLISECONDS);
            logisticsServicesPool.awaitTermination(duration*speed, TimeUnit.MILLISECONDS);
            resourceServicesPool.awaitTermination(duration*speed, TimeUnit.MILLISECONDS);
            apiServicesPool.awaitTermination(duration*speed, TimeUnit.MILLISECONDS);

            // print the output files after the services are terminated
            Printer.print(customerHashMap,args[1]); // customer list
            currentInventory.printInventoryToFile(args[2]); // books in the inventory
            currentMoneyRegister.printOrderReceipts(args[3]); // order receipts in the MoneyRegister
            Printer.print(currentMoneyRegister, args[4]); // MoneyRegister object

        } catch (FileNotFoundException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    // private nested classes - used only for reading the input JSON file

    private class InitialBookStoreInfo {

        private BookInventoryInfo[] initialInventory;
        private InitialResources[] initialResources;
        private Services services;


        private InitialBookStoreInfo(BookInventoryInfo[] initialInventory, InitialResources[] initialResources, Services services) {

            this.initialInventory = initialInventory;
            this.initialResources = initialResources;
            this.services = services;
        }
    }

    private class InitialResources {

        private DeliveryVehicle[] vehicles;

        private InitialResources(DeliveryVehicle[] vehicles) {

            this.vehicles = vehicles;
        }
    }

    private class Services {

        private TimeService time;
        private int selling, inventoryService, logistics, resourcesService;
        private Customer[] customers;

        private Services(TimeService time, int selling, int inventoryService, int logistics, int resourcesService, Customer[] customers) {
            this.time = time;
            this.selling = selling;
            this.inventoryService = inventoryService;
            this.logistics = logistics;
            this.resourcesService = resourcesService;
            this.customers = customers;
        }
    }
}