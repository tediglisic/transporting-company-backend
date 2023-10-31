/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;
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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author glisha
 */
public class gt190572_CityOperations implements CityOperations {

    @Override
    public int createCity(String string) {
        
           //Provera da li je zadato ime grada vec postojece
          if(Check.checkCityUnique(string)) return -1;
          
          
          //Dodaje novi grad
          Connection con = DB.getInstance().getConnection();
          try(PreparedStatement ps1 = con.prepareStatement("insert into City (Name) values (?)");){
             
              ps1.setString(1, string);
              ps1.executeUpdate();
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
          
          
          //Dohvata Id novokreiranog grada
          try(PreparedStatement ps2 = con.prepareStatement( "select IdCity from City where Name = ?");){
             
              ps2.setString(1, string);
              ResultSet rs1 = ps2.executeQuery();
              if(!rs1.next()){
                  rs1.close();
                  return -1;
              }else{
                  int i = rs1.getInt(1);
                  rs1.close();
                  return i;
              }
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        } 
          
         
        return -1;
    }

    @Override
    public List<Integer> getCities() {
        
        List<Integer> cities = null;
        
        
        Connection con = DB.getInstance().getConnection();
        try(Statement stmt = con.createStatement()){ 
              ResultSet rs = stmt.executeQuery("select* from City");
              while(rs.next()){
                  if(cities == null) cities = new ArrayList<>();
                  cities.add(rs.getInt("IdCity"));
              }
              return cities;
              
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public int connectCities(int i, int i1, int i2) {
        
        //Proverava da li postoji vec postoji konekcija izmedju dva grada
        if(Check.checkCityConnections(i, i1)) return -1;
        
        //Kreira novu putanju izmedju dva grada
         Connection con = DB.getInstance().getConnection();
          try(PreparedStatement ps1 = con.prepareStatement("insert into CityConnection (IdCity1,IdCity2,Distance)\n" +
                    " values (?,?,?)"
                 + " select IdLine from CityConnection where IdLine = SCOPE_IDENTITY()");){
             
               ps1.setInt(1, i);
               ps1.setInt(2, i1);
               ps1.setInt(3, i2);
               
               
               ResultSet rs = ps1.executeQuery();
             
               if(!rs.next()) return -1;
               return rs.getInt(1);
             
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        return -1;
    }

    
    @Override
    public List<Integer> getConnectedCities(int i) {
        
        List<Integer> connections = null;
        
        //Dohvata sve putanje koje ima grad i
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement stmt = con.prepareStatement("select * from CityConnection where IdCity1 = ? or IdCity2 = ?")){
                stmt.setInt(1,i);
                stmt.setInt(2,i);
                
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    
                    if(connections == null) connections = new ArrayList<>();
                    if(rs.getInt("IdCity1") == i){
                         connections.add(rs.getInt("IdCity2"));
                    }else{
                        connections.add(rs.getInt("IdCity1"));
                    }
     
                }
                
              return connections;
              
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    
    @Override
    public List<Integer> getShops(int i) {
        
        //Proverava da li zadati grad postoji
        if(!Check.checkCity(i)) return null;
        
        //Dohvata sve prodavnice koje se nalaze u zadatom gradu
        List<Integer> shops = null;
        Connection con = DB.getInstance().getConnection();
        try(PreparedStatement stmt = con.prepareStatement("select* from Shop where IdCity = ?")){ 
              stmt.setInt(1, i);
              ResultSet rs = stmt.executeQuery();
              
              while(rs.next()){
                  if(shops == null) shops = new ArrayList<>();
                  shops.add(rs.getInt("IdShop"));
              }
              return shops;
              
         } catch (SQLException ex) { 
            Logger.getLogger(gt190572_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        return null;
    }
    
}
