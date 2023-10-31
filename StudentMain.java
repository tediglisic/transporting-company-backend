/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gt190572sab;

import rs.etf.sab.operations.*;
import org.junit.Test;
import rs.etf.sab.student.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

import java.util.Calendar;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new gt190572_ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new gt190572_BuyerOperations();
        CityOperations cityOperations = new gt190572_CityOperations();
        GeneralOperations generalOperations = new gt190572_GeneralOperations();
        OrderOperations orderOperations = new gt190572_OrderOperations();
        ShopOperations shopOperations = new gt190572_ShopOperations();
        TransactionOperations transactionOperations = new gt190572_TransactionOperations();

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
    }
}

