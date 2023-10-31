/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.BuyerOperations;

/**
 *
 * @author glisha
 */
public class gt190572_BuyerOperations implements BuyerOperations{

    @Override
    //TO DO
    public int createBuyer(String string, int i) {
        
         //Proverava postojanje grada gde se stvara kupac
         if(!Check.checkCity(i)) return -1;
         
         //Stvara novog kupca i vraca kao povratnu vrednost id novog kupca
          Connection con = DB.getInstance().getConnection();
          try(PreparedStatement ps1 = con.prepareStatement("insert into Buyer (IdCity,Name,Credits) values (?,?,?)"
                 + " select IdBuyer from Buyer where IdBuyer = SCOPE_IDENTITY()");){
              ps1.setInt(1,i);
              ps1.setString(2, string);
              ps1.setInt(3,0);
              
              ResultSet rs = ps1.executeQuery();
              if(!rs.next()) return -1;
              return rs.getInt(1);
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int setCity(int i, int i1) {
        
        //Proverava postojanje kupca i novog grada 
        if(!Check.checkBuyer(i) || !Check.checkCity(i1)) return -1;
        
        //Postavlja kupcu novi grad
        Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("update Buyer\n" +
"		set IdCity = ?\n" +
"		where IdBuyer = ?")){
              cs.setInt(2, i);
              cs.setInt(1,i1);
              
              cs.executeUpdate();
              return 1;
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getCity(int i) {
        
        //Proverava postojanje kupca
        if(!Check.checkBuyer(i)) return -1;
        
        //Dohvata grad za zadatog kupca
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select IdCity\n" +
"			from Buyer\n" +
"			where IdBuyer = ?")){
              cs.setInt(1, i);    
              ResultSet rs =  cs.executeQuery();
             
              if(rs.next()) return rs.getInt(1);
            
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return -1;
    }

    @Override
    public BigDecimal increaseCredit(int i, BigDecimal bd) {
        
        
        //Proverava postojanje kupca i kolicine novih kredita
        if(!Check.checkBuyer(i)) return new BigDecimal(-1).setScale(3);
        if(bd.doubleValue()<0) return new BigDecimal(-1).setScale(3);
        
       
        
         Connection con = DB.getInstance().getConnection();
         
         //Dodaj mu novu kolicinu credits
         try(PreparedStatement cs = con.prepareStatement("update Buyer\n" +
"			set Credits = Credits + ?\n" +
"			where IdBuyer = ?")){
              cs.setInt(2, i);
              cs.setDouble(1, bd.doubleValue());
              
              cs.executeUpdate();
                  
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         
         //Dovuci novu kolicinu kredita za zadatog kupca
         try(PreparedStatement cs1 = con.prepareStatement("select Credits from Buyer where IdBuyer="+i);   
              ResultSet rs = cs1.executeQuery();){
             
               if(!rs.next()) return new BigDecimal(-1).setScale(3);
               
                 return new BigDecimal(rs.getDouble(1)).setScale(3);
                  
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BigDecimal(-1).setScale(3);
    }

    @Override
    public int createOrder(int i) {
        
        //Proveri da li dati kupac postoji
          if(!Check.checkBuyer(i)) return -1;
          
          
          //Kreiraj novu porudzbinu za kupca bez artikala
          Connection con = DB.getInstance().getConnection();
          try(PreparedStatement ps1 = con.prepareStatement("insert into [dbo].[Order] (Status,IdBuyer,SendingTime,RecievingTime)\n" +
                   " values ('created',?,NULL,NULL)"
                 + " select IdOrder from [dbo].[Order] where IdOrder = SCOPE_IDENTITY()");){
              ps1.setInt(1,i);
              ResultSet rs = ps1.executeQuery();
              if(!rs.next()) return -1;
              return rs.getInt(1);
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getOrders(int i) {
        
        //Proveri da li zadati kupac postoji
        if(!Check.checkBuyer(i)) return null;
        
       List<Integer> orders = new ArrayList<>();
       Connection con = DB.getInstance().getConnection();
       
       
       //Dohvati sve ordere od zadatog kupca
       try(PreparedStatement ps = con.prepareStatement("select IdOrder from [dbo].[Order] where IdBuyer = ?");){
           
           ps.setInt(1,i);
           ResultSet rs = ps.executeQuery();
           
           while(rs.next()){
               orders.add(rs.getInt(1));
           }
           return orders;
           
        }catch (SQLException ex) {
            Logger.getLogger(gt190572_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       
         return null;
       
    }

    @Override
    public BigDecimal getCredit(int i) {
        
        //Proveri da li zadati kupac postoji
        if(!Check.checkBuyer(i)) return new BigDecimal(-1).setScale(3);
        
        
        //Dohvata kredite od zadatog kupca
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select Credits from Buyer where IdBuyer=?")){
             
              cs.setInt(1, i);
              
              try(ResultSet rs = cs.executeQuery();){
                  if(!rs.next()) return new BigDecimal(-1).setScale(3);
                  return new BigDecimal(rs.getDouble(1)).setScale(3);
              }
             
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return new BigDecimal(-1).setScale(3);
    }
    
}
