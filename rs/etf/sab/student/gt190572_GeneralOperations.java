/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author glisha
 */
public class gt190572_GeneralOperations implements GeneralOperations {
    
    public Calendar initialTime;
  

    @Override
    public void setInitialTime(Calendar clndr) {
        
        initialTime = (Calendar) clndr.clone();
        //Postavljam u bazu trenutni datum
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement ps = con.prepareStatement("DELETE TodayDate "
                + "insert into TodayDate (Date) values(?) ")){
            
            ps.setDate(1, new java.sql.Date(initialTime.getTimeInMillis()));
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void recievingOrders() {
        
        Connection con = DB.getInstance().getConnection();
        
        
        try(Statement ps = con.createStatement()){
            ResultSet rs = ps.executeQuery("select IdOrder from [dbo].[Order] where Status = 'sent'");
            
            while(rs.next()){
                int IdOrder = rs.getInt(1);
                try(PreparedStatement ps1 = con.prepareStatement("select top(1) t.Date\n" +
                    "from TracingOrder t \n" +
                    " where t.IdOrder = ?\n" +
                    " order by t.Date desc" )){
                    
                    
                    ps1.setInt(1, IdOrder);
                    
                    ResultSet rs1 = ps1.executeQuery();
                    
                    if(rs1.next()){
                        try(PreparedStatement ps3 = con.prepareStatement("select * "
                                + " from TracingOrder "
                                + " where Date = ? and Date<= ?")){
                            
                            ps3.setDate(1,rs1.getDate(1));
                            ps3.setDate(2, new java.sql.Date(this.getCurrentTime().getTimeInMillis()));
                            
                            ResultSet rs3 = ps3.executeQuery();
                            if(!rs3.next()) return;
                        }
                        
                        
                        try(PreparedStatement ps2 = con.prepareStatement("update [dbo].[Order] "
                                + "set RecievingTime=? , Status=? "
                                + " where IdOrder = ?")){
                            
                            ps2.setDate(1, rs1.getDate(1));
                            ps2.setString(2, "arrived");
                            ps2.setInt(3,IdOrder);
                            ps2.executeUpdate();
                            
                        }
                        
                    }
                    
                
            }   catch (SQLException ex) {
                    Logger.getLogger(gt190572_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Calendar time(int i) {
        
        if(initialTime == null) return null;
        
        //Dodaj dodatne dane na trenutni datum
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement ps = con.prepareStatement("update TodayDate "
                + "set Date = ? ")){
            
            initialTime.add(Calendar.DAY_OF_YEAR,i);
            
            ps.setDate(1, new java.sql.Date(initialTime.getTimeInMillis()));
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        recievingOrders();
        
        return initialTime;
    }

    @Override
    public Calendar getCurrentTime() {
        
        //Dohvati danasnji datum
        Calendar c = Calendar.getInstance();
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement ps = con.prepareStatement("select date from TodayDate")){
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                c.setTime(rs.getDate(1));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(gt190572_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return c;
    }

    @Override
    public void eraseAll() {
         Connection con = DB.getInstance().getConnection();
         try(PreparedStatement cs = con.prepareStatement("delete Transaction_Shop\n" +
"	delete Transaction_Buyer \n" +
"	delete SystemTable\n" +
"	delete TodayDate \n" +
"	delete [dbo].[Transaction] DBCC CHECKIDENT ('[dbo].[Transaction]', RESEED, 0)\n" +
"	delete TracingOrder DBCC CHECKIDENT ('TracingOrder', RESEED, 0)\n" +
"	delete Order_Article DBCC CHECKIDENT ('Order_Article', RESEED, 0)\n" +
"	delete [dbo].[Order] DBCC CHECKIDENT ('[dbo].[Order]', RESEED, 0) \n" +
"	delete Buyer "
                     + "DBCC CHECKIDENT ('Buyer', RESEED, 0)\n"
                     + " delete Article DBCC CHECKIDENT ('Article', RESEED, 0)\n"
                     + " delete Shop DBCC CHECKIDENT ('Shop', RESEED, 0)\n"
                     + " delete CityConnection DBCC CHECKIDENT ('CityConnection', RESEED, 0)\n"
                     + " delete City DBCC CHECKIDENT ('City', RESEED, 0)")){
             cs.executeUpdate();
          
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
}
