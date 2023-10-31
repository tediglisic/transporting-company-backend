/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author glisha
 */
public class Check {
    
    
        public static boolean checkShop(int idShop){
            
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select * from Shop where IdShop = ? ");){
              ps.setInt(1, idShop);
              ResultSet rs = ps.executeQuery();
              
              if(rs.next()) return true;
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
            return false;
        }
         public static boolean checkCityHasShop(int IdCity){
            
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select * from Shop where IdCity = ? ");){
              ps.setInt(1, IdCity);
              ResultSet rs = ps.executeQuery();
              
              if(rs.next()) return true;
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
            return false;
        }
    
        
         public static boolean checkCity(int IdCity){
            
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select * from City where IdCity = ? ");){
              ps.setInt(1, IdCity);
              ResultSet rs = ps.executeQuery();
              if(rs.next()) return true;
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            return false;
        }
         
        public static boolean checkBuyer(int IdBuyer){
            
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select * from Buyer where IdBuyer = ? ");){
              ps.setInt(1, IdBuyer);
              ResultSet rs = ps.executeQuery();
              if(rs.next()) return true;
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            return false;
        }
            
         public static boolean checkOrder(int IdOrder){
            
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select * from [dbo].[Order] where IdOrder = ? ");){
              ps.setInt(1, IdOrder);
              ResultSet rs = ps.executeQuery();
              if(rs.next()) return true;
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            return false;
        }
         
          public static boolean checkArticle(int IdArticle){
            
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select * from Article where IdArticle = ? ");){
              ps.setInt(1, IdArticle);
              ResultSet rs = ps.executeQuery();
              if(rs.next()){ return true;}
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            return false;
        }
        
         public static boolean checkCityConnections(int city1, int city2){
             
        
            
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select IdLine from CityConnection \n" +
"			where (IdCity1 = ? and IdCity2 = ?)or(\n" +
"					IdCity2 = ? and IdCity2 =?) ");){
              ps.setInt(1, city1);
              ps.setInt(2,city2);
              ps.setInt(3,city2);
              ps.setInt(4, city1);
              ResultSet rs = ps.executeQuery();
              if(rs.next()) return true;
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            return false;
        }

    static boolean checkCityUnique(String string) {
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select IdCity from City \n" +
"			where Name = ? ");){
              ps.setString(1, string);
             
              ResultSet rs = ps.executeQuery();
              if(rs.next()) {
                  return true;
              }
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            return false;
        }
    

    static boolean checkShopUnique(String string) {
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select IdShop from Shop \n" +
"			where Name = ? ");){
              ps.setString(1, string);
             
              ResultSet rs = ps.executeQuery();
              if(rs.next()) return true;
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            return false;
        }
    
    
      public static boolean checkOrderBTransactions(int IdOrder){
            
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select * from Transaction_Buyer where IdOrder = ? ");){
              ps.setInt(1, IdOrder);
              ResultSet rs = ps.executeQuery();
              if(rs.next()) return true;
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            return false;
        }
      
        public static boolean checkOrderSTransaction(int IdOrder){
            
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement ps = con.prepareStatement("select * from Transaction_Shop where IdOrder = ? ");){
              ps.setInt(1, IdOrder);
              ResultSet rs = ps.executeQuery();
              if(rs.next()) return true;
              rs.close(); 
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
            return false;
        }
    }

