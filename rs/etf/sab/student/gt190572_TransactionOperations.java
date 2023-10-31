/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.TransactionOperations;

/**
 *
 * @author glisha
 */
public class gt190572_TransactionOperations implements TransactionOperations{
    
    private BigDecimal systemProfit = new BigDecimal(0).setScale(3);
   
    
    @Override
    public BigDecimal getBuyerTransactionsAmmount(int i) {
       
       if(!Check.checkBuyer(i)) return new BigDecimal(-1).setScale(3);
        
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select sum(TotalPrice) from Transaction_Buyer where IdBuyer = ?")){
             
              cs.setInt(1, i);
              
              ResultSet rs = cs.executeQuery();
              if(!rs.next()) return new BigDecimal(-1).setScale(3);
              
              int id = rs.getInt(1);
              
              
              return new BigDecimal(rs.getInt(1)).setScale(3);
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
         
      return new BigDecimal(-1).setScale(3);
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int i) {
         if(!Check.checkShop(i)) return new BigDecimal(-1).setScale(3);
        
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select sum(TotalPrice) from Transaction_Shop where IdShop = ?")){
             
              cs.setInt(1, i);
              
              ResultSet rs = cs.executeQuery();
              if(!rs.next()) return new BigDecimal(-1).setScale(3);
              
              int sum = rs.getInt(1);
              
              rs.close();
              return new BigDecimal(sum).setScale(3);
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
         
      return new BigDecimal(-1).setScale(3);
    }

    @Override
    public List<Integer> getTransationsForBuyer(int i) {
        
         if(!Check.checkBuyer(i)) return null;
        
        List<Integer> buyerTransactions = null;
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement stmt = con.prepareStatement("select IdTransaction from Transaction_Buyer where IdBuyer = ?")){ 
                stmt.setInt(1, i);
              ResultSet rs = stmt.executeQuery();
              while(rs.next()){
                  if(buyerTransactions == null) buyerTransactions = new ArrayList<>();
                  buyerTransactions.add(rs.getInt("IdTransaction"));
              }
              return buyerTransactions;
              
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public int getTransactionForBuyersOrder(int i) {
        
       if(!Check.checkOrderBTransactions(i)) return -1;
       
       Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select IdTransaction from [dbo].[Transaction]  where IdOrder = ? "
                 + "and IdTrasaction in (select IdTransaction from Transaction_Buyer)")){
             
              cs.setInt(1, i);
              
              ResultSet rs = cs.executeQuery();
              rs.next();
              
              int id = rs.getInt(1);
              
              rs.close();
              return id;
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
        return -1;
    }
    
    @Override
    public int getTransactionForShopAndOrder(int i, int i1) {
        
       Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("select s.IdTransaction from [dbo].[Transaction]  t join Transaction_Shop s on "
                 + " t.IdTransaction = s.IdTransaction "
                 + " where IdOrder = ? and IdShop=? ")){
             
              cs.setInt(1, i);
              cs.setInt(2, i1);
              
              ResultSet rs = cs.executeQuery();
              if(!rs.next()) return -1;
              
              int id = rs.getInt(1);
              
              rs.close();
              return id;
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
        return -1;
    }

    @Override
    public List<Integer> getTransationsForShop(int i) {
        if(!Check.checkShop(i)) return null;
        
        List<Integer> shopTransactions = null;
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement stmt = con.prepareStatement("select IdTransaction from Transaction_Shop where IdShop = ?")){ 
                stmt.setInt(1, i);
              ResultSet rs = stmt.executeQuery();
              while(rs.next()){
                  if(shopTransactions == null) shopTransactions = new ArrayList<>();
                  shopTransactions.add(rs.getInt(1));
              }
              return shopTransactions;
              
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public Calendar getTimeOfExecution(int i) {
        
        Connection c = DB.getInstance().getConnection();
        try(PreparedStatement ps = c.prepareStatement("select ExecutionTime from [dbo].[Transaction] t join [dbo].[Order] o "
                + "on t.IdOrder = o.IdOrder where t.IdTransaction = ? and "
                + " IdTransaction in (select IdTransaction from Transaction_Buyer )");){
            
            ps.setInt(1, i);
            
            ResultSet rs = ps.executeQuery();
            Calendar cc = Calendar.getInstance();
            if(rs.next()) {cc.setTime(rs.getDate(1)); return cc;}
            
        }catch (SQLException ex) {
            Logger.getLogger(gt190572_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try(PreparedStatement ps = c.prepareStatement("select ExecutionTime from [dbo].[Transaction] t join [dbo].[Order] o "
                + "on t.IdOrder = o.IdOrder where t.IdTransaction = ? and t.ExecutionTime=o.RecievingTime and  "
                + " IdTransaction in (select IdTransaction from Transaction_Shop )");){
            
            ps.setInt(1, i);
            
            ResultSet rs = ps.executeQuery();
            Calendar cc = Calendar.getInstance();
            if(rs.next()) {cc.setTime(rs.getDate(1)); return cc;}
            
        }catch (SQLException ex) {
            Logger.getLogger(gt190572_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
       return null;
    }

    @Override
    public BigDecimal getAmmountThatBuyerPayedForOrder(int i) {
        
        
         Connection c = DB.getInstance().getConnection();
        try(PreparedStatement ps = c.prepareStatement("select TotalPrice from [dbo].[Transaction]   "
                + "where o.IdOrder = ?  and IdTransaction in (select IdTransaction from Transaction_Buyer");){
            
            ps.setInt(1, i);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {return new BigDecimal(rs.getDouble(1)).setScale(3);}
            
        }catch (SQLException ex) {
            Logger.getLogger(gt190572_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal(-1).setScale(3);
    }

    @Override
    public BigDecimal getAmmountThatShopRecievedForOrder(int i, int i1) {
         Connection c = DB.getInstance().getConnection();
        try(PreparedStatement ps = c.prepareStatement("select TotalPrice from [dbo].[Transaction]  t join Transaction_Shop s"
                + "on t.IdTransaction = s.IdTransaction  "
                + "where t.IdOrder = ? and s.IdShop=? ");){
            
            ps.setInt(1, i);
            ps.setInt(2, i1);
            
             ResultSet rs = ps.executeQuery();
            if(rs.next()) {return new BigDecimal(rs.getDouble(1)).setScale(3);}
            
        }catch (SQLException ex) {
            Logger.getLogger(gt190572_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal(-1).setScale(3);
    }

    @Override
    public BigDecimal getTransactionAmount(int i) {
       Connection c = DB.getInstance().getConnection();
        try(PreparedStatement ps = c.prepareStatement("select TotalSum from [dbo].[Transaction]  "
                + "where IdTransaction = ?");){
            
            ps.setInt(1, i);
            
             ResultSet rs = ps.executeQuery();
            Calendar cc = Calendar.getInstance();
            if(rs.next()) {return new BigDecimal(rs.getDouble(1)).setScale(3);}
            
        }catch (SQLException ex) {
            Logger.getLogger(gt190572_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal(-1).setScale(3);
    }
    

    @Override
    public BigDecimal getSystemProfit() {
        
        Connection con = DB.getInstance().getConnection();
        try(Statement s = con.createStatement()){
            
            ResultSet rs = s.executeQuery("Select Profit from SystemTable");
            if(rs.next()) return new BigDecimal(rs.getDouble(1)).setScale(3);
            
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         return new BigDecimal(0).setScale(3);
    }
    
}
